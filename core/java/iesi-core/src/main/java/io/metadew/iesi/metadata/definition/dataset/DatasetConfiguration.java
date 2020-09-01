package io.metadew.iesi.metadata.definition.dataset;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatasetConfiguration extends Configuration<Dataset, DatasetKey> {

    private static String fetchQuery = "SELECT ID, NAME FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName();
    private static String fetchSingleQuery = "SELECT ID, NAME FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() +
            " WHERE ID={0};";
    private static String fetchByNameQuery = "SELECT ID, NAME FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Datasets").getName() +
            " WHERE NAME={0}";
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
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery, SQLTools.GetStringForSQL(metadataKey.getUuid())),
                    "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapRow(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Dataset> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery, SQLTools.GetStringForSQL(name)),
                    "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapRow(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Dataset mapRow(CachedRowSet cachedRowSet) throws SQLException {
        return new Dataset(
                new DatasetKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("NAME"),
                new ArrayList<>());
    }

    @Override
    public List<Dataset> getAll() {
        try {
            List<Dataset> datasets = new ArrayList<>();
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchQuery,
                    "reader");
            while (cachedRowSet.next()) {
                datasets.add(mapRow(cachedRowSet));
            }
            return datasets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DatasetKey metadataKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(deleteQuery,
                SQLTools.GetStringForSQL(metadataKey.getUuid().toString())));
    }

    @Override
    public void insert(Dataset metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(insertQuery,
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid()),
                SQLTools.GetStringForSQL(metadata.getName())));
    }
}
