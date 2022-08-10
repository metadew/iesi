package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DatasetServiceTest {

    SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());

    @BeforeEach
    void prepare() {
        // Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance().getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);

        SecurityGroupConfiguration.getInstance().insert(new SecurityGroup(securityGroupKey, "PUBLIC", new HashSet<>(), new HashSet<>()));
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        // Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @Test
    void importDatasetsTestJson() {
        try {
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

            List<Dataset> datasets = DatasetService.getInstance().importDatasets(jsonContent);
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