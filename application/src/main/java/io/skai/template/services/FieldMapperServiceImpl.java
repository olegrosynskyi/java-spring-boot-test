package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("fieldMapperService")
@Slf4j
@RequiredArgsConstructor
public class FieldMapperServiceImpl implements FieldMapperService {

    private static final String AD_GROUP_PREFIX = "adGroup.";

    private static final List<FieldMapper<?, Campaign.CampaignBuilder>> CAMPAIGN_FIELDS = List.of(
            new FieldMapper<>("id", CampaignTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value)),
            new FieldMapper<>("status", CampaignTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", CampaignTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", CampaignTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    private static final List<FieldMapper<?, AdGroup.AdGroupBuilder>> AD_CROUP_FIELDS = List.of(
            new FieldMapper<>("id", AdGroupTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value)),
            new FieldMapper<>("name", AdGroupTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("status", AdGroupTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", AdGroupTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", AdGroupTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    @Override
    public List<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignFields(List<String> fields) {
        final List<String> filterFields = fields.contains("id")
                ? fields
                : Stream.of(fields, List.of("id")).flatMap(Collection::stream).toList();

        return CAMPAIGN_FIELDS.stream()
                .filter(campaignFields -> filterFields.contains(campaignFields.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFields(List<String> fields) {
        final List<String> adGroupFilterFields = fields.stream()
                .filter(field -> field.startsWith(AD_GROUP_PREFIX))
                .map(field -> field.substring(AD_GROUP_PREFIX.length()))
                .toList();

        return adGroupFilterFields.isEmpty()
                ? AD_CROUP_FIELDS
                : AD_CROUP_FIELDS.stream()
                .filter(adGroupFields -> adGroupFilterFields.contains(adGroupFields.getName()))
                .toList();
    }

}
