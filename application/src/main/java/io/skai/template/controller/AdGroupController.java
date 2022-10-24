package io.skai.template.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.services.AdGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/v1/ad_group")
@RequiredArgsConstructor
public class AdGroupController {

    private final AdGroupService adGroupService;

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ApiResponse<WriteResponseDto<Long>> createAdGroup(@RequestBody @JsonProperty("ad_group") AdGroup adGroup) {
        return adGroupService.create(adGroup);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<AdGroup> findAdGroup(@PathVariable long id) {
        return adGroupService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> updateCampaign(@PathVariable long id, @RequestBody AdGroup adGroup) {
        return adGroupService.update(id, adGroup);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> markCampaignAsDeleted(@PathVariable long id) {
        return adGroupService.deleteById(id);
    }

}
