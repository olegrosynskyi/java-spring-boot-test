package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.List;

public interface FilterQueryService {

    List<Condition> filteringCampaigns(List<QueryFilter<List<String>>> queryFilters, List<TableField<Record, ?>> fields);

    List<Condition> filteringCampaignsWithPrefix(List<QueryFilter<List<String>>> queryFilters, List<TableField<Record, ?>> fields);

}
