package io.skai.template.dataaccess.entities;

import java.util.List;

public record CampaignQuery(List<String> fields, String filters, long limit) { }
