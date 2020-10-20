package io.metadew.iesi.datatypes.dataset.implementation;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
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

    private static String existsQuery = "select dataset_impls.ID as dataset_impl_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "WHERE dataset_impls.ID={0}";

    private static String existsByNameAndLabelsQuery = "select dataset_impls.ID as dataset_impl_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
            "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "on dataset_impls.DATASET_ID=datasets.ID " +
            "where datasets.NAME={0} and dataset_impls.ID in (" +
            "SELECT dataset_impl_labels.DATASET_IMPL_ID FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            " where dataset_impl_labels.VALUE in ({1})" +
            " GROUP BY dataset_impl_labels.DATASET_IMPL_ID " +
            "HAVING COUNT(DISTINCT dataset_impl_labels.VALUE) = {2});";

    private static String selectQuery = "SELECT " +
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

    private static String selectSingleQuery = "SELECT " +
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

    private static String selectByDatasetIdQuery = "SELECT " +
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

    private static String selectByNameAndLabelsQuery = "SELECT " +
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
            "where datasets.NAME={0} and dataset_impls.ID in (" +
            "SELECT dataset_impl_labels.DATASET_IMPL_ID FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
            " where dataset_impl_labels.VALUE in ({1})" +
            " GROUP BY dataset_impl_labels.DATASET_IMPL_ID " +
            "HAVING COUNT(DISTINCT dataset_impl_labels.VALUE) = {2});";

    private static String insertQuery = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() +
            " (ID, DATASET_ID) " +
            "VALUES ({0}, {1});";

    private static String insertDatasetImplementationLabelQuery = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
            " (ID, DATASET_IMPL_ID, VALUE) " +
            "VALUES ({0}, {1}, {2});";

    private static String insertInMemoryDatasetImplementationQuery = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
            " (ID) " +
            "VALUES ({0});";

    private static String insertInMemoryDatasetImplementationKeyValueQuery = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " (ID, IMPL_MEM_ID, KEY, VALUE) " +
            "VALUES ({0}, {1}, {2}, {3});";

    private static String deleteQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementations").getName() +
            " WHERE ID={0};";

    private static String deleteDatasetImplementationLabelByDatasetImplementationIdQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetImplementationLabels").getName() +
            " WHERE DATASET_IMPL_ID={0};";

    private static String deleteInMemoryDatasetImplementationByDatasetImplementationIdQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() +
            " WHERE ID={0};";

    private static String deleteInMemoryDatasetImplementationKeyValueByDatasetImplementationIdQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " WHERE IMPL_MEM_ID={0};";


    private static DatasetImplementationConfiguration INSTANCE;

    public synchronized static DatasetImplementationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasetImplementationConfiguration();
        }
        return INSTANCE;
    }

    private DatasetImplementationConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public boolean exists(DatasetImplementationKey datasetImplementationKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsQuery, SQLTools.GetStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String name, List<String> labels) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsByNameAndLabelsQuery,
                            SQLTools.GetStringForSQL(name),
                            labels.stream().map(SQLTools::GetStringForSQL).collect(Collectors.joining(",")),
                            SQLTools.GetStringForSQL(labels.size())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<DatasetImplementation> get(DatasetImplementationKey datasetImplementationKey) {
        try {
            Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectSingleQuery, SQLTools.GetStringForSQL(datasetImplementationKey.getUuid())),
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectByNameAndLabelsQuery,
                            SQLTools.GetStringForSQL(name),
                            labels.stream().map(SQLTools::GetStringForSQL).collect(Collectors.joining(",")),
                            SQLTools.GetStringForSQL(labels.size())),
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
                    MessageFormat.format(selectByDatasetIdQuery,
                            SQLTools.GetStringForSQL(datasetKey.getUuid())),
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(selectQuery, "reader");
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
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery,
                SQLTools.GetStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteDatasetImplementationLabelByDatasetImplementationIdQuery,
                SQLTools.GetStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteInMemoryDatasetImplementationByDatasetImplementationIdQuery,
                SQLTools.GetStringForSQL(metadataKey.getUuid())));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteInMemoryDatasetImplementationKeyValueByDatasetImplementationIdQuery,
                SQLTools.GetStringForSQL(metadataKey.getUuid())));
    }

    @Override
    public void insert(DatasetImplementation metadata) {
        if (metadata instanceof InMemoryDatasetImplementation) {
            getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery,
                    SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()),
                    SQLTools.GetStringForSQL(metadata.getDatasetKey().getUuid())));
            getMetadataRepository().executeUpdate(MessageFormat.format(insertInMemoryDatasetImplementationQuery, SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid())));
            metadata.getDatasetImplementationLabels().forEach(datasetImplementationLabel ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(insertDatasetImplementationLabelQuery,
                            SQLTools.GetStringForSQL(datasetImplementationLabel.getMetadataKey().getUuid()),
                            SQLTools.GetStringForSQL(datasetImplementationLabel.getDatasetImplementationKey().getUuid()),
                            SQLTools.GetStringForSQL(datasetImplementationLabel.getValue()))));
            ((InMemoryDatasetImplementation) metadata).getKeyValues().forEach(inMemoryDatasetImplementationKeyValue ->
                    getMetadataRepository().executeUpdate(MessageFormat.format(insertInMemoryDatasetImplementationKeyValueQuery,
                            SQLTools.GetStringForSQL(inMemoryDatasetImplementationKeyValue.getMetadataKey().getUuid()),
                            SQLTools.GetStringForSQL(inMemoryDatasetImplementationKeyValue.getDatasetImplementationKey().getUuid()),
                            SQLTools.GetStringForSQL(inMemoryDatasetImplementationKeyValue.getKey()),
                            SQLTools.GetStringForSQL(inMemoryDatasetImplementationKeyValue.getValue()))));
        } else {
            throw new RuntimeException("Cannot insert dataset implementation of type " + metadata.getClass().getSimpleName());
        }
    }

    public void mapRow(CachedRowSet cachedRowSet, Map<String, DatasetImplementationBuilder> datasetImplementationBuilderMap) throws SQLException {
        String datasetImplementationId = cachedRowSet.getString("dataset_impl_id");
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

    private void mapInMemoryDatasetImplementation(CachedRowSet cachedRowSet, InMemoryDatasetImplementationBuilder inMemoryDatasetImplementationBuilder) throws SQLException {
        String inMemoryKeyValueId = cachedRowSet.getString("dataset_in_mem_impl_kv_id");
        if (inMemoryKeyValueId != null && inMemoryDatasetImplementationBuilder.getKeyValues().get(inMemoryKeyValueId) == null) {
            inMemoryDatasetImplementationBuilder.getKeyValues().put(inMemoryKeyValueId,
                    new InMemoryDatasetImplementationKeyValue(
                            new InMemoryDatasetImplementationKeyValueKey(UUID.fromString(inMemoryKeyValueId)),
                            new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_in_mem_impl_kv_impl_id"))),
                            cachedRowSet.getString("dataset_in_mem_impl_kvs_key"),
                            cachedRowSet.getString("dataset_in_mem_impl_kvs_value"))
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
