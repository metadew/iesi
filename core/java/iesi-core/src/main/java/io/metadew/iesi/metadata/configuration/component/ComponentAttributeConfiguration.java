package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
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


public class ComponentAttributeConfiguration extends Configuration<ComponentAttribute, ComponentAttributeKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentAttributeConfiguration INSTANCE;

    public synchronized static ComponentAttributeConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentAttributeConfiguration();
        }
        return INSTANCE;
    }

    private final static String queryComponentAttribute = "select COMP_ID, COMP_VRS_NB, COMP_ATT_NM, ENV_NM, COMP_ATT_VAL from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId AND COMP_VRS_NB = :versionNb AND  ENV_NM = :environmentName AND COMP_ATT_NM = :componentAttributeName ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) VALUES (:id,:versionNumber,:environmentName,:attributeName, :value)  ;";
    private final static String queryAll = "select * FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() + ";";
    private final static String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId  AND COMP_VRS_NB = :componentVersionNb AND  ENV_NM = :environmentName AND COMP_ATT_NM = :componentAttributeName ;";
    //    private final static String getByComponentAndEnvironment = "select * from "
//            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
//            " where COMP_ID = :componentId AND COMP_VRS_NB = :versionNumber AND  ENV_NM = :environmentName ;";
    private final static String deleteByComponent = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId  AND COMP_VRS_NB = :versionNumber ;";
    private final static String deleteByComponentId = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId ;";
    private final static String deleteByComponentAndEnvironment = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId AND COMP_VRS_NB =:versionNumber AND ENV_NM = :environmentName ;";
    private final static String deleteAll = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() + " ;";
    private final static String getByComponent = "select * FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() + " WHERE COMP_ID = :componentId AND COMP_VRS_NB = :versionNumber";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ComponentAttributeConfiguration() {
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
    public Optional<ComponentAttribute> get(ComponentAttributeKey componentAttributeKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentAttributeKey.getComponentKey().getId())
                .addValue("versionNb", componentAttributeKey.getComponentKey().getVersionNumber())
                .addValue("componentAttributeName", componentAttributeKey.getComponentAttributeName())
                .addValue("environmentName", componentAttributeKey.getEnvironmentKey().getName());
        Optional<ComponentAttribute> componentAttribute = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryComponentAttribute,
                        sqlParameterSource,
                        new ComponentAttributeExtractor())));
        if (!componentAttribute.isPresent()) {
            return Optional.empty();
        }
        return componentAttribute;
    }

    @Override
    public List<ComponentAttribute> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new ComponentAttributeExtractor());
    }

    @Override
    public void delete(ComponentAttributeKey componentAttributeKey) {
        LOGGER.trace(MessageFormat.format("Deleting ComponentAttribute {0}.", componentAttributeKey.toString()));
        if (!exists(componentAttributeKey)) {
            throw new MetadataDoesNotExistException(componentAttributeKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentAttributeKey.getComponentKey().getId())
                .addValue("componentVersionNb", componentAttributeKey.getComponentKey().getVersionNumber())
                .addValue("environmentName", componentAttributeKey.getEnvironmentKey().getName())
                .addValue("componentAttributeName", componentAttributeKey.getComponentAttributeName());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ComponentAttribute componentAttribute) {
        LOGGER.trace(MessageFormat.format("Inserting ComponentAttribute {0}.", componentAttribute.getMetadataKey().toString()));
        if (exists(componentAttribute.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(componentAttribute.getMetadataKey());
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentAttribute.getMetadataKey().getComponentKey().getId())
                .addValue("versionNumber", componentAttribute.getMetadataKey().getComponentKey().getVersionNumber())
                .addValue("environmentName", componentAttribute.getMetadataKey().getEnvironmentKey().getName())
                .addValue("attributeName", componentAttribute.getMetadataKey().getComponentAttributeName())
                .addValue("value", componentAttribute.getValue());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public List<ComponentAttribute> getByComponent(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber());
        return
                namedParameterJdbcTemplate.query(
                        getByComponent,
                        sqlParameterSource,
                        new ComponentAttributeExtractor());
    }

    private final static String getByComponentAndEnvironment = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " where COMP_ID = :componentId AND COMP_VRS_NB = :versionNumber AND  ENV_NM = :environmentName ;";

    public List<ComponentAttribute> getByComponentAndEnvironment(ComponentKey componentKey, EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber())
                .addValue("environmentName", environmentKey.getName());
        return namedParameterJdbcTemplate.query(
                getByComponentAndEnvironment,
                sqlParameterSource,
                new ComponentAttributeExtractor());
    }

    public void deleteByComponent(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber());
        namedParameterJdbcTemplate.update(
                deleteByComponent,
                sqlParameterSource);
    }

    public void deleteByComponentId(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentId);
        namedParameterJdbcTemplate.update(
                deleteByComponentId,
                sqlParameterSource);
    }

    public void deleteByComponentAndEnvironment(ComponentKey componentKey, EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("componentId", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber())
                .addValue("environmentName", environmentKey.getName());

        namedParameterJdbcTemplate.update(
                deleteByComponentAndEnvironment,
                sqlParameterSource);
    }

    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAll,
                sqlParameterSource);
    }

}