package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.dao.AdGroupDao;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.AdGroup;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdGroupServiceTest {

    private static final Long CAMPAIGN_WRONG_ID = 999_999_999L;
    private static final Long CAMPAIGN_ID = 22L;
    private static final Long AD_GROUP_WRONG_ID = 999_999_999L;
    private static final Long AD_GROUP_ID = 4L;
    private static final String CAMPAIGN_NAME = "CAMPAIGN_NAME_1";
    private static final String CAMPAIGN_KS_NAME = "CAMPAIGN_KS_NAME_1";
    private static final String AD_GROUP_NAME = "AD_GROUP_NAME_1";
    private static final String AD_GROUP_NAME_TO_UPDATE = "AD_GROUP_NAME_TO_UPDATE_1";
    private static final Status CAMPAIGN_ACTIVE = Status.ACTIVE;
    private static final Status AD_GROUP_ACTIVE = Status.ACTIVE;
    private static final Status AD_GROUP_PAUSED = Status.PAUSED;

    @InjectMocks
    private AdGroupServiceImpl adGroupService;

    @Mock
    private CampaignDao campaignDao;
    @Mock
    private AdGroupDao adGroupDao;

    @Captor
    private ArgumentCaptor<AdGroup> adGroupArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> adGroupIdArgumentCaptor;

    @Test
    public void verifyWhenAdGroupCreated() {
        final Campaign campaign = Campaign.builder()
                .name(CAMPAIGN_NAME)
                .ksName(CAMPAIGN_KS_NAME)
                .status(CAMPAIGN_ACTIVE)
                .build();

        final AdGroup adGroup = AdGroup.builder()
                .campaignId(CAMPAIGN_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        when(campaignDao.findById(eq(CAMPAIGN_ID))).thenReturn(Optional.ofNullable(campaign));
        when(adGroupDao.create(adGroup)).thenReturn(AD_GROUP_ID);

        final long adGroupId = adGroupService.create(adGroup);

        verify(adGroupDao).create(adGroupArgumentCaptor.capture());

        final AdGroup adGroupCaptorValue = adGroupArgumentCaptor.getValue();

        assertThat(adGroupId, is(AD_GROUP_ID));
        assertThat(adGroupCaptorValue.getName(), is(AD_GROUP_NAME));
        assertThat(adGroupCaptorValue.getStatus(), is(AD_GROUP_ACTIVE));
        assertThat(adGroupCaptorValue.getCampaignId(), is(CAMPAIGN_ID));
    }

    @Test
    public void verifyWhenAdGroupNotCreatedWithWrongCampaignId() {
        final AdGroup adGroup = AdGroup.builder()
                .campaignId(CAMPAIGN_WRONG_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .build();

        when(campaignDao.findById(CAMPAIGN_WRONG_ID)).thenReturn(Optional.empty());

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.create(adGroup)
        );

        assertThat(exception.getEntityId(), is(nullValue()));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("campaign_id", "AdGroup not created because 'campaign_id' not found or invalid"))));
    }

    @Test
    public void verifyWhenAdGroupFoundById() {
        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .campaignId(null)
                .campaign(null)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .createDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(adGroupDao.findById(AD_GROUP_ID)).thenReturn(Optional.ofNullable(adGroup));

        final AdGroup adGroupFound = adGroupService.findById(AD_GROUP_ID);

        assertThat(adGroupFound.getId(), is(AD_GROUP_ID));
        assertThat(adGroupFound.getName(), is(AD_GROUP_NAME));
        assertThat(adGroupFound.getStatus(), is(AD_GROUP_ACTIVE));
        assertThat(adGroupFound.getCampaignId(), is(nullValue()));
        assertThat(adGroupFound.getCampaign(), is(nullValue()));
        assertThat(adGroupFound.getCreateDate(), is(notNullValue()));
        assertThat(adGroupFound.getLastUpdated(), is(notNullValue()));
    }

    @Test
    public void verifyWhenAdGroupNotFoundById() {
        when(adGroupDao.findById(AD_GROUP_WRONG_ID)).thenReturn(Optional.empty());

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.findById(AD_GROUP_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup by id not found or invalid."))));
    }

    @Test
    public void verifyWhenAdGroupUpdated() {
        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .createDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        final AdGroup adGroupDataToUpdate = AdGroup.builder()
                .name(AD_GROUP_NAME_TO_UPDATE)
                .status(AD_GROUP_PAUSED)
                .build();

        when(adGroupDao.findById(AD_GROUP_ID)).thenReturn(Optional.ofNullable(adGroup));
        adGroupService.update(AD_GROUP_ID, adGroupDataToUpdate);

        verify(adGroupDao).update(adGroupArgumentCaptor.capture());

        final AdGroup adGroupCaptorValue = adGroupArgumentCaptor.getValue();

        assertThat(adGroupCaptorValue.getName(), is(AD_GROUP_NAME_TO_UPDATE));
        assertThat(adGroupCaptorValue.getStatus(), is(AD_GROUP_PAUSED));
    }

    @Test
    public void verifyWhenAdGroupNotUpdatedWithWongId() {
        final AdGroup adGroupDataToUpdate = AdGroup.builder()
                .name(AD_GROUP_NAME_TO_UPDATE)
                .status(AD_GROUP_PAUSED)
                .build();

        when(adGroupDao.findById(AD_GROUP_WRONG_ID)).thenReturn(Optional.empty());

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.update(AD_GROUP_WRONG_ID, adGroupDataToUpdate)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup not found or invalid."))));
    }

    @Test
    public void verifyWhenAdGroupChangeStatusToDeletedById() {
        final AdGroup adGroup = AdGroup.builder()
                .id(AD_GROUP_ID)
                .name(AD_GROUP_NAME)
                .status(AD_GROUP_ACTIVE)
                .createDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(adGroupDao.findById(AD_GROUP_ID)).thenReturn(Optional.ofNullable(adGroup));

        adGroupService.deleteById(AD_GROUP_ID);

        verify(adGroupDao).deleteById(adGroupIdArgumentCaptor.capture());

        final Long adGroupIdForDelete = adGroupIdArgumentCaptor.getValue();

        assertThat(adGroupIdForDelete, is(AD_GROUP_ID));
    }

    @Test
    public void verifyWhenAdGroupNotChangeStatusToDeletedById() {
        when(adGroupDao.findById(AD_GROUP_WRONG_ID)).thenReturn(Optional.empty());

        final FieldValidationException exception = assertThrows(
                FieldValidationException.class,
                () -> adGroupService.deleteById(AD_GROUP_WRONG_ID)
        );

        assertThat(exception.getEntityId(), is(AD_GROUP_WRONG_ID));
        assertThat(exception.getFieldErrors(), is(List.of(new FieldError("id", "AdGroup not found or invalid."))));
    }

}