package io.skai.template.dataaccess.dao;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.entities.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignDao {

    long create(Campaign campaign);

    Optional<Campaign> findById(long id);

    long update(Campaign campaign);

    long deleteById(long id);

    List<Campaign> fetchCampaigns(ApiFetchRequest<QueryFilter<List<String>>> apiFetchRequest);

}
