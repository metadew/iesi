package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ComponentParameterConfiguration extends Configuration<ComponentParameter, ComponentParameterKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentParameterConfiguration INSTANCE;

    public synchronized static ComponentParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentParameterConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ComponentParameterConfiguration() {
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


    private final static String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() +
            " where COMP_ID = :id and COMP_VRS_NB = :versionNumber and COMP_PAR_NM = :parameterName  ";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName()
            + " (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) VALUES (:id, :versionNumber,:parameterName,:value) ";
    private final static String deleteByComponent = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() +
            " where COMP_ID = :id AND COMP_VRS_NB = :versionNumber ";
    private final static String deleteByComponentId = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() +
            " where COMP_ID = :id ";
    private final static String deleteAll = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ;";

    @Override
    public Optional<ComponentParameter> get(ComponentParameterKey componentParameterKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ComponentParameter> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ComponentParameterKey componentParameterKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentParameterKey.getComponentKey().getId())
                .addValue("versionNumber", componentParameterKey.getComponentKey().getVersionNumber())
                .addValue("parameterName", componentParameterKey.getParameterName());
        LOGGER.trace(MessageFormat.format("Deleting ComponentParameter {0}.", componentParameterKey.toString()));
        if (!exists(componentParameterKey)) {
            throw new MetadataDoesNotExistException(componentParameterKey);
        }

        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ComponentParameter componentParameter) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentParameter {0}.", componentParameter.getMetadataKey().toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentParameter.getMetadataKey().getComponentKey().getId())
                .addValue("versionNumber", componentParameter.getMetadataKey().getComponentKey().getVersionNumber())
                .addValue("parameterName", componentParameter.getMetadataKey().getParameterName())
                .addValue("value", componentParameter.getValue());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public void deleteByComponent(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber());
        namedParameterJdbcTemplate.update(
                deleteByComponent,
                sqlParameterSource);

    }

    public void deleteByComponentId(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        namedParameterJdbcTemplate.update(
                deleteByComponentId,
                sqlParameterSource);
    }

    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAll,
                sqlParameterSource);
    }

}