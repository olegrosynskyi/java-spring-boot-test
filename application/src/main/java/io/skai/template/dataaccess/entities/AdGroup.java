package io.skai.template.dataaccess.entities;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AdGroup {

    long id;
    Campaign campaign;
    String name;
    Status status;
    LocalDateTime createDate;
    LocalDateTime lastUpdated;

}
