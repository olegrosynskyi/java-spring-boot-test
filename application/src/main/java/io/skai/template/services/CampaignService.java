package io.skai.template.services;

import io.skai.template.dataaccess.entities.Campaign;

public interface CampaignService {

    long create(Campaign campaign);

    Campaign findById(long id);

    long update(long id, Campaign campaign);

    long deleteById(long id);

}
