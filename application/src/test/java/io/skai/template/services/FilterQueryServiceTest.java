package io.skai.template.services;

import com.kenshoo.openplatform.apimodel.QueryFilter;
import com.kenshoo.openplatform.apimodel.enums.FilterOperator;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import org.jooq.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilterQueryServiceTest {

    private static final String CAMPAIGN_NAME_1 = "name-1";
    private static final String CAMPAIGN_NAME_2 = "name-2";
    private static final String CAMPAIGN_NAME_3 = "name-3";
    private static final String CAMPAIGN_NAME_4 = "name-4";
    private static final String CAMPAIGN_NAME_5 = "name-5";
    private static final String CAMPAIGN_KS_NAME_1 = "ks-name-1";
    private static final String CAMPAIGN_KS_NAME_2 = "ks-name-2";
    private static final String CAMPAIGN_KS_NAME_3 = "ks-name-3";
    private static final String AD_GROUP_CAMPAIGN_ID_1 = "1";
    private static final String AD_GROUP_CAMPAIGN_ID_2 = "2";
    private static final String AD_GROUP_CAMPAIGN_ID_3 = "3";

    @InjectMocks
    private FilterQueryServiceImpl filterQueryService;

    @Mock
    private FieldMapperService fieldMapperService;

    @Test
    public void verifyFilteringByCampaignFieldsByEqualsWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));

        final Optional<Condition> condition = filterQueryService.filteringByCampaignFields(queryFilters);

        assertThat(condition.get(), is(
                        CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_1)
                                .or(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_2))
                                .and(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_3))
                )
        );
    }

    @Test
    public void filteringByAdGroupFieldsWithPrefixesByEqualsWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("adGroup.campaignId", FilterOperator.EQUALS, List.of(AD_GROUP_CAMPAIGN_ID_1, AD_GROUP_CAMPAIGN_ID_2)),
                new QueryFilter<>("adGroup.campaignId", FilterOperator.EQUALS, List.of(AD_GROUP_CAMPAIGN_ID_3))
        );

        when(fieldMapperService.parseAdGroupFieldWithPrefix(anyString())).thenReturn(Optional.of(new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value))));

        final Optional<Condition> condition = filterQueryService.filteringByAdGroupFieldsWithPrefixes(queryFilters);

        assertThat(condition.get(), is(
                        AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_1)
                                .or(AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_2))
                                .and(AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_3))
                )
        );
    }

    @Test
    public void verifyFilteringByCampaignFieldsByInWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));

        final Optional<Condition> condition = filterQueryService.filteringByCampaignFields(queryFilters);

        assertThat(condition.get(), is(
                        CampaignTable.TABLE.name.in(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)
                                .and(CampaignTable.TABLE.name.in(CAMPAIGN_NAME_3))
                )
        );
    }

    @Test
    public void filteringByAdGroupFieldsWithPrefixesByInWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("adGroup.campaignId", FilterOperator.IN, List.of(AD_GROUP_CAMPAIGN_ID_1, AD_GROUP_CAMPAIGN_ID_2)),
                new QueryFilter<>("adGroup.campaignId", FilterOperator.IN, List.of(AD_GROUP_CAMPAIGN_ID_3))
        );

        when(fieldMapperService.parseAdGroupFieldWithPrefix(anyString())).thenReturn(Optional.of(new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value))));

        final Optional<Condition> condition = filterQueryService.filteringByAdGroupFieldsWithPrefixes(queryFilters);

        assertThat(condition.get(), is(
                        AdGroupTable.TABLE.campaignId.in(1L, 2L)
                                .and(AdGroupTable.TABLE.campaignId.in(3L))
                )
        );
    }

    @Test
    public void verifyFilteringByCampaignFieldsByEqualsAndInWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_3)),
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_4, CAMPAIGN_NAME_5))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));

        final Optional<Condition> condition = filterQueryService.filteringByCampaignFields(queryFilters);

        assertThat(condition.get(), is(
                        CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_1)
                                .or(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_2))
                                .and(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_3))
                                .and(CampaignTable.TABLE.name.in(CAMPAIGN_NAME_4, CAMPAIGN_NAME_5))
                )
        );
    }

}
