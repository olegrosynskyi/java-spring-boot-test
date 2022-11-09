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

@Service("fieldMapperService")
@Slf4j
@RequiredArgsConstructor
public class FieldMapperServiceImpl implements FieldMapperService {

    private static final String AD_GROUP_PREFIX = "adGroup.";
    private static final String CAMPAIGN_PREFIX = "campaign.";

    private static final List<String> prefixes = List.of(
            AD_GROUP_PREFIX,
            CAMPAIGN_PREFIX
    );

    private static final FieldMapper<Long, AdGroup.AdGroupBuilder> AD_GROUP_ID_FIELD = new FieldMapper<>("id", AdGroupTable.TABLE.id, (builder, value) -> builder.id(value));
    private static final FieldMapper<Long, Campaign.CampaignBuilder> CAMPAIGN_ID_FIELD = new FieldMapper<>("id", CampaignTable.TABLE.id, (builder, value) -> builder.id(value));

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
        final List<String> filterFields = getFieldsWithoutPrefix(fields);
        return Seq.seq(getCampaignFields(filterFields)).append(CAMPAIGN_ID_FIELD).distinct(FieldMapper::getName).toList();
    }

    @Override
    public List<FieldMapper<?, AdGroup.AdGroupBuilder>> parseCampaignFieldsWithPrefix(List<String> fields) {
        final List<String> filterFields = getFieldsWithPrefix(fields, AD_GROUP_PREFIX);
        return Seq.seq(getAdGroupFields(filterFields)).append(AD_GROUP_ID_FIELD).distinct(FieldMapper::getName).toList();
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

    private List<String> getFieldsWithoutPrefix(List<String> fields) {
        return fields.stream().filter(field -> prefixes.stream().noneMatch(field::startsWith)).toList();
    }

}
