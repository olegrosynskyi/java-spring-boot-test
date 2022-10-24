package io.skai.template.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.services.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/v1/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ApiResponse<WriteResponseDto<Long>> createCampaign(@RequestBody @JsonProperty("campaign") Campaign campaign) {
        return campaignService.create(campaign);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<Campaign> findCampaign(@PathVariable long id) {
        return campaignService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> updateCampaign(@PathVariable long id, @RequestBody Campaign campaign) {
        return campaignService.update(id, campaign);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> markCampaignAsDeleted(@PathVariable long id) {
        return campaignService.deleteById(id);
    }

}
