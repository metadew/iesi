package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationKeyValueDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class DatasetImplementationDtoModelAssemblerTest {

    @Autowired
    private DatasetImplementationDtoModelAssembler datasetImplementationDtoModelAssembler;


    @Test
    void toModelTest() {
        UUID datasetUuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationUuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationLabel1Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationLabel2Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationKeyValue1Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationKeyValue2Uuid = UUID.randomUUID();

        assertThat(datasetImplementationDtoModelAssembler.toModel(
                DatabaseDatasetImplementation.builder()
                        .metadataKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                        .datasetKey(new DatasetKey(datasetUuid))
                        .name("dataset")
                        .keyValues(Stream.of(
                                DatabaseDatasetImplementationKeyValue.builder()
                                        .metadataKey(new DatabaseDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue1Uuid))
                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                                        .key("key1")
                                        .value("value1")
                                        .build(),
                                DatabaseDatasetImplementationKeyValue.builder()
                                        .metadataKey(new DatabaseDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue2Uuid))
                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                                        .key("key2")
                                        .value("value2")
                                        .build()
                        ).collect(Collectors.toSet()))
                        .datasetImplementationLabels(Stream.of(
                                DatasetImplementationLabel.builder()
                                        .metadataKey(new DatasetImplementationLabelKey(inMemoryDatasetImplementationLabel1Uuid))
                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                                        .value("label1")
                                        .build(),
                                DatasetImplementationLabel.builder()
                                        .metadataKey(new DatasetImplementationLabelKey(inMemoryDatasetImplementationLabel2Uuid))
                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                                        .value("label2")
                                        .build()
                        ).collect(Collectors.toSet()))
                        .build())
        ).isEqualTo(DatabaseDatasetImplementationDto.builder()
                .uuid(inMemoryDatasetImplementationUuid)
                .keyValues(Stream.of(
                        DatabaseDatasetImplementationKeyValueDto.builder()
                                .uuid(inMemoryDatasetImplementationKeyValue1Uuid)
                                .key("key1")
                                .value("value1")
                                .build(),
                        DatabaseDatasetImplementationKeyValueDto.builder()
                                .uuid(inMemoryDatasetImplementationKeyValue2Uuid)
                                .key("key2")
                                .value("value2")
                                .build()
                ).collect(Collectors.toSet()))
                .labels(Stream.of(
                        DatasetImplementationLabelDto.builder()
                                .uuid(inMemoryDatasetImplementationLabel1Uuid)
                                .label("label1")
                                .build(),
                        DatasetImplementationLabelDto.builder()
                                .uuid(inMemoryDatasetImplementationLabel2Uuid)
                                .label("label2")
                                .build()
                ).collect(Collectors.toSet()))
                .build());
    }

}