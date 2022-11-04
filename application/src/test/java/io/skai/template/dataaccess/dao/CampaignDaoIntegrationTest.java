package io.skai.template.dataaccess.dao;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import io.skai.template.Application;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class CampaignDaoIntegrationTest {

    @Autowired
    private CampaignDao campaignDao;
    @Autowired
    private DSLContext dslContext;

    private static final long CAMPAIGN_ID = 500L;
    private static final long CAMPAIGN_ANOTHER_ID = 632L;
    private static final long CAMPAIGN_ONE_MORE_ID = 222L;
    private static final long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final long AD_GROUP_ID = 134L;
    private static final long AD_GROUP_ANOTHER_ID = 342L;
    private static final long AD_GROUP_ONE_MORE_ID = 564L;
    private static final String CAMPAIGN_NAME = "name";
    private static final String AD_GROUP_NAME = "ad_group_name";
    private static final String CAMPAIGN_KS_NAME = "ks_name";
    private static final Status CAMPAIGN_STATUS = Status.ACTIVE;
    private static final Status AD_GROUP_STATUS = Status.ACTIVE;
    private static final String CAMPAIGN_UPDATED_NAME = "name_1";
    private static final String CAMPAIGN_UPDATED_KS_NAME = "ks_name_2";
    private static final Status CAMPAIGN_UPDATED_STATUS = Status.PAUSED;
    private static final Status CAMPAIGN_DELETED_STATUS = Status.DELETED;
    private static final String QUERY_FIELD = "name";
    private static final FilterOperator FILTER_OPERATOR_EQUALS = FilterOperator.EQUALS;
    private static final String FILTER_VALUES = "name_1";
    private static final List<String> API_FETCH_REQUEST_FIELDS = List.of("id", "name", "status", "adGroup.id", "adGroup.campaignId");
    private static final int API_FETCH_REQUEST_LIMIT = 3;

    @BeforeEach
    public void init() {
        dslContext.truncate(CampaignTable.TABLE).execute();
        dslContext.truncate(AdGroupTable.TABLE).execute();
    }


    @Test
    public void verifyCampaignCreation() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long numberOfRecords = createCampaign(campaign);

        assertThat(numberOfRecords, is(1L));
    }

    @Test
    public void verifyCampaignFindById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long campaignId = createCampaignWithId(campaign);
        final Optional<Campaign> result = campaignDao.findById(campaignId);

        assertThat(result.isPresent(), is(true));

        assertThat(result.get().getName(), is(CAMPAIGN_NAME));
        assertThat(result.get().getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(result.get().getStatus(), is(CAMPAIGN_STATUS));

        assertThat(result.get().getCreateDate(), is(notNullValue()));
        assertThat(result.get().getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignFindByIdWhenNotExists() {
        final Optional<Campaign> result = campaignDao.findById(CAMPAIGN_WRONG_ID);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void verifyCampaignUpdateWithSameId() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long campaignId = createCampaignWithId(campaign);

        final Campaign campaignBeforeUpdate = campaignDao.findById(campaignId).get();

        final Campaign campaignToUpdate = Campaign.builder()
                .id(campaignId)
                .name(CAMPAIGN_UPDATED_NAME)
                .ksName(CAMPAIGN_UPDATED_KS_NAME)
                .status(CAMPAIGN_UPDATED_STATUS)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);
        final Campaign campaignAfterUpdate = campaignDao.findById(campaignId).get();

        assertThat(numberOfUpdatedRecords, is(1L));

        assertThat(campaignBeforeUpdate.getId(), is(campaignAfterUpdate.getId()));

        assertThat(campaignBeforeUpdate.getName(), is(CAMPAIGN_NAME));
        assertThat(campaignBeforeUpdate.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaignBeforeUpdate.getStatus(), is(CAMPAIGN_STATUS));
        assertThat(campaignBeforeUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignBeforeUpdate.getLastUpdated(), is(notNullValue()));

        assertThat(campaignAfterUpdate.getName(), is(CAMPAIGN_UPDATED_NAME));
        assertThat(campaignAfterUpdate.getKsName(), is(CAMPAIGN_UPDATED_KS_NAME));
        assertThat(campaignAfterUpdate.getStatus(), is(CAMPAIGN_UPDATED_STATUS));
        assertThat(campaignAfterUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignAfterUpdate.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignWhenLastUpdateFieldUpdated() {
        final LocalDateTime createdLastUpdatedTime = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(4).withNano(0);
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();
        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_UPDATED_NAME)
                .ksName(CAMPAIGN_UPDATED_KS_NAME)
                .status(CAMPAIGN_UPDATED_STATUS)
                .lastUpdated(createdLastUpdatedTime)
                .build();

        final long campaignId = createCampaignWithId(campaign);
        final Campaign campaignRecordBeforeUpdate = campaignDao.findById(campaignId).get();

        assertThat(campaignRecordBeforeUpdate.getLastUpdated(), is(notNullValue()));

        updateCampaign(campaignToUpdate);
        final Campaign campaignRecordAfterUpdate = campaignDao.findById(campaignId).get();

        assertThat(campaignRecordAfterUpdate.getLastUpdated(), is(notNullValue()));
        assertThat(campaignRecordAfterUpdate.getLastUpdated(), not(equalTo(campaignRecordBeforeUpdate.getLastUpdated())));
    }

    @Test
    public void verifyCampaignCreateDateWhenCreateCampaign() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long campaignId = createCampaignWithId(campaign);
        final Campaign campaignRecord = campaignDao.findById(campaignId).get();

        assertThat(campaignRecord.getCreateDate(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignUpdateWhenNotExists() {
        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_WRONG_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, is(0L));
    }

    @Test
    public void verifyCampaignDeleteById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long campaignId = createCampaignWithId(campaign);
        final long numberOfUpdatedRecords = campaignDao.deleteById(campaignId);
        System.out.println(campaignId);
        System.out.println(numberOfUpdatedRecords);
        assertThat(numberOfUpdatedRecords, is(1L));
    }

    @Test
    public void verifyCampaignMarkedAsDeletedWhenDeleteById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        final long campaignId = createCampaignWithId(campaign);
        campaignDao.deleteById(campaignId);

        final Campaign campaignRecordAfterDelete = campaignDao.findById(campaignId).get();

        assertThat(campaignRecordAfterDelete.getStatus(), is(CAMPAIGN_DELETED_STATUS));
    }

    @Test
    public void verifyFetchCampaignsWhenDoApiFetchRequest() {
        final List<Campaign> campaignsForCreate = List.of(
                Campaign.builder()
                        .id(CAMPAIGN_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of(
                                AdGroup.builder()
                                        .id(AD_GROUP_ID)
                                        .campaignId(CAMPAIGN_ID)
                                        .name(AD_GROUP_NAME)
                                        .status(AD_GROUP_STATUS)
                                        .build(),
                                AdGroup.builder()
                                        .id(AD_GROUP_ANOTHER_ID)
                                        .campaignId(CAMPAIGN_ID)
                                        .name(AD_GROUP_NAME)
                                        .status(AD_GROUP_STATUS)
                                        .build()
                        )).build(),
                Campaign.builder()
                        .id(CAMPAIGN_ANOTHER_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of(
                                AdGroup.builder()
                                        .id(AD_GROUP_ONE_MORE_ID)
                                        .campaignId(CAMPAIGN_ANOTHER_ID)
                                        .name(AD_GROUP_NAME)
                                        .status(AD_GROUP_STATUS)
                                        .build()
                        )).build(),
                Campaign.builder()
                        .id(CAMPAIGN_ONE_MORE_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of())
                        .build()
        );

        createCampaignsWithAdGroups(campaignsForCreate);

        final List<QueryFilter<String>> queryFilters = List.of(
                new QueryFilter.Builder<String>()
                        .withField(QUERY_FIELD)
                        .withOperator(FILTER_OPERATOR_EQUALS)
                        .withValues(FILTER_VALUES)
                        .build()
        );

        final ApiFetchRequest<QueryFilter<String>> apiFetchRequest = new ApiFetchRequest.Builder<QueryFilter<String>>()
                .withFields(API_FETCH_REQUEST_FIELDS)
                .withFilters(queryFilters)
                .withLimit(API_FETCH_REQUEST_LIMIT)
                .build();

        final List<Campaign> campaigns = campaignDao.fetchCampaigns(apiFetchRequest);

        assertThat(campaigns.size(), is(API_FETCH_REQUEST_LIMIT));
        assertThat(campaigns, containsInAnyOrder(
                Campaign.builder()
                        .id(CAMPAIGN_ID)
                        .name(CAMPAIGN_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of(
                                AdGroup.builder()
                                        .id(AD_GROUP_ID)
                                        .campaignId(CAMPAIGN_ID)
                                        .build(),
                                AdGroup.builder()
                                        .id(AD_GROUP_ANOTHER_ID)
                                        .campaignId(CAMPAIGN_ID)
                                        .build()
                        )).build(),
                Campaign.builder()
                        .id(CAMPAIGN_ANOTHER_ID)
                        .name(CAMPAIGN_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of(
                                AdGroup.builder()
                                        .id(AD_GROUP_ONE_MORE_ID)
                                        .campaignId(CAMPAIGN_ANOTHER_ID)
                                        .build()
                        )).build(),
                Campaign.builder()
                        .id(CAMPAIGN_ONE_MORE_ID)
                        .name(CAMPAIGN_NAME)
                        .status(CAMPAIGN_STATUS)
                        .adGroups(List.of())
                        .build()
        ));
    }

    private long createCampaign(Campaign campaign) {
        return campaignDao.create(campaign);
    }

    private long createCampaignWithId(Campaign campaign) {
        dslContext.insertInto(
                CampaignTable.TABLE,
                CampaignTable.TABLE.name,
                CampaignTable.TABLE.ksName,
                CampaignTable.TABLE.status
        ).values(
                campaign.getName(),
                campaign.getKsName(),
                campaign.getStatus().name()
        ).execute();
        return dslContext.lastID().longValue();
    }

    private void createCampaignWithAddedId(Campaign campaign) {
        dslContext.insertInto(
                CampaignTable.TABLE,
                CampaignTable.TABLE.id,
                CampaignTable.TABLE.name,
                CampaignTable.TABLE.ksName,
                CampaignTable.TABLE.status
        ).values(
                campaign.getId(),
                campaign.getName(),
                campaign.getKsName(),
                campaign.getStatus().name()
        ).execute();
    }

    private long createAdGroupWithId(AdGroup adGroup) {
        dslContext.insertInto(
                AdGroupTable.TABLE,
                AdGroupTable.TABLE.id,
                AdGroupTable.TABLE.campaignId,
                AdGroupTable.TABLE.name,
                AdGroupTable.TABLE.status
        ).values(
                adGroup.getId(),
                adGroup.getCampaignId(),
                adGroup.getName(),
                adGroup.getStatus().name()
        ).execute();

        return dslContext.lastID().longValue();
    }

    private void createCampaignsWithAdGroups(List<Campaign> campaigns) {
        campaigns.forEach(campaign -> {
            createCampaignWithAddedId(campaign);
            campaign.getAdGroups().forEach(this::createAdGroupWithId);
        });
    }

    private long updateCampaign(Campaign campaign) {
        return dslContext.update(CampaignTable.TABLE)
                .set(CampaignTable.TABLE.name, campaign.getName())
                .set(CampaignTable.TABLE.ksName, campaign.getKsName())
                .set(CampaignTable.TABLE.status, campaign.getStatus().name())
                .set(CampaignTable.TABLE.lastUpdated, campaign.getLastUpdated())
                .execute();
    }

}