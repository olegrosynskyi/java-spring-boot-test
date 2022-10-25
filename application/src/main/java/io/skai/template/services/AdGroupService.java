package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;

public interface AdGroupService {

    long create(AdGroup adGroup);

    AdGroup findById(long id);

    long update(long id, AdGroup adGroup);

    long deleteById(long id);

}
