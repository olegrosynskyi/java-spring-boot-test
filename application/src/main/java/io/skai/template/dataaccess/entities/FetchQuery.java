package io.skai.template.dataaccess.entities;

import java.util.List;

public record FetchQuery(List<String> fields, String filters, int limit) { }
