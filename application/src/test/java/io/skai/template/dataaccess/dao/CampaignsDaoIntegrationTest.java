package io.skai.template.dataaccess.dao;

import io.skai.template.Application;
import io.skai.template.dataaccess.entities.Campaigns;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.CampaignsTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class CampaignsDaoIntegrationTest {

    @Autowired
    private CampaignsDao campaignsDao;
    @Autowired
    private DSLContext dslContext;


    @Test
    public void verifyCampaignCreation() {
        Campaigns campaign = campaignWithArgs("name_1", "ks_name_1", Status.ACTIVE);

        long numberOfRecords = createCampaign(campaign);

        assertThat(numberOfRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignFindById() {
        Campaigns campaign = campaignWithArgs("name_2", "ks_name_2", Status.PAUSED);

        createCampaign(campaign);
        Campaigns lastCampaignRecord = getLastRecordFromCampaignsDB();
        long lastCampaignRecordId = lastCampaignRecord.getId();
        Optional<Campaigns> result = campaignsDao.findById(lastCampaignRecordId);

        result.ifPresentOrElse(
                persistedCampaign -> assertThat(persistedCampaign, is(lastCampaignRecord)),
                () -> fail("Campaign not found by id : " + lastCampaignRecordId));
    }

    @Test
    public void verifyCampaignFindByIdWhenNotExists() {
        Optional<Campaigns> result = campaignsDao.findById(100_000_000L);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void verifyCampaignUpdateWithSameId() {
        Campaigns campaign = campaignWithArgs("name_3", "ks_name_3", Status.ACTIVE);

        createCampaign(campaign);
        Campaigns lastCampaignRecord = getLastRecordFromCampaignsDB();
        Campaigns campaignToUpdate = Campaigns.builder()
                .id(lastCampaignRecord.getId())
                .name("name_33")
                .ksName("ks_name_33")
                .status(Status.PAUSED)
                .build();

        long numberOfUpdatedRecords = campaignsDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignWhenLastUpdateFieldUpdated() {
        Campaigns campaign = campaignWithArgs("name_4", "ks_name_4", Status.PAUSED);

        createCampaign(campaign);
        Campaigns lastCampaignRecordBeforeUpdate = getLastRecordFromCampaignsDB();
        Campaigns campaignToUpdate = Campaigns.builder()
                .id(lastCampaignRecordBeforeUpdate.getId())
                .name("name_44")
                .ksName("ks_name_44")
                .status(Status.PAUSED)
                .build();

        assertThat(lastCampaignRecordBeforeUpdate.getLastUpdated(), is(nullValue()));

        campaignsDao.update(campaignToUpdate);
        Campaigns lastCampaignRecordAfterUpdate = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecordAfterUpdate.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignCreateDateWhenCreateCampaign() {
        Campaigns campaign = campaignWithArgs("name_5", "ks_name_5", Status.PAUSED);

        createCampaign(campaign);
        Campaigns lastCampaignRecord = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecord.getCreateDate(), is(notNullValue()));
    }

    @Test
    public void verifyCampaignUpdateWhenNotExists() {
        Campaigns campaignToUpdate = Campaigns.builder()
                .id(999_999_999)
                .name("name")
                .ksName("ks_name")
                .status(Status.PAUSED)
                .build();

        long numberOfUpdatedRecords = campaignsDao.update(campaignToUpdate);

        assertThat(numberOfUpdatedRecords, is(0L));
    }

    @Test
    public void verifyCampaignDeleteById() {
        Campaigns campaign = campaignWithArgs("name_6", "ks_name_6", Status.ACTIVE);

        createCampaign(campaign);
        Campaigns lastCampaignRecordBeforeDelete = getLastRecordFromCampaignsDB();
        long numberOfUpdatedRecords = campaignsDao.deleteById(lastCampaignRecordBeforeDelete.getId());

        assertThat(numberOfUpdatedRecords, greaterThan(0L));
    }

    @Test
    public void verifyCampaignMarkedAsDeletedWhenDeleteById() {
        Campaigns campaign = campaignWithArgs("name_7", "ks_name_7", Status.PAUSED);

        createCampaign(campaign);
        Campaigns lastCampaignRecordBeforeDelete = getLastRecordFromCampaignsDB();
        campaignsDao.deleteById(lastCampaignRecordBeforeDelete.getId());

        assertThat(lastCampaignRecordBeforeDelete.getStatus(), is(Status.PAUSED));

        Campaigns lastCampaignRecordAfterDelete = getLastRecordFromCampaignsDB();

        assertThat(lastCampaignRecordAfterDelete.getStatus(), is(Status.DELETED));
    }

    private Campaigns campaignWithArgs(String name, String ksName, Status status) {
        return Campaigns.builder()
                .name(name)
                .ksName(ksName)
                .status(status)
                .build();
    }

    private long createCampaign(Campaigns campaign) {
        return campaignsDao.create(campaign);
    }

    private Campaigns getLastRecordFromCampaignsDB() {
        return dslContext.selectFrom(CampaignsTable.TABLE)
                .orderBy(CampaignsTable.TABLE.id.desc())
                .limit(1)
                .fetchOne(campaignRec -> Campaigns.builder()
                        .id(campaignRec.get(CampaignsTable.TABLE.id))
                        .name(campaignRec.get(CampaignsTable.TABLE.name))
                        .ksName(campaignRec.get(CampaignsTable.TABLE.ksName))
                        .status(Status.valueOf(campaignRec.get(CampaignsTable.TABLE.status)))
                        .createDate(campaignRec.get(CampaignsTable.TABLE.createDate))
                        .lastUpdated(campaignRec.get(CampaignsTable.TABLE.lastUpdated))
                        .build());

    }

}