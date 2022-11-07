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
import java.util.stream.Stream;

@Service("fieldMapperService")
@Slf4j
@RequiredArgsConstructor
public class FieldMapperServiceImpl implements FieldMapperService {

    private static final String AD_GROUP_PREFIX = "adGroup.";
    private static final String CAMPAIGN_PREFIX = "campaign.";

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
        final boolean isAdGroupPrefix = fields.stream().anyMatch(field -> field.startsWith(AD_GROUP_PREFIX));

        final List<String> filterFields = (isAdGroupPrefix)
                ? addId(fields.stream()
                .filter(field -> !field.startsWith(AD_GROUP_PREFIX))
                .toList())
                : addId(fields.stream()
                .filter(field -> field.startsWith(CAMPAIGN_PREFIX))
                .map(filed -> filed.substring(CAMPAIGN_PREFIX.length()))
                .toList());

        return (isContainsAnyPrefix(fields))
                ? CAMPAIGN_FIELDS.stream()
                .filter(adGroupFields -> filterFields.contains(adGroupFields.getName()))
                .toList()
                : CAMPAIGN_FIELDS.stream()
                .filter(adGroupFields -> addId(fields).contains(adGroupFields.getName()))
                .toList();
    }

    @Override
    public List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFields(List<String> fields) {
        final boolean isCampaignPrefix = fields.stream().anyMatch(field -> field.startsWith(CAMPAIGN_PREFIX));

        final List<String> filterFields = (isCampaignPrefix)
                ? addId(fields.stream().filter(field -> !field.startsWith(CAMPAIGN_PREFIX)).toList())
                : addId(fields.stream().filter(field -> field.startsWith(AD_GROUP_PREFIX)).map(filed -> filed.substring(AD_GROUP_PREFIX.length())).toList());

        return (isContainsAnyPrefix(fields))
                ? AD_CROUP_FIELDS.stream()
                .filter(adGroupFields -> filterFields.contains(adGroupFields.getName()))
                .toList()
                : AD_CROUP_FIELDS.stream()
                .filter(adGroupFields -> addId(fields).contains(adGroupFields.getName()))
                .toList();
    }

    private boolean isContainsAnyPrefix(List<String> fields) {
        return fields.stream().anyMatch(field -> field.startsWith(CAMPAIGN_PREFIX) || field.startsWith(AD_GROUP_PREFIX));

    }

    private List<String> addId(List<String> filterFields) {
        return filterFields.contains("id")
                ? filterFields
                : Stream.of(filterFields, List.of("id")).flatMap(Collection::stream).toList();
    }

}
