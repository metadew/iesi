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
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.ArrayList;
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

    // Constructors
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
//        ComponentVersionConfiguration.getInstance().init(metadataRepository);
//        ComponentParameterConfiguration.getInstance().init(metadataRepository);
//        ComponentAttributeConfiguration.getInstance().init(metadataRepository);
    }

    private final static String getByNameAndVersion = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " where COMP_NM = :name ;";
    private final static String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " where COMP_ID = :id ;";
    private final static String getAll = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " ;";
    private final static String deleteById = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            "  where COMP_ID = :id ;";
    private final static String getByID = "select distinct(COMP_ID),COMP_TYP_NM,COMP_NM,COMP_DSC from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            "  where COMP_ID = :id ;";
    private final static String exists = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " where COMP_ID = :id ;";
    private final static String deleteAll = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + "  ;";
    private final static String getDeleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() + " WHERE COMP_ID = :id ;";
    private final static String countQuery = "SELECT COUNT(DISTINCT COMP_VRS_NB ) AS total_versions FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " WHERE COMP_ID = :id AND  COMP_VRS_NB != :versionNumber ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (:id, :type, :name, :description)";
    private final static String existsById = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " where COMP_ID = :id";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Components").getName() +
            " SET COMP_TYP_NM = :type , COMP_NM = :name , COMP_DSC = :description  WHERE COMP_ID = :id ; ";

    public Optional<Component> getByNameAndVersion(String name, Long version) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", name);
        List<Component> components = namedParameterJdbcTemplate.query(
                getByNameAndVersion,
                sqlParameterSource,
                new ComponentExtractor());
        if (components.size() == 0) {
            return Optional.empty();
        } else if (components.size() > 1) {
            LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", name));
        }
        ComponentKey componentKey = new ComponentKey(components.get(0).getMetadataKey().getId(), version);
        Optional<ComponentVersion> componentVersion = ComponentVersionConfiguration.getInstance().get(new ComponentVersionKey(componentKey));
        if (!componentVersion.isPresent()) {
            return Optional.empty();
        }
        List<ComponentParameter> componentParameters = ComponentParameterConfiguration.getInstance().getByComponent(componentKey);
        List<ComponentAttribute> componentAttributes = ComponentAttributeConfiguration.getInstance().getByComponent(componentKey);
        return Optional.of(new Component(componentKey,
                components.get(0).getType(),
                components.get(0).getName(),
                components.get(0).getDescription(),
                componentVersion.get(),
                componentParameters,
                componentAttributes));
    }

    public Optional<Component> get(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId());
        List<Component> components = namedParameterJdbcTemplate.query(
                queryComponent,
                sqlParameterSource,
                new ComponentExtractor());
        if (components.size() == 0) {
            return Optional.empty();
        } else if (components.size() > 1) {
            LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", componentKey.toString()));
        }
        Optional<ComponentVersion> componentVersion = ComponentVersionConfiguration.getInstance().get(new ComponentVersionKey(componentKey));
        if (!componentVersion.isPresent()) {
            return Optional.empty();
        }
        List<ComponentParameter> componentParameters = ComponentParameterConfiguration.getInstance().getByComponent(componentKey);
        List<ComponentAttribute> componentAttributes = ComponentAttributeConfiguration.getInstance().getByComponent(componentKey);
        return Optional.of(new Component(componentKey,
                components.get(0).getType(),
                components.get(0).getName(),
                components.get(0).getDescription(),
                componentVersion.get(),
                componentParameters,
                componentAttributes));
    }

    @Override
    public List<Component> getAll() {
        List<Component> components = namedParameterJdbcTemplate.query(getAll, new ComponentExtractor());
        List<Component> components1 = new ArrayList<>();
//        String queryComponent = "select COMP_ID from " + getMetadataRepository().getTableNameByLabel("Components");
//        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
//        try {
//            while (crsComponent.next()) {
//                components.addAll(getByID(crsComponent.getString("COMP_ID")));
//            }
        for (Component component : components) {
            System.out.println(component.getMetadataKey().getId());
            components1.addAll(getByID(component.getMetadataKey().getId()));
        }
//            components.addAll(getByID(crsComponent.getString("COMP_ID")));
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.warn("exception=" + e.getMessage());
//            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
//        }
        System.out.println(components1);
        System.out.println(components);
        return components1;
//        return namedParameterJdbcTemplate.query(getAll, new ComponentExtractor());
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

    public List<Component> getByID(String componentId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentId);
        List<Component> components = namedParameterJdbcTemplate.query(getByID, sqlParameterSource, new ComponentExtractor());
        if (components.size() == 0) {
            LOGGER.warn(MessageFormat.format("component.version=no implementations for component {0}.", componentId));
            return components;
        }
        List<ComponentVersion> componentVersions = ComponentVersionConfiguration.getInstance().getByComponent(componentId);
        componentVersions
                .forEach(componentVersion -> get(componentVersion.getMetadataKey().getComponentKey())
                        .ifPresent(components::add));
        return components;
    }

    public boolean exists(ComponentKey componentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", componentKey.getId());
        List<Component> components = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new ComponentExtractor());
        if (components.size() == 0) {
            return false;
        }
        return ComponentVersionConfiguration.getInstance().exists(new ComponentVersionKey(componentKey));
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
                .addValue("id", componentKey.getId());
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
            throw new MetadataAlreadyExistsException(component);
        }
        ComponentVersionConfiguration.getInstance().insert(component.getVersion());
        for (ComponentParameter componentParameter : component.getParameters()) {
            ComponentParameterConfiguration.getInstance().insert(componentParameter);
        }
        for (ComponentAttribute componentAttribute : component.getAttributes()) {
            ComponentAttributeConfiguration.getInstance().insert(componentAttribute);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", component.getMetadataKey().getId())
                .addValue("type", component.getType())
                .addValue("name", component.getName())
                .addValue("description", component.getDescription());
        if (!existsById(component.getMetadataKey().getId())) {
            namedParameterJdbcTemplate.update(
                    insert,
                    sqlParameterSource);
        } else {
            Optional.empty();
        }
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