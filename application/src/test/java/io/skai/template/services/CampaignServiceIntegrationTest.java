package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.Application;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class CampaignServiceIntegrationTest {

    private static final long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final String CAMPAIGN_NAME = "CAMPAIGN_NAME_1";
    private static final String CAMPAIGN_KS_NAME = "CAMPAIGN_KS_NAME_1";
    private static final String CAMPAIGN_NAME_TO_UPDATE = "CAMPAIGN_NAME_TO_UPDATE_1";
    private static final String CAMPAIGN_KS_NAME_TO_UPDATE = "CAMPAIGN_KS_NAME_TO_UPDATE_1";
    private static final Status ACTIVE = Status.ACTIVE;
    private static final Status PAUSED_TO_UPDATE = Status.PAUSED;
    private static final Status DELETED = Status.DELETED;

    @Autowired
    private CampaignService campaignService;
    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    public void init() {
        dslContext.truncate(CampaignTable.TABLE).execute();
    }

    @Test
    public void verifyWhenCampaignCreated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);

        assertThat(campaignId, is(1L));
    }

    @Test
    public void verifyWhenCampaignFoundById() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);
        final Campaign foundCampaign = campaignService.findById(campaignId);

        assertThat(foundCampaign, is(notNullValue()));

        assertThat(campaignId, is(foundCampaign.getId()));
        assertThat(foundCampaign.getName(), is(CAMPAIGN_NAME));
        assertThat(foundCampaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(foundCampaign.getStatus(), is(ACTIVE));
        assertThat(foundCampaign.getCreateDate(), is(notNullValue()));
        assertThat(foundCampaign.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenCampaignNotFoundById() {
        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> campaignService.findById(CAMPAIGN_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "Campaign by id not found or invalid."))));
    }

    @Test
    public void verifyWhenCampaignUpdated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        final Campaign campaignDataToUpdate = Campaign.builder()
                .name(CAMPAIGN_NAME_TO_UPDATE)
                .ksName(CAMPAIGN_KS_NAME_TO_UPDATE)
                .status(PAUSED_TO_UPDATE)
                .build();

        final long campaignId = campaignService.create(campaign);
        final Campaign campaignBeforeUpdate = campaignService.findById(campaignId);

        final long campaignIdFromUpdatedCampaign = campaignService.update(campaignId, campaignDataToUpdate);
        final Campaign campaignAfterUpdate = campaignService.findById(campaignIdFromUpdatedCampaign);

        assertThat(campaignId, is(campaignIdFromUpdatedCampaign));

        assertThat(campaignBeforeUpdate.getName(), is(CAMPAIGN_NAME));
        assertThat(campaignBeforeUpdate.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaignBeforeUpdate.getStatus(), is(ACTIVE));
        assertThat(campaignBeforeUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignBeforeUpdate.getLastUpdated(), is(notNullValue()));

        assertThat(campaignAfterUpdate.getName(), is(CAMPAIGN_NAME_TO_UPDATE));
        assertThat(campaignAfterUpdate.getKsName(), is(CAMPAIGN_KS_NAME_TO_UPDATE));
        assertThat(campaignAfterUpdate.getStatus(), is(PAUSED_TO_UPDATE));
        assertThat(campaignAfterUpdate.getCreateDate(), is(notNullValue()));
        assertThat(campaignAfterUpdate.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenCampaignNotUpdatedWithWongId() {
        final Campaign campaignDataToUpdate = Campaign.builder()
                .name(CAMPAIGN_NAME_TO_UPDATE)
                .ksName(CAMPAIGN_KS_NAME_TO_UPDATE)
                .status(PAUSED_TO_UPDATE)
                .build();

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> campaignService.update(CAMPAIGN_WRONG_ID, campaignDataToUpdate)
        );

        assertThat(exception.getEntityId(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "Campaign not found or invalid."))));
    }

    @Test
    public void verifyWhenCampaignChangeStatusToDeletedById() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        final long campaignId = campaignService.create(campaign);
        final long campaignIdDeleted = campaignService.deleteById(campaignId);
        final Campaign campaignAfterDelete = campaignService.findById(campaignIdDeleted);

        assertThat(campaignIdDeleted, is(campaignId));
        assertThat(campaignAfterDelete.getStatus(), is(DELETED));
    }

    @Test
    public void verifyWhenCampaignNotChangeStatusToDeletedById() {
        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> campaignService.deleteById(CAMPAIGN_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "Campaign not found or invalid."))));
    }

}