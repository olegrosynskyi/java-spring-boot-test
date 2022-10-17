package io.skai.template.dataaccess.dao;

import io.skai.template.dataaccess.entities.Campaign;

import java.util.Optional;

public interface CampaignDao {

    long create(Campaign campaign);

    Optional<Campaign> findById(long id);

    long update(Campaign campaign);

    long deleteById(long id);

}
