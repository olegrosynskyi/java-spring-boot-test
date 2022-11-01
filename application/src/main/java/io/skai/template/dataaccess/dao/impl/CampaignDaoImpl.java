package io.skai.template.dataaccess.dao.impl;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.*;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import io.skai.template.services.FieldMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CampaignDaoImpl implements CampaignDao {

    private final DSLContext dslContext;
    private final FieldMapperService fieldMapperService;

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
    public List<CampaignFetch> fetchCampaigns(ApiFetchRequest<QueryFilter<String>> apiFetchRequest) {
        log.info("Fetch campaign with fetch request: {}", apiFetchRequest);

        final List<QueryFilter<String>> queryFilters = apiFetchRequest.getFilters();
        final List<String> fetchFields = apiFetchRequest.getFields();
        final long limit = apiFetchRequest.getLimit();

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(fetchFields);
        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.getAllAdGroupFields();
        final List<TableField<Record, ?>> selectFields = getFetchSelectFields(campaignFields, adGroupFields);

        final Campaign.CampaignBuilder campaignBuilder = Campaign.builder();
        final AdGroup.AdGroupBuilder adGroupBuilder = AdGroup.builder();

        final Stream<Record> campaignsStream = dslContext.select(selectFields)
                .from(CampaignTable.TABLE)
                .leftJoin(AdGroupTable.TABLE)
                .on(CampaignTable.TABLE.id.eq(AdGroupTable.TABLE.campaignId))
                .where(AdGroupTable.TABLE.campaignId.isNotNull())
                .stream();

        return getFetchResponseResult(campaignsStream, limit, campaignFields, adGroupFields, campaignBuilder, adGroupBuilder);
    }

    private List<CampaignFetch> getFetchResponseResult(Stream<Record> campaignRecordsStream,
                                                       long limit,
                                                       List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields,
                                                       List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields,
                                                       Campaign.CampaignBuilder campaignBuilder,
                                                       AdGroup.AdGroupBuilder adGroupBuilder) {
        final Map<Long, List<Record>> groupOfCampaignRecords = campaignRecordsStream.collect(Collectors.groupingBy(record -> record.get(CampaignTable.TABLE.id)));

        return groupOfCampaignRecords.entrySet().stream()
                .limit(limit)
                .map(entry -> {
                            entry.getValue()
                                    .stream()
                                    .limit(1)
                                    .forEach(rec -> campaignFields.forEach(field -> field.getValueApplier().apply(campaignBuilder, rec)));

                            final List<AdGroup> adGroups = entry.getValue()
                                    .stream()
                                    .map(rec -> {
                                        adGroupFields.forEach(field -> field.getValueApplier().apply(adGroupBuilder, rec));
                                        return adGroupBuilder.build();
                                    }).toList();

                            final Campaign campaign = campaignBuilder.build();

                            return CampaignFetch.builder()
                                    .campaign(campaign)
                                    .adGroups(adGroups)
                                    .build();
                        }
                ).toList();
    }

    private List<TableField<Record, ?>> getFetchSelectFields(List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields,
                                                             List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields) {
        return Stream.of(campaignFields, adGroupFields)
                .flatMap(Collection::stream)
                .map(FieldMapper::getDbField)
                .collect(Collectors.toList());
    }

}
