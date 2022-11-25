package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import org.jooq.Condition;

import java.util.List;
import java.util.Optional;

public interface FilterQueryService {

    Optional<Condition> filteringByCampaignFields(List<QueryFilter<List<String>>> queryFilters);

    Optional<Condition> filteringByAdGroupFieldsWithPrefixes(List<QueryFilter<List<String>>> queryFilters);

}
