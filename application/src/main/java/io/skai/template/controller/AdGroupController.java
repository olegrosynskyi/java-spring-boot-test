package io.skai.template.controller;

import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.services.AdGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/ad_group")
@RequiredArgsConstructor
public class AdGroupController {

    private final AdGroupService adGroupService;

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ApiResponse<WriteResponseDto<Long>> createAdGroup(@RequestBody AdGroup adGroup) {
        final long adGroupId = adGroupService.create(adGroup);
        return responseAdGroup(adGroupId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<AdGroup> findAdGroup(@PathVariable long id) {
        final AdGroup adGroup = adGroupService.findById(id);
        return new ApiResponse.Builder<AdGroup>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(List.of(adGroup))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> updateAdGroup(@PathVariable long id, @RequestBody AdGroup adGroup) {
        final long adGroupId = adGroupService.update(id, adGroup);
        return responseAdGroup(adGroupId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> markAdGroupAsDeleted(@PathVariable long id) {
        final long adGroupId = adGroupService.deleteById(id);
        return responseAdGroup(adGroupId);
    }

    private ApiResponse<WriteResponseDto<Long>> responseAdGroup(long id) {
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
