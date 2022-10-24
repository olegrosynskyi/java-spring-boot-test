package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service("campaignService")
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignDao campaignDao;

    @Override
    public ApiResponse<WriteResponseDto<Long>> create(Campaign campaign) {
        final long campaignId = campaignDao.create(campaign);
        return responseCampaignId(campaignId);
    }

    @Override
    public ApiResponse<Campaign> findById(long id) {
        final Optional<Campaign> campaignById = campaignDao.findById(id);

        if (campaignById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "Campaign by id not found or invalid.")));
        }

        return new ApiResponse.Builder<Campaign>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(campaignById.map(List::of).orElseGet(List::of))
                .build();
    }

    @Override
    public ApiResponse<WriteResponseDto<Long>> update(long id, Campaign campaign) {
        final Campaign campaignToUpdate = Campaign.builder()
                .id(id)
                .name(campaign.getName())
                .ksName(campaign.getKsName())
                .status(campaign.getStatus())
                .build();

        final Optional<Campaign> campaignById = campaignDao.findById(campaignToUpdate.getId());
        if (campaignById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "Campaign not found or invalid.")));
        }
        campaignDao.update(campaignToUpdate);

        return responseCampaignId(id);
    }

    @Override
    public ApiResponse<WriteResponseDto<Long>> deleteById(long id) {
        final Optional<Campaign> campaignById = campaignDao.findById(id);
        if (campaignById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "Campaign not found or invalid.")));
        }
        campaignDao.deleteById(id);

        return responseCampaignId(id);
    }

    public ApiResponse<WriteResponseDto<Long>> responseCampaignId(long id) {
        final WriteResponseDto<Long> dto = new WriteResponseDto.Builder<Long>()
                .withErrors(Collections.emptyList())
                .withId(id)
                .build();

        return new ApiResponse.Builder<WriteResponseDto<Long>>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(List.of(dto))
                .build();
    }

}
