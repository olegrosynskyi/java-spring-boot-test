package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.entities.AdGroup;

import java.util.List;

public interface AdGroupService {

    long create(AdGroup adGroup);

    AdGroup findById(long id);

    long update(long id, AdGroup adGroup);

    long deleteById(long id);

    List<AdGroup> fetchAdGroups(ApiFetchRequest<QueryFilter<String>> apiFetchRequest);

}
