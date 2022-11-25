package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.Record;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldMapperServiceTest {

    private static final Long CAMPAIGN_ID = 1L;
    private static final Long AD_GROUP_ID = 1L;
    private static final String CAMPAIGN_NAME = "campaign name";
    private static final String AD_GROUP_NAME = "ad group name";
    private static final String CAMPAIGN_KS_NAME = "campaign ks name";
    private static final Status CAMPAIGN_STATUS_ACTIVE = Status.ACTIVE;
    private static final Status AD_GROUP_STATUS_ACTIVE = Status.ACTIVE;
    private static final LocalDateTime CAMPAIGN_CREATE_DATE = LocalDateTime.now();
    private static final LocalDateTime AD_GROUP_CREATE_DATE = LocalDateTime.now();
    private static final LocalDateTime CAMPAIGN_LAST_UPDATED = LocalDateTime.now();
    private static final LocalDateTime AD_GROUP_LAST_UPDATED = LocalDateTime.now();
    private static final String CAMPAIGN_FIELD = "ksName";
    private static final String CAMPAIGN_FIELD_WITH_PREFIX = "adGroup.campaignId";
    private static final List<String> filtersWithAllFieldsCampaign = List.of(
            "id",
            "name",
            "ksName",
            "status",
            "createDate",
            "lastUpdated",
            "adGroup.id",
            "adGroup.campaignId",
            "adGroup.name",
            "adGroup.status",
            "adGroup.createDate",
            "adGroup.lastUpdated"
    );

    private static final List<String> filtersWithAllAdGroupFields = List.of(
            "id",
            "campaignId",
            "name",
            "status",
            "createDate",
            "lastUpdated",
            "campaign.id",
            "campaign.name",
            "campaign.ksName",
            "campaign.status",
            "campaign.createDate",
            "campaign.lastUpdated"
    );

    @InjectMocks
    private FieldMapperServiceImpl fieldMapperService;

    @Mock
    private Record record;

    @Test
    public void verifyParseCampaignFieldsWhenAllFieldsInFilterList() {
        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filtersWithAllFieldsCampaign);

        when(record.get(CampaignTable.TABLE.id)).thenReturn(CAMPAIGN_ID);
        when(record.get(CampaignTable.TABLE.name)).thenReturn(CAMPAIGN_NAME);
        when(record.get(CampaignTable.TABLE.ksName)).thenReturn(CAMPAIGN_KS_NAME);
        when(record.get(CampaignTable.TABLE.status)).thenReturn(CAMPAIGN_STATUS_ACTIVE.name());
        when(record.get(CampaignTable.TABLE.createDate)).thenReturn(CAMPAIGN_CREATE_DATE);
        when(record.get(CampaignTable.TABLE.lastUpdated)).thenReturn(CAMPAIGN_LAST_UPDATED);

        final Campaign campaign = buildCampaign(campaignFields);

        assertThat(campaignFields, hasSize(6));
        assertThat(campaign.getId(), is(CAMPAIGN_ID));
        assertThat(campaign.getName(), is(CAMPAIGN_NAME));
        assertThat(campaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaign.getStatus(), is(CAMPAIGN_STATUS_ACTIVE));
        assertThat(campaign.getCreateDate(), is(CAMPAIGN_CREATE_DATE));
        assertThat(campaign.getLastUpdated(), is(CAMPAIGN_LAST_UPDATED));
        assertThat(campaign.getAdGroups(), is(nullValue()));
    }

    @Test
    public void verifyParseCampaignFieldsWillAddIdIfNotExists() {
        final List<String> filtersForCampaignWithoutId = List.of(
                "name"
        );

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(getFieldsWithoutPrefix(addSpecificQueryId(filtersForCampaignWithoutId, null), "campaign.", "adGroup."));

        when(record.get(CampaignTable.TABLE.id)).thenReturn(CAMPAIGN_ID);
        when(record.get(CampaignTable.TABLE.name)).thenReturn(CAMPAIGN_NAME);

        final Campaign campaign = buildCampaign(campaignFields);

        assertThat(campaignFields, hasSize(2));
        assertThat(campaignFields.stream().map(FieldMapper::getName).toList(), containsInAnyOrder("id", "name"));

        assertThat(campaign.getId(), is(CAMPAIGN_ID));
        assertThat(campaign.getName(), is(CAMPAIGN_NAME));
    }

    @Test
    public void verifyParseCampaignFieldsWhenFilterContainsOnlySpecificFields() {
        final List<String> filterFields = List.of(
                "id",
                "ksName",
                "status",
                "lastUpdated"
        );

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filterFields);

        when(record.get(CampaignTable.TABLE.id)).thenReturn(CAMPAIGN_ID);
        when(record.get(CampaignTable.TABLE.ksName)).thenReturn(CAMPAIGN_KS_NAME);
        when(record.get(CampaignTable.TABLE.status)).thenReturn(CAMPAIGN_STATUS_ACTIVE.name());
        when(record.get(CampaignTable.TABLE.lastUpdated)).thenReturn(CAMPAIGN_LAST_UPDATED);

        final Campaign campaign = buildCampaign(campaignFields);

        assertThat(campaignFields, hasSize(4));
        assertThat(campaign.getId(), is(CAMPAIGN_ID));
        assertThat(campaign.getName(), is(nullValue()));
        assertThat(campaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaign.getStatus(), is(CAMPAIGN_STATUS_ACTIVE));
        assertThat(campaign.getCreateDate(), is(nullValue()));
        assertThat(campaign.getLastUpdated(), is(CAMPAIGN_LAST_UPDATED));
        assertThat(campaign.getAdGroups(), is(nullValue()));
    }

    @Test
    public void verifyWhenParseCampaignField() {
        final Optional<FieldMapper<?, Campaign.CampaignBuilder>> field = fieldMapperService.parseCampaignField(CAMPAIGN_FIELD);

        when(record.get(CampaignTable.TABLE.ksName)).thenReturn(CAMPAIGN_KS_NAME);

        assertThat(field.isPresent(), is(true));

        final Campaign campaign = buildCampaign(List.of(field.get()));

        assertThat(campaign.getId(), is(nullValue()));
        assertThat(campaign.getName(), is(nullValue()));
        assertThat(campaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaign.getStatus(), is(nullValue()));
        assertThat(campaign.getCreateDate(), is(nullValue()));
        assertThat(campaign.getLastUpdated(), is(nullValue()));
        assertThat(campaign.getAdGroups(), is(nullValue()));
    }

    @Test
    public void verifyWhenParseCampaignFieldWithPrefix() {
        final Optional<FieldMapper<?, AdGroup.AdGroupBuilder>> field = fieldMapperService.parseAdGroupFieldWithPrefix(CAMPAIGN_FIELD_WITH_PREFIX);

        when(record.get(AdGroupTable.TABLE.campaignId)).thenReturn(CAMPAIGN_ID);

        assertThat(field.isPresent(), is(true));

        final AdGroup adGroup = buildAdGroup(List.of(field.get()));

        assertThat(adGroup.getId(), is(nullValue()));
        assertThat(adGroup.getCampaignId(), is(CAMPAIGN_ID));
        assertThat(adGroup.getName(), is(nullValue()));
        assertThat(adGroup.getStatus(), is(nullValue()));
        assertThat(adGroup.getCreateDate(), is(nullValue()));
        assertThat(adGroup.getLastUpdated(), is(nullValue()));
        assertThat(adGroup.getCampaign(), is(nullValue()));
    }

    @Test
    public void verifyParseAdGroupFieldsWhenAllFieldsInFilterList() {
        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(getFieldsWithoutPrefix(addSpecificQueryId(filtersWithAllAdGroupFields, null), "campaign.", "adGroup."));

        when(record.get(AdGroupTable.TABLE.id)).thenReturn(AD_GROUP_ID);
        when(record.get(AdGroupTable.TABLE.campaignId)).thenReturn(CAMPAIGN_ID);
        when(record.get(AdGroupTable.TABLE.name)).thenReturn(AD_GROUP_NAME);
        when(record.get(AdGroupTable.TABLE.status)).thenReturn(AD_GROUP_STATUS_ACTIVE.name());
        when(record.get(AdGroupTable.TABLE.createDate)).thenReturn(AD_GROUP_CREATE_DATE);
        when(record.get(AdGroupTable.TABLE.lastUpdated)).thenReturn(AD_GROUP_LAST_UPDATED);

        final AdGroup adGroup = buildAdGroup(adGroupFields);

        assertThat(adGroupFields, hasSize(6));
        assertThat(adGroup.getId(), is(AD_GROUP_ID));
        assertThat(adGroup.getCampaignId(), is(CAMPAIGN_ID));
        assertThat(adGroup.getName(), is(AD_GROUP_NAME));
        assertThat(adGroup.getStatus(), is(AD_GROUP_STATUS_ACTIVE));
        assertThat(adGroup.getCreateDate(), is(AD_GROUP_CREATE_DATE));
        assertThat(adGroup.getLastUpdated(), is(AD_GROUP_LAST_UPDATED));
        assertThat(adGroup.getCampaign(), is(nullValue()));
    }

    @Test
    public void verifyParseAdGroupFieldsWhenFilterIsEmptyShouldReturnAllAdGroupFields() {
        final List<String> emptyFilter = List.of();

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(getFieldsWithoutPrefix(addSpecificQueryId(emptyFilter, null), "campaign.", "adGroup."));

        when(record.get(AdGroupTable.TABLE.id)).thenReturn(AD_GROUP_ID);

        final AdGroup adGroup = buildAdGroup(adGroupFields);

        assertThat(adGroupFields, hasSize(1));
        assertThat(adGroup.getId(), is(AD_GROUP_ID));
    }

    @Test
    public void verifyParseAdGroupFieldsWhenFilterContainsOnlySpecificFields() {
        final List<String> filterFields = List.of(
                "campaignId",
                "status",
                "createDate"
        );

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(getFieldsWithoutPrefix(addSpecificQueryId(filterFields, null), "campaign.", "adGroup."));

        when(record.get(AdGroupTable.TABLE.id)).thenReturn(AD_GROUP_ID);
        when(record.get(AdGroupTable.TABLE.campaignId)).thenReturn(CAMPAIGN_ID);
        when(record.get(AdGroupTable.TABLE.status)).thenReturn(AD_GROUP_STATUS_ACTIVE.name());
        when(record.get(AdGroupTable.TABLE.createDate)).thenReturn(AD_GROUP_CREATE_DATE);

        final AdGroup adGroup = buildAdGroup(adGroupFields);

        assertThat(adGroupFields, hasSize(4));
        assertThat(adGroup.getId(), is(AD_GROUP_ID));
        assertThat(adGroup.getCampaignId(), is(CAMPAIGN_ID));
        assertThat(adGroup.getName(), is(nullValue()));
        assertThat(adGroup.getStatus(), is(AD_GROUP_STATUS_ACTIVE));
        assertThat(adGroup.getCreateDate(), is(AD_GROUP_CREATE_DATE));
        assertThat(adGroup.getLastUpdated(), is(nullValue()));
        assertThat(adGroup.getCampaign(), is(nullValue()));
    }

    @Test
    public void verifyParseCampaignFieldsWhenFilterContainsAdGroupPrefix() {
        final List<String> filterFields = List.of(
                "id",
                "ksName",
                "status",
                "adGroup.id",
                "adGroup.campaignId",
                "adGroup.status"
        );

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filterFields);

        when(record.get(CampaignTable.TABLE.id)).thenReturn(CAMPAIGN_ID);
        when(record.get(CampaignTable.TABLE.ksName)).thenReturn(CAMPAIGN_KS_NAME);
        when(record.get(CampaignTable.TABLE.status)).thenReturn(CAMPAIGN_STATUS_ACTIVE.name());

        final Campaign campaign = buildCampaign(campaignFields);

        assertThat(campaign.getId(), is(CAMPAIGN_ID));
        assertThat(campaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaign.getStatus(), is(CAMPAIGN_STATUS_ACTIVE));
    }

    @Test
    public void verifyParseAdGroupFieldsWhenFilterContainsCampaignPrefix() {
        final List<String> filterFields = List.of(
                "id",
                "campaignId",
                "status",
                "createDate",
                "campaign.id",
                "campaign.name",
                "campaign.status"
        );

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(filterFields);

        when(record.get(AdGroupTable.TABLE.id)).thenReturn(AD_GROUP_ID);
        when(record.get(AdGroupTable.TABLE.campaignId)).thenReturn(CAMPAIGN_ID);
        when(record.get(AdGroupTable.TABLE.status)).thenReturn(AD_GROUP_STATUS_ACTIVE.name());
        when(record.get(AdGroupTable.TABLE.createDate)).thenReturn(AD_GROUP_CREATE_DATE);

        final AdGroup adGroup = buildAdGroup(adGroupFields);

        assertThat(adGroupFields, hasSize(4));
        assertThat(adGroup.getId(), is(AD_GROUP_ID));
        assertThat(adGroup.getCampaignId(), is(CAMPAIGN_ID));
        assertThat(adGroup.getStatus(), is(AD_GROUP_STATUS_ACTIVE));
        assertThat(adGroup.getCreateDate(), is(AD_GROUP_CREATE_DATE));
    }

    private Campaign buildCampaign(List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields) {
        final Campaign.CampaignBuilder builder = Campaign.builder();
        campaignFields.forEach(field -> field.getValueApplier().apply(builder, record));
        return builder.build();
    }

    private AdGroup buildAdGroup(List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields) {
        final AdGroup.AdGroupBuilder builder = AdGroup.builder();
        adGroupFields.forEach(field -> field.getValueApplier().apply(builder, record));
        return builder.build();
    }

    private List<String> getFieldsWithPrefix(List<String> fields, String prefix) {
        return fields.stream().filter(field -> field.startsWith(prefix)).map(field -> field.substring(prefix.length())).toList();
    }

    private List<String> getFieldsWithoutPrefix(List<String> fields, String... prefix) {
        return fields.stream().filter(field -> !field.startsWith(prefix[0]) && !field.startsWith(prefix[1])).toList();
    }

    private List<String> addSpecificQueryId(List<String> fields, String prefix) {
        final Set<String> filterFields = new HashSet<>(fields);
        final String id = "id";
        filterFields.add(id);
        if (prefix != null) {
            filterFields.add(prefix + id);
        }
        return new ArrayList<>(filterFields);
    }

}