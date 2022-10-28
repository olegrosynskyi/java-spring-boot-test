package io.skai.template.services;

import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.CampaignFetch;
import io.skai.template.dataaccess.entities.CampaignQuery;

import java.util.List;

public interface CampaignService {

    long create(Campaign campaign);

    Campaign findById(long id);

    long update(long id, Campaign campaign);

    long deleteById(long id);

    List<CampaignFetch> fetchCampaigns(CampaignQuery campaignQuery);

}
