package io.skai.template.dataaccess.dao.impl;

import io.skai.template.dataaccess.dao.AdGroupDao;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AdGroupDaoImpl implements AdGroupDao {

    private final DSLContext dslContext;

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

}
