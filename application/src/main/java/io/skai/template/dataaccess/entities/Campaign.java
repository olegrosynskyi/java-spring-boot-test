package io.skai.template.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.OpenPlatformDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Campaign implements OpenPlatformDto {

    long id;
    String name;
    @JsonProperty("ks_name")
    String ksName;
    Status status;
    @JsonProperty("create_date")
    LocalDateTime createDate;
    @JsonProperty("last_updated")
    LocalDateTime lastUpdated;

}
