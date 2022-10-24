package io.skai.template.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.OpenPlatformDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AdGroup implements OpenPlatformDto {

    long id;
    @JsonProperty("campaign_id")
    Long campaignId;
    Campaign campaign;
    String name;
    Status status;
    @JsonProperty("create_date")
    LocalDateTime createDate;
    @JsonProperty("last_updated")
    LocalDateTime lastUpdated;

}
