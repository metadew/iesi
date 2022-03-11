package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.dto.DatasetDto;
import io.metadew.iesi.server.rest.dataset.dto.IDatasetDtoRepository;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationLabelDto;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationKeyValueDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class DatasetDtoRepositoryTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private DatasetConfiguration datasetConfiguration;

    @Autowired
    private IDatasetDtoRepository datasetDtoRepository;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllPaginatedNoImplementationsLinkedToDataset() {
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset")
                .datasetImplementations(new HashSet<>())
                .build();
        DatasetDto datasetDto = DatasetDto.builder()
                .uuid(dataset.getMetadataKey().getUuid())
                .name("dataset")
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        datasetConfiguration.insert(dataset);
        Pageable pageable = PageRequest.of(0, 2);
        assertThat(
                datasetDtoRepository.fetchAll(null, pageable, new HashSet<>()))
                .containsOnly(datasetDto);
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithNoImplementationsLinkedToDatasetGroupA() {
        Map<String, Object> datasetInfoA = generateDataset(0, 0, 0, 0, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 0, 0, 0, "GROUPB");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoA.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "DATASETS_READ@GROUPB"
    })
    void getAllPaginatedWithNoImplementationsLinkedToDatasetGroupB() {
        Map<String, Object> datasetInfoA = generateDataset(0, 0, 0, 0, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 0, 0, 0, "GROUPB");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoB.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userBA", authorities = {
            "DATASETS_READ@GROUPB",
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithNoImplementationsLinkedToDatasetGroupBA() {
        Map<String, Object> datasetInfoA = generateDataset(0, 0, 0, 0, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 0, 0, 0, "GROUPB");
        Map<String, Object> datasetInfoC = generateDataset(2, 0, 0, 0, "GROUPC");
        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");
        Dataset datasetC = (Dataset) datasetInfoC.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);
        datasetConfiguration.insert(datasetC);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoA.get("datasetDto"), (DatasetDto) datasetInfoB.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithNoImplementationsLinkedToDatasetNoMatchedSecurityGroup() {
        Map<String, Object> datasetInfoA = generateDataset(0, 0, 0, 0, "PUBLIC");
        Map<String, Object> datasetInfoB = generateDataset(1, 0, 0, 0, "PUBLIC");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getAllPaginatedWithNoImplementationsLinkedToDatasetNoSecurityGroup() {
        Map<String, Object> datasetInfoA = generateDataset(0, 0, 0, 0, "PUBLIC");
        Map<String, Object> datasetInfoB = generateDataset(1, 0, 0, 0, "PUBLIC");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).isEmpty();
    }

    @Test
    void getAllPaginatedWithImplementationsLinkedToDataset() {
        Map<String, Object> dataset1Info = generateDataset(0, 2, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        Pageable pageable = PageRequest.of(0, 2);
        assertThat(datasetDtoRepository.fetchAll(null, pageable, new HashSet<>()))
                .containsOnly((DatasetDto) dataset1Info.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithImplementationsLinkedToDatasetGroupA() {
        Map<String, Object> datasetInfoA = generateDataset(0, 2, 2, 2, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 2, 2, 2, "GROUPB");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoA.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "DATASETS_READ@GROUPB"
    })
    void getAllPaginatedWithImplementationsLinkedToDatasetGroupB() {
        Map<String, Object> datasetInfoA = generateDataset(0, 2, 2, 2, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 2, 2, 2, "GROUPB");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoB.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userBA", authorities = {
            "DATASETS_READ@GROUPB",
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithImplementationsLinkedToDatasetGroupBA() {
        Map<String, Object> datasetInfoA = generateDataset(0, 2, 2, 2, "GROUPA");
        Map<String, Object> datasetInfoB = generateDataset(1, 2, 2, 2, "GROUPB");
        Map<String, Object> datasetInfoC = generateDataset(2, 2, 2, 2, "GROUPC");
        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");
        Dataset datasetC = (Dataset) datasetInfoC.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);
        datasetConfiguration.insert(datasetC);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).containsOnly((DatasetDto) datasetInfoA.get("datasetDto"), (DatasetDto) datasetInfoB.get("datasetDto"));
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "DATASETS_READ@GROUPA"
    })
    void getAllPaginatedWithImplementationsLinkedToDatasetNoMatchedSecurityGroups() {
        Map<String, Object> datasetInfoA = generateDataset(0, 2, 2, 2, "PUBLIC");
        Map<String, Object> datasetInfoB = generateDataset(1, 2, 2, 2, "PUBLIC");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getAllPaginatedWithImplementationsLinkedToDatasetNoSecurityGroups() {
        Map<String, Object> datasetInfoA = generateDataset(0, 2, 2, 2, "PUBLIC");
        Map<String, Object> datasetInfoB = generateDataset(1, 2, 2, 2, "PUBLIC");

        Dataset datasetA = (Dataset) datasetInfoA.get("dataset");
        Dataset datasetB = (Dataset) datasetInfoB.get("dataset");

        datasetConfiguration.insert(datasetA);
        datasetConfiguration.insert(datasetB);

        assertThat(datasetDtoRepository.fetchAll(
                SecurityContextHolder.getContext().getAuthentication(),
                PageRequest.of(0, 2),
                new HashSet<>()
        )).isEmpty();
    }

    @Test
    void getDatasetImplementationByUuid() {
        Map<String, Object> dataset1Info = generateDataset(0, 2, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        assertThat(datasetDtoRepository.fetchImplementationByUuid(((DatasetImplementation) dataset1Info.get("datasetImplementation1")).getMetadataKey().getUuid()))
                .hasValue((DatasetImplementationDto) dataset1Info.get("datasetImplementationDto1"));
    }

    @Test
    void getDatasetImplementationByUuidNotFound() {
        Map<String, Object> dataset1Info = generateDataset(0, 2, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        assertThat(datasetDtoRepository.fetchImplementationByUuid(UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void getDatasetImplementationsByDatasetUuid() {
        Map<String, Object> dataset1Info = generateDataset(0, 2, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        assertThat(datasetDtoRepository.fetchImplementationsByDatasetUuid(((Dataset) dataset1Info.get("dataset")).getMetadataKey().getUuid()))
                .containsOnly((DatasetImplementationDto) dataset1Info.get("datasetImplementationDto1"),
                        (DatasetImplementationDto) dataset1Info.get("datasetImplementationDto0"));
    }

    @Test
    void getDatasetImplementationsByDatasetUuidNotExisting() {
        Map<String, Object> dataset1Info = generateDataset(0, 2, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        assertThat(datasetDtoRepository.fetchImplementationsByDatasetUuid(((Dataset) dataset1Info.get("dataset")).getMetadataKey().getUuid()))
                .doesNotContain((DatasetImplementationDto) dataset1Info.get("datasetImplementationDto3"),
                        (DatasetImplementationDto) dataset1Info.get("datasetImplementationDto4"));
    }

    @Test
    void getDatasetImplementationsByDatasetUuidEmpty() {
        Map<String, Object> dataset1Info = generateDataset(0, 0, 2, 2, "PUBLIC");
        Dataset dataset = (Dataset) dataset1Info.get("dataset");
        datasetConfiguration.insert(dataset);

        assertThat(datasetDtoRepository.fetchImplementationsByDatasetUuid(UUID.randomUUID())
                .isEmpty());
    }

    @Test
    void getAllPaginatedNoImplementationsLinkedToDatasetPageOverflow() throws InterruptedException {
        Dataset dataset1 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .name("dataset1")
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .datasetImplementations(new HashSet<>())
                .build();
        Dataset dataset2 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset2")
                .datasetImplementations(new HashSet<>())
                .build();
        Dataset dataset3 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset3")
                .datasetImplementations(new HashSet<>())
                .build();
        datasetConfiguration.insert(dataset1);
        datasetConfiguration.insert(dataset2);
        // sleep needed for the ordering of the datasets based on load timestamps
        sleep(1);
        datasetConfiguration.insert(dataset3);
        DatasetDto datasetDto3 = DatasetDto.builder()
                .uuid(dataset3.getMetadataKey().getUuid())
                .securityGroupName("PUBLIC")
                .name("dataset3")
                .implementations(new HashSet<>())
                .build();
        Pageable pageable = PageRequest.of(1, 2);
        assertThat(
                datasetDtoRepository.fetchAll(null, pageable, new HashSet<>()))
                .containsOnly(datasetDto3);
    }

    @Test
    void getAllFilterByName() throws InterruptedException {
        Dataset dataset1 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset1")
                .datasetImplementations(new HashSet<>())
                .build();
        Dataset dataset2 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset11")
                .datasetImplementations(new HashSet<>())
                .build();
        Dataset dataset3 = Dataset.builder()
                .metadataKey(new DatasetKey(UUID.randomUUID()))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("dataset3")
                .datasetImplementations(new HashSet<>())
                .build();
        datasetConfiguration.insert(dataset1);
        datasetConfiguration.insert(dataset2);
        datasetConfiguration.insert(dataset3);
        DatasetDto datasetDto1 = DatasetDto.builder()
                .uuid(dataset1.getMetadataKey().getUuid())
                .name("dataset1")
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        DatasetDto datasetDto2 = DatasetDto.builder()
                .uuid(dataset2.getMetadataKey().getUuid())
                .name("dataset11")
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        DatasetDto datasetDto3 = DatasetDto.builder()
                .uuid(dataset3.getMetadataKey().getUuid())
                .name("dataset3")
                .securityGroupName("PUBLIC")
                .implementations(new HashSet<>())
                .build();
        assertThat(
                datasetDtoRepository.fetchAll(
                        null,
                        Pageable.unpaged(),
                        Stream.of(
                                new DatasetFilter(DatasetFilterOption.NAME, "dataset", false)
                        ).collect(Collectors.toSet())))
                .containsOnly(datasetDto1, datasetDto2, datasetDto3);
        assertThat(
                datasetDtoRepository.fetchAll(
                        null,
                        Pageable.unpaged(),
                        Stream.of(
                                new DatasetFilter(DatasetFilterOption.NAME, "dataset1", false)
                        ).collect(Collectors.toSet())))
                .containsOnly(datasetDto1, datasetDto2);
        assertThat(
                datasetDtoRepository.fetchAll(
                        null,
                        Pageable.unpaged(),
                        Stream.of(
                                new DatasetFilter(DatasetFilterOption.NAME, "dataset3", false)
                        ).collect(Collectors.toSet())))
                .containsOnly(datasetDto3);
        assertThat(
                datasetDtoRepository.fetchAll(
                        null,
                        Pageable.unpaged(),
                        Stream.of(
                                new DatasetFilter(DatasetFilterOption.NAME, "dataset4", false)
                        ).collect(Collectors.toSet())))
                .isEmpty();
    }

    private Map<String, Object> generateDataset(int datasetIndex, int implementationCount, int labelCount, int keyValueCount, String securityGroupName) {
        Map<String, Object> info = new HashMap<>();

        UUID datasetUUID = UUID.randomUUID();
        info.put("datasetUUID", datasetUUID);
        Dataset dataset = Dataset.builder()
                .metadataKey(new DatasetKey(datasetUUID))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName(securityGroupName)
                .name(String.format("dataset%d", datasetIndex))
                .datasetImplementations(
                        IntStream.range(0, implementationCount).boxed()
                                .map(implementationIndex -> {
                                    UUID datasetImplementationUUID = UUID.randomUUID();
                                    info.put(String.format("datasetImplementation%dUUID", implementationIndex), datasetImplementationUUID);
                                    DatasetImplementation datasetImplementation = DatabaseDatasetImplementation.builder()
                                            .metadataKey(new DatasetImplementationKey(datasetImplementationUUID))
                                            .datasetKey(new DatasetKey(datasetUUID))
                                            .name(String.format("dataset%d", datasetIndex))
                                            .datasetImplementationLabels(
                                                    IntStream.range(0, labelCount).boxed()
                                                            .map(labelIndex -> {
                                                                        UUID datasetImplementationLabelUUID = UUID.randomUUID();
                                                                        info.put(String.format("datasetImplementation%dLabel%dUUID", implementationIndex, labelIndex), datasetImplementationLabelUUID);
                                                                        return DatasetImplementationLabel.builder()
                                                                                .metadataKey(new DatasetImplementationLabelKey(datasetImplementationLabelUUID))
                                                                                .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                                .value(String.format("label%d%d%d", datasetIndex, implementationIndex, labelIndex))
                                                                                .build();
                                                                    }
                                                            ).collect(Collectors.toSet()))
                                            .keyValues(
                                                    IntStream.range(0, keyValueCount).boxed()
                                                            .map(keyValueIndex -> {
                                                                UUID datasetImplementationKeyValueUUID = UUID.randomUUID();
                                                                info.put(String.format("datasetImplementation%dKeyValue%dUUID", implementationIndex, keyValueIndex), datasetImplementationKeyValueUUID);

                                                                return DatabaseDatasetImplementationKeyValue.builder()
                                                                        .metadataKey(new DatabaseDatasetImplementationKeyValueKey(datasetImplementationKeyValueUUID))
                                                                        .datasetImplementationKey(new DatasetImplementationKey(datasetImplementationUUID))
                                                                        .key(String.format("key%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .value(String.format("value%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .build();
                                                            }).collect(Collectors.toSet())
                                            )
                                            .build();
                                    info.put(String.format("datasetImplementation%d", implementationIndex), datasetImplementation);
                                    return datasetImplementation;

                                })
                                .collect(Collectors.toSet()))
                .build();
        info.put("dataset", dataset);
        DatasetDto datasetDto = DatasetDto.builder()
                .uuid(datasetUUID)
                .name(String.format("dataset%d", datasetIndex))
                .securityGroupName(securityGroupName)
                .implementations(
                        IntStream.range(0, implementationCount).boxed()
                                .map(implementationIndex -> {
                                    UUID datasetImplementationUUID = (UUID) info.get(String.format("datasetImplementation%dUUID", implementationIndex));
                                    DatabaseDatasetImplementationDto databaseDatasetImplementationDto = DatabaseDatasetImplementationDto.builder()
                                            .uuid(datasetImplementationUUID)
                                            .labels(
                                                    IntStream.range(0, labelCount).boxed()
                                                            .map(labelIndex -> {
                                                                        UUID datasetImplementationLabelUUID = (UUID) info.get(String.format("datasetImplementation%dLabel%dUUID", implementationIndex, labelIndex));
                                                                        return DatasetImplementationLabelDto.builder()
                                                                                .uuid(datasetImplementationLabelUUID)
                                                                                .label(String.format("label%d%d%d", datasetIndex, implementationIndex, labelIndex))
                                                                                .build();
                                                                    }
                                                            ).collect(Collectors.toSet()))
                                            .keyValues(
                                                    IntStream.range(0, keyValueCount).boxed()
                                                            .map(keyValueIndex -> {
                                                                UUID datasetImplementationKeyValueUUID = (UUID) info.get(String.format("datasetImplementation%dKeyValue%dUUID", implementationIndex, keyValueIndex));
                                                                return DatabaseDatasetImplementationKeyValueDto.builder()
                                                                        .uuid(datasetImplementationKeyValueUUID)
                                                                        .key(String.format("key%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .value(String.format("value%d%d%d", datasetIndex, implementationIndex, keyValueIndex))
                                                                        .build();
                                                            }).collect(Collectors.toSet())
                                            )
                                            .build();
                                    info.put(String.format("datasetImplementationDto%d", implementationIndex), databaseDatasetImplementationDto);
                                    return databaseDatasetImplementationDto;
                                })
                                .map(DatasetImplementationDto::getUuid)
                                .collect(Collectors.toSet()))
                .build();
        info.put("datasetDto", datasetDto);
        return info;
    }

}
