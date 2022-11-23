package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("FilterQueryService")
@Slf4j
@RequiredArgsConstructor
public class FilterQueryServiceImpl implements FilterQueryService {

    private static final String AD_GROUP_PREFIX = "adGroup.";
    private final FieldMapperService fieldMapperService;

    @Override
    public Condition filteringCampaigns(List<QueryFilter<List<String>>> queryFilters) {
        final List<QueryFilter<List<String>>> filters = getQueryFiltersWithoutPrefix(queryFilters);

        return Seq.seq(filters).map(this::filtering).reduce(Condition::and).orElse(null);
    }

    @Override
    public Condition filteringCampaignsWithPrefix(List<QueryFilter<List<String>>> queryFilters) {
        final List<QueryFilter<List<String>>> filters = getQueryFiltersWithPrefix(queryFilters, AD_GROUP_PREFIX);

        return Seq.seq(filters).map(this::filtering).reduce(Condition::and).orElse(null);
    }

    private List<QueryFilter<List<String>>> getQueryFiltersWithoutPrefix(List<QueryFilter<List<String>>> queryFilters) {
        return queryFilters.stream().filter(filter -> !filter.getField().contains(".")).toList();
    }

    private List<QueryFilter<List<String>>> getQueryFiltersWithPrefix(List<QueryFilter<List<String>>> queryFilters, String prefix) {
        return queryFilters.stream().filter(filter -> filter.getField().startsWith(prefix)).toList();
    }

    private Condition filtering(QueryFilter<List<String>> filter) {
        final FilterOperator operator = filter.getOperator();
        final List<String> values = filter.getValues();
        final String field = filter.getField();

        return switch (operator) {
            case EQUALS -> filteringByEquals(values, field);
            case IN -> filteringByIn(values, field);
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };
    }

    private Condition filteringByEquals(List<String> values, String field) {
        final List<TableField<Record, ?>> fieldToSearch = getTableFields(field).stream().map(FieldMapper::getDbField).collect(Collectors.toList());
        return Seq.seq(values)
                .map(value -> Seq.seq(fieldToSearch).map(dbField -> dbField.equalIgnoreCase(value)))
                .flatMap(Seq::stream)
                .reduce(Condition::or)
                .orElse(null);
    }


    private Condition filteringByIn(List<String> values, String field) {
        return Seq.seq(getTableFields(field))
                .map(dbField -> dbField.getDbField().in(values))
                .reduce(Condition::or)
                .orElse(null);
    }

    private List<FieldMapper<?, ?>> getTableFields(String field) {
        final FieldMapper<?, Campaign.CampaignBuilder> fieldToSearchWithoutPrefix = fieldMapperService.parseCampaignField(field).orElse(null);
        final FieldMapper<?, AdGroup.AdGroupBuilder> fieldToSearchWithPrefix = fieldMapperService.parseCampaignFieldWithPrefix(field).orElse(null);

        return Seq.of(
                fieldToSearchWithoutPrefix,
                fieldToSearchWithPrefix
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
