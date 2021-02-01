package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
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
                InMemoryDatasetImplementation.builder()
                        .metadataKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                        .datasetKey(new DatasetKey(datasetUuid))
                        .name("dataset")
                        .keyValues(Stream.of(
                                InMemoryDatasetImplementationKeyValue.builder()
                                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue1Uuid))
                                        .datasetImplementationKey(new DatasetImplementationKey(inMemoryDatasetImplementationUuid))
                                        .key("key1")
                                        .value("value1")
                                        .build(),
                                InMemoryDatasetImplementationKeyValue.builder()
                                        .metadataKey(new InMemoryDatasetImplementationKeyValueKey(inMemoryDatasetImplementationKeyValue2Uuid))
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
        ).isEqualTo(InMemoryDatasetImplementationDto.builder()
                .uuid(inMemoryDatasetImplementationUuid)
                .keyValues(Stream.of(
                        InMemoryDatasetImplementationKeyValueDto.builder()
                                .uuid(inMemoryDatasetImplementationKeyValue1Uuid)
                                .key("key1")
                                .value("value1")
                                .build(),
                        InMemoryDatasetImplementationKeyValueDto.builder()
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