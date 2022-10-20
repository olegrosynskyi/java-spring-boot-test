package io.skai.template.dataaccess.dao;

import io.skai.template.dataaccess.entities.AdGroup;

import java.util.List;
import java.util.Optional;

public interface AdGroupDao {

    long create(AdGroup adGroup);

    Optional<AdGroup> findById(long id);

    long update(AdGroup adGroup);

    long deleteById(long id);

    List<AdGroup> fetchNotDeletedByKsName(String ksName);

}
