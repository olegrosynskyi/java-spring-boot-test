package io.skai.template.dataaccess.dao.impl;

import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.CampaignTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CampaignDaoImpl implements CampaignDao {

    private final DSLContext dslContext;

    @Override
    public long create(Campaign campaign) {
        log.info("Create campaign : {}", campaign);
        return dslContext.insertInto(
                CampaignTable.TABLE,
                CampaignTable.TABLE.id,
                CampaignTable.TABLE.name,
                CampaignTable.TABLE.ksName,
                CampaignTable.TABLE.status
        ).values(
                campaign.getId(),
                campaign.getName(),
                campaign.getKsName(),
                campaign.getStatus().name()
        ).execute();
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

}
