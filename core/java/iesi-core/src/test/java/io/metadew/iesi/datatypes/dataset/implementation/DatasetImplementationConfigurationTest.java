package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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

        assertThat(datasetImplementationConfiguration.exists("dataset", singletonList("label1")))
                .isTrue();
    }

    @Test
    void existsByDatasetNameNotFoundAndImplementationLabels() {
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementationLabel> datasetImplementationLabels = Stream.of(new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(UUID.randomUUID()),
                datasetImplementationKey,
                "label1")).collect(Collectors.toSet());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                datasetImplementationLabels,
                new HashSet<>());

        datasetImplementationConfiguration.insert(datasetImplementation);

        assertThat(datasetImplementationConfiguration.exists("dataset", singletonList("label1")))
                .isFalse();

    }
    @Test
    void existsByDatasetNameAndImplementationLabelsNotFound() {
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

        assertThat(datasetImplementationConfiguration.exists("dataset", singletonList("label1")))
                .isFalse();
    }

    @Test
    void isEmpty() {
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                new HashSet<>(),
                new HashSet<>());

        datasetImplementationConfiguration.insert(datasetImplementation);

        assertThat(datasetImplementationConfiguration.isEmpty(datasetImplementationKey))
                .isTrue();
    }

    @Test
    void isNotEmpty() {
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatabaseDatasetImplementationKeyValue> databaseDatasetImplementationKeyValues = Stream.of(new DatabaseDatasetImplementationKeyValue(
                new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                datasetImplementationKey,
                "key",
                "value"
        )).collect(Collectors.toSet());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                new HashSet<>(),
                databaseDatasetImplementationKeyValues);

        datasetImplementationConfiguration.insert(datasetImplementation);

        assertThat(datasetImplementationConfiguration.isEmpty(datasetImplementationKey))
                .isFalse();
    }

    @Test
    void get() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                new DatasetKey(UUID.randomUUID()),
                "dataset",
                new HashSet<>(),
                new HashSet<>());

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

        assertThat(datasetImplementationConfiguration.get(datasetImplementationKey))
                .hasValue(datasetImplementation);
    }
    @Test
    void getNotFound() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());


        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                new HashSet<>()
        ));

        assertThat(datasetImplementationConfiguration.get(datasetImplementationKey))
                .isEmpty();
    }

    @Test
    void getByNameAndLabels() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementationLabel> datasetImplementationLabels = Stream.of(new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(UUID.randomUUID()),
                datasetImplementationKey,
                "label1")).collect(Collectors.toSet());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                datasetImplementationLabels,
                new HashSet<>());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                Stream.of(datasetImplementation).collect(Collectors.toSet())
        ));

        assertThat(datasetImplementationConfiguration.getByNameAndLabels("dataset", singletonList("label1")))
                .hasValue(datasetImplementation);
    }

    @Test
    void getByNameAndLabelsNotFound() {
        DatasetKey datasetKey = new DatasetKey(UUID.randomUUID());
        DatasetImplementationKey datasetImplementationKey = new DatasetImplementationKey(UUID.randomUUID());

        Set<DatasetImplementationLabel> datasetImplementationLabels = Stream.of(new DatasetImplementationLabel(
                new DatasetImplementationLabelKey(UUID.randomUUID()),
                datasetImplementationKey,
                "label1")).collect(Collectors.toSet());

        DatasetImplementation datasetImplementation = new DatabaseDatasetImplementation(
                datasetImplementationKey,
                datasetKey,
                "dataset",
                datasetImplementationLabels,
                new HashSet<>());

        datasetConfiguration.insert(new Dataset(
                datasetKey,
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "dataset",
                Stream.of(datasetImplementation).collect(Collectors.toSet())
        ));

        assertThat(datasetImplementationConfiguration.getByNameAndLabels("dataset", singletonList("label2")))
                .isEmpty();
    }

}
