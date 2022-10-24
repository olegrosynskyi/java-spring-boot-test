package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import io.skai.template.dataaccess.entities.Campaign;

public interface CampaignService {

    ApiResponse<WriteResponseDto<Long>> create(Campaign campaign);

    ApiResponse<Campaign> findById(long id);

    ApiResponse<WriteResponseDto<Long>> update(long id, Campaign campaign);

    ApiResponse<WriteResponseDto<Long>> deleteById(long id);

}
