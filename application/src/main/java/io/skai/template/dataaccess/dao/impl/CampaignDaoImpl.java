package io.skai.template.dataaccess.dao.impl;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import io.skai.template.services.FieldMapperService;
import io.skai.template.services.FilterQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.lambda.Seq;
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
    private final FilterQueryService filterQueryService;

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
    public List<Campaign> fetchCampaigns(ApiFetchRequest<QueryFilter<List<String>>> apiFetchRequest) {
        log.info("Fetch campaign with fetch request: {}", apiFetchRequest);

        final List<QueryFilter<List<String>>> queryFilters = apiFetchRequest.getFilters();
        final List<String> fetchFields = apiFetchRequest.getFields();
        final long limit = apiFetchRequest.getLimit();

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(fetchFields);
        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFieldsWithPrefix(fetchFields);

        final List<TableField<Record, ?>> selectFields = getFetchSelectFields(campaignFields, adGroupFields);

        final Optional<Condition> campaignCondition = filterQueryService.filteringByCampaignFields(queryFilters);
        final Optional<Condition> adGroupCondition = filterQueryService.filteringByAdGroupFieldsWithPrefixes(queryFilters);

        final Condition condition = Seq.of(adGroupCondition, campaignCondition)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Condition::and)
                .orElse(null);

        final Stream<Record> campaignsStream = dslContext.select(selectFields)
                .from(CampaignTable.TABLE)
                .leftJoin(AdGroupTable.TABLE)
                .on(CampaignTable.TABLE.id.eq(AdGroupTable.TABLE.campaignId))
                .where(condition)
                .stream();

        return getFetchResponseResult(campaignsStream, limit, campaignFields, adGroupFields);
    }

    private List<Campaign> getFetchResponseResult(Stream<Record> campaignRecordsStream,
                                                  long limit,
                                                  List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields,
                                                  List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields) {
        final Map<Long, List<Record>> groupOfCampaignRecords = campaignRecordsStream.collect(Collectors.groupingBy(record -> record.get(CampaignTable.TABLE.id)));

        final Campaign.CampaignBuilder campaignBuilder = Campaign.builder();
        return groupOfCampaignRecords.entrySet().stream()
                .limit(limit)
                .map(entry -> {

                            final Record campaignRecord = entry.getValue().get(0);
                            campaignFields.forEach(field -> field.getValueApplier().apply(campaignBuilder, campaignRecord));

                            final List<AdGroup> adGroups = entry.getValue()
                                    .stream()
                                    .filter(rec -> adGroupFields.stream().noneMatch(field -> rec.get(field.getDbField()) == null))
                                    .map(rec -> {
                                        final AdGroup.AdGroupBuilder adGroupBuilder = AdGroup.builder();

                                        adGroupFields.forEach(field -> {
                                            field.getValueApplier().apply(adGroupBuilder, rec);
                                        });
                                        return adGroupBuilder.build();
                                    }).toList();

                            campaignBuilder.adGroups(adGroups);

                            return campaignBuilder.build();
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
