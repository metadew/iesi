package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class DatasetImplementationConfiguration extends Configuration<DatasetImplementation, DatasetImplementationKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    private String existsQuery() {
        return "select dataset_impls.ID as dataset_impl_id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "WHERE dataset_impls.ID={0}";
    }

    private String existsByNameAndLabelsQuery() {
        return "select dataset_impls.ID as dataset_impl_id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "where datasets.NAME={0} and dataset_impls.ID in ({1});";
    }

    private String selectQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID;";
    }

    private String isEmptyQuery() {
        return "SELECT " +
                "count(*) as key_value_pairs " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "WHERE dataset_in_mem_impl_kvs.IMPL_MEM_ID={0};";
    }

    private String selectSingleQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where dataset_impls.ID={0};";
    }

    private String selectByDatasetIdQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where datasets.ID={0};";
    }


    private String selectByNameAndLabelsQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where datasets.NAME={0} and dataset_impls.ID in ({1});";
    }

    private String selectByDatasetIdAndLabelsQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "inner join " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where datasets.ID={0} and dataset_impls.ID in ({1});";
    }


    private String getByLabelSetValueSubQuery() {
        return "SELECT " +
                "dataset_impl_labels.DATASET_IMPL_ID " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "WHERE dataset_impl_labels.VALUE = {0}";
    }

    private String getByLabelSetCountSubQuery() {
        return "SELECT " +
                "dataset_impl_labels.DATASET_IMPL_ID " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "GROUP BY dataset_impl_labels.DATASET_IMPL_ID " +
                "HAVING COUNT(DISTINCT dataset_impl_labels.VALUE) = {0}";
    }

    private String insertQuery() {
        return "insert into " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() +
                " (ID, DATASET_ID) " +
                "VALUES ({0}, {1});";
    }

    private String insertDatasetImplementationLabelQuery() {
        return "insert into " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
                " (ID, DATASET_IMPL_ID, VALUE) " +
                "VALUES ({0}, {1}, {2});";
    }

    private String insertInMemoryDatasetImplementationQuery() {
        return "insert into " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
                " (ID) " +
                "VALUES ({0});";
    }

    private String insertInMemorydatasetImplementationKeyValueQuery() {
        return "insert into " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " (ID, IMPL_MEM_ID, KEY, VALUE) " +
                "VALUES ({0}, {1}, {2}, {3});";
    }

    private String deleteQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() +
                " WHERE ID={0};";
    }

    private String deleteDatasetImplementationLabelBydatasetImplementationIdQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
                " WHERE DATASET_IMPL_ID={0};";
    }

    private String deleteInMemoryDatasetImplementationByDatasetImplementationIdQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
                " WHERE ID={0};";
    }

    private String deleteInMemoryDatasetImplementationKeyValuesByDatasetImplementationIdQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " WHERE IMPL_MEM_ID={0};";
    }



    public DatasetImplementationConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }


    @PostConstruct
    public void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDataMetadataRepository());
    }

    @Override
    public boolean exists(DatasetImplementationKey datasetImplementationKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsQuery(), SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String name, List<String> labels) {
        try {
            String labelSetQuery = labels.stream()
                    .map(s -> MessageFormat.format(getByLabelSetValueSubQuery(), SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(getByLabelSetCountSubQuery(), SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsByNameAndLabelsQuery(),
                            SQLTools.getStringForSQL(name),
                            labelSetQuery),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isEmpty(DatasetImplementationKey datasetImplementationKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(
                            isEmptyQuery(),
                            SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            cachedRowSet.next();
            return Integer.parseInt(cachedRowSet.getString("key_value_pairs")) == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey) {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectSingleQuery(), SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetImplementationBuilderMap);
            }
            return datasetImplementationBuilderMap.values().stream()
                    .findFirst()
                    .map(DatasetImplementationBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<DatasetImplementation> getByNameAndLabels(String name, List<String> labels) {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            String labelSetQuery = labels.stream()
                    .map(s -> MessageFormat.format(getByLabelSetValueSubQuery(), SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(getByLabelSetCountSubQuery(), SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectByNameAndLabelsQuery(),
                            SQLTools.getStringForSQL(name), labelSetQuery),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetImplementationBuilderMap);
            }
            return datasetImplementationBuilderMap.values().stream()
                    .findFirst()
                    .map(DatasetImplementationBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<DatasetImplementation> getByDatasetIdAndLabels(DatasetKey datasetKey, List<String> labels) {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            String labelSetQuery = labels.stream()
                    .map(s -> MessageFormat.format(getByLabelSetValueSubQuery(), SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(getByLabelSetCountSubQuery(), SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(
                            selectByDatasetIdAndLabelsQuery(),
                            SQLTools.getStringForSQL(datasetKey.getUuid()),
                            labelSetQuery),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetImplementationBuilderMap);
            }
            return datasetImplementationBuilderMap.values().stream()
                    .findFirst()
                    .map(DatasetImplementationBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DatasetImplementation> getByDatasetId(DatasetKey datasetKey) {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectByDatasetIdQuery(),
                            SQLTools.getStringForSQL(datasetKey.getUuid())),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetImplementationBuilderMap);
            }
            return datasetImplementationBuilderMap.values().stream()
                    .map(DatasetImplementationBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DatasetImplementation> getAll() {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(selectQuery(), "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetImplementationBuilderMap);
            }
            return datasetImplementationBuilderMap.values().stream()
                    .map(DatasetImplementationBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DatasetImplementationKey metadataKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery(),
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteDatasetImplementationLabelBydatasetImplementationIdQuery(),
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteInMemoryDatasetImplementationByDatasetImplementationIdQuery(),
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteInMemoryDatasetImplementationKeyValuesByDatasetImplementationIdQuery(),
                SQLTools.getStringForSQL(metadataKey.getUuid())));
    }

    public void deleteByDatasetId(DatasetKey datasetKey) {
        getByDatasetId(datasetKey)
                .forEach(datasetImplementation -> delete(datasetImplementation.getMetadataKey()));
    }

    @Override
    public void insert(DatasetImplementation metadata) {
        if (metadata instanceof DatabaseDatasetImplementation) {
            getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery(),
                    SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                    SQLTools.getStringForSQL(metadata.getDatasetKey().getUuid())));
            getMetadataRepository().executeUpdate(MessageFormat.format(insertInMemoryDatasetImplementationQuery(), SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid())));
            metadata.getDatasetImplementationLabels().forEach(datasetImplementationLabel ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(insertDatasetImplementationLabelQuery(),
                            SQLTools.getStringForSQL(datasetImplementationLabel.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(datasetImplementationLabel.getDatasetImplementationKey().getUuid()),
                            SQLTools.getStringForSQL(datasetImplementationLabel.getValue()))));
            ((DatabaseDatasetImplementation) metadata).getKeyValues().forEach(inMemoryDatasetImplementationKeyValue ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(insertInMemorydatasetImplementationKeyValueQuery(),
                            SQLTools.getStringForSQL(inMemoryDatasetImplementationKeyValue.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(inMemoryDatasetImplementationKeyValue.getDatasetImplementationKey().getUuid()),
                            SQLTools.getStringForSQL(inMemoryDatasetImplementationKeyValue.getKey()),
                            SQLTools.getStringForSQLClob(inMemoryDatasetImplementationKeyValue.getValue(),
                                    getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                            .findFirst()
                                            .orElseThrow(RuntimeException::new)
                            ))));
        } else {
            throw new RuntimeException("Cannot insert dataset implementation of type " + metadata.getClass().getSimpleName());
        }
    }

    public void mapRow(CachedRowSet cachedRowSet, Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap) throws SQLException {
        String datasetImplementationId = cachedRowSet.getString("dataset_impl_id");
        if (datasetImplementationId != null) {
            DatasetImplementationBuilder datasetImplementationBuilder = datasetImplementationBuilderMap.get(datasetImplementationId);
            if (datasetImplementationBuilder == null) {
                datasetImplementationBuilder = extractDatasetImplementationBuilderMapRow(cachedRowSet);
                datasetImplementationBuilderMap.put(datasetImplementationId, datasetImplementationBuilder);
            }
            String type = mapType(cachedRowSet);
            if (type.equalsIgnoreCase("database")) {
                mapInMemoryDatasetImplementation(cachedRowSet, (DatabaseDatasetImplementationBuilder) datasetImplementationBuilder);
            } else {
                log.warn("no type found for dataset implementation");
            }
            mapDatasetImplementationLabel(cachedRowSet, datasetImplementationBuilder);
        }
    }

    private void mapInMemoryDatasetImplementation(CachedRowSet cachedRowSet, DatabaseDatasetImplementationBuilder databaseDatasetImplementationBuilder) throws SQLException {
        String inMemoryKeyValueId = cachedRowSet.getString("dataset_in_mem_impl_kv_id");
        if (inMemoryKeyValueId != null && databaseDatasetImplementationBuilder.getKeyValues().get(inMemoryKeyValueId) == null) {
            String key = cachedRowSet.getString("dataset_in_mem_impl_kvs_key");
            String clobValue = SQLTools.getStringFromSQLClob(cachedRowSet, "dataset_in_mem_impl_kvs_value");

            databaseDatasetImplementationBuilder.getKeyValues().put(inMemoryKeyValueId,
                    new DatabaseDatasetImplementationKeyValue(
                            new DatabaseDatasetImplementationKeyValueKey(UUID.fromString(inMemoryKeyValueId)),
                            new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_in_mem_impl_kv_impl_id"))),
                            key,
                            clobValue)
            );
        }
    }

    private DatasetImplementationBuilder extractDatasetImplementationBuilderMapRow(CachedRowSet cachedRowSet) throws SQLException {
        // "dataset_impls.ID as dataset_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
        String type = mapType(cachedRowSet);
        if (type.equalsIgnoreCase("database")) {
            return extractInMemoryDatasetImplementation(cachedRowSet);
        } else {
            throw new RuntimeException("cannot create dataset implementation for type " + type);
        }
    }

    private String mapType(CachedRowSet cachedRowSet) throws SQLException {
        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
        if (cachedRowSet.getString("dataset_in_mem_impl_id") != null) {
            return "database";
        } else {
            throw new RuntimeException("cannot determine the type of dataset_implementation");
        }
    }

    private void mapDatasetImplementationLabel(CachedRowSet cachedRowSet, DatasetImplementationBuilder datasetImplementationBuilder) throws SQLException {
        // "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.VALUE as dataset_impl_label_value, "
        String datasetImplementationLabelId = cachedRowSet.getString("dataset_impl_label_id");
        if (datasetImplementationLabelId != null && datasetImplementationBuilder.getDatasetImplementationLabels().get(datasetImplementationLabelId) == null) {
            datasetImplementationBuilder.getDatasetImplementationLabels().put(
                    datasetImplementationLabelId,
                    new DatasetImplementationLabel(
                            new DatasetImplementationLabelKey(UUID.fromString(datasetImplementationLabelId)),
                            new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_impl_label_impl_id"))),
                            cachedRowSet.getString("dataset_impl_label_value"))
            );
        }
    }

    private DatasetImplementationBuilder extractInMemoryDatasetImplementation(CachedRowSet cachedRowSet) throws SQLException {
        // "dataset_impls.ID as dataset_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
        // "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
        return new DatabaseDatasetImplementationBuilder(
                new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_impl_id"))),
                new DatasetKey(UUID.fromString(cachedRowSet.getString("dataset_id"))),
                cachedRowSet.getString("dataset_name"),
                new HashMap<>(),
                new HashMap<>()
        );
    }


    @AllArgsConstructor
    @Getter
    @ToString
    public abstract class DatasetImplementationBuilder {
        private DatasetImplementationKey datasetImplementationKey;
        private DatasetKey datasetKey;
        private String name;
        public Map<String, DatasetImplementationLabel> datasetImplementationLabels;

        public abstract DatasetImplementation build();

    }


    @Getter
    @ToString(callSuper = true)
    private class DatabaseDatasetImplementationBuilder extends DatasetImplementationBuilder {

        private Map<String, DatabaseDatasetImplementationKeyValue> keyValues;

        public DatabaseDatasetImplementationBuilder(DatasetImplementationKey datasetImplementationKey, DatasetKey datasetKey, String name, Map<String, DatasetImplementationLabel> executionRequestLabels, Map<String, DatabaseDatasetImplementationKeyValue> keyValues) {
            super(datasetImplementationKey, datasetKey, name, executionRequestLabels);
            this.keyValues = keyValues;
        }

        @Override
        public DatasetImplementation build() {
            return new DatabaseDatasetImplementation(
                    getDatasetImplementationKey(),
                    getDatasetKey(),
                    getName(),
                    new HashSet<>(getDatasetImplementationLabels().values()),
                    new HashSet<>(getKeyValues().values()));
        }
    }
}
