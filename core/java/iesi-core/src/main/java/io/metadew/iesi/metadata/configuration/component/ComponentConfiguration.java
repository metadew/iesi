package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
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

public class ComponentConfiguration extends Configuration<Component, ComponentKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ComponentConfiguration INSTANCE;

    public synchronized static ComponentConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ComponentConfiguration() {
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

    private final static String getByNameAndVersion = "select Components.COMP_ID AS Components_COMP_ID , Components.COMP_TYP_NM AS Components_COMP_TYP_NM, Components.COMP_NM AS Components_COMP_NM, Components.COMP_DSC AS Components_COMP_DSC, " +
            " ComponentAttributes.COMP_ID AS ComponentAttributes_COMP_ID, ComponentAttributes.COMP_VRS_NB AS  ComponentAttributes_COMP_VRS_NB, ComponentAttributes.COMP_ATT_NM AS ComponentAttributes_COMP_ATT_NM, ComponentAttributes.ENV_NM AS ComponentAttributes_ENV_NM , ComponentAttributes.COMP_ATT_VAL AS  ComponentAttributes_COMP_ATT_VAL," +
            " ComponentVersions.COMP_ID AS ComponentVersions_COMP_ID, ComponentVersions.COMP_VRS_NB AS ComponentVersions_COMP_VRS_NB, ComponentVersions.COMP_VRS_DSC AS ComponentVersions_COMP_VRS_DSC, " +
            " ComponentParameters.COMP_ID AS ComponentParameters_COMP_ID,ComponentParameters.COMP_VRS_NB AS ComponentParameters_COMP_VRS_NB, ComponentParameters.COMP_PAR_NM AS ComponentParameters_COMP_PAR_NM, ComponentParameters.COMP_PAR_VAL AS ComponentParameters_COMP_PAR_VAL " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName()
            + " Components  LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " ComponentAttributes ON Components.COMP_ID=ComponentAttributes.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ComponentVersions ON Components.COMP_ID=ComponentVersions.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ComponentParameters ON Components.COMP_ID=ComponentParameters.COMP_ID where Components_COMP_NM = :name ;";
    private final static String queryComponent = "select Components.COMP_ID AS Components_COMP_ID , Components.COMP_TYP_NM AS Components_COMP_TYP_NM, Components.COMP_NM AS Components_COMP_NM, Components.COMP_DSC AS Components_COMP_DSC, " +
            " ComponentAttributes.COMP_ID AS ComponentAttributes_COMP_ID, ComponentAttributes.COMP_VRS_NB AS  ComponentAttributes_COMP_VRS_NB, ComponentAttributes.COMP_ATT_NM AS ComponentAttributes_COMP_ATT_NM, ComponentAttributes.ENV_NM AS ComponentAttributes_ENV_NM , ComponentAttributes.COMP_ATT_VAL AS  ComponentAttributes_COMP_ATT_VAL," +
            " ComponentVersions.COMP_ID AS ComponentVersions_COMP_ID, ComponentVersions.COMP_VRS_NB AS ComponentVersions_COMP_VRS_NB, ComponentVersions.COMP_VRS_DSC AS ComponentVersions_COMP_VRS_DSC, " +
            " ComponentParameters.COMP_ID AS ComponentParameters_COMP_ID,ComponentParameters.COMP_VRS_NB AS ComponentParameters_COMP_VRS_NB, ComponentParameters.COMP_PAR_NM AS ComponentParameters_COMP_PAR_NM, ComponentParameters.COMP_PAR_VAL AS ComponentParameters_COMP_PAR_VAL " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName()
            + " Components  LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " ComponentAttributes ON Components.COMP_ID=ComponentAttributes.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ComponentVersions ON Components.COMP_ID=ComponentVersions.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ComponentParameters ON Components.COMP_ID=ComponentParameters.COMP_ID " +
            " where Components.COMP_ID = :id ;";
    private final static String getAll = "select Components.COMP_ID AS Components_COMP_ID , Components.COMP_TYP_NM AS Components_COMP_TYP_NM, Components.COMP_NM AS Components_COMP_NM, Components.COMP_DSC AS Components_COMP_DSC ," +
            " ComponentAttributes.COMP_ID AS ComponentAttributes_COMP_ID, ComponentAttributes.COMP_VRS_NB AS  ComponentAttributes_COMP_VRS_NB, ComponentAttributes.COMP_ATT_NM AS ComponentAttributes_COMP_ATT_NM, ComponentAttributes.ENV_NM AS ComponentAttributes_ENV_NM , ComponentAttributes.COMP_ATT_VAL AS  ComponentAttributes_COMP_ATT_VAL," +
            " ComponentVersions.COMP_ID AS ComponentVersions_COMP_ID, ComponentVersions.COMP_VRS_NB AS ComponentVersions_COMP_VRS_NB, ComponentVersions.COMP_VRS_DSC AS ComponentVersions_COMP_VRS_DSC ," +
            " ComponentParameters.COMP_ID AS ComponentParameters_COMP_ID,ComponentParameters.COMP_VRS_NB AS ComponentParameters_COMP_VRS_NB, ComponentParameters.COMP_PAR_NM AS ComponentParameters_COMP_PAR_NM, ComponentParameters.COMP_PAR_VAL AS ComponentParameters_COMP_PAR_VAL " +
            " from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName()
            + " Components  LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " ComponentAttributes ON Components.COMP_ID=ComponentAttributes.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ComponentVersions ON Components.COMP_ID=ComponentVersions.COMP_ID LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ComponentParameters ON Components.COMP_ID=ComponentParameters.COMP_ID ";
    private final static String deleteById = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            "  where COMP_ID = :id ;";
    private final static String exists =
            "select Components.COMP_ID AS Components_COMP_ID , Components.COMP_TYP_NM AS Components_COMP_TYP_NM, Components.COMP_NM AS Components_COMP_NM, Components.COMP_DSC AS Components_COMP_DSC, " +
                    " ComponentAttributes.COMP_ID AS ComponentAttributes_COMP_ID, ComponentAttributes.COMP_VRS_NB AS  ComponentAttributes_COMP_VRS_NB, ComponentAttributes.COMP_ATT_NM AS ComponentAttributes_COMP_ATT_NM, ComponentAttributes.ENV_NM AS ComponentAttributes_ENV_NM , ComponentAttributes.COMP_ATT_VAL AS  ComponentAttributes_COMP_ATT_VAL," +
                    " ComponentVersions.COMP_ID AS ComponentVersions_COMP_ID, ComponentVersions.COMP_VRS_NB AS ComponentVersions_COMP_VRS_NB, ComponentVersions.COMP_VRS_DSC AS ComponentVersions_COMP_VRS_DSC, " +
                    " ComponentParameters.COMP_ID AS ComponentParameters_COMP_ID,ComponentParameters.COMP_VRS_NB AS ComponentParameters_COMP_VRS_NB, ComponentParameters.COMP_PAR_NM AS ComponentParameters_COMP_PAR_NM, ComponentParameters.COMP_PAR_VAL AS ComponentParameters_COMP_PAR_VAL " +
                    " from "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName()
                    + " Components  LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
                    " ComponentAttributes ON Components.COMP_ID=ComponentAttributes.COMP_ID LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ComponentVersions ON Components.COMP_ID=ComponentVersions.COMP_ID LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ComponentParameters ON Components.COMP_ID=ComponentParameters.COMP_ID where  Components.COMP_ID  = :id";
    private final static String deleteAll = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + "  ;";
    private final static String getDeleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " WHERE COMP_ID = :id ;";
    private final static String countQuery = "SELECT COUNT(DISTINCT Components.COMP_VRS_NB ) AS total_versions FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
            " Components WHERE Components.COMP_ID = :id AND  Components.COMP_VRS_NB != :versionNumber ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (:id, :type, :name, :description)";
    private final static String existsById =
            "select Components.COMP_ID AS Components_COMP_ID , Components.COMP_TYP_NM AS Components_COMP_TYP_NM, Components.COMP_NM AS Components_COMP_NM, Components.COMP_DSC AS Components_COMP_DSC, " +
                    " ComponentAttributes.COMP_ID AS ComponentAttributes_COMP_ID, ComponentAttributes.COMP_VRS_NB AS  ComponentAttributes_COMP_VRS_NB, ComponentAttributes.COMP_ATT_NM AS ComponentAttributes_COMP_ATT_NM, ComponentAttributes.ENV_NM AS ComponentAttributes_ENV_NM , ComponentAttributes.COMP_ATT_VAL AS  ComponentAttributes_COMP_ATT_VAL," +
                    " ComponentVersions.COMP_ID AS ComponentVersions_COMP_ID, ComponentVersions.COMP_VRS_NB AS ComponentVersions_COMP_VRS_NB, ComponentVersions.COMP_VRS_DSC AS ComponentVersions_COMP_VRS_DSC, " +
                    " ComponentParameters.COMP_ID AS ComponentParameters_COMP_ID,ComponentParameters.COMP_VRS_NB AS ComponentParameters_COMP_VRS_NB, ComponentParameters.COMP_PAR_NM AS ComponentParameters_COMP_PAR_NM, ComponentParameters.COMP_PAR_VAL AS ComponentParameters_COMP_PAR_VAL " +
                    " from "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName()
                    + " Components  LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentAttributes").getName() +
                    " ComponentAttributes ON Components.COMP_ID=ComponentAttributes.COMP_ID LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentVersions").getName() + " ComponentVersions ON Components.COMP_ID=ComponentVersions.COMP_ID LEFT OUTER JOIN "
                    + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ComponentParameters").getName() + " ComponentParameters ON Components.COMP_ID=ComponentParameters.COMP_ID where  Components.COMP_ID  = :id";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " SET COMP_TYP_NM = :type , COMP_NM = :name , COMP_DSC = :description  WHERE COMP_ID = :id ; ";

    public Optional<Component> getByNameAndVersion(String name, Long version) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", name);
        Optional<Component> components = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        getByNameAndVersion,
                        sqlParameterSource,
                        new ComponentExtractor())));
        ComponentKey componentKey = new ComponentKey(components.get().getMetadataKey().getId(), version);
        return Optional.of(new Component(componentKey,
                components.get().getType(),
                components.get().getName(),
                components.get().getDescription(),
                components.get().getVersion(),
                components.get().getParameters(),
                components.get().getAttributes()));
    }

    public Optional<Component> get(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber());
        Optional<Component> components = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryComponent,
                        sqlParameterSource,
                        new ComponentExtractor())));
        return components;
    }

    @Override
    public List<Component> getAll() {
        return namedParameterJdbcTemplate.query(getAll, new ComponentExtractor());
    }

    @Override
    public void delete(ComponentKey componentKey) {
        LOGGER.trace(MessageFormat.format("Deleting Component {0}.", componentKey.toString()));
        if (!exists(componentKey)) {
            throw new MetadataDoesNotExistException(componentKey);
        }
        ComponentVersionConfiguration.getInstance().delete(new ComponentVersionKey(componentKey));
        ComponentParameterConfiguration.getInstance().deleteByComponent(componentKey);
        ComponentAttributeConfiguration.getInstance().deleteByComponent(componentKey);
        getDeleteStatement(componentKey);
    }

    public void deleteById(String componentId) {
        LOGGER.trace(MessageFormat.format("Deleting Component with id {0}.", componentId));
        ComponentVersionConfiguration.getInstance().deleteByComponentId(componentId);
        ComponentParameterConfiguration.getInstance().deleteByComponentId(componentId);
        ComponentAttributeConfiguration.getInstance().deleteByComponentId(componentId);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        namedParameterJdbcTemplate.update(
                deleteById,
                sqlParameterSource);
    }

    public boolean exists(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId());
        List<Component> components = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new ComponentExtractor());
        return components.size() >= 1;
    }

    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        ComponentVersionConfiguration.getInstance().deleteAll();
        ComponentAttributeConfiguration.getInstance().deleteAll();
        ComponentParameterConfiguration.getInstance().deleteAll();
        namedParameterJdbcTemplate.update(
                deleteAll,
                sqlParameterSource);
    }

    private void getDeleteStatement(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSourceDelete = new MapSqlParameterSource()
                .addValue("id", componentKey.getId());
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId())
                .addValue("versionNumber", componentKey.getVersionNumber());
        int total_environments = namedParameterJdbcTemplate.query(
                countQuery,
                sqlParameterSource,
                new ComponentExtractorTotal());
        if (total_environments == 0) {
            namedParameterJdbcTemplate.update(
                    getDeleteStatement,
                    sqlParameterSourceDelete);
        } else {
            Optional.empty();
        }
    }

    public void insert(Component component) {
        if (exists(component.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(component.getMetadataKey());
        }
        for (ComponentParameter componentParameter : component.getParameters()) {
            ComponentParameterConfiguration.getInstance().insert(componentParameter);
        }
        for (ComponentAttribute componentAttribute : component.getAttributes()) {
            ComponentAttributeConfiguration.getInstance().insert(componentAttribute);
        }
        ComponentVersionConfiguration.getInstance().insert(component.getVersion());
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", component.getMetadataKey().getId())
                .addValue("type", component.getType())
                .addValue("name", component.getName())
                .addValue("description", component.getDescription());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    private boolean existsById(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        List<Component> components = namedParameterJdbcTemplate.query(
                existsById,
                sqlParameterSource,
                new ComponentExtractor());
        return components.size() != 0;
    }

    public Optional<Component> get(String componentId) {
        return get(new ComponentKey(componentId,
                ComponentVersionConfiguration.getInstance().getLatestVersionByComponentId(componentId)));
    }

    @Override
    public void update(Component component) {
        LOGGER.trace(MessageFormat.format("Deleting Component {0}.", component.toString()));
        if (!exists(component)) {
            throw new MetadataDoesNotExistException(component.getMetadataKey());
        }
        ComponentParameterConfiguration.getInstance().deleteByComponent(component.getMetadataKey());
        ComponentAttributeConfiguration.getInstance().deleteByComponent(component.getMetadataKey());
        for (ComponentParameter componentParameter : component.getParameters()) {
            ComponentParameterConfiguration.getInstance().insert(componentParameter);
        }
        for (ComponentAttribute componentAttribute : component.getAttributes()) {
            ComponentAttributeConfiguration.getInstance().insert(componentAttribute);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("type", component.getType())
                .addValue("name", component.getName())
                .addValue("description", component.getDescription())
                .addValue("id", component.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}