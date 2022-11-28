package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DatasetConfiguration extends Configuration<Dataset, DatasetKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final DatasetImplementationConfiguration datasetImplementationConfiguration;

    private String existQuery() {
        return "SELECT " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "WHERE datasets.ID = {0}";
    }

    private String fetchIdByName() {
        return "SELECT " +
                "datasets.ID as dataset_id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "WHERE datasets.NAME = {0}";
    }

    private String fetchSingleQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
                "datasets.SECURITY_GROUP_ID as dataset_security_group_id, " +
                "datasets.SECURITY_GROUP_NM as dataset_security_group_name,  " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "where datasets.ID={0};";
    }

    private String fetchQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
                "datasets.SECURITY_GROUP_ID as dataset_security_group_id, " +
                "datasets.SECURITY_GROUP_NM as dataset_security_group_name,  " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID;";
    }

    private String fetchByNameQuery() {
        return "SELECT " +
                "dataset_impls.ID as dataset_impl_id, " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id, " +
                "datasets.SECURITY_GROUP_ID as dataset_security_group_id, " +
                "datasets.SECURITY_GROUP_NM as dataset_security_group_name,  " +
                "dataset_impl_labels.ID as dataset_impl_label_id, dataset_impl_labels.DATASET_IMPL_ID as dataset_impl_label_impl_id, dataset_impl_labels.VALUE as dataset_impl_label_value, " +
                "dataset_in_mem_impls.ID as dataset_in_mem_impl_id, " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementations").getName() + " dataset_impls " +
                "on dataset_impls.DATASET_ID=datasets.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementations").getName() + " dataset_in_mem_impls " +
                "on dataset_impls.ID = dataset_in_mem_impls.ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "on dataset_in_mem_impls.ID = dataset_in_mem_impl_kvs.IMPL_MEM_ID " +
                "left outer join " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetImplementationLabels").getName() + " dataset_impl_labels " +
                "on dataset_impls.ID = dataset_impl_labels.DATASET_IMPL_ID " +
                "WHERE datasets.NAME={0};";
    }

    private String existsByNameQuery() {
        return "SELECT " +
                "datasets.NAME as dataset_name, datasets.ID as dataset_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() + " datasets " +
                "WHERE datasets.NAME={0};";
    }

    private String insertQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() +
                " (ID, SECURITY_GROUP_ID, SECURITY_GROUP_NM, NAME) VALUES ({0}, {1}, {2}, {3})";
    }

    private String updateQuery() {
        return  "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() +
                " SET NAME={0}, SECURITY_GROUP_NM={1}" +
                "WHERE ID={2}";
    }

    private String deleteQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Datasets").getName() +
                " WHERE ID={0}";
    }

    public DatasetConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration,
                                MetadataTablesConfiguration metadataTablesConfiguration,
                                DatasetImplementationConfiguration datasetImplementationConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.datasetImplementationConfiguration = datasetImplementationConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDataMetadataRepository());
    }

    @Override
    public Optional<Dataset> get(DatasetKey metadataKey) {
        try {
            Map<String, DatasetBuilder> datasetBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid())),
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
                    MessageFormat.format(existQuery(), SQLTools.getStringForSQL(metadataKey.getUuid())),
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
                    MessageFormat.format(fetchByNameQuery(), SQLTools.getStringForSQL(name)),
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

    public boolean existsByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsByNameQuery(), SQLTools.getStringForSQL(name)),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<DatasetKey> getIdByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchIdByName(), SQLTools.getStringForSQL(name)),
                    "reader");
            if (cachedRowSet.next()) {
                return Optional.of(new DatasetKey(UUID.fromString(cachedRowSet.getString("dataset_id"))));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Dataset> getAll() {
        try {
            Map<String, DatasetBuilder> datasetBuilderMap = new LinkedHashMap<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchQuery(),
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
        List<DatasetImplementation> datasetImplementations = datasetImplementationConfiguration
                .getByDatasetId(datasetKey);
        datasetImplementations
                .forEach(datasetImplementation -> datasetImplementationConfiguration.delete(datasetImplementation.getMetadataKey()));
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery(),
                SQLTools.getStringForSQL(datasetKey.getUuid().toString())));
    }

    @Override
    public void insert(Dataset dataset) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery(),
                SQLTools.getStringForSQL(dataset.getMetadataKey().getUuid()),
                SQLTools.getStringForSQL(dataset.getSecurityGroupKey().getUuid()),
                SQLTools.getStringForSQL(dataset.getSecurityGroupName()),
                SQLTools.getStringForSQL(dataset.getName())));
        dataset.getDatasetImplementations()
                .forEach(datasetImplementation -> datasetImplementationConfiguration.insert(datasetImplementation));
    }

    @Override
    public void update(Dataset dataset) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery(),
                SQLTools.getStringForSQL(dataset.getName()),
                SQLTools.getStringForSQL(dataset.getSecurityGroupName()),
                SQLTools.getStringForSQL(dataset.getMetadataKey().getUuid())));
        datasetImplementationConfiguration.deleteByDatasetId(dataset.getMetadataKey());
        dataset.getDatasetImplementations()
                .forEach(datasetImplementation -> datasetImplementationConfiguration.insert(datasetImplementation));
    }

    private void mapRow(CachedRowSet cachedRowSet, Map<String, DatasetBuilder> datasetBuilderMap) throws SQLException {
        String datasetId = cachedRowSet.getString("dataset_id");
        DatasetBuilder datasetBuilder = datasetBuilderMap.get(datasetId);
        if (datasetBuilder == null) {
            datasetBuilder = extractDatasetBuilder(cachedRowSet);
            datasetBuilderMap.put(datasetId, datasetBuilder);
        }
        datasetImplementationConfiguration.mapRow(cachedRowSet, datasetBuilder.getDatasetImplementationBuilders());
    }

    private DatasetBuilder extractDatasetBuilder(CachedRowSet cachedRowSet) throws SQLException {
        return new DatasetBuilder(
                new DatasetKey(UUID.fromString(cachedRowSet.getString("dataset_id"))),
                new SecurityGroupKey(UUID.fromString(cachedRowSet.getString("dataset_security_group_id"))),
                cachedRowSet.getString("dataset_security_group_name"),
                cachedRowSet.getString("dataset_name"),
                new HashMap<>()
        );
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private class DatasetBuilder {
        private final DatasetKey datasetKey;
        private final SecurityGroupKey securityGroupKey;
        private final String securityGroupName;
        private final String name;
        public final Map<String, DatasetImplementationConfiguration.DatasetImplementationBuilder> datasetImplementationBuilders;

        public Dataset build() {
            return new Dataset(datasetKey, securityGroupKey, securityGroupName, name, datasetImplementationBuilders.values().stream()
                    .map(DatasetImplementationConfiguration.DatasetImplementationBuilder::build)
                    .collect(Collectors.toSet()));
        }

    }

}
