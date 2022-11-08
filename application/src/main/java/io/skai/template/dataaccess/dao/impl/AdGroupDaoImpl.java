package io.skai.template.dataaccess.dao.impl;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import io.skai.template.dataaccess.dao.AdGroupDao;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import io.skai.template.services.FieldMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AdGroupDaoImpl implements AdGroupDao {

    private final DSLContext dslContext;
    private final FieldMapperService fieldMapperService;

    @Override
    public long create(AdGroup adGroup) {
        log.info("Create ad group: {}", adGroup);
        dslContext.insertInto(
                AdGroupTable.TABLE,
                AdGroupTable.TABLE.name,
                AdGroupTable.TABLE.status,
                AdGroupTable.TABLE.campaignId
        ).values(
                adGroup.getName(),
                adGroup.getStatus().name(),
                adGroup.getCampaignId()
        ).execute();
        return dslContext.lastID().longValue();
    }

    @Override
    public Optional<AdGroup> findById(long id) {
        log.info("Searching ad group in DB by id : {}", id);
        final AdGroup adGroup = dslContext.selectFrom(AdGroupTable.TABLE)
                .where(AdGroupTable.TABLE.id.eq(id))
                .fetchOne(adGroupByIdRecordMapper());
        return Optional.ofNullable(adGroup);
    }

    @Override
    public long update(AdGroup adGroup) {
        log.info("Updating ad group in DB by id : {}", adGroup.getId());
        return dslContext.update(AdGroupTable.TABLE)
                .set(AdGroupTable.TABLE.name, adGroup.getName())
                .set(AdGroupTable.TABLE.status, adGroup.getStatus().name())
                .where(AdGroupTable.TABLE.id.eq(adGroup.getId()))
                .execute();
    }

    @Override
    public long deleteById(long id) {
        log.info("Deleting ad group in DB by id : {}", id);
        return dslContext.update(AdGroupTable.TABLE)
                .set(AdGroupTable.TABLE.status, Status.DELETED.name())
                .where(AdGroupTable.TABLE.id.eq(id))
                .execute();
    }

    @Override
    public List<AdGroup> fetchNotDeletedByKsName(String ksName) {
        log.info("Fetching ad group without deleted data in DB by ks name : {}", ksName);
        final List<AdGroup> adGroups = dslContext.select()
                .from(AdGroupTable.TABLE)
                .leftJoin(CampaignTable.TABLE)
                .on(CampaignTable.TABLE.id.eq(AdGroupTable.TABLE.campaignId))
                .where(
                        CampaignTable.TABLE.ksName.eq(ksName)
                                .and(CampaignTable.TABLE.status.notEqual(Status.DELETED.name()))
                                .and(AdGroupTable.TABLE.status.notEqual(Status.DELETED.name()))
                ).fetch(adGroupFetchNotDeletedByKsNameRecordMapper());
        return adGroups;
    }

    @Override
    public List<AdGroup> fetchAdGroups(ApiFetchRequest<QueryFilter<String>> apiFetchRequest) {
        log.info("Fetch adGroup with fetch request: {}", apiFetchRequest);

        final List<QueryFilter<String>> queryFilters = apiFetchRequest.getFilters();
        final List<String> fetchFields = apiFetchRequest.getFields();
        final long limit = apiFetchRequest.getLimit();

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(getFieldsWithPrefix(addSpecificQueryId(fetchFields, "campaign."), "campaign."));
        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(getFieldsWithoutPrefix(addSpecificQueryId(fetchFields, null), "campaign.", "adGroup."));

        System.out.println(campaignFields);
        final List<TableField<Record, ?>> selectFields = getFetchSelectFields(campaignFields, adGroupFields);

        final Stream<Record> adGroupsStream = dslContext.select(selectFields)
                .from(AdGroupTable.TABLE)
                .innerJoin(CampaignTable.TABLE)
                .on(AdGroupTable.TABLE.campaignId.eq(CampaignTable.TABLE.id))
                .stream();

        return getFetchResponseResult(adGroupsStream, limit, campaignFields, adGroupFields);
    }

    private List<AdGroup> getFetchResponseResult(Stream<Record> campaignRecordsStream,
                                                 long limit,
                                                 List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields,
                                                 List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields) {
        return campaignRecordsStream
                .limit(limit)
                .map(record -> {
                    final AdGroup.AdGroupBuilder adGroupBuilder = AdGroup.builder();
                    final Campaign.CampaignBuilder campaignBuilder = Campaign.builder();

                    adGroupFields.forEach(field -> field.getValueApplier().apply(adGroupBuilder, record));
                    campaignFields.forEach(field -> field.getValueApplier().apply(campaignBuilder, record));

                    final Campaign campaign = campaignBuilder.build();

                    return adGroupBuilder
                            .campaign(campaign)
                            .build();
                }).toList();
    }

    private RecordMapper<Record, AdGroup> adGroupByIdRecordMapper() {
        return adGroupRec -> AdGroup.builder()
                .id(adGroupRec.get(AdGroupTable.TABLE.id))
                .name(adGroupRec.get(AdGroupTable.TABLE.name))
                .status(Status.valueOf(adGroupRec.get(AdGroupTable.TABLE.status)))
                .createDate(adGroupRec.get(AdGroupTable.TABLE.createDate))
                .lastUpdated(adGroupRec.get(AdGroupTable.TABLE.lastUpdated))
                .build();
    }

    private RecordMapper<Record, AdGroup> adGroupFetchNotDeletedByKsNameRecordMapper() {
        return adGroupRec -> AdGroup.builder()
                .id(adGroupRec.get(AdGroupTable.TABLE.id))
                .campaignId(adGroupRec.get(CampaignTable.TABLE.id))
                .campaign(Campaign.builder()
                        .id(adGroupRec.get(CampaignTable.TABLE.id))
                        .name(adGroupRec.get(CampaignTable.TABLE.name))
                        .ksName(adGroupRec.get(CampaignTable.TABLE.ksName))
                        .status(Status.valueOf(adGroupRec.get(CampaignTable.TABLE.status)))
                        .createDate(adGroupRec.get(CampaignTable.TABLE.createDate))
                        .lastUpdated(adGroupRec.get(CampaignTable.TABLE.lastUpdated))
                        .build())
                .name(adGroupRec.get(AdGroupTable.TABLE.name))
                .status(Status.valueOf(adGroupRec.get(AdGroupTable.TABLE.status)))
                .createDate(adGroupRec.get(AdGroupTable.TABLE.createDate))
                .lastUpdated(adGroupRec.get(AdGroupTable.TABLE.lastUpdated))
                .build();
    }

    private List<TableField<Record, ?>> getFetchSelectFields
            (List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields,
             List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields) {
        return Stream.of(campaignFields, adGroupFields)
                .flatMap(Collection::stream)
                .map(FieldMapper::getDbField)
                .collect(Collectors.toList());
    }

    private List<String> getFieldsWithPrefix(List<String> fields, String prefix) {
        return fields.stream().filter(field -> field.startsWith(prefix)).map(field -> field.substring(prefix.length())).toList();
    }

    private List<String> getFieldsWithoutPrefix(List<String> fields, String... prefix) {
        return fields.stream().filter(field -> !field.startsWith(prefix[0]) && !field.startsWith(prefix[1])).toList();
    }

    private List<String> addSpecificQueryId(List<String> fields, String prefix) {
        final Set<String> filterFields = new HashSet<>(fields);
        final String id = "id";
        filterFields.add(id);
        if (prefix != null) {
            filterFields.add(prefix + id);
        }
        return new ArrayList<>(filterFields);
    }

}
