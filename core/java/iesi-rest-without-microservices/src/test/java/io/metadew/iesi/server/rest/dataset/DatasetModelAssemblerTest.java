package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueDto;
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
class DatasetModelAssemblerTest {

    @Autowired
    private DatasetDtoModelAssembler datasetDtoModelAssembler;

    @Test
    void toModelTest() {
        UUID datasetUuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementation1Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementation2Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationLabel1Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationLabel2Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationKeyValue1Uuid = UUID.randomUUID();
        UUID inMemoryDatasetImplementationKeyValue2Uuid = UUID.randomUUID();

        assertThat(datasetDtoModelAssembler.toModel(
                Dataset.builder()
                        .metadataKey(new DatasetKey(datasetUuid))
                        .name("dataset")
                        .datasetImplementations(Stream.of(
                                InMemoryDatasetImplementation.builder()
                                        .metadataKey(new DatasetImplementationKey(inMemoryDatasetImplementation1Uuid))
                                        .datasetKey(new DatasetKey(datasetUuid))
                                        .name("dataset")
                                        .keyValues(Stream.of(
                                                InMemoryDatasetImplementationKeyValue.builder()
                                                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue1Uuid))
                                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementation1Uuid))
                                                        .key("key1")
                                                        .value("value1")
                                                        .build()
                                        ).collect(Collectors.toSet()))
                                        .datasetImplementationLabels(Stream.of(
                                                DatasetImplementationLabel.builder()
                                                        .metadataKey(new DatasetImplementationLabelKey(inMemoryDatasetImplementationLabel1Uuid))
                                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementation1Uuid))
                                                        .value("label1")
                                                        .build()
                                        ).collect(Collectors.toSet()))
                                        .build(),
                                InMemoryDatasetImplementation.builder()
                                        .metadataKey(new DatasetImplementationKey(inMemoryDatasetImplementation2Uuid))
                                        .datasetKey(new DatasetKey(datasetUuid))
                                        .name("dataset")
                                        .keyValues(Stream.of(
                                                InMemoryDatasetImplementationKeyValue.builder()
                                                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue2Uuid))
                                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementation2Uuid))
                                                        .key("key2")
                                                        .value("value2")
                                                        .build()
                                        ).collect(Collectors.toSet()))
                                        .datasetImplementationLabels(Stream.of(
                                                DatasetImplementationLabel.builder()
                                                        .metadataKey(new DatasetImplementationLabelKey(inMemoryDatasetImplementationLabel2Uuid))
                                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementation2Uuid))
                                                        .value("label2")
                                                        .build()
                                        ).collect(Collectors.toSet()))
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
        ).isEqualTo(DatasetDto.builder()
                .uuid(datasetUuid)
                .name("dataset")
                .implementations(Stream.of(
                        InMemoryDatasetImplementationDto.builder()
                                .uuid(inMemoryDatasetImplementation1Uuid)
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValueDto.builder()
                                                .uuid(inMemoryDatasetImplementationKeyValue1Uuid)
                                                .key("key1")
                                                .value("value1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelDto.builder()
                                                .uuid(inMemoryDatasetImplementationLabel1Uuid)
                                                .label("label1")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build(),
                        InMemoryDatasetImplementationDto.builder()
                                .uuid(inMemoryDatasetImplementation2Uuid)
                                .keyValues(Stream.of(
                                        InMemoryDatasetImplementationKeyValueDto.builder()
                                                .uuid(inMemoryDatasetImplementationKeyValue2Uuid)
                                                .key("key2")
                                                .value("value2")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .labels(Stream.of(
                                        DatasetImplementationLabelDto.builder()
                                                .uuid(inMemoryDatasetImplementationLabel2Uuid)
                                                .label("label2")
                                                .build()
                                ).collect(Collectors.toSet()))
                                .build()
                ).collect(Collectors.toSet()))
                .build());
    }

}