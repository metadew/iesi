package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ComponentVersionConfiguration extends Configuration<ComponentVersion, ComponentVersionKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentVersionConfiguration INSTANCE;

    public synchronized static ComponentVersionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentVersionConfiguration();
        }
        return INSTANCE;
    }

    private final static String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " where COMP_ID = :id and COMP_VRS_NB = :versionNumber ";
    private final static String queryAll = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + ";";
    private final static String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " where COMP_ID = :id AND COMP_VRS_NB = :versionNumber";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) VALUES (:id,:versionNumber,:description) ";
    private final static String getByComponent = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " where COMP_ID = :id";
    private final static String deleteByComponentId = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " where COMP_ID = :id ";
    private final static String deleteAll = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ;";
    private final static String getLatestVersionByComponentId = "select max(COMP_VRS_NB) as \"MAX_VRS_NB\" from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() +
            " where COMP_ID = :id ";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ComponentVersionConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ComponentVersion> get(ComponentVersionKey componentVersionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentVersionKey.getComponentKey().getId())
                .addValue("versionNumber", componentVersionKey.getComponentKey().getVersionNumber());

        Optional<ComponentVersion> componentVersion = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryComponentVersion,
                        sqlParameterSource,
                        new ComponentVersionExtractor())));

        return componentVersion;
    }

    @Override
    public List<ComponentVersion> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new ComponentVersionExtractor());
    }

    @Override
    public void delete(ComponentVersionKey componentVersionKey) {
        LOGGER.trace(MessageFormat.format("Deleting ComponentVersion {0}.", componentVersionKey.toString()));
        if (!exists(componentVersionKey)) {
            throw new MetadataDoesNotExistException(componentVersionKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentVersionKey.getComponentKey().getId())
                .addValue("versionNumber", componentVersionKey.getComponentKey().getVersionNumber());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }


    @Override
    public void insert(ComponentVersion componentVersion) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentVersion {0}.", componentVersion.getMetadataKey().toString()));
        if (exists(componentVersion.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(componentVersion.getMetadataKey());
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentVersion.getMetadataKey().getComponentKey().getId())
                .addValue("versionNumber", componentVersion.getMetadataKey().getComponentKey().getVersionNumber())
                .addValue("description", componentVersion.getDescription());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public List<ComponentVersion> getByComponent(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);

        return namedParameterJdbcTemplate.query(
                getByComponent,
                sqlParameterSource,
                new ComponentVersionExtractor());
    }


    public void deleteByComponentId(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        namedParameterJdbcTemplate.update(
                deleteByComponentId,
                sqlParameterSource);
    }

    public long getLatestVersionByComponentId(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        return namedParameterJdbcTemplate.query(
                getLatestVersionByComponentId,
                sqlParameterSource,
                new ComponentVersionExtractorLatestVersion());
    }


    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAll,
                sqlParameterSource);
    }
}