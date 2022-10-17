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

    @BeforeEach
    public void init() {
        dslContext.truncate(CampaignTable.TABLE).execute();
    }


    @Test
    public void verifyCampaignCreation() {
        final Campaign campaign = Campaign.builder()
                .name("name_1")
                .ksName("ks_name_1")
                .status(Status.ACTIVE)
                .build();

        final long numberOfRecords = createCampaign(campaign);

        assertThat(numberOfRecords, is(1L));
    }

    @Test
    public void verifyCampaignFindById() {
        final long CAMPAIGN_ID = 500L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_2")
                .ksName("ks_name_2")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        final Optional<Campaign> result = campaignDao.findById(CAMPAIGN_ID);

        assertThat(result.isPresent(), is(true));

        assertThat(result.get().getName(), is("name_2"));
        assertThat(result.get().getKsName(), is("ks_name_2"));
        assertThat(result.get().getStatus(), is(Status.PAUSED));

        assertThat(result.get().getCreateDate(), is(notNullValue()));
        assertThat(result.get().getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignFindByIdWhenNotExists() {
        final long CAMPAIGN_WRONG_ID = 100_000_000L;
        final Optional<Campaign> result = campaignDao.findById(CAMPAIGN_WRONG_ID);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void verifyCampaignUpdateWithSameId() {
        final long CAMPAIGN_ID = 500L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_3")
                .ksName("ks_name_3")
                .status(Status.ACTIVE)
                .build();

        createCampaign(campaign);

        final Campaign campaignBeforeUpdate = campaignDao.findById(CAMPAIGN_ID).get();

        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_33")
                .ksName("ks_name_33")
                .status(Status.PAUSED)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);
        final Campaign campaignAfterUpdate = campaignDao.findById(CAMPAIGN_ID).get();

        assertThat(numberOfUpdatedRecords, is(1L));

        assertThat(campaignBeforeUpdate.getId(), is(campaignAfterUpdate.getId()));

        assertThat(campaignBeforeUpdate.getName(), is("name_3"));
        assertThat(campaignBeforeUpdate.getKsName(), is("ks_name_3"));
        assertThat(campaignBeforeUpdate.getStatus(), is(Status.ACTIVE));
        assertThat(campaignBeforeUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignBeforeUpdate.getLastUpdated(), is(notNullValue()));

        assertThat(campaignAfterUpdate.getName(), is("name_33"));
        assertThat(campaignAfterUpdate.getKsName(), is("ks_name_33"));
        assertThat(campaignAfterUpdate.getStatus(), is(Status.PAUSED));
        assertThat(campaignAfterUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignAfterUpdate.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignWhenLastUpdateFieldUpdated() {
        final LocalDateTime createdLastUpdatedTime = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(4).withNano(0);
        final long CAMPAIGN_ID = 500L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_4")
                .ksName("ks_name_4")
                .status(Status.PAUSED)
                .build();
        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_44")
                .ksName("ks_name_44")
                .status(Status.PAUSED)
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
        final long CAMPAIGN_ID = 228L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_5")
                .ksName("ks_name_5")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        final Campaign campaignRecord = campaignDao.findById(CAMPAIGN_ID).get();

        assertThat(campaignRecord.getCreateDate(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignUpdateWhenNotExists() {
        final long CAMPAIGN_WRONG_ID = 999_999_999L;
        final Campaign campaignToUpdate = Campaign.builder()
                .id(CAMPAIGN_WRONG_ID)
                .name("name")
                .ksName("ks_name")
                .status(Status.PAUSED)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, is(0L));
    }

    @Test
    public void verifyCampaignDeleteById() {
        final long CAMPAIGN_ID = 322L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_6")
                .ksName("ks_name_6")
                .status(Status.ACTIVE)
                .build();

        createCampaign(campaign);
        final long numberOfUpdatedRecords = campaignDao.deleteById(CAMPAIGN_ID);

        assertThat(numberOfUpdatedRecords, is(1L));
    }

    @Test
    public void verifyCampaignMarkedAsDeletedWhenDeleteById() {
        final long CAMPAIGN_ID = 110L;
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name("name_7")
                .ksName("ks_name_7")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        campaignDao.deleteById(CAMPAIGN_ID);

        final Campaign campaignRecordAfterDelete = campaignDao.findById(CAMPAIGN_ID).get();

        assertThat(campaignRecordAfterDelete.getStatus(), is(Status.DELETED));
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