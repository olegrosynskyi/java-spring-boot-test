package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("fieldMapperService")
@Slf4j
@RequiredArgsConstructor
public class FieldMapperServiceImpl implements FieldMapperService {

    private static final String AD_GROUP_PREFIX = "adGroup.";
    private static final String CAMPAIGN_PREFIX = "campaign.";

    private static final FieldMapper<Long, AdGroup.AdGroupBuilder> AD_GROUP_ID_FIELD = new FieldMapper<>("id", AdGroupTable.TABLE.id, (builder, value) -> builder.id(value));
    private static final FieldMapper<Long, Campaign.CampaignBuilder> CAMPAIGN_ID_FIELD = new FieldMapper<>("id", CampaignTable.TABLE.id, (builder, value) -> builder.id(value));

    private static final List<FieldMapper<?, Campaign.CampaignBuilder>> CAMPAIGN_FIELDS = List.of(
            CAMPAIGN_ID_FIELD,
            new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value)),
            new FieldMapper<>("status", CampaignTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", CampaignTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", CampaignTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    private static final List<FieldMapper<?, AdGroup.AdGroupBuilder>> AD_CROUP_FIELDS = List.of(
            AD_GROUP_ID_FIELD,
            new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value)),
            new FieldMapper<>("name", AdGroupTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("status", AdGroupTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", AdGroupTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", AdGroupTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    @Override
    public List<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignFields(List<String> fields) {
        final List<String> filterFields = getFieldsWithoutPrefix(fields);
        return Seq.seq(getCampaignFields(filterFields)).append(CAMPAIGN_ID_FIELD).distinct(FieldMapper::getName).toList();
    }

    @Override
    public Optional<FieldMapper<?, Campaign.CampaignBuilder>> parseCampaignField(String field) {
        final String filterField = getFieldWithoutPrefix(field);
        return (filterField != null)
                ? Optional.of(getCampaignFields(List.of(filterField)).get(0))
                : Optional.empty();
    }

    @Override
    public List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseCampaignFieldsWithPrefix(List<String> fields) {
        final List<String> filterFields = getFieldsWithPrefix(fields, AD_GROUP_PREFIX);
        return Seq.seq(getAdGroupFields(filterFields)).append(AD_GROUP_ID_FIELD).distinct(FieldMapper::getName).toList();
    }

    @Override
    public Optional<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFieldWithPrefix(String field) {
        final String filterField = getFieldWithPrefix(field, AD_GROUP_PREFIX);
        return (filterField != null)
                ? Optional.of(getAdGroupFields(List.of(filterField)).get(0))
                : Optional.empty();
    }

    @Override
    public List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseAdGroupFields(List<String> fields) {
        final List<String> filterFields = getFieldsWithoutPrefix(fields);
        return Seq.seq(getAdGroupFields(filterFields)).append(AD_GROUP_ID_FIELD).distinct(FieldMapper::getName).toList();
    }

    @Override
    public List<FieldMapper<?, Campaign.CampaignBuilder>> parseAdGroupFieldsWithPrefix(List<String> fields) {
        final List<String> filterFields = getFieldsWithPrefix(fields, CAMPAIGN_PREFIX);
        return Seq.seq(getCampaignFields(filterFields)).append(CAMPAIGN_ID_FIELD).distinct(FieldMapper::getName).toList();
    }

    private List<FieldMapper<?, Campaign.CampaignBuilder>> getCampaignFields(List<String> fields) {
        return CAMPAIGN_FIELDS.stream().filter(campaignField -> fields.contains(campaignField.getName())).toList();
    }

    private List<FieldMapper<?, AdGroup.AdGroupBuilder>> getAdGroupFields(List<String> fields) {
        return AD_CROUP_FIELDS.stream().filter(campaignField -> fields.contains(campaignField.getName())).toList();
    }

    private List<String> getFieldsWithPrefix(List<String> fields, String prefix) {
        return fields.stream().filter(field -> field.startsWith(prefix)).map(field -> field.substring(prefix.length())).toList();
    }

    private String getFieldWithPrefix(String field, String prefix) {
        return field.startsWith(prefix) ? field.substring(prefix.length()) : null;
    }

    private List<String> getFieldsWithoutPrefix(List<String> fields) {
        return fields.stream().filter(field -> !field.contains(".")).toList();
    }

    private String getFieldWithoutPrefix(String field) {
        return !field.contains(".") ? field : null;
    }

}
