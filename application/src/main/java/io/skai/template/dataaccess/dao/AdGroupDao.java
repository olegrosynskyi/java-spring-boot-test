package io.skai.template.dataaccess.dao;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.entities.AdGroup;

import java.util.List;
import java.util.Optional;

public interface AdGroupDao {

    long create(AdGroup adGroup);

    Optional<AdGroup> findById(long id);

    long update(AdGroup adGroup);

    long deleteById(long id);

    List<AdGroup> fetchNotDeletedByKsName(String ksName);

    List<AdGroup> fetchAdGroups(ApiFetchRequest<QueryFilter<String>> apiFetchRequest);

}
