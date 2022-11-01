package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.CampaignFetch;

import java.util.List;

public interface CampaignService {

    long create(Campaign campaign);

    Campaign findById(long id);

    long update(long id, Campaign campaign);

    long deleteById(long id);

    List<CampaignFetch> fetchCampaigns(ApiFetchRequest<QueryFilter<String>> apiFetchRequest);

}
