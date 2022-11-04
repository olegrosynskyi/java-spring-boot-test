package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;

import java.util.List;

public interface FieldMapperService {

    List<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignFields(List<String> fields);

    List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFields(List<String> fields);

}
