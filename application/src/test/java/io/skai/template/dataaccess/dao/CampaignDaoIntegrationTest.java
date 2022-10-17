package io.skai.template.dataaccess.dao;

import io.skai.template.Application;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private static final long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final String CAMPAIGN_NAME = "name";

    private static final String CAMPAIGN_KS_NAME = "ks_name";
    private static final Status CAMPAIGN_STATUS = Status.ACTIVE;

    private static final String CAMPAIGN_UPDATED_NAME = "name_1";
    private static final String CAMPAIGN_UPDATED_KS_NAME = "ks_name_2";
    private static final Status CAMPAIGN_UPDATED_STATUS = Status.PAUSED;
    private static final Status CAMPAIGN_DELETED_STATUS = Status.DELETED;

    @BeforeEach
    public void init() {
        dslContext.truncate(CampaignTable.TABLE).execute();
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

        createCampaign(campaign);
        final Optional<Campaign> result = campaignDao.findById(CAMPAIGN_ID);

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

        createCampaign(campaign);

        final Campaign campaignBeforeUpdate = campaignDao.findById(CAMPAIGN_ID).get();

        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_UPDATED_NAME)
                .ksName(CAMPAIGN_UPDATED_KS_NAME)
                .status(CAMPAIGN_UPDATED_STATUS)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);
        final Campaign campaignAfterUpdate = campaignDao.findById(CAMPAIGN_ID).get();

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

        createCampaign(campaign);
        final Campaign campaignRecordBeforeUpdate = campaignDao.findById(CAMPAIGN_ID).get();

        assertThat(campaignRecordBeforeUpdate.getLastUpdated(), is(notNullValue()));

        updateCampaign(campaignToUpdate);
        final Campaign campaignRecordAfterUpdate = campaignDao.findById(CAMPAIGN_ID).get();

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

        createCampaign(campaign);
        final Campaign campaignRecord = campaignDao.findById(CAMPAIGN_ID).get();

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

        createCampaign(campaign);
        final long numberOfUpdatedRecords = campaignDao.deleteById(CAMPAIGN_ID);

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

        createCampaign(campaign);
        campaignDao.deleteById(CAMPAIGN_ID);

        final Campaign campaignRecordAfterDelete = campaignDao.findById(CAMPAIGN_ID).get();

        assertThat(campaignRecordAfterDelete.getStatus(), is(CAMPAIGN_DELETED_STATUS));
    }

    private long createCampaign(Campaign campaign) {
        return campaignDao.create(campaign);
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