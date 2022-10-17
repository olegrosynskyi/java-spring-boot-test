package io.skai.template.dataaccess.entities;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Campaign {

    long id;
    String name;
    String ksName;
    Status status;
    LocalDateTime createDate;
    LocalDateTime lastUpdated;

}
