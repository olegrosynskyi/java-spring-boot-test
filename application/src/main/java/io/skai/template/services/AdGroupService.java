package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import io.skai.template.dataaccess.entities.AdGroup;

public interface AdGroupService {

    ApiResponse<WriteResponseDto<Long>> create(AdGroup adGroup);

    ApiResponse<AdGroup> findById(long id);

    ApiResponse<WriteResponseDto<Long>> update(long id, AdGroup adGroup);

    ApiResponse<WriteResponseDto<Long>> deleteById(long id);

}
