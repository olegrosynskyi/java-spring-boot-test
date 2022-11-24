package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;

import java.util.List;
import java.util.Optional;

public interface FieldMapperService {

    List<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignFields(List<String> fields);

    Optional<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignField(String field);

    List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseCampaignFieldsWithPrefix(List<String> fields);

    Optional<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFieldWithPrefix(String field);

    List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFields(List<String> fields);

    List<FieldMapper<?, Campaign.CampaignBuilder>> parseAdGroupFieldsWithPrefix(List<String> fields);

}
