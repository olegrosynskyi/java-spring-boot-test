package io.skai.template.dataaccess.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.OpenPlatformDto;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Campaign implements OpenPlatformDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String name;
    @JsonProperty("ks_name")
    String ksName;
    Status status;
    @JsonProperty(value = "create_date", access = JsonProperty.Access.READ_ONLY)
    LocalDateTime createDate;
    @JsonProperty(value = "last_updated", access = JsonProperty.Access.READ_ONLY)
    LocalDateTime lastUpdated;
    @JsonIgnore
    AdGroup adGroup;

}
