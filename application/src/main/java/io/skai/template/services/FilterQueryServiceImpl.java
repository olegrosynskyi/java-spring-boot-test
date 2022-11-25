package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("FilterQueryService")
@Slf4j
@RequiredArgsConstructor
public class FilterQueryServiceImpl implements FilterQueryService {
    private final FieldMapperService fieldMapperService;

    @Override
    public Optional<Condition> filteringByCampaignFields(List<QueryFilter<List<String>>> queryFilters) {
        return Seq.seq(queryFilters)
                .map(queryFilter -> new Tuple2<>(queryFilter, fieldMapperService.parseCampaignField(queryFilter.getField())))
                .filter(queryFilter -> queryFilter.v2().isPresent())
                .map(queryFilter -> filtering(queryFilter.v1(), queryFilter.v2().get().getDbField()))
                .reduce(Condition::and);
    }

    @Override
    public Optional<Condition> filteringByAdGroupFieldsWithPrefixes(List<QueryFilter<List<String>>> queryFilters) {
        return Seq.seq(queryFilters)
                .map(queryFilter -> new Tuple2<>(queryFilter, fieldMapperService.parseAdGroupFieldWithPrefix(queryFilter.getField())))
                .filter(queryFilter -> queryFilter.v2().isPresent())
                .map(queryFilter -> filtering(queryFilter.v1(), queryFilter.v2().get().getDbField()))
                .reduce(Condition::and);
    }

    private Condition filtering(QueryFilter<List<String>> filter, TableField<Record, ?> field) {
        final FilterOperator operator = filter.getOperator();
        final List<String> values = filter.getValues();

        return switch (operator) {
            case EQUALS -> filteringByEquals(values, field);
            case IN -> filteringByIn(values, field);
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };
    }

    private Condition filteringByEquals(List<String> values, TableField<Record, ?> field) {
        return Seq.seq(values)
                .map(field::equalIgnoreCase)
                .reduce(Condition::or)
                .orElse(null);
    }

    private Condition filteringByIn(List<String> values, TableField<Record, ?> field) {
        return field.in(values);
    }

}
