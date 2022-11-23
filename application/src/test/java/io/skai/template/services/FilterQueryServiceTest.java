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
    private static final String AD_GROUP_NAME_1 = "name-1";
    private static final String AD_GROUP_NAME_2 = "name-2";
    private static final String AD_GROUP_NAME_3 = "name-3";
    private static final String AD_GROUP_NAME_4 = "name-4";
    private static final String AD_GROUP_NAME_5 = "name-5";
    private static final String AD_GROUP_CAMPAIGN_ID_1 = "1";
    private static final String AD_GROUP_CAMPAIGN_ID_2 = "2";
    private static final String AD_GROUP_CAMPAIGN_ID_3 = "3";

    @InjectMocks
    private FilterQueryServiceImpl filterQueryService;

    @Mock
    private FieldMapperService fieldMapperService;

    @Test
    public void verifyFilteringCampaignsByEqualsWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(
                        CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_1)
                                .or(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_2))
                                .and(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_3))
                )
        );
    }

    @Test
    public void verifyFilteringCampaignsByEqualsWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("ksName", FilterOperator.EQUALS, List.of(CAMPAIGN_KS_NAME_1)),
                new QueryFilter<>("adGroup.name", FilterOperator.EQUALS, List.of(AD_GROUP_NAME_1, AD_GROUP_NAME_2)),
                new QueryFilter<>("adGroup.name", FilterOperator.EQUALS, List.of(AD_GROUP_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(CampaignTable.TABLE.ksName.equalIgnoreCase(CAMPAIGN_KS_NAME_1)));
    }

    @Test
    public void filteringCampaignsWithPrefixByEqualsWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("adGroup.campaignId", FilterOperator.EQUALS, List.of(AD_GROUP_CAMPAIGN_ID_1, AD_GROUP_CAMPAIGN_ID_2)),
                new QueryFilter<>("adGroup.campaignId", FilterOperator.EQUALS, List.of(AD_GROUP_CAMPAIGN_ID_3))
        );

        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.of(new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value))));
        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaignsWithPrefix(queryFilters);

        assertThat(condition, is(
                        AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_1)
                                .or(AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_2))
                                .and(AdGroupTable.TABLE.campaignId.equalIgnoreCase(AD_GROUP_CAMPAIGN_ID_3))
                )
        );
    }

    @Test
    public void verifyFilteringCampaignsByInWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(
                        CampaignTable.TABLE.name.in(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)
                                .and(CampaignTable.TABLE.name.in(CAMPAIGN_NAME_3))
                )
        );
    }

    @Test
    public void verifyFilteringCampaignsByInWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("ksName", FilterOperator.IN, List.of(CAMPAIGN_KS_NAME_1)),
                new QueryFilter<>("adGroup.name", FilterOperator.IN, List.of(AD_GROUP_NAME_1, AD_GROUP_NAME_2)),
                new QueryFilter<>("adGroup.name", FilterOperator.IN, List.of(AD_GROUP_NAME_3))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(CampaignTable.TABLE.ksName.in(CAMPAIGN_KS_NAME_1)));
    }

    @Test
    public void filteringCampaignsWithPrefixByInWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("adGroup.campaignId", FilterOperator.IN, List.of(AD_GROUP_CAMPAIGN_ID_1, AD_GROUP_CAMPAIGN_ID_2)),
                new QueryFilter<>("adGroup.campaignId", FilterOperator.IN, List.of(AD_GROUP_CAMPAIGN_ID_3))
        );

        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.of(new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value))));
        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaignsWithPrefix(queryFilters);

        assertThat(condition, is(
                        AdGroupTable.TABLE.campaignId.in(1L, 2L)
                                .and(AdGroupTable.TABLE.campaignId.in(3L))
                )
        );
    }

    @Test
    public void verifyFilteringCampaignsByEqualsAndInWithoutAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_1, CAMPAIGN_NAME_2)),
                new QueryFilter<>("name", FilterOperator.EQUALS, List.of(CAMPAIGN_NAME_3)),
                new QueryFilter<>("name", FilterOperator.IN, List.of(CAMPAIGN_NAME_4, CAMPAIGN_NAME_5))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(
                        CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_1)
                                .or(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_2))
                                .and(CampaignTable.TABLE.name.equalIgnoreCase(CAMPAIGN_NAME_3))
                                .and(CampaignTable.TABLE.name.in(CAMPAIGN_NAME_4, CAMPAIGN_NAME_5))
                )
        );
    }

    @Test
    public void verifyFilteringCampaignsByEqualsAndInWithAdGroupPrefix() {
        final List<QueryFilter<List<String>>> queryFilters = List.of(
                new QueryFilter<>("ksName", FilterOperator.EQUALS, List.of(CAMPAIGN_KS_NAME_1)),
                new QueryFilter<>("ksName", FilterOperator.IN, List.of(CAMPAIGN_KS_NAME_2, CAMPAIGN_KS_NAME_3)),
                new QueryFilter<>("adGroup.name", FilterOperator.EQUALS, List.of(AD_GROUP_NAME_1, AD_GROUP_NAME_2)),
                new QueryFilter<>("adGroup.name", FilterOperator.EQUALS, List.of(AD_GROUP_NAME_3)),
                new QueryFilter<>("adGroup.name", FilterOperator.IN, List.of(AD_GROUP_NAME_4, AD_GROUP_NAME_5))
        );

        when(fieldMapperService.parseCampaignField(anyString())).thenReturn(Optional.of(new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value))));
        when(fieldMapperService.parseCampaignFieldWithPrefix(anyString())).thenReturn(Optional.empty());

        final Condition condition = filterQueryService.filteringCampaigns(queryFilters);

        assertThat(condition, is(
                CampaignTable.TABLE.ksName.equalIgnoreCase(CAMPAIGN_KS_NAME_1)
                        .and(CampaignTable.TABLE.ksName.in(CAMPAIGN_KS_NAME_2, CAMPAIGN_KS_NAME_3))
        ));
    }

}
