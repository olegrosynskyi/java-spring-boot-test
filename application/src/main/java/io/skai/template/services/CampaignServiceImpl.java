package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("campaignService")
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignDao campaignDao;

    @Override
    public long create(Campaign campaign) {
        return campaignDao.create(campaign);
    }

    @Override
    public Campaign findById(long id) {
        final Optional<Campaign> campaignById = campaignDao.findById(id);
        return campaignById.orElseThrow(() ->
                new FieldValidationException(id, List.of(new FieldError("id", "Campaign by id not found or invalid."))));
    }

    @Override
    public long update(long id, Campaign campaign) {
        final Campaign campaignToUpdate = Campaign.builder()
                .id(id)
                .name(campaign.getName())
                .ksName(campaign.getKsName())
                .status(campaign.getStatus())
                .build();

        final Optional<Campaign> campaignById = campaignDao.findById(campaignToUpdate.getId());
        if (campaignById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "Campaign not found or invalid.")));
        }
        campaignDao.update(campaignToUpdate);

        return campaignToUpdate.getId();
    }

    @Override
    public long deleteById(long id) {
        final Optional<Campaign> campaignById = campaignDao.findById(id);
        if (campaignById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "Campaign not found or invalid.")));
        }
        campaignDao.deleteById(id);

        return id;
    }

    @Override
    public List<Campaign> fetchCampaigns(ApiFetchRequest<QueryFilter<List<String>>> apiFetchRequest) {
        return campaignDao.fetchCampaigns(apiFetchRequest);
    }

}
