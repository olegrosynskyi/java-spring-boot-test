package io.skai.template.dataaccess.dao;

import io.skai.template.dataaccess.entities.Campaigns;

import java.util.Optional;

public interface CampaignsDao {

    long create(Campaigns campaign);

    Optional<Campaigns> findById(long id);

    long update(Campaigns campaign);

    long deleteById(long id);

}
