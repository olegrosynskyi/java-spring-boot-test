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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

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

        long numberOfRecords = createCampaign(campaign);

        assertThat(numberOfRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignFindById() {
        final Campaign campaign = Campaign.builder()
                .name("name_2")
                .ksName("ks_name_2")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        final Campaign lastCampaignRecord = getLastRecordFromCampaignsDB();
        final long lastCampaignRecordId = lastCampaignRecord.getId();
        final Optional<Campaign> result = campaignDao.findById(lastCampaignRecordId);

        result.ifPresentOrElse(
                persistedCampaign -> assertThat(persistedCampaign, is(lastCampaignRecord)),
                () -> fail("Campaign not found by id : " + lastCampaignRecordId));
    }

    @Test
    public void verifyCampaignFindByIdWhenNotExists() {
        final Optional<Campaign> result = campaignDao.findById(100_000_000L);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void verifyCampaignUpdateWithSameId() {
        final Campaign campaign = Campaign.builder()
                .name("name_3")
                .ksName("ks_name_3")
                .status(Status.ACTIVE)
                .build();

        createCampaign(campaign);
        final Campaign lastCampaignRecord = getLastRecordFromCampaignsDB();
        final Campaign campaignToUpdate = Campaign.builder()
                .id(lastCampaignRecord.getId())
                .name("name_33")
                .ksName("ks_name_33")
                .status(Status.PAUSED)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignWhenLastUpdateFieldUpdated() throws InterruptedException {
        final Campaign campaign = Campaign.builder()
                .name("name_4")
                .ksName("ks_name_4")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        TimeUnit.MILLISECONDS.sleep(1500L);
        final Campaign lastCampaignRecordBeforeUpdate = getLastRecordFromCampaignsDB();
        final Campaign campaignToUpdate = Campaign.builder()
                .id(lastCampaignRecordBeforeUpdate.getId())
                .name("name_44")
                .ksName("ks_name_44")
                .status(Status.PAUSED)
                .build();

        assertThat(lastCampaignRecordBeforeUpdate.getLastUpdated(), is(notNullValue()));

        campaignDao.update(campaignToUpdate);
        final Campaign lastCampaignRecordAfterUpdate = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecordAfterUpdate.getLastUpdated(), is(notNullValue()));
        assertThat(lastCampaignRecordAfterUpdate.getLastUpdated(), not(equalTo(lastCampaignRecordBeforeUpdate.getLastUpdated())));
    }

    @Test
    public void verifyCampaignCreateDateWhenCreateCampaign() {
        final Campaign campaign = Campaign.builder()
                .name("name_5")
                .ksName("ks_name_5")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        final Campaign lastCampaignRecord = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecord.getCreateDate(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignUpdateWhenNotExists() {
        final Campaign campaignToUpdate = Campaign.builder()
                .id(999_999_999)
                .name("name")
                .ksName("ks_name")
                .status(Status.PAUSED)
                .build();

        final long numberOfUpdatedRecords = campaignDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, is(0L));
    }

    @Test
    public void verifyCampaignDeleteById() {
        final Campaign campaign = Campaign.builder()
                .name("name_6")
                .ksName("ks_name_6")
                .status(Status.ACTIVE)
                .build();

        createCampaign(campaign);
        final Campaign lastCampaignRecordBeforeDelete = getLastRecordFromCampaignsDB();
        final long numberOfUpdatedRecords = campaignDao.deleteById(lastCampaignRecordBeforeDelete.getId());

        assertThat(numberOfUpdatedRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignMarkedAsDeletedWhenDeleteById() {
        final Campaign campaign = Campaign.builder()
                .name("name_7")
                .ksName("ks_name_7")
                .status(Status.PAUSED)
                .build();

        createCampaign(campaign);
        final Campaign lastCampaignRecordBeforeDelete = getLastRecordFromCampaignsDB();
        campaignDao.deleteById(lastCampaignRecordBeforeDelete.getId());

        assertThat(lastCampaignRecordBeforeDelete.getStatus(), is(Status.PAUSED));

        final Campaign lastCampaignRecordAfterDelete = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecordAfterDelete.getStatus(), is(Status.DELETED));
    }

    private long createCampaign(Campaign campaign) {
        return campaignDao.create(campaign);
    }

    private Campaign getLastRecordFromCampaignsDB() {
        return dslContext.selectFrom(CampaignTable.TABLE)
                .orderBy(CampaignTable.TABLE.id.desc())
                .limit(1)
                .fetchOne(campaignRec -> Campaign.builder()
                        .id(campaignRec.get(CampaignTable.TABLE.id))
                        .name(campaignRec.get(CampaignTable.TABLE.name))
                        .ksName(campaignRec.get(CampaignTable.TABLE.ksName))
                        .status(Status.valueOf(campaignRec.get(CampaignTable.TABLE.status)))
                        .createDate(campaignRec.get(CampaignTable.TABLE.createDate))
                        .lastUpdated(campaignRec.get(CampaignTable.TABLE.lastUpdated))
                        .build());

    }

}