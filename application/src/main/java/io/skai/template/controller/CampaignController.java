package io.skai.template.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FetchQuery;
import io.skai.template.dataaccess.entities.QueryFilterException;
import io.skai.template.services.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ApiResponse<WriteResponseDto<Long>> createCampaign(@RequestBody Campaign campaign) {
        final long campaignId = campaignService.create(campaign);
        return responseCampaign(campaignId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<Campaign> findCampaign(@PathVariable long id) {
        final Campaign campaign = campaignService.findById(id);
        return new ApiResponse.Builder<Campaign>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(List.of(campaign))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> updateCampaign(@PathVariable long id, @RequestBody Campaign campaign) {
        final long campaignId = campaignService.update(id, campaign);
        return responseCampaign(campaignId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ApiResponse<WriteResponseDto<Long>> markCampaignAsDeleted(@PathVariable long id) {
        final long campaignId = campaignService.deleteById(id);
        return responseCampaign(campaignId);
    }

    @GetMapping("/")
    public ApiResponse<Campaign> fetchAllCampaigns(FetchQuery fetchQuery) {
        final ApiFetchRequest<QueryFilter<String>> apiFetchRequest = new ApiFetchRequest.Builder<QueryFilter<String>>()
                .withFilters(parseFilterQuery(fetchQuery.filters()))
                .withFields(fetchQuery.fields())
                .withLimit(fetchQuery.limit())
                .build();

        final List<Campaign> fetchedCampaigns = campaignService.fetchCampaigns(apiFetchRequest);

        return new ApiResponse.Builder<Campaign>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(fetchedCampaigns)
                .build();
    }

    private ApiResponse<WriteResponseDto<Long>> responseCampaign(long id) {
        final WriteResponseDto<Long> dto = new WriteResponseDto.Builder<Long>()
                .withErrors(Collections.emptyList())
                .withId(id)
                .build();

        return new ApiResponse.Builder<WriteResponseDto<Long>>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(List.of(dto))
                .build();
    }

    private static List<QueryFilter<String>> parseFilterQuery(String filter) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final QueryFilter<String>[] queryFilters = mapper.readValue(filter, QueryFilter[].class);
            return Arrays.asList(queryFilters);
        } catch (JsonProcessingException e) {
            throw new QueryFilterException(List.of(new FieldError("filters", "Cannot parse filters query param. Invalid json pattern")));
        }
    }

}
