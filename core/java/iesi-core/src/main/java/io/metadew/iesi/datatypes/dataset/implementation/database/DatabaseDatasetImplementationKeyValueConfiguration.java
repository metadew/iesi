package io.metadew.iesi.datatypes.dataset.implementation.database;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.metadata.configuration.Configuration;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class DatabaseDatasetImplementationKeyValueConfiguration extends Configuration<DatabaseDatasetImplementationKeyValue, DatabaseDatasetImplementationKeyValueKey> {

    private static final String EXISTS_QUERY = "SELECT " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "where dataset_in_mem_impl_kvs.ID={0};";

    private static final String SELECT_QUERY = "SELECT " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs;";

    private static final String SELECT_SINGLE_QUERY = "SELECT " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "where dataset_in_mem_impl_kvs.ID={0};";

    private static final String SELECT_BY_DATASET_IMPL_ID_QUERY = "SELECT " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "where dataset_in_mem_impl_kvs.IMPL_MEM_ID={0};";

    private static final String SELECT_BY_DATASET_IMPL_ID_AND_KEY_QUERY = "SELECT " +
            "dataset_in_mem_impl_kvs.ID as dataset_in_mem_impl_kv_id, dataset_in_mem_impl_kvs.IMPL_MEM_ID as dataset_in_mem_impl_kv_impl_id, dataset_in_mem_impl_kvs.KEY as dataset_in_mem_impl_kvs_key, dataset_in_mem_impl_kvs.VALUE as dataset_in_mem_impl_kvs_value " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() + " dataset_in_mem_impl_kvs " +
            "where dataset_in_mem_impl_kvs.IMPL_MEM_ID={0} and dataset_in_mem_impl_kvs.KEY={1};";

    private static final String INSERT_QUERY = "insert into " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " (ID, IMPL_MEM_ID, KEY, VALUE) " +
            "VALUES ({0}, {1}, {2}, {3});";

    private static final String DELETE_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " WHERE ID={0};";

    private static final String DELETE_BY_DATASET_IMPLEMENTATION_ID_QUERY = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " WHERE IMPL_MEM_ID={0};";

    private static String UPDATE_QUERY = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("DatasetInMemoryImplementationKeyValues").getName() +
            " SET IMPL_MEM_ID = {0}, KEY ={1}, VALUE = {2}" +
            " WHERE ID = {3};";

    private static DatabaseDatasetImplementationKeyValueConfiguration instance;

    public static synchronized DatabaseDatasetImplementationKeyValueConfiguration getInstance() {
        if (instance == null) {
            instance = new DatabaseDatasetImplementationKeyValueConfiguration();
        }
        return instance;
    }

    private DatabaseDatasetImplementationKeyValueConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDataMetadataRepository());
    }

    @Override
    public boolean exists(DatabaseDatasetImplementationKeyValueKey datasetImplementationKeyValueKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(EXISTS_QUERY, SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())),
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
                    MessageFormat.format(SELECT_SINGLE_QUERY, SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())),
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
                    MessageFormat.format(SELECT_BY_DATASET_IMPL_ID_QUERY,
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
                    MessageFormat.format(SELECT_BY_DATASET_IMPL_ID_AND_KEY_QUERY,
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
                    SELECT_QUERY,
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
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_QUERY,
                SQLTools.getStringForSQL(datasetImplementationKeyValueKey.getUuid())));
    }

    public void deleteByDatasetImplementationId(DatasetImplementationKey datasetImplementationKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(DELETE_BY_DATASET_IMPLEMENTATION_ID_QUERY,
                SQLTools.getStringForSQL(datasetImplementationKey.getUuid())));
    }

    @Override
    public void insert(DatabaseDatasetImplementationKeyValue datasetImplementationKeyValue) {
        getMetadataRepository().executeUpdate(MessageFormat.format(INSERT_QUERY,
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
        getMetadataRepository().executeUpdate(MessageFormat.format(UPDATE_QUERY,
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
