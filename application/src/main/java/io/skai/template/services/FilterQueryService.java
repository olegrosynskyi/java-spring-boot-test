package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import org.jooq.Condition;

import java.util.List;

public interface FilterQueryService {

    Condition filteringCampaigns(List<QueryFilter<List<String>>> queryFilters);

    Condition filteringCampaignsWithPrefix(List<QueryFilter<List<String>>> queryFilters);

}
