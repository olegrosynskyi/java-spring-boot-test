package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.ApiFetchRequest;
import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.errors.FieldError;
import io.skai.template.dataaccess.dao.AdGroupDao;
import io.skai.template.dataaccess.dao.CampaignDao;
import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("adGroupService")
@Slf4j
@RequiredArgsConstructor
public class AdGroupServiceImpl implements AdGroupService {

    private final AdGroupDao adGroupDao;
    private final CampaignDao campaignDao;

    @Override
    public long create(AdGroup adGroup) {
        final long campaignId = adGroup.getCampaignId();
        final Optional<Campaign> campaignById = campaignDao.findById(campaignId);
        if (campaignById.isEmpty()) {
            throw new FieldValidationException(null, List.of(new FieldError("campaign_id", "AdGroup not created because 'campaign_id' not found or invalid")));
        }
        return adGroupDao.create(adGroup);
    }

    @Override
    public AdGroup findById(long id) {
        final Optional<AdGroup> adGroupById = adGroupDao.findById(id);

        return adGroupById.orElseThrow(() ->
                new FieldValidationException(id, List.of(new FieldError("id", "AdGroup by id not found or invalid."))));
    }

    @Override
    public long update(long id, AdGroup adGroup) {
        final AdGroup adGroupToUpdate = AdGroup.builder()
                .id(id)
                .name(adGroup.getName())
                .status(adGroup.getStatus())
                .build();

        final Optional<AdGroup> adGroupById = adGroupDao.findById(adGroupToUpdate.getId());
        if (adGroupById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "AdGroup not found or invalid.")));
        }

        adGroupDao.update(adGroupToUpdate);

        return adGroupToUpdate.getId();
    }

    @Override
    public long deleteById(long id) {
        final Optional<AdGroup> adGroupById = adGroupDao.findById(id);
        if (adGroupById.isEmpty()) {
            throw new FieldValidationException(id, List.of(new FieldError("id", "AdGroup not found or invalid.")));
        }
        adGroupDao.deleteById(id);

        return id;
    }

    @Override
    public List<AdGroup> fetchAdGroups(ApiFetchRequest<QueryFilter<String>> apiFetchRequest) {
        return adGroupDao.fetchAdGroups(apiFetchRequest);
    }

}
