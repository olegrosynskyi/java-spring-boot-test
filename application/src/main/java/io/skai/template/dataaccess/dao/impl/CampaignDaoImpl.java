package io.skai.template.dataaccess.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.*;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CampaignDaoImpl implements CampaignDao {

    private final DSLContext dslContext;

    @Override
    public long create(Campaign campaign) {
        log.info("Create campaign : {}", campaign);
        dslContext.insertInto(
                CampaignTable.TABLE,
                CampaignTable.TABLE.name,
                CampaignTable.TABLE.ksName,
                CampaignTable.TABLE.status
        ).values(
                campaign.getName(),
                campaign.getKsName(),
                campaign.getStatus().name()
        ).execute();
        return dslContext.lastID().longValue();
    }

    @Override
    public Optional<Campaign> findById(long id) {
        log.info("Searching campaign in DB by id : {}", id);
        final Campaign campaign = dslContext.selectFrom(CampaignTable.TABLE)
                .where(CampaignTable.TABLE.id.eq(id))
                .fetchOne(campaignRec -> Campaign.builder()
                        .id(campaignRec.get(CampaignTable.TABLE.id))
                        .name(campaignRec.get(CampaignTable.TABLE.name))
                        .ksName(campaignRec.get(CampaignTable.TABLE.ksName))
                        .status(Status.valueOf(campaignRec.get(CampaignTable.TABLE.status)))
                        .createDate(campaignRec.get(CampaignTable.TABLE.createDate))
                        .lastUpdated(campaignRec.get(CampaignTable.TABLE.lastUpdated))
                        .build());
        return Optional.ofNullable(campaign);
    }

    @Override
    public long update(Campaign campaign) {
        log.info("Updating campaign in DB with id: {}", campaign.getId());
        return dslContext
                .update(CampaignTable.TABLE)
                .set(CampaignTable.TABLE.name, campaign.getName())
                .set(CampaignTable.TABLE.ksName, campaign.getKsName())
                .set(CampaignTable.TABLE.status, campaign.getStatus().name())
                .where(CampaignTable.TABLE.id.eq(campaign.getId()))
                .execute();
    }

    @Override
    public long deleteById(long id) {
        log.info("Deleting campaign in DB with id: {}", id);
        return dslContext.update(CampaignTable.TABLE)
                .set(CampaignTable.TABLE.status, Status.DELETED.name())
                .where(CampaignTable.TABLE.id.eq(id))
                .execute();
    }

    @Override
    public List<CampaignFetch> fetchCampaigns(CampaignQuery campaignQuery) {
        final List<QueryFilter<String>> queryFilters = parseFilterQuery(campaignQuery.filters());
        final List<String> fields = campaignQuery.fields();
        final long limit = campaignQuery.limit();

        final List<TableField<Record, ?>> tableFields = campaignMatchFields(fields);
        tableFields.addAll(List.of(
                AdGroupTable.TABLE.id,
                AdGroupTable.TABLE.campaignId,
                AdGroupTable.TABLE.name,
                AdGroupTable.TABLE.status,
                AdGroupTable.TABLE.createDate,
                AdGroupTable.TABLE.lastUpdated
        ));

        List<Campaign> campaigns = dslContext.select(tableFields)
                .from(CampaignTable.TABLE)
                .leftJoin(AdGroupTable.TABLE)
                .on(CampaignTable.TABLE.id.eq(AdGroupTable.TABLE.campaignId))
                .limit(limit)
                .fetch(fetchCampaignsRecordMapper(fields));

        return getFetchResponseResult(campaigns);
    }

    private List<CampaignFetch> getFetchResponseResult(List<Campaign> campaigns) {
        final Map<Long, List<Campaign>> groupCampaigns = campaigns.stream().collect(Collectors.groupingBy(Campaign::getId));

        return groupCampaigns.entrySet().stream()
                .map(entry ->
                        CampaignFetch.builder()
                                .campaign(entry.getValue().get(0))
                                .adGroups(entry.getValue().stream()
                                        .map(Campaign::getAdGroup)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                                ).build()
                ).toList();
    }

    private RecordMapper<Record, Campaign> fetchCampaignsRecordMapper(List<String> fields) {
        Campaign.CampaignBuilder builder = Campaign.builder();
        return rec -> {
            if (fields.contains("id")) {
                builder.id(rec.get(CampaignTable.TABLE.id));
            }
            if (fields.contains("name")) {
                builder.name(rec.get(CampaignTable.TABLE.name));
            }
            if (fields.contains("ksName")) {
                builder.ksName(rec.get(CampaignTable.TABLE.ksName));
            }
            if (fields.contains("status")) {
                builder.status(Status.valueOf(rec.get(CampaignTable.TABLE.status)));
            }
            if (fields.contains("createDate")) {
                builder.createDate(rec.get(CampaignTable.TABLE.createDate));
            }
            if (fields.contains("lastUpdated")) {
                builder.lastUpdated(rec.get(CampaignTable.TABLE.lastUpdated));
            }

            AdGroup.AdGroupBuilder adGroupBuilder = AdGroup.builder();
            if (rec.field(AdGroupTable.TABLE.id) != null && rec.get(AdGroupTable.TABLE.campaignId) != null) {
                builder.adGroup(adGroupBuilder
                        .id(rec.get(AdGroupTable.TABLE.id))
                        .campaignId(rec.get(AdGroupTable.TABLE.campaignId))
                        .name(rec.get(AdGroupTable.TABLE.name))
                        .status(Status.valueOf(rec.get(AdGroupTable.TABLE.status)))
                        .createDate(rec.get(AdGroupTable.TABLE.createDate))
                        .lastUpdated(rec.get(AdGroupTable.TABLE.lastUpdated))
                        .build());
            } else {
                builder.adGroup(null);
            }

            return builder.build();
        };
    }

    private List<QueryFilter<String>> parseFilterQuery(String filter) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final QueryFilter<String>[] queryFilters = mapper.readValue(filter, QueryFilter[].class);
            return Arrays.asList(queryFilters);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TableField<Record, ?>> campaignMatchFields(List<String> fields) {
        return fields.stream().map(field -> switch (field) {
            case "id" -> CampaignTable.TABLE.id;
            case "name" -> CampaignTable.TABLE.name;
            case "ksName" -> CampaignTable.TABLE.ksName;
            case "status" -> CampaignTable.TABLE.status;
            case "createDate" -> CampaignTable.TABLE.createDate;
            case "lastUpdated" -> CampaignTable.TABLE.lastUpdated;
            default -> null;
        }).collect(Collectors.toList());
    }

}
