package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("FilterQueryService")
@Slf4j
@RequiredArgsConstructor
public class FilterQueryServiceImpl implements FilterQueryService {

    private static final String CAMPAIGN_TABLE_NAME = CampaignTable.TABLE.getName();
    private static final String AD_GROUP_PREFIX = "adGroup.";
    private static final Map<String, String> matchRequestFieldToDbFieldMap = Map.of(
            "campaign", "campaign",
            "adGroup", "ad_groups"
    );

    @Override
    public List<Condition> filteringCampaigns(List<QueryFilter<List<String>>> queryFilters, List<TableField<Record, ?>> fields) {
        final List<QueryFilter<List<String>>> filters = getQueryFiltersWithoutPrefix(queryFilters);
        final Map<String, List<QueryFilter<List<String>>>> groupedFilters = groupingQueries(filters);

        return filtering(groupedFilters, CAMPAIGN_TABLE_NAME, fields);
    }

    @Override
    public List<Condition> filteringCampaignsWithPrefix(List<QueryFilter<List<String>>> queryFilters, List<TableField<Record, ?>> fields) {
        final List<QueryFilter<List<String>>> filters = getQueryFiltersWithPrefix(queryFilters, AD_GROUP_PREFIX);
        final Map<String, List<QueryFilter<List<String>>>> groupedFilters = groupingQueries(filters);

        return filtering(groupedFilters, CAMPAIGN_TABLE_NAME, fields);
    }

    private List<QueryFilter<List<String>>> getQueryFiltersWithoutPrefix(List<QueryFilter<List<String>>> queryFilters) {
        return queryFilters.stream().filter(filter -> !filter.getField().contains(".")).toList();
    }

    private List<QueryFilter<List<String>>> getQueryFiltersWithPrefix(List<QueryFilter<List<String>>> queryFilters, String prefix) {
        return queryFilters.stream().filter(filter -> filter.getField().startsWith(prefix)).toList();
    }

    private Map<String, List<QueryFilter<List<String>>>> groupingQueries(List<QueryFilter<List<String>>> filters) {
        return filters.stream().collect(Collectors.groupingBy(filter -> filter.getOperator().getValue()));
    }

    private List<Condition> filtering(Map<String, List<QueryFilter<List<String>>>> groupedFilters, String tableName, List<TableField<Record, ?>> fields) {
        return groupedFilters.entrySet().stream().map(entry -> {
                    final String operation = entry.getKey();
                    final List<QueryFilter<List<String>>> queries = entry.getValue();

                    return switch (FilterOperator.valueOf(operation)) {
                        case EQUALS -> filteringByEquals(queries, tableName, fields);
                        case IN -> filteringByIn(queries, tableName, fields);
                        default -> throw new IllegalStateException("Unexpected value: " + FilterOperator.valueOf(operation));
                    };
                })
                .toList();
    }

    private Condition filteringByEquals(List<QueryFilter<List<String>>> queryFilters, String tableName, List<TableField<Record, ?>> fields) {
        return queryFilters.stream().map(query -> {
                    final String queryField = query.getField();
                    final TableField<Record, ?> fieldToSearch = getFieldToSearchFormQueryField(queryField, tableName, fields);

                    return query.getValues().stream().map(queryValue -> {
                        final String sql = String.join(".", fieldToSearch.getQualifiedName().getName()) + "=" + "?";
                        return DSL.condition(sql, queryValue);
                    }).toList();
                })
                .flatMap(Collection::stream)
                .reduce(Condition::or)
                .orElse(null);
    }

    private Condition filteringByIn(List<QueryFilter<List<String>>> queryFilters, String tableName, List<TableField<Record, ?>> fields) {
        return queryFilters.stream().map(query -> {
                    final String queryField = query.getField();
                    final TableField<Record, ?> fieldToSearch = getFieldToSearchFormQueryField(queryField, tableName, fields);

                    return fieldToSearch.in(query.getValues());
                })
                .reduce(Condition::or)
                .orElse(null);
    }

    private String formattingQueryNames(String queryFieldName) {
        return queryFieldName.replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase();
    }

    private TableField<Record, ?> getFieldToSearchFormQueryField(String queryField, String tableName, List<TableField<Record, ?>> fields) {
        final String field = queryField.contains(".") ? queryField : tableName + "." + queryField;
        final String originalField = Arrays
                .stream(field.split("\\."))
                .reduce((v1, v2) -> formattingQueryNames(String.join(".", matchRequestFieldToDbFieldMap.get(v1), v2)))
                .orElse(null);

        return fields
                .stream()
                .filter(dbField -> String.join(".", dbField.getQualifiedName().getName()).equals(originalField))
                .findFirst()
                .orElse(null);
    }

}
