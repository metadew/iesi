package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
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

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
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

    // Constructors
    private ComponentConfiguration() {
    }


    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ComponentVersionConfiguration.getInstance().init(metadataRepository);
        ComponentParameterConfiguration.getInstance().init(metadataRepository);
        ComponentAttributeConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Component> get(ComponentKey componentKey) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentKey.getId());
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", componentKey.toString()));
            }
            crsComponent.next();

            // get version
            Optional<ComponentVersion> componentVersion = ComponentVersionConfiguration.getInstance().get(new ComponentVersionKey(componentKey));
            if (!componentVersion.isPresent()) {
                return Optional.empty();
            }

            List<ComponentParameter> componentParameters = ComponentParameterConfiguration.getInstance().getByComponent(componentKey);
            List<ComponentAttribute> componentAttributes = ComponentAttributeConfiguration.getInstance().getByComponent(componentKey);

            String componentType = crsComponent.getString("COMP_TYP_NM");
            String componentDescription = crsComponent.getString("COMP_DSC");
            String componentName = crsComponent.getString("COMP_NM");
            crsComponent.close();
            return Optional.of(new Component(componentKey,
                    componentType,
                    componentName,
                    componentDescription,
                    componentVersion.get(),
                    componentParameters,
                    componentAttributes));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Component> getAll() {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select COMP_ID from " + getMetadataRepository().getTableNameByLabel("Components");
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            while (crsComponent.next()) {
                components.addAll(getByID(crsComponent.getString("COMP_ID")));
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return components;
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

        getDeleteStatement(componentKey).ifPresent(getMetadataRepository()::executeUpdate);
    }

    public void deleteById(String componentId) {
        LOGGER.trace(MessageFormat.format("Deleting Component with id {0}.", componentId));
        ComponentVersionConfiguration.getInstance().deleteByComponentId(componentId);
        ComponentParameterConfiguration.getInstance().deleteByComponentId(componentId);
        ComponentAttributeConfiguration.getInstance().deleteByComponentId(componentId);

        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentId) + ";");
    }

    public List<Component> getByID(String componentId) {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select distinct(COMP_ID) from " + getMetadataRepository().getTableNameByLabel("Components") +
                " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + ";";
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                LOGGER.warn(MessageFormat.format("component.version=no implementations for component {0}.", componentId));
                return components;
            }
            crsComponent.next();
            List<ComponentVersion> componentVersions = ComponentVersionConfiguration.getInstance().getByComponent(componentId);
            componentVersions
                    .forEach(componentVersion -> get(componentVersion.getMetadataKey().getComponentKey())
                            .ifPresent(components::add));
            crsComponent.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return components;
    }


    public boolean exists(ComponentKey componentKey) {
        System.out.println(componentKey);
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentKey.getId());
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        if (crsComponent.size() == 0) {
            return false;
        }
        return ComponentVersionConfiguration.getInstance().exists(new ComponentVersionKey(componentKey));
    }

    public void deleteAll() {
        ComponentVersionConfiguration.getInstance().deleteAll();
        ComponentAttributeConfiguration.getInstance().deleteAll();
        ComponentParameterConfiguration.getInstance().deleteAll();
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") + ";");
    }

    private Optional<String> getDeleteStatement(ComponentKey componentKey) {
        String countQuery = "SELECT COUNT(DISTINCT COMP_VRS_NB ) AS total_versions FROM "
                + getMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentKey.getId()) + " AND "
                + " COMP_VRS_NB != " + SQLTools.GetStringForSQL(componentKey.getVersionNumber()) + ";";
        CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") +
                        " WHERE COMP_ID = " + SQLTools.GetStringForSQL(componentKey.getId()) + ";");
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }

        return Optional.empty();
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

        getInsertStatement(component).ifPresent(getMetadataRepository()::executeUpdate);

    }

    private Optional<String> getInsertStatement(Component component) {
        if (!existsById(component.getMetadataKey().getId())) {
            return Optional.of("INSERT INTO " + getMetadataRepository().getTableNameByLabel("Components") +
                    " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(component.getMetadataKey().getId()) + "," +
                    SQLTools.GetStringForSQL(component.getType()) + "," +
                    SQLTools.GetStringForSQL(component.getName()) + "," +
                    SQLTools.GetStringForSQL(component.getDescription()) + ");");
        }
        return Optional.empty();
    }

    private boolean existsById(String componentId) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId);
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        return crsComponent.size() != 0;
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
        getMetadataRepository().executeUpdate("UPDATE " + getMetadataRepository().getTableNameByLabel("Components") +
                " SET COMP_TYP_NM = " + SQLTools.GetStringForSQL(component.getType()) + "," +
                " COMP_NM = " + SQLTools.GetStringForSQL(component.getName()) + "," +
                " COMP_DSC = " + SQLTools.GetStringForSQL(component.getDescription()) +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getMetadataKey().getId()) + ";");
    }
}