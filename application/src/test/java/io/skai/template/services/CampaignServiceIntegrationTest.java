package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import io.skai.template.dataaccess.entities.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceIntegrationTest {

    private static final long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final long CAMPAIGN_ID = 3L;
    private static final String CAMPAIGN_NAME = "CAMPAIGN_NAME_1";
    private static final String CAMPAIGN_KS_NAME = "CAMPAIGN_KS_NAME_1";
    private static final String CAMPAIGN_NAME_TO_UPDATE = "CAMPAIGN_NAME_TO_UPDATE_1";
    private static final String CAMPAIGN_KS_NAME_TO_UPDATE = "CAMPAIGN_KS_NAME_TO_UPDATE_1";
    private static final Status ACTIVE = Status.ACTIVE;
    private static final Status PAUSED_TO_UPDATE = Status.PAUSED;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Mock
    private CampaignDao campaignDao;

    @Captor
    private ArgumentCaptor<Campaign> campaignArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> campaignIdArgumentCaptor;

    @Test
    public void verifyWhenCampaignCreated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        when(campaignDao.create(campaign)).thenReturn(CAMPAIGN_ID);

        final long campaignId = campaignService.create(campaign);

        verify(campaignDao).create(campaignArgumentCaptor.capture());

        final Campaign campaignArguments = campaignArgumentCaptor.getValue();

        assertThat(campaignId, is(CAMPAIGN_ID));
        assertThat(campaignArguments.getName(), is(CAMPAIGN_NAME));
        assertThat(campaignArguments.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(campaignArguments.getStatus(), is(ACTIVE));
    }

    @Test
    public void verifyWhenCampaignFoundById() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .createDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(campaignDao.findById(CAMPAIGN_ID)).thenReturn(Optional.ofNullable(campaign));

        final Campaign foundCampaign = campaignService.findById(CAMPAIGN_ID);

        assertThat(foundCampaign, is(notNullValue()));

        assertThat(foundCampaign.getId(), is(CAMPAIGN_ID));
        assertThat(foundCampaign.getName(), is(CAMPAIGN_NAME));
        assertThat(foundCampaign.getKsName(), is(CAMPAIGN_KS_NAME));
        assertThat(foundCampaign.getStatus(), is(ACTIVE));
        assertThat(foundCampaign.getCreateDate(), is(notNullValue()));
        assertThat(foundCampaign.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenCampaignNotFoundById() {
        when(campaignDao.findById(CAMPAIGN_WRONG_ID)).thenThrow(
                new FieldValidationException(CAMPAIGN_WRONG_ID, List.of(new com.kenshoo.openplatform.apimodel.errors.FieldError("id", "Campaign by id not found or invalid.")))
        );

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> campaignService.findById(CAMPAIGN_WRONG_ID)
        );

        verify(campaignDao).findById(campaignIdArgumentCaptor.capture());

        assertThat(campaignIdArgumentCaptor.getValue(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getEntityId(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "Campaign by id not found or invalid."))));
    }

    @Test
    public void verifyWhenCampaignUpdated() {
        final Campaign campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        final Campaign campaignDataToUpdate = Campaign.builder()
                .name(CAMPAIGN_NAME_TO_UPDATE)
                .ksName(CAMPAIGN_KS_NAME_TO_UPDATE)
                .status(PAUSED_TO_UPDATE)
                .build();

        when(campaignDao.findById(CAMPAIGN_ID)).thenReturn(Optional.ofNullable(campaign));
        campaignService.update(CAMPAIGN_ID, campaignDataToUpdate);

        verify(campaignDao).update(campaignArgumentCaptor.capture());

        final Campaign campaignCaptorValue = campaignArgumentCaptor.getValue();

        assertThat(campaignCaptorValue.getName(), is(CAMPAIGN_NAME_TO_UPDATE));
        assertThat(campaignCaptorValue.getKsName(), is(CAMPAIGN_KS_NAME_TO_UPDATE));
        assertThat(campaignCaptorValue.getStatus(), is(PAUSED_TO_UPDATE));
    }

    @Test
    public void verifyWhenCampaignNotUpdatedWithWongId() {
        final Campaign campaignDataToUpdate = Campaign.builder()
                .name(CAMPAIGN_NAME_TO_UPDATE)
                .ksName(CAMPAIGN_KS_NAME_TO_UPDATE)
                .status(PAUSED_TO_UPDATE)
                .build();

        when(campaignDao.findById(CAMPAIGN_WRONG_ID)).thenThrow(
                new FieldValidationException(CAMPAIGN_WRONG_ID, List.of(new FieldError("id", "Campaign not found or invalid.")))
        );

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
                .id(CAMPAIGN_ID)
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(ACTIVE)
                .build();

        when(campaignDao.findById(CAMPAIGN_ID)).thenReturn(Optional.ofNullable(campaign));

        campaignService.deleteById(CAMPAIGN_ID);

        verify(campaignDao).deleteById(campaignIdArgumentCaptor.capture());

        final Long campaignIdForDelete = campaignIdArgumentCaptor.getValue();

        assertThat(campaignIdForDelete, is(CAMPAIGN_ID));
    }

    @Test
    public void verifyWhenCampaignNotChangeStatusToDeletedById() {
        when(campaignDao.findById(CAMPAIGN_WRONG_ID)).thenThrow(
                new FieldValidationException(CAMPAIGN_WRONG_ID, List.of(new FieldError("id", "Campaign not found or invalid.")))
        );

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> campaignService.deleteById(CAMPAIGN_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(CAMPAIGN_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "Campaign not found or invalid."))));
    }

}