package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, DatasetConfiguration.class, DatasetImplementationConfiguration.class })
@ActiveProfiles("test")
class DatasetImplementationConfigurationTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    DatasetImplementationConfiguration datasetImplementationConfiguration;

    @Autowired
    DatasetConfiguration datasetConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void exists() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        ));

        assertThat(datasetConfiguration.exists(datasetKey))
                .isTrue();
    }

    @Test
    void existsNotFound() {
        datasetConfiguration.insert(new Dataset(
                new DatasetKey(UUID.randomUUID()),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        ));

        assertThat(datasetConfiguration.exists(new DatasetKey(UUID.randomUUID())))
                .isFalse();
    }

    @Test
    void existsByName() {
        datasetConfiguration.insert(new Dataset(
                new DatasetKey(UUID.randomUUID()),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        ));

        assertThat(datasetConfiguration.existsByName("dataset"))
                .isTrue();
    }

    @Test
    void existsByNameNotFound() {
        datasetConfiguration.insert(new Dataset(
                new DatasetKey(UUID.randomUUID()),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        ));

        assertThat(datasetConfiguration.existsByName("dataset1"))
                .isFalse();
    }

    @Test
    void getById() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementation> datasetImplementations = Stream.of(new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                new HashSet<>(),
                new HashSet<>())).collect(Collectors.toSet());

        Dataset dataset = new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                datasetImplementations
        );

        datasetConfiguration.insert(dataset);

        assertThat(datasetConfiguration.get(datasetKey))
                .hasValue(dataset);
    }

    @Test
    void getByIdNoImplementations() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        Dataset dataset = new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        );

        datasetConfiguration.insert(dataset);

        assertThat(datasetConfiguration.get(datasetKey))
                .hasValue(dataset);
    }

    @Test
    void getByIdNoLabels() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        Dataset dataset = new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        );

        datasetConfiguration.insert(dataset);

        assertThat(datasetConfiguration.get(datasetKey))
                .hasValue(dataset);
    }

    @Test
    void getByIdNoKeyValues() {

    }

    @Test
    void existsByDatasetImplementationKey() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementation> datasetImplementations = Stream.of(new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                new HashSet<>(),
                new HashSet<>())).collect(Collectors.toSet());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                datasetImplementations
        ));

        assertThat(datasetImplementationConfiguration.exists(datasetImplementationKey))
                .isTrue();

    }

    @Test
    void existsByDatasetImplementationKeyNotFound() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementation> datasetImplementations = Stream.of(new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                new HashSet<>(),
                new HashSet<>())).collect(Collectors.toSet());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                datasetImplementations
        ));

        assertThat(datasetImplementationConfiguration.exists(new DatasetImplementationKey(UUID.randomUUID())))
                .isFalse();

    }

    @Test
    void existsByDatasetNameAndImplementationLabels() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementationLabel> datasetImplementationLabels = Stream.of(new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(UUID.randomUUID()),
                datasetImplementationKey,
                "label1")).collect(Collectors.toSet());

        Set<DatasetImplementation> datasetImplementations = Stream.of(new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                datasetImplementationLabels,
                new HashSet<>())).collect(Collectors.toSet());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                datasetImplementations
        ));

        assertThat(datasetImplementationConfiguration.exists("dataset", Collections.singletonList("label1")))
                .isTrue();
    }
}
