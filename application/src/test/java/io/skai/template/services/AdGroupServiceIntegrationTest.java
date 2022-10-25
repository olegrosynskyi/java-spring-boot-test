package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.Application;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class AdGroupServiceIntegrationTest {

    private static final long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final long AD_GROUP_WRONG_ID = 999_999_999L;
    private static final String CAMPAIGN_NAME = "CAMPAIGN_NAME_1";
    private static final String CAMPAIGN_KS_NAME = "CAMPAIGN_KS_NAME_1";
    private static final String AD_GROUP_NAME = "AD_GROUP_NAME_1";
    private static final String AD_GROUP_NAME_TO_UPDATE = "AD_GROUP_NAME_TO_UPDATE_1";
    private static final Status CAMPAIGN_ACTIVE = Status.ACTIVE;
    private static final Status AD_GROUP_ACTIVE = Status.ACTIVE;
    private static final Status AD_GROUP_PAUSED = Status.PAUSED;
    private static final Status DELETED = Status.DELETED;

    @Autowired
    private CampaignService campaignService;
    @Autowired
    private AdGroupService adGroupService;
    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    public void init() {
        dslContext.truncate(AdGroupTable.TABLE).execute();
        dslContext.truncate(CampaignTable.TABLE).execute();
    }

    @Test
    public void verifyWhenAdGroupCreated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(campaignId)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        final long adGroupId = adGroupService.create(adGroup);

        assertThat(adGroupId, is(1L));
    }

    @Test
    public void verifyWhenAdGroupNotCreatedWithWrongCampaignId() {
        final AdGroup adGroup = AdGroup.builder()
                .campaignId(CAMPAIGN_WRONG_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.create(adGroup)
        );

        assertThat(exception.getEntityId(), is(nullValue()));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("campaign_id", "AdGroup not created because 'campaign_id' not found or invalid"))));
    }

    @Test
    public void verifyWhenAdGroupFoundById() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(campaignId)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        final long adGroupId = adGroupService.create(adGroup);

        final AdGroup adGroupFound = adGroupService.findById(adGroupId);

        assertThat(adGroupFound.getId(), is(adGroupId));
        assertThat(adGroupFound.getName(), is(AD_GROUP_NAME));
        assertThat(adGroupFound.getStatus(), is(AD_GROUP_ACTIVE));
        assertThat(adGroupFound.getCampaignId(), is(nullValue()));
        assertThat(adGroupFound.getCampaign(), is(nullValue()));
        assertThat(adGroupFound.getCreateDate(), is(notNullValue()));
        assertThat(adGroupFound.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenAdGroupNotFoundById() {
        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.findById(AD_GROUP_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup by id not found or invalid."))));
    }

    @Test
    public void verifyWhenAdGroupUpdated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(campaignId)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        final AdGroup adGroupDataToUpdate = AdGroup.builder()
                .name(AD_GROUP_NAME_TO_UPDATE)
                .status(AD_GROUP_PAUSED)
                .build();

        final long adGroupId = adGroupService.create(adGroup);
        final AdGroup adGroupBeforeUpdate = adGroupService.findById(adGroupId);

        final long adGroupIdFromUpdatedCampaign = adGroupService.update(adGroupId, adGroupDataToUpdate);
        final AdGroup adGroupAfterUpdate = adGroupService.findById(adGroupIdFromUpdatedCampaign);

        assertThat(adGroupBeforeUpdate.getId(), is(adGroupAfterUpdate.getId()));

        assertThat(adGroupBeforeUpdate.getName(), is(AD_GROUP_NAME));
        assertThat(adGroupBeforeUpdate.getStatus(), is(AD_GROUP_ACTIVE));
        assertThat(adGroupBeforeUpdate.getCampaignId(), is(nullValue()));
        assertThat(adGroupBeforeUpdate.getCampaign(), is(nullValue()));
        assertThat(adGroupBeforeUpdate.getCreateDate(), is(notNullValue()));
        assertThat(adGroupBeforeUpdate.getLastUpdated(), is(notNullValue()));

        assertThat(adGroupAfterUpdate.getName(), is(AD_GROUP_NAME_TO_UPDATE));
        assertThat(adGroupAfterUpdate.getStatus(), is(AD_GROUP_PAUSED));
        assertThat(adGroupAfterUpdate.getCampaignId(), is(nullValue()));
        assertThat(adGroupAfterUpdate.getCampaign(), is(nullValue()));
        assertThat(adGroupAfterUpdate.getCreateDate(), is(notNullValue()));
        assertThat(adGroupAfterUpdate.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenAdGroupNotUpdatedWithWongId() {
        final AdGroup adGroupDataToUpdate = AdGroup.builder()
                .name(AD_GROUP_NAME_TO_UPDATE)
                .status(AD_GROUP_PAUSED)
                .build();

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.update(AD_GROUP_WRONG_ID, adGroupDataToUpdate)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup not found or invalid."))));
    }

    @Test
    public void verifyWhenAdGroupChangeStatusToDeletedById() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(campaignId)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        final long adGroupId = adGroupService.create(adGroup);
        final long adGroupIdDeleted = adGroupService.deleteById(adGroupId);
        final AdGroup adGroupAfterDelete = adGroupService.findById(adGroupIdDeleted);

        assertThat(adGroupIdDeleted, is(adGroupId));
        assertThat(adGroupAfterDelete.getStatus(), is(DELETED));
    }

    @Test
    public void verifyWhenAdGroupNotChangeStatusToDeletedById() {
        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.deleteById(AD_GROUP_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup not found or invalid."))));
    }

}