package io.skai.template.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.OpenPlatformDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignFetch implements OpenPlatformDto {

    Campaign campaign;
    @JsonProperty("ad_groups")
    List<AdGroup> adGroups;

}
