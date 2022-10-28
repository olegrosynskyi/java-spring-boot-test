package io.skai.template.dataaccess.dao;

import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.CampaignFetch;
import io.skai.template.dataaccess.entities.CampaignQuery;

import java.util.List;
import java.util.Optional;

public interface CampaignDao {

    long create(Campaign campaign);

    Optional<Campaign> findById(long id);

    long update(Campaign campaign);

    long deleteById(long id);

    List<CampaignFetch> fetchCampaigns(CampaignQuery campaignQuery);

}
