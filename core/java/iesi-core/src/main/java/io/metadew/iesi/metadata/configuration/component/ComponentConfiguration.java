package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
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
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Component
public class ComponentConfiguration extends Configuration<Component, ComponentKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final ComponentVersionConfiguration componentVersionConfiguration;
    private final ComponentParameterConfiguration componentParameterConfiguration;
    private final ComponentAttributeConfiguration componentAttributeConfiguration;

    public ComponentConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, ComponentVersionConfiguration componentVersionConfiguration, ComponentParameterConfiguration componentParameterConfiguration, ComponentAttributeConfiguration componentAttributeConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.componentVersionConfiguration = componentVersionConfiguration;
        this.componentParameterConfiguration = componentParameterConfiguration;
        this.componentAttributeConfiguration = componentAttributeConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDesignMetadataRepository());
    }


    public Optional<Component> getByNameAndVersion(String name, Long version) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC, SECURITY_GROUP_ID, SECURITY_GROUP_NM from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_NM = " + SQLTools.getStringForSQL(name);
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component %s. Returning the version provided", name));
            }
            crsComponent.next();
            ComponentKey componentKey = new ComponentKey(crsComponent.getString("COMP_ID"), version);
            Optional<ComponentVersion> componentVersion = componentVersionConfiguration.get(new ComponentVersionKey(componentKey));

            if (!componentVersion.isPresent()) {
                return Optional.empty();
            }
            List<ComponentParameter> componentParameters = componentParameterConfiguration.getByComponent(componentKey);
            List<ComponentAttribute> componentAttributes = componentAttributeConfiguration.getByComponent(componentKey);

            SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.fromString(crsComponent.getString("SECURITY_GROUP_ID")));
            String securityGroupName = crsComponent.getString("SECURITY_GROUP_NM");
            String componentType = crsComponent.getString("COMP_TYP_NM");
            String componentDescription = crsComponent.getString("COMP_DSC");
            String componentName = crsComponent.getString("COMP_NM");
            crsComponent.close();
            return Optional.of(new Component(componentKey,
                    securityGroupKey,
                    securityGroupName,
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

    public Optional<Component> getByNameAndLatestVersion(String name) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC, SECURITY_GROUP_ID, SECURITY_GROUP_NM from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_NM = " + SQLTools.getStringForSQL(name);
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component %s. Returning latest implementation.", name));
            }

            crsComponent.next();
            Optional<ComponentVersion> componentVersion = componentVersionConfiguration
                    .getLatestVersionByComponentId(crsComponent.getString("COMP_ID"));

            if (!componentVersion.isPresent()) {
                return Optional.empty();
            }

            ComponentKey componentKey = componentVersion.get().getMetadataKey().getComponentKey();
            List<ComponentParameter> componentParameters = componentParameterConfiguration.getByComponent(componentKey);
            List<ComponentAttribute> componentAttributes = componentAttributeConfiguration.getByComponent(componentKey);

            String componentType = crsComponent.getString("COMP_TYP_NM");
            SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.fromString(crsComponent.getString("SECURITY_GROUP_ID")));
            String securityGroupName = crsComponent.getString("SECURITY_GROUP_NM");
            String componentDescription = crsComponent.getString("COMP_DSC");
            String componentName = crsComponent.getString("COMP_NM");
            crsComponent.close();
            return Optional.of(new Component(componentKey,
                    securityGroupKey,
                    securityGroupName,
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

    public Optional<Component> get(ComponentKey componentKey) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC, SECURITY_GROUP_ID, SECURITY_GROUP_NM from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId());
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", componentKey.toString()));
            }
            crsComponent.next();

            Optional<ComponentVersion> componentVersion = componentVersionConfiguration.get(new ComponentVersionKey(componentKey));
            if (!componentVersion.isPresent()) {
                return Optional.empty();
            }

            List<ComponentParameter> componentParameters = componentParameterConfiguration.getByComponent(componentKey);
            List<ComponentAttribute> componentAttributes = componentAttributeConfiguration.getByComponent(componentKey);

            SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.fromString(crsComponent.getString("SECURITY_GROUP_ID")));
            String securityGroupName = crsComponent.getString("SECURITY_GROUP_NM");
            String componentType = crsComponent.getString("COMP_TYP_NM");
            String componentDescription = crsComponent.getString("COMP_DSC");
            String componentName = crsComponent.getString("COMP_NM");
            crsComponent.close();
            return Optional.of(new Component(componentKey,
                    securityGroupKey,
                    securityGroupName,
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
        componentVersionConfiguration.delete(new ComponentVersionKey(componentKey));
        componentParameterConfiguration.deleteByComponent(componentKey);
        componentAttributeConfiguration.deleteByComponent(componentKey);

        getDeleteStatement(componentKey).ifPresent(getMetadataRepository()::executeUpdate);
    }

    public void deleteById(String componentId) {
        LOGGER.trace(MessageFormat.format("Deleting Component with id {0}.", componentId));
        componentVersionConfiguration.deleteByComponentId(componentId);
        componentParameterConfiguration.deleteByComponentId(componentId);
        componentAttributeConfiguration.deleteByComponentId(componentId);

        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentId) + ";");
    }

    public List<Component> getByID(String componentId) {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select distinct(COMP_ID) from " + getMetadataRepository().getTableNameByLabel("Components") +
                " where COMP_ID = " + SQLTools.getStringForSQL(componentId) + ";";
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                LOGGER.warn(MessageFormat.format("component.version=no implementations for component {0}.", componentId));
                return components;
            }
            crsComponent.next();
            List<ComponentVersion> componentVersions = componentVersionConfiguration.getByComponent(componentId);
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
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId());
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        if (crsComponent.size() == 0) {
            return false;
        }
        return componentVersionConfiguration.exists(new ComponentVersionKey(componentKey));
    }

    public void deleteAll() {
        componentVersionConfiguration.deleteAll();
        componentAttributeConfiguration.deleteAll();
        componentParameterConfiguration.deleteAll();
        getMetadataRepository().executeUpdate("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") + ";");
    }

    private Optional<String> getDeleteStatement(ComponentKey componentKey) {
        String countQuery = "SELECT COUNT(DISTINCT COMP_VRS_NB ) AS total_versions FROM "
                + getMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) + " AND "
                + " COMP_VRS_NB != " + SQLTools.getStringForSQL(componentKey.getVersionNumber()) + ";";
        CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Components") +
                        " WHERE COMP_ID = " + SQLTools.getStringForSQL(componentKey.getId()) + ";");
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
        componentVersionConfiguration.insert(component.getVersion());
        for (ComponentParameter componentParameter : component.getParameters()) {
            componentParameterConfiguration.insert(componentParameter);
        }
        for (ComponentAttribute componentAttribute : component.getAttributes()) {
            componentAttributeConfiguration.insert(componentAttribute);
        }

        getInsertStatement(component).ifPresent(getMetadataRepository()::executeUpdate);

    }

    private Optional<String> getInsertStatement(Component component) {
        if (!existsById(component.getMetadataKey().getId())) {
            return Optional.of("INSERT INTO " + getMetadataRepository().getTableNameByLabel("Components") +
                    " (COMP_ID, SECURITY_GROUP_ID, SECURITY_GROUP_NM, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (" +
                    SQLTools.getStringForSQL(component.getMetadataKey().getId()) + "," +
                    SQLTools.getStringForSQL(component.getSecurityGroupKey().getUuid()) + "," +
                    SQLTools.getStringForSQL(component.getSecurityGroupName()) + "," +
                    SQLTools.getStringForSQL(component.getType()) + "," +
                    SQLTools.getStringForSQL(component.getName()) + "," +
                    SQLTools.getStringForSQL(component.getDescription()) + ");");
        }
        return Optional.empty();
    }

    private boolean existsById(String componentId) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + getMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_ID = " + SQLTools.getStringForSQL(componentId);
        CachedRowSet crsComponent = getMetadataRepository().executeQuery(queryComponent, "reader");
        return crsComponent.size() != 0;
    }

    public Optional<Component> get(String componentId) {
        ComponentVersion componentVersion = componentVersionConfiguration.getLatestVersionByComponentId(componentId).orElseThrow(
                () -> new RuntimeException(String.format("No versions found with the componentId %s ", componentId))
        );
        return get(componentVersion.getMetadataKey().getComponentKey());
    }


    @Override
    public void update(Component component) {
        LOGGER.trace(MessageFormat.format("Deleting Component {0}.", component.toString()));
        if (!exists(component)) {
            throw new MetadataDoesNotExistException(component.getMetadataKey());
        }

        componentParameterConfiguration.deleteByComponent(component.getMetadataKey());
        componentAttributeConfiguration.deleteByComponent(component.getMetadataKey());
        for (ComponentParameter componentParameter : component.getParameters()) {
            componentParameterConfiguration.insert(componentParameter);
        }
        for (ComponentAttribute componentAttribute : component.getAttributes()) {
            componentAttributeConfiguration.insert(componentAttribute);
        }
        getMetadataRepository().executeUpdate("UPDATE " + getMetadataRepository().getTableNameByLabel("Components") +
                " SET COMP_TYP_NM = " + SQLTools.getStringForSQL(component.getType()) + "," +
                " COMP_NM = " + SQLTools.getStringForSQL(component.getName()) + "," +
                " COMP_DSC = " + SQLTools.getStringForSQL(component.getDescription()) +
                " WHERE COMP_ID = " + SQLTools.getStringForSQL(component.getMetadataKey().getId()) + ";");
    }
}