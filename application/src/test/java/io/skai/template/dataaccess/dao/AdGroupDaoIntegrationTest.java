package io.skai.template.dataaccess.dao;

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
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class AdGroupDaoIntegrationTest {

    private static final long CAMPAIGN_ID = 22L;
    private static final long ANOTHER_CAMPAIGN_ID = 100L;
    private static final long ONE_MORE_CAMPAIGN_ID = 1019L;
    private static final long CAMPAIGN1_ID = 10L;
    private static final long CAMPAIGN2_ID = 11L;
    private static final String CAMPAIGN_NAME = "campaign_name_test";
    private static final String ANOTHER_CAMPAIGN_NAME = "campaign_name_another_test";
    private static final String CAMPAIGN_KS_NAME = "campaign_ks_name_test";
    private static final String ANOTHER_CAMPAIGN_KS_NAME_THAT_CANNOT_BE_INCLUDED_BECAUSE_NOT_THAT_NAME_FOR_SEARCHING = "campaign_ks_name_another_test";
    private static final Status CAMPAIGN_STATUS = Status.ACTIVE;
    private static final long AD_GROUP_ID = 4L;
    private static final long AD_GROUP_WRONG_ID = 999_999_999L;
    private static final String AD_GROUP_NAME = "ad_group_name_test";
    private static final String ANOTHER_AD_GROUP_NAME = "ad_group_name_another_test";
    private static final Status AD_GROUP_STATUS = Status.ACTIVE;
    private static final String AD_GROUP_NAME_UPDATED = "ad_group_name_test_updated";
    private static final Status AD_GROUP_STATUS_UPDATED = Status.PAUSED;
    private static final Status STATUS_DELETED = Status.DELETED;
    private static final Status STATUS_PAUSED = Status.PAUSED;
    private static final Status STATUS_ACTIVE = Status.ACTIVE;
    private static final Status CAMPAIGN_STATUS_THAT_CANNOT_BE_INCLUDED_BECAUSE__DELETED = Status.DELETED;
    @Autowired
    private AdGroupDao adGroupDao;
    @Autowired
    private DSLContext dslContext;


    @BeforeEach
    public void init() {
        dslContext.truncate(AdGroupTable.TABLE).execute();
        dslContext.truncate(CampaignTable.TABLE).execute();
    }

    @Test
    public void verifyAdGroupCreation() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        final long numberOfRecords = createAdGroup(adGroup);

        assertThat(numberOfRecords, is(1L));
    }

    @Test
    public void verifyWhenAdGroupCreationWithoutCampaign() {
        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        createAdGroupWithIdAndDate(adGroup);
        Optional<AdGroup> result = adGroupDao.findById(AD_GROUP_ID);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getCampaignId(), is(0L));
    }

    @Test
    public void verifyAdGroupFindById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        createAdGroupWithIdAndDate(adGroup);

        final Optional<AdGroup> result = adGroupDao.findById(AD_GROUP_ID);

        assertThat(result.isPresent(), is(true));

        assertThat(result.get().getId(), is(AD_GROUP_ID));
        assertThat(result.get().getCampaign(), is(nullValue()));
        assertThat(result.get().getCampaignId(), is(0L));
        assertThat(result.get().getName(), is(AD_GROUP_NAME));
        assertThat(result.get().getStatus(), is(AD_GROUP_STATUS));

        assertThat(result.get().getCreateDate(), is(notNullValue()));
        assertThat(result.get().getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyAdGroupFindByIdWhenWrongId() {
        final Optional<AdGroup> result = adGroupDao.findById(AD_GROUP_WRONG_ID);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void verifyAdGroupWhenUpdated() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        final AdGroup adGroupToUpdate = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME_UPDATED)
                .status(AD_GROUP_STATUS_UPDATED)
                .build();

        createAdGroupWithIdAndDate(adGroup);

        final Optional<AdGroup> adGroupBeforeUpdate = adGroupDao.findById(AD_GROUP_ID);
        adGroupDao.update(adGroupToUpdate);
        final Optional<AdGroup> adGroupAfterUpdate = adGroupDao.findById(AD_GROUP_ID);

        assertThat(adGroupBeforeUpdate.get().getId(), is(adGroupAfterUpdate.get().getId()));

        assertThat(adGroupBeforeUpdate.get().getName(), is(AD_GROUP_NAME));
        assertThat(adGroupBeforeUpdate.get().getStatus(), is(AD_GROUP_STATUS));
        assertThat(adGroupBeforeUpdate.get().getCampaign(), is(nullValue()));
        assertThat(adGroupBeforeUpdate.get().getCampaignId(), is(0L));
        assertThat(adGroupBeforeUpdate.get().getCreateDate(), is(notNullValue()));
        assertThat(adGroupBeforeUpdate.get().getLastUpdated(), is(notNullValue()));

        assertThat(adGroupAfterUpdate.get().getName(), is(AD_GROUP_NAME_UPDATED));
        assertThat(adGroupAfterUpdate.get().getStatus(), is(AD_GROUP_STATUS_UPDATED));
        assertThat(adGroupAfterUpdate.get().getCampaign(), is(nullValue()));
        assertThat(adGroupAfterUpdate.get().getCampaignId(), is(0L));
        assertThat(adGroupAfterUpdate.get().getCreateDate(), is(notNullValue()));
        assertThat(adGroupAfterUpdate.get().getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyAdGroupWhenLastUpdateFieldUpdated() {
        final LocalDateTime createdLastUpdatedTime = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(4).withNano(0);
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .lastUpdated(createdLastUpdatedTime)
                .build();

        final AdGroup adGroupToUpdate = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME_UPDATED)
                .status(AD_GROUP_STATUS_UPDATED)
                .build();

        createAdGroupWithIdAndDate(adGroup);

        final Optional<AdGroup> adGroupBeforeUpdate = adGroupDao.findById(AD_GROUP_ID);
        assertThat(adGroupBeforeUpdate.get().getLastUpdated(), is(notNullValue()));

        adGroupDao.update(adGroupToUpdate);

        final Optional<AdGroup> adGroupAfterUpdate = adGroupDao.findById(AD_GROUP_ID);
        assertThat(adGroupAfterUpdate.get().getLastUpdated(), is(notNullValue()));

        assertThat(adGroupBeforeUpdate.get().getLastUpdated(), not(equalTo(adGroupAfterUpdate.get().getLastUpdated())));
    }

    @Test
    public void verifyAdGroupWhenUpdatedWithWrongId() {
        final LocalDateTime timeToCheckUpdated = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(4).withNano(0);
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        final AdGroup adGroupToUpdate = AdGroup.builder()
                .id(AD_GROUP_WRONG_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME_UPDATED)
                .status(AD_GROUP_STATUS_UPDATED)
                .createDate(timeToCheckUpdated)
                .lastUpdated(timeToCheckUpdated)
                .build();

        createAdGroupWithIdAndDate(adGroup);

        final Optional<AdGroup> adGroupBeforeUpdate = adGroupDao.findById(AD_GROUP_ID);
        adGroupDao.update(adGroupToUpdate);
        final Optional<AdGroup> adGroupAfterUpdate = adGroupDao.findById(AD_GROUP_ID);

        assertThat(adGroupBeforeUpdate.get().getId(), is(adGroupAfterUpdate.get().getId()));

        assertThat(adGroupBeforeUpdate.get().getName(), is(AD_GROUP_NAME));
        assertThat(adGroupBeforeUpdate.get().getStatus(), is(AD_GROUP_STATUS));
        assertThat(adGroupBeforeUpdate.get().getCampaign(), is(nullValue()));
        assertThat(adGroupBeforeUpdate.get().getCampaignId(), is(0L));
        assertThat(adGroupBeforeUpdate.get().getCreateDate(), is(notNullValue()));
        assertThat(adGroupBeforeUpdate.get().getLastUpdated(), is(notNullValue()));

        assertThat(adGroupAfterUpdate.get().getName(), is(AD_GROUP_NAME));
        assertThat(adGroupAfterUpdate.get().getStatus(), is(AD_GROUP_STATUS));
        assertThat(adGroupAfterUpdate.get().getCampaign(), is(nullValue()));
        assertThat(adGroupAfterUpdate.get().getCampaignId(), is(0L));

        assertThat(adGroupAfterUpdate.get().getCreateDate(), not(equalTo(timeToCheckUpdated)));
        assertThat(adGroupAfterUpdate.get().getLastUpdated(), not(equalTo(timeToCheckUpdated)));
    }

    @Test
    public void verifyAdGroupWhenDeleteById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        createAdGroupWithIdAndDate(adGroup);
        final long numberOfUpdatedRecords = adGroupDao.deleteById(AD_GROUP_ID);

        assertThat(numberOfUpdatedRecords, is(1L));
    }

    @Test
    public void verifyAdGroupMarkedAsDeletedWhenDeleteById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_STATUS)
                .build();

        createCampaign(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_STATUS)
                .build();

        createAdGroupWithIdAndDate(adGroup);

        adGroupDao.deleteById(AD_GROUP_ID);
        final Optional<AdGroup> adGroupRecordAfterDelete = adGroupDao.findById(AD_GROUP_ID);

        assertThat(adGroupRecordAfterDelete.get().getStatus(), is(STATUS_DELETED));
    }

    @Test
    public void verifyFetchNotDeletedByKsName() {
        Stream.of(
                Campaign.builder()
                        .id(CAMPAIGN_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(STATUS_PAUSED)
                        .build(),
                Campaign.builder()
                        .id(ANOTHER_CAMPAIGN_ID)
                        .name(ANOTHER_CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(STATUS_ACTIVE)
                        .build(),
                Campaign.builder()
                        .id(ONE_MORE_CAMPAIGN_ID)
                        .name(ANOTHER_CAMPAIGN_NAME)
                        .ksName(ANOTHER_CAMPAIGN_KS_NAME_THAT_CANNOT_BE_INCLUDED_BECAUSE_NOT_THAT_NAME_FOR_SEARCHING)
                        .status(STATUS_ACTIVE)
                        .build(),
                Campaign.builder()
                        .id(CAMPAIGN1_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS_THAT_CANNOT_BE_INCLUDED_BECAUSE__DELETED)
                        .build()
        ).peek(this::createCampaign).forEach(campaign -> {
            final AdGroup adGroup = AdGroup.builder()
                    .campaignId(campaign.getId())
                    .name(AD_GROUP_NAME)
                    .status(STATUS_PAUSED)
                    .build();

            createAdGroupWithIdAndDate(adGroup);
            createAdGroupWithIdAndDate(adGroup);
        });

        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN2_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(STATUS_PAUSED)
                .build();

        createCampaign(campaign);

        final AdGroup adGroupWithDeletedStatus = AdGroup.builder()
                .campaignId(CAMPAIGN2_ID)
                .name(ANOTHER_AD_GROUP_NAME)
                .status(STATUS_DELETED)
                .build();

        createAdGroupWithIdAndDate(adGroupWithDeletedStatus);

        final List<AdGroup> result = adGroupDao.fetchNotDeletedByKsName(CAMPAIGN_KS_NAME);

        assertThat(result.size(), is(4));
    }

    @Test
    public void verifyFetchNotDeletedByKsNameWhenCampaignStatusDeleted() {
        Stream.of(
                Campaign.builder()
                        .id(CAMPAIGN_ID)
                        .name(ANOTHER_CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS_THAT_CANNOT_BE_INCLUDED_BECAUSE__DELETED)
                        .build(),
                Campaign.builder()
                        .id(ANOTHER_CAMPAIGN_ID)
                        .name(CAMPAIGN_NAME)
                        .ksName(CAMPAIGN_KS_NAME)
                        .status(CAMPAIGN_STATUS_THAT_CANNOT_BE_INCLUDED_BECAUSE__DELETED)
                        .build()
        ).peek(this::createCampaign).forEach(campaign -> {
            final AdGroup adGroup = AdGroup.builder()
                    .campaignId(campaign.getId())
                    .name(AD_GROUP_NAME)
                    .status(STATUS_ACTIVE)
                    .build();

            createAdGroupWithIdAndDate(adGroup);
            createAdGroupWithIdAndDate(adGroup);
        });

        final List<AdGroup> result = adGroupDao.fetchNotDeletedByKsName(CAMPAIGN_KS_NAME);

        assertThat(result.isEmpty(), is(true));
    }

    private long createAdGroup(AdGroup adGroup) throws NullPointerException {
        return adGroupDao.create(adGroup);
    }

    private long createAdGroupWithIdAndDate(AdGroup adGroup) {
        return dslContext.insertInto(
                        AdGroupTable.TABLE,
                        AdGroupTable.TABLE.id,
                        AdGroupTable.TABLE.campaignId,
                        AdGroupTable.TABLE.name,
                        AdGroupTable.TABLE.status,
                        AdGroupTable.TABLE.createDate,
                        AdGroupTable.TABLE.lastUpdated)
                .values(
                        adGroup.getId(),
                        adGroup.getCampaignId(),
                        adGroup.getName(),
                        adGroup.getStatus().name(),
                        adGroup.getCreateDate(),
                        adGroup.getLastUpdated()
                ).execute();
    }

    private long updateAdGroup(AdGroup adGroup) {
        return dslContext.update(AdGroupTable.TABLE)
                .set(AdGroupTable.TABLE.id, adGroup.getId())
                .set(AdGroupTable.TABLE.campaignId, adGroup.getCampaignId())
                .set(AdGroupTable.TABLE.name, adGroup.getName())
                .set(AdGroupTable.TABLE.status, adGroup.getStatus().name())
                .set(AdGroupTable.TABLE.createDate, adGroup.getCreateDate())
                .set(AdGroupTable.TABLE.lastUpdated, adGroup.getLastUpdated())
                .where(AdGroupTable.TABLE.id.eq(adGroup.getId()))
                .execute();
    }

    private long createCampaign(Campaign campaign) {
        return dslContext.insertInto(
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

}