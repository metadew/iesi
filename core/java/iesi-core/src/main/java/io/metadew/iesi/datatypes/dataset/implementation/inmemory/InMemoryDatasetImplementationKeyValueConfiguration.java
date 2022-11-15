package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.configuration.Configuration;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
public class DatabaseDatasetImplementationKeyValueConfiguration extends Configuration<DatabaseDatasetImplementationKeyValue, DatabaseDatasetImplementationKeyValueKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    public DatabaseDatasetImplementationKeyValueConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDataMetadataRepository());
    }

    private String existsQuery() {
        return "SELECT " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "where dataset_in_mem_impl_kvs.ID={0};";
    }

    private String selectQuery() {
        return "SELECT " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs;";
    }

    private String selectSingleQuery() {
        return "SELECT " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "where dataset_in_mem_impl_kvs.ID={0};";
    }

    private String selectByDatasetImplIdQuery() {
        return "SELECT " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "where dataset_in_mem_impl_kvs.IMPL_MEM_ID={0};";
    }

    private String selectByDatasetImplIdAndKeyQuery() {
        return "SELECT " +
                "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
                "where dataset_in_mem_impl_kvs.IMPL_MEM_ID={0} and dataset_in_mem_impl_kvs.KEY={1};";
    }

    private String insertQuery() {
        return "insert into " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " (ID, IMPL_MEM_ID, KEY, VALUE) " +
                "VALUES ({0}, {1}, {2}, {3});";
    }

    private String deleteQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " WHERE ID={0};";
    }


    private String deleteByDatasetImplementationIdQuery() {
        return "delete from " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " WHERE IMPL_MEM_ID={0};";
    }

    private String updateQuery() {
        return "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
                " SET IMPL_MEM_ID = {0}, KEY ={1}, VALUE = {2}" +
                " WHERE ID = {3};";
    }

    @Override
    public boolean exists(DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(existsQuery(), SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())),
                    "reader");
            return cachedRowSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<DatabaseDatasetImplementationKeyValue> get(DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectSingleQuery(), SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())),
                    "reader");
            if (cachedRowSet.size() > 1) {
                log.warn(String.format("found more than 1 %s with id %s", DatabaseDatasetImplementationKeyValue.class.getSimpleName(), datasetImplementationKeyValueKey));
            }
            if (cachedRowSet.next()) {
                return Optional.of(mapRow(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DatabaseDatasetImplementationKeyValue> getByDatasetImplementationId(DatasetImplementationKey datasetImplementationKey) {
        try {
            List<DatabaseDatasetImplementationKeyValue> datasetImplementationKeyValues = new LinkedList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectByDatasetImplIdQuery(),
                            SQLTools.getStringForSQL(datasetImplementationKey.getUuid())),
                    "reader");
            while (cachedRowSet.next()) {
                datasetImplementationKeyValues.add(mapRow(cachedRowSet));
            }
            return datasetImplementationKeyValues;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<DatabaseDatasetImplementationKeyValue> getByDatasetImplementationIdAndKey(DatasetImplementationKey datasetImplementationKey, String key) {
        try {
            List<DatabaseDatasetImplementationKeyValue> datasetImplementationKeyValues = new LinkedList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(selectByDatasetImplIdAndKeyQuery(),
                            SQLTools.getStringForSQL(datasetImplementationKey.getUuid()),
                            SQLTools.getStringForSQL(key)),
                    "reader");
            if (cachedRowSet.size() > 1) {
                log.warn(String.format("found more than 1 %s with dataset implementation id %s and key %s", DatabaseDatasetImplementationKeyValue.class.getSimpleName(), datasetImplementationKey, key));
            }
            if (cachedRowSet.next()) {
                return Optional.of(mapRow(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DatabaseDatasetImplementationKeyValue> getAll() {
        try {
            List<DatabaseDatasetImplementationKeyValue> datasetImplementationKeyValues = new LinkedList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    selectQuery(),
                    "reader");
            while (cachedRowSet.next()) {
                datasetImplementationKeyValues.add(mapRow(cachedRowSet));
            }
            return datasetImplementationKeyValues;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery(),
                SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())));
    }

    public void deleteByDatasetImplementationId(DatasetImplementationKey datasetImplementationKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteByDatasetImplementationIdQuery(),
                SQLTools.getStringForSQL(datasetImplementationKey.getUuid())));
    }

    @Override
    public void insert(DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery(),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getMetadataKey().getUuid()),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getDatasetImplementationKey().getUuid()),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getKey()),
                SQLTools.getStringForSQLClob(datasetImplementationKeyValue.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new))));
    }

    @Override
    public void update(DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery(),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getDatasetImplementationKey().getUuid()),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getKey()),
                SQLTools.getStringForSQLClob(datasetImplementationKeyValue.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)),
                SQLTools.getStringForSQL(datasetImplementationKeyValue.getMetadataKey().getUuid())));
    }

    public DatabaseDatasetImplementationKeyValue mapRow(CachedRowSet cachedRowSet) throws SQLException {
        String inMemoryKeyValueId = cachedRowSet.getString("dataset_in_mem_impl_kv_id");
        String key = cachedRowSet.getString("dataset_in_mem_impl_kvs_key");
        String clobValue = SQLTools.getStringFromSQLClob(cachedRowSet, "dataset_in_mem_impl_kvs_value");
        return new DatabaseDatasetImplementationKeyValue(
                new DatabaseDatasetImplementationKeyValueKey(UUID.fromString(inMemoryKeyValueId)),
                new DatasetImplementationKey(UUID.fromString(cachedRowSet.getString("dataset_in_mem_impl_kv_impl_id"))),
                key,
                clobValue);
    }
}
