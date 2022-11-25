package io.skai.template.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.ApiResponse;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.WriteResponseDto;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import com.kenshoo.openplatform.apimodel.enums.StatusResponse;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FetchQuery;
import io.skai.template.dataaccess.entities.QueryFilterException;
import io.skai.template.services.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Seq;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        final ApiFetchRequest<QueryFilter<List<String>>> apiFetchRequest = new ApiFetchRequest.Builder<QueryFilter<List<String>>>()
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

    private static List<QueryFilter<List<String>>> parseFilterQuery(String filter) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return Seq.seq(mapper.readTree(filter).iterator()).map(jsonNode -> {
                final String fieldNotContainsMessage = "Json not contains field";
                final String field = Optional.ofNullable(jsonNode.get("field"))
                        .orElseThrow(
                                () -> new QueryFilterException(List.of(new FieldError("field", fieldNotContainsMessage)))
                        ).asText();
                final String operator = Optional.ofNullable(jsonNode.get("operator"))
                        .orElseThrow(
                                () -> new QueryFilterException(List.of(new FieldError("operator", fieldNotContainsMessage)))
                        ).asText();
                final List<String> values = Seq.seq(Optional.ofNullable(jsonNode.get("values"))
                        .orElseThrow(
                                () -> new QueryFilterException(List.of(new FieldError("values", fieldNotContainsMessage)))
                        ).iterator()).map(JsonNode::asText).toList();

                validateJsonValues(field, operator, values);

                final FilterOperator filterOperator = FilterOperator.valueOf(operator);


                return new QueryFilter<>(field, filterOperator, values);
            }).toList();
        } catch (JsonProcessingException e) {
            throw new QueryFilterException(List.of(new FieldError("filters", "Cannot parse filters query param. Invalid json pattern")));
        }
    }

    private static void validateJsonValues(String field, String operator, List<String> values) {
        if (StringUtils.isEmpty(field) || StringUtils.isBlank(field)) {
            throw new QueryFilterException(List.of(new FieldError("field", "Field value can not be empty")));
        } else if (StringUtils.isEmpty(operator) || StringUtils.isBlank(operator)) {
            throw new QueryFilterException(List.of(new FieldError("operator", "Operator value can not be empty")));
        } else if (values.isEmpty()) {
            throw new QueryFilterException(List.of(new FieldError("values", "Value array can not be empty")));
        }
    }

}
