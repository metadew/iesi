package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, SecurityGroupService.class, SecurityGroupConfiguration.class, DatasetService.class, DatasetConfiguration.class, DatasetImplementationConfiguration.class })
@ActiveProfiles("test")
class DatasetServiceTest {

    SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    SecurityGroupConfiguration securityGroupConfiguration;

    @Autowired
    DatasetService datasetService;

    @BeforeEach
    void beforeEach() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void afterEach() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void importDatasetsTestJson() {
        try {
            securityGroupConfiguration.insert(new SecurityGroup(
                    new SecurityGroupKey(UUID.randomUUID()),
                    "PUBLIC",
                    new HashSet<>(),
                    new HashSet<>()
            ));
            String filePath = getClass().getClassLoader().getResource("io.metadew.iesi.datatypes.dataset/dataset_single.json").getFile();
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            DatasetKey datasetKey = new DatasetKey();
            DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey();
            Dataset expectedDataset = null;
            Dataset dataset = new Dataset(
                    datasetKey,
                    securityGroupKey,
                    "PUBLIC",
                    "CreateEnvironment",
                    Stream.of(new DatabaseDatasetImplementation(
                            datasetImplementationKey,
                            datasetKey,
                            "CreateEnvironment",
                            Stream.of(
                                    new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, "input"),
                                    new DatasetImplementationLabel(new DatasetImplementationLabelKey(), datasetImplementationKey, "tst")
                            ).collect(Collectors.toSet()),
                            Stream.of(
                                    new DatabaseDatasetImplementationKeyValue(new DatabaseDatasetImplementationKeyValueKey(), datasetImplementationKey, "protocol.version.major", "1"),
                                    new DatabaseDatasetImplementationKeyValue(new DatabaseDatasetImplementationKeyValueKey(), datasetImplementationKey, "status.code", "200"),
                                    new DatabaseDatasetImplementationKeyValue(new DatabaseDatasetImplementationKeyValueKey(), datasetImplementationKey, "protocol", "http")
                            ).collect(Collectors.toSet())
                    )).collect(Collectors.toSet())
            );

            List<Dataset> datasets = datasetService.importDatasets(jsonContent);
            List<Dataset> expectedDatasets = Stream.of(dataset).collect(Collectors.toList());

            expectedDataset = expectedDatasets.stream().findFirst().get();

            assertThat(datasets).isNotEmpty();
            assertThat(dataset.getName()).isEqualTo(expectedDataset.getName());
            assertThat(dataset.getDatasetImplementations()).isEqualTo(expectedDataset.getDatasetImplementations());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}