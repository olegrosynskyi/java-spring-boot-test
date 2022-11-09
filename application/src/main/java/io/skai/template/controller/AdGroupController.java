package io.skai.template.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.FetchQuery;
import io.skai.template.dataaccess.entities.QueryFilterException;
import io.skai.template.services.AdGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @GetMapping("/")
    public ApiResponse<AdGroup> fetchAllAdGroups(FetchQuery fetchQuery) {
        final ApiFetchRequest<QueryFilter<String>> apiFetchRequest = new ApiFetchRequest.Builder<QueryFilter<String>>()
                .withFilters(parseFilterQuery(fetchQuery.filters()))
                .withFields(fetchQuery.fields())
                .withLimit(fetchQuery.limit())
                .build();

        final List<AdGroup> fetchedCampaigns = adGroupService.fetchAdGroups(apiFetchRequest);

        return new ApiResponse.Builder<AdGroup>()
                .withStatus(StatusResponse.SUCCESS)
                .withEntities(fetchedCampaigns)
                .build();
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
