package io.skai.template.services;

import io.skai.template.dataaccess.entities.AdGroup;
import io.skai.template.dataaccess.entities.Campaign;
import io.skai.template.dataaccess.entities.FieldMapper;
import io.skai.template.dataaccess.entities.Status;
import io.skai.template.dataaccess.table.AdGroupTable;
import io.skai.template.dataaccess.table.CampaignTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldMapperServiceTest {

    @Mock
    private FieldMapperServiceImpl fieldMapperService;

    private final List<String> filters = List.of(
            "id",
            "name",
            "status",
            "lastUpdated",
            "adGroup.id",
            "adGroup.campaignId",
            "adGroup.createDate"
    );

    private final List<String> anotherSetOfFilterFieldsList = List.of(
            "ksName",
            "createDate",
            "adGroup.name",
            "adGroup.lastUpdated",
            "adGroup.status"
    );

    private final List<String> filtersWithAllFields = List.of(
            "id",
            "name",
            "ksName",
            "status",
            "createDate",
            "lastUpdated",
            "adGroup.id",
            "adGroup.campaignId",
            "adGroup.name",
            "adGroup.status",
            "adGroup.createDate",
            "adGroup.lastUpdated"
    );

    private final List<String> filtersWithoutId = List.of(
            "name",
            "status",
            "lastUpdated",
            "adGroup.id",
            "adGroup.campaignId",
            "adGroup.createDate"
    );

    private final List<String> emptyFilter = List.of();

    private final List<FieldMapper<?, Campaign.CampaignBuilder>> filteredCampaignFields = List.of(
            new FieldMapper<>("id", CampaignTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("status", CampaignTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("lastUpdated", CampaignTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    private final List<FieldMapper<?, Campaign.CampaignBuilder>> filteredCampaignWithAnotherFields = List.of(
            new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value)),
            new FieldMapper<>("createDate", CampaignTable.TABLE.createDate, (builder, value) -> builder.createDate(value))
    );

    private final List<FieldMapper<?, Campaign.CampaignBuilder>> filteredCampaignFieldsWithAllFields = List.of(
            new FieldMapper<>("id", CampaignTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("name", CampaignTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("ksName", CampaignTable.TABLE.ksName, (builder, value) -> builder.ksName(value)),
            new FieldMapper<>("status", CampaignTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", CampaignTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", CampaignTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    private final List<FieldMapper<?, AdGroup.AdGroupBuilder>> filteredAdGroupFields = List.of(
            new FieldMapper<>("id", AdGroupTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value)),
            new FieldMapper<>("createDate", AdGroupTable.TABLE.createDate, (builder, value) -> builder.createDate(value))
    );

    private final List<FieldMapper<?, AdGroup.AdGroupBuilder>> filteredAdGroupWithAnotherFields = List.of(
            new FieldMapper<>("name", AdGroupTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("status", AdGroupTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("lastUpdated", AdGroupTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    private final List<FieldMapper<?, AdGroup.AdGroupBuilder>> filteredAdGroupFieldsWithAllFields = List.of(
            new FieldMapper<>("id", AdGroupTable.TABLE.id, (builder, value) -> builder.id(value)),
            new FieldMapper<>("campaignId", AdGroupTable.TABLE.campaignId, (builder, value) -> builder.campaignId(value)),
            new FieldMapper<>("name", AdGroupTable.TABLE.name, (builder, value) -> builder.name(value)),
            new FieldMapper<>("status", AdGroupTable.TABLE.status, (builder, value) -> builder.status(Status.valueOf(value))),
            new FieldMapper<>("createDate", AdGroupTable.TABLE.createDate, (builder, value) -> builder.createDate(value)),
            new FieldMapper<>("lastUpdated", AdGroupTable.TABLE.lastUpdated, (builder, value) -> builder.lastUpdated(value))
    );

    @Test
    public void verifyParseCampaignFields() {
        when(fieldMapperService.parseCampaignFields(filters)).thenReturn(filteredCampaignFields);

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filters);

        assertThat(campaignFields, hasSize(4));
        assertThat(campaignFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "id", "name", "status", "lastUpdated"
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                CampaignTable.TABLE.id, CampaignTable.TABLE.name, CampaignTable.TABLE.status, CampaignTable.TABLE.lastUpdated
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(), notNullValue(), notNullValue(), notNullValue()
        ));
    }

    @Test
    public void verifyParseCampaignFieldsWithAnotherFields() {
        when(fieldMapperService.parseCampaignFields(anotherSetOfFilterFieldsList)).thenReturn(filteredCampaignWithAnotherFields);

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(anotherSetOfFilterFieldsList);

        assertThat(campaignFields, hasSize(2));
        assertThat(campaignFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "ksName", "createDate"
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                CampaignTable.TABLE.ksName, CampaignTable.TABLE.createDate
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(), notNullValue()
        ));
    }

    @Test
    public void verifyParseCampaignFieldsWithoutIdWillAddId() {
        when(fieldMapperService.parseCampaignFields(filtersWithoutId)).thenReturn(filteredCampaignFields);
        assertThat(filtersWithoutId, not(hasItem("id")));

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filtersWithoutId);


        assertThat(campaignFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), hasItem("id"));
        assertThat(campaignFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), hasItem(CampaignTable.TABLE.id));
        assertThat(campaignFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), hasItem(notNullValue()));
    }

    @Test
    public void verifyParseCampaignFieldsWithAllFields() {
        when(fieldMapperService.parseCampaignFields(filtersWithAllFields)).thenReturn(filteredCampaignFieldsWithAllFields);

        final List<FieldMapper<?, Campaign.CampaignBuilder>> campaignFields = fieldMapperService.parseCampaignFields(filtersWithAllFields);

        assertThat(campaignFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "id", "name", "ksName", "status", "createDate", "lastUpdated"
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                CampaignTable.TABLE.id,
                CampaignTable.TABLE.name,
                CampaignTable.TABLE.ksName,
                CampaignTable.TABLE.status,
                CampaignTable.TABLE.createDate,
                CampaignTable.TABLE.lastUpdated
        ));
        assertThat(campaignFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue()
        ));
    }

    @Test
    public void verifyParseAdGroupFields() {
        when(fieldMapperService.parseAdGroupFields(filters)).thenReturn(filteredAdGroupFields);

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(filters);

        assertThat(adGroupFields, hasSize(3));
        assertThat(adGroupFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "id", "campaignId", "createDate"
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                AdGroupTable.TABLE.id, AdGroupTable.TABLE.campaignId, AdGroupTable.TABLE.createDate
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(), notNullValue(), notNullValue()
        ));
    }

    @Test
    public void verifyParseAdGroupFieldsWithAnotherFields() {
        when(fieldMapperService.parseAdGroupFields(anotherSetOfFilterFieldsList)).thenReturn(filteredAdGroupWithAnotherFields);

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(anotherSetOfFilterFieldsList);

        assertThat(adGroupFields, hasSize(3));
        assertThat(adGroupFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "name", "status", "lastUpdated"
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                AdGroupTable.TABLE.name, AdGroupTable.TABLE.status, AdGroupTable.TABLE.lastUpdated
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(), notNullValue(), notNullValue()
        ));
    }

    @Test
    public void verifyParseAdGroupFieldsWithAllFields() {
        when(fieldMapperService.parseAdGroupFields(filtersWithAllFields)).thenReturn(filteredAdGroupFieldsWithAllFields);

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(filtersWithAllFields);

        assertThat(adGroupFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "id", "campaignId", "name", "status", "createDate", "lastUpdated"
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                AdGroupTable.TABLE.id,
                AdGroupTable.TABLE.campaignId,
                AdGroupTable.TABLE.name,
                AdGroupTable.TABLE.status,
                AdGroupTable.TABLE.createDate,
                AdGroupTable.TABLE.lastUpdated
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue()
        ));
    }

    @Test
    public void verifyParseAdGroupWhenFilterIsEmpty() {
        when(fieldMapperService.parseAdGroupFields(emptyFilter)).thenReturn(filteredAdGroupFieldsWithAllFields);

        final List<FieldMapper<?, AdGroup.AdGroupBuilder>> adGroupFields = fieldMapperService.parseAdGroupFields(emptyFilter);

        assertThat(adGroupFields, hasSize(6));
        assertThat(adGroupFields.stream().map(FieldMapper::getName).collect(Collectors.toList()), containsInAnyOrder(
                "id", "campaignId", "name", "status", "createDate", "lastUpdated"
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getDbField).collect(Collectors.toList()), containsInAnyOrder(
                AdGroupTable.TABLE.id,
                AdGroupTable.TABLE.campaignId,
                AdGroupTable.TABLE.name,
                AdGroupTable.TABLE.status,
                AdGroupTable.TABLE.createDate,
                AdGroupTable.TABLE.lastUpdated
        ));
        assertThat(adGroupFields.stream().map(FieldMapper::getValueApplier).collect(Collectors.toList()), containsInAnyOrder(
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue(),
                notNullValue()
        ));
    }

}