package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DatasetImplementationConfiguration extends Configuration<DatasetImplementation, DatasetImplementationKey> {

    private static final String EXISTS_QUERY = "select dataset_impls.ID as dataset_impl_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "WHERE dataset_impls.ID={0}";

    private static final String EXISTS_BY_NAME_AND_LABELS_QUERY = "select dataset_impls.ID as dataset_impl_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "where datasets.NAME={0} and dataset_impls.ID in ({1});";

    private static final String SELECT_QUERY = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
            "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
            "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
            "on dataset_impls.ID = dataset_in_mem_impls.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID;";

    private static final String IS_EMPTY_QUERY = "SELECT " +
            "count(*) as key_value_pairs " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "WHERE dataset_in_mem_impl_kvs.IMPL_MEM_ID={0};";

    private static final String SELECT_SINGLE_QUERY = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
            "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
            "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
            "on dataset_impls.ID = dataset_in_mem_impls.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
            "where dataset_impls.ID={0};";

    private static final String SELECT_BY_DATASET_ID_QUERY = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
            "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
            "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
            "on dataset_impls.ID = dataset_in_mem_impls.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
            "where datasets.ID={0};";

    private static final String SELECT_BY_NAME_AND_LABELS_QUERY = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
            "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
            "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
            "on dataset_impls.ID = dataset_in_mem_impls.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
            "where datasets.NAME={0} and dataset_impls.ID in ({1});";

    private static final String SELECT_BY_DATASET_ID_AND_LABELS_QUERY = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.ID as dataset_id, datasets.NAME as dataset_name, " +
            "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
            "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
            "on dataset_impls.ID = dataset_in_mem_impls.ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
            "left outer join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
            "where datasets.ID={0} and dataset_impls.ID in ({1});";

    private static final String GET_BY_LABEL_SET_VALUE_SUB_QUERY = "SELECT " +
            "dataset_impl_labels.DATASET_IMPL_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "WHERE dataset_impl_labels.VALUE = {0}";

    private static final String GET_BY_LABEL_SET_COUNT_SUB_QUERY = "SELECT " +
            "dataset_impl_labels.DATASET_IMPL_ID " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            "GROUP BY dataset_impl_labels.DATASET_IMPL_ID " +
            "HAVING COUNT(DISTINCT dataset_impl_labels.VALUE) = {0}";

    private static final String INSERT_QUERY = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() +
            " (ID, DATASET_ID) " +
            "VALUES ({0}, {1});";

    private static final String INSERT_DATASET_IMPLEMENTATION_LABEL_QUERY = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
            " (ID, DATASET_IMPL_ID, VALUE) " +
            "VALUES ({0}, {1}, {2});";

    private static final String INSERT_IN_MEMORY_DATASET_IMPLEMENTATION_QUERY = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
            " (ID) " +
            "VALUES ({0});";

    private static final String INSERT_IN_MEMORY_DATASET_IMPLEMENTATION_KEY_VALUE_QUERY = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " (ID, IMPL_MEM_ID, KEY, VALUE) " +
            "VALUES ({0}, {1}, {2}, {3});";

    private static final String DELETE_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() +
            " WHERE ID={0};";

    private static final String DELETE_DATASET_IMPLEMENTATION_LABEL_BY_DATASET_IMPLEMENTATION_ID_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
            " WHERE DATASET_IMPL_ID={0};";

    private static final String DELETE_IN_MEMORY_DATASET_IMPLEMENTATION_BY_DATASET_IMPLEMENTATION_ID_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
            " WHERE ID={0};";

    private static final String DELETE_IN_MEMORY_DATASET_IMPLEMENTATION_KEY_VALUES_BY_DATASET_IMPLEMENTATION_ID_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " WHERE IMPL_MEM_ID={0};";

    private static DatasetImplementationConfiguration INSTANCE;

    public static synchronized DatasetImplementationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasetImplementationConfiguration();
        }
        return INSTANCE;
    }

    private DatasetImplementationConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDataMetadataRepository());
    }

    @Override
    public boolean exists(DatasetImplementationKey datasetImplementationKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(EXISTS_QUERY, SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String name, List<String> labels) {
        try {
            String labelSetQuery = labels.stream()
                    .map(s -> MessageFormat.format(GET_BY_LABEL_SET_VALUE_SUB_QUERY, SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(GET_BY_LABEL_SET_COUNT_SUB_QUERY, SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(EXISTS_BY_NAME_AND_LABELS_QUERY,
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
                            IS_EMPTY_QUERY,
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
                    MessageFormat.format(SELECT_SINGLE_QUERY, SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
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
                    .map(s -> MessageFormat.format(GET_BY_LABEL_SET_VALUE_SUB_QUERY, SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(GET_BY_LABEL_SET_COUNT_SUB_QUERY, SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(SELECT_BY_NAME_AND_LABELS_QUERY,
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
                    .map(s -> MessageFormat.format(GET_BY_LABEL_SET_VALUE_SUB_QUERY, SQLTools.getStringForSQL(s)))
                    .collect(Collectors.joining(" intersect "));
            labelSetQuery = labelSetQuery + " intersect " + MessageFormat.format(GET_BY_LABEL_SET_COUNT_SUB_QUERY, SQLTools.getStringForSQL(labels.size()));

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(
                            SELECT_BY_DATASET_ID_AND_LABELS_QUERY,
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
                    MessageFormat.format(SELECT_BY_DATASET_ID_QUERY,
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(SELECT_QUERY, "reader");
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
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_QUERY,
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_DATASET_IMPLEMENTATION_LABEL_BY_DATASET_IMPLEMENTATION_ID_QUERY,
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_IN_MEMORY_DATASET_IMPLEMENTATION_BY_DATASET_IMPLEMENTATION_ID_QUERY,
                SQLTools.getStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_IN_MEMORY_DATASET_IMPLEMENTATION_KEY_VALUES_BY_DATASET_IMPLEMENTATION_ID_QUERY,
                SQLTools.getStringForSQL(metadataKey.getUuid())));
    }

    public void deleteByDatasetId(DatasetKey datasetKey) {
        getByDatasetId(datasetKey)
                .forEach(datasetImplementation -> delete(datasetImplementation.getMetadataKey()));
    }

    @Override
    public void insert(DatasetImplementation metadata) {
        if (metadata instanceof InMemoryDatasetImplementation) {
            getMetadataRepository().executeUpdate(MessageFormat.format(INSERT_QUERY,
                    SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                    SQLTools.getStringForSQL(metadata.getDatasetKey().getUuid())));
            getMetadataRepository().executeUpdate(MessageFormat.format(INSERT_IN_MEMORY_DATASET_IMPLEMENTATION_QUERY, SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid())));
            metadata.getDatasetImplementationLabels().forEach(datasetImplementationLabel ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(INSERT_DATASET_IMPLEMENTATION_LABEL_QUERY,
                            SQLTools.getStringForSQL(datasetImplementationLabel.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(datasetImplementationLabel.getDatasetImplementationKey().getUuid()),
                            SQLTools.getStringForSQL(datasetImplementationLabel.getValue()))));
            ((InMemoryDatasetImplementation) metadata).getKeyValues().forEach(inMemoryDatasetImplementationKeyValue ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(INSERT_IN_MEMORY_DATASET_IMPLEMENTATION_KEY_VALUE_QUERY,
                            SQLTools.getStringForSQL(inMemoryDatasetImplementationKeyValue.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(inMemoryDatasetImplementationKeyValue.getDatasetImplementationKey().getUuid()),
                            SQLTools.getStringForSQLClob(inMemoryDatasetImplementationKeyValue.getKey(),
                                    getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                            .findFirst()
                                            .orElseThrow(RuntimeException::new)
                            ),
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
            if (type.equalsIgnoreCase("in_memory")) {
                mapInMemoryDatasetImplementation(cachedRowSet, (InMemoryDatasetImplementationBuilder) datasetImplementationBuilder);
            } else {
                log.warn("no type found for dataset implementation");
            }
            mapDatasetImplementationLabel(cachedRowSet, datasetImplementationBuilder);
        }
    }

    private void mapInMemoryDatasetImplementation(CachedRowSet cachedRowSet, InMemoryDatasetImplementationBuilder inMemoryDatasetImplementationBuilder) throws SQLException {
        String inMemoryKeyValueId = cachedRowSet.getString("dataset_in_mem_impl_kv_id");
        if (inMemoryKeyValueId != null && inMemoryDatasetImplementationBuilder.getKeyValues().get(inMemoryKeyValueId) == null) {
            String key = SQLTools.getStringForSQL("dataset_in_mem_impl_kvs_key");
            String clobValue = SQLTools.getStringFromSQLClob(cachedRowSet, "dataset_in_mem_impl_kvs_value");

            inMemoryDatasetImplementationBuilder.getKeyValues().put(inMemoryKeyValueId,
                    new InMemoryDatasetImplementationKeyValue(
                            new InMemoryDatasetImplementationKeyValueKey(UUID.fromString(inMemoryKeyValueId)),
                            new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_in_mem_impl_kv_impl_id"))),
                            key,
                            clobValue)
            );
        }
    }

    private DatasetImplementationBuilder extractDatasetImplementationBuilderMapRow(CachedRowSet cachedRowSet) throws SQLException {
        // "dataset_impls.ID as dataset_impl_id, dataset_impls.DATASET_ID as dataset_impl_dataset_id, " +
        String type = mapType(cachedRowSet);
        if (type.equalsIgnoreCase("in_memory")) {
            return extractInMemoryDatasetImplementation(cachedRowSet);
        } else {
            throw new RuntimeException("cannot create dataset implementation for type " + type);
        }
    }

    private String mapType(CachedRowSet cachedRowSet) throws SQLException {
        // "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
        if (cachedRowSet.getString("dataset_in_mem_impl_id") != null) {
            return "in_memory";
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
        return new InMemoryDatasetImplementationBuilder(
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
    private class InMemoryDatasetImplementationBuilder extends DatasetImplementationBuilder {

        private Map<String, InMemoryDatasetImplementationKeyValue> keyValues;

        public InMemoryDatasetImplementationBuilder(DatasetImplementationKey datasetImplementationKey, DatasetKey datasetKey, String name, Map<String, DatasetImplementationLabel> executionRequestLabels, Map<String, InMemoryDatasetImplementationKeyValue> keyValues) {
            super(datasetImplementationKey, datasetKey, name, executionRequestLabels);
            this.keyValues = keyValues;
        }

        @Override
        public DatasetImplementation build() {
            return new InMemoryDatasetImplementation(
                    getDatasetImplementationKey(),
                    getDatasetKey(),
                    getName(),
                    new HashSet<>(getDatasetImplementationLabels().values()),
                    new HashSet<>(getKeyValues().values()));
        }
    }
}
