package io.skai.template.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.OpenPlatformDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AdGroup implements OpenPlatformDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    @JsonProperty("campaign_id")
    Long campaignId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Campaign campaign;
    String name;
    Status status;
    @JsonProperty(value = "create_date", access = JsonProperty.Access.READ_ONLY)
    LocalDateTime createDate;
    @JsonProperty(value = "last_updated", access = JsonProperty.Access.READ_ONLY)
    LocalDateTime lastUpdated;

}
