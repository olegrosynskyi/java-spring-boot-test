package io.skai.template.dataaccess.dao.impl;

import io.skai.template.dataaccess.dao.CampaignsDao;
import io.skai.template.dataaccess.entities.Campaigns;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.CampaignsTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CampaignsDaoImpl implements CampaignsDao {

    private final DSLContext dslContext;

    @Override
    public long create(Campaigns campaign) {
        log.info("Create campaign : {}", campaign);
        return dslContext.insertInto(
                CampaignsTable.TABLE,
                CampaignsTable.TABLE.name,
                CampaignsTable.TABLE.ksName,
                CampaignsTable.TABLE.status
        ).values(
                campaign.getName(),
                campaign.getKsName(),
                campaign.getStatus().name()
        ).execute();
    }

    @Override
    public Optional<Campaigns> findById(long id) {
        log.info("Searching campaign in DB by id : {}", id);
        Campaigns campaign = dslContext.selectFrom(CampaignsTable.TABLE)
                .where(CampaignsTable.TABLE.id.eq(id))
                .fetchOne(campaignRec -> Campaigns.builder()
                        .id(campaignRec.get(CampaignsTable.TABLE.id))
                        .name(campaignRec.get(CampaignsTable.TABLE.name))
                        .ksName(campaignRec.get(CampaignsTable.TABLE.ksName))
                        .status(Status.valueOf(campaignRec.get(CampaignsTable.TABLE.status)))
                        .createDate(campaignRec.get(CampaignsTable.TABLE.createDate))
                        .lastUpdated(campaignRec.get(CampaignsTable.TABLE.lastUpdated))
                        .build());
        return Optional.ofNullable(campaign);
    }

    @Override
    public long update(Campaigns campaign) {
        log.info("Updating campaign in DB with id: {}", campaign.getId());
        return dslContext
                .update(CampaignsTable.TABLE)
                .set(CampaignsTable.TABLE.name, campaign.getName())
                .set(CampaignsTable.TABLE.ksName, campaign.getKsName())
                .set(CampaignsTable.TABLE.status, campaign.getStatus().name())
                .where(CampaignsTable.TABLE.id.eq(campaign.getId()))
                .execute();
    }

    @Override
    public long deleteById(long id) {
        log.info("Deleting campaign in DB with id: {}", id);
        return dslContext.update(CampaignsTable.TABLE)
                .set(CampaignsTable.TABLE.status, Status.DELETED.name())
                .where(CampaignsTable.TABLE.id.eq(id))
                .execute();
    }

}
