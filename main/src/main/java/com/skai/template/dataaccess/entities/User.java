package com.skai.template.dataaccess.entities;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {
    long id;
    String name;
}
