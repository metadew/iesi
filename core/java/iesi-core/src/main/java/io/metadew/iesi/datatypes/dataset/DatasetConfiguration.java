package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DatasetConfiguration extends Configuration<Dataset, DatasetKey> {

    private static String existQuery = "SELECT " +
            "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "WHERE datasets.ID = {0}";

    private static String existByNameQuery = "SELECT " +
            "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
            "WHERE datasets.NAME = {0}";

    private static String fetchSingleQuery = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
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

    private static String fetchQuery = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
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

    private static String fetchByNameQuery = "SELECT " +
            "dataset_impls.ID as dataset_impl_id, " +
            "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
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
            "WHERE datasets.NAME={0};";

    private static String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() +
            " (ID, NAME) VALUES ({0}, {1})";
    private static String deleteQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() +
            " WHERE ID={0}";


    private static DatasetConfiguration INSTANCE;

    public synchronized static DatasetConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatasetConfiguration();
        }
        return INSTANCE;
    }

    private DatasetConfiguration() {
    }


    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        DatasetImplementationConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Dataset> get(DatasetKey metadataKey) {
        try {
            Map<String, DatasetBuilder> datasetBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.GetStringForSQL(metadataKey.getUuid())),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetBuilderMap);
            }
            return datasetBuilderMap.values().stream()
                    .findFirst()
                    .map(DatasetBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(DatasetKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existQuery, SQLTools.GetStringForSQL(metadataKey.getUuid())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByName(String datasetName) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existQuery, SQLTools.GetStringForSQL(datasetName)),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Dataset> getByName(String name) {
        try {
            Map<String, DatasetBuilder> datasetBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery, SQLTools.GetStringForSQL(name)),
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetBuilderMap);
            }
            return datasetBuilderMap.values().stream()
                    .findFirst()
                    .map(DatasetBuilder::build);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Dataset> getAll() {
        try {
            Map<String, DatasetBuilder> datasetBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchQuery,
                    "reader");
            while (cachedRowSet.next()) {
                mapRow(cachedRowSet, datasetBuilderMap);
            }
            return datasetBuilderMap.values().stream()
                    .map(DatasetBuilder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DatasetKey datasetKey) {
        List<DatasetImplementation> datasetImplementations = DatasetImplementationConfiguration.getInstance()
                .getByDatasetId(datasetKey);
        datasetImplementations
                .forEach(datasetImplementation -> DatasetImplementationConfiguration.getInstance().delete(datasetImplementation.getMetadataKey()));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery,
                SQLTools.GetStringForSQL(datasetKey.getUuid().toString())));
    }

    @Override
    public void insert(Dataset dataset) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery,
                SQLTools.GetStringForSQL(dataset.getMetadataKey().getUuid()),
                SQLTools.GetStringForSQL(dataset.getName())));
        dataset.getDatasetImplementations()
                .forEach(datasetImplementation -> DatasetImplementationConfiguration.getInstance().insert(datasetImplementation));
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, DatasetBuilder> datasetBuilderMap) throws SQLException {
        String datasetId = cachedRowSet.getString("dataset_id");
        DatasetBuilder datasetBuilder = datasetBuilderMap.get(datasetId);
        if (datasetBuilder == null) {
            datasetBuilder = extractDatasetBuilder(cachedRowSet);
            datasetBuilderMap.put(datasetId, datasetBuilder);
        }
        DatasetImplementationConfiguration.getInstance().mapRow(cachedRowSet, datasetBuilder.getDatasetImplementationBuilders());
    }

    private DatasetBuilder extractDatasetBuilder(CachedRowSet cachedRowSet) throws SQLException {
        return new DatasetBuilder(
                new DatasetKey(UUID.fromString(cachedRowSet.getString("dataset_id"))),
                cachedRowSet.getString("dataset_name"),
                new HashMap<>()
        );
    }


    @AllArgsConstructor
    @Getter
    @ToString
    private class DatasetBuilder {
        private DatasetKey datasetKey;
        private String name;
        public Map<String, DatasetImplementationConfiguration.DatasetImplementationBuilder> datasetImplementationBuilders;

        public Dataset build() {
            return new Dataset(datasetKey, name, datasetImplementationBuilders.values().stream()
                    .map(DatasetImplementationConfiguration.DatasetImplementationBuilder::build)
                    .collect(Collectors.toSet()));
        }

    }

}
