package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

public class ComponentConfiguration extends MetadataConfiguration {

    private final static Logger LOGGER = LogManager.getLogger();
    private final ComponentVersionConfiguration componentVersionConfiguration;

    // Constructors
    public ComponentConfiguration() {
        componentVersionConfiguration = new ComponentVersionConfiguration();
    }

    @Override
    public List<Component> getAllObjects() {
        return this.getAll();
    }


    public List<Component> getAll() {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select COMP_NM from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components");
        CachedRowSet crsComponent = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            while (crsComponent.next()) {
                components.addAll(getByName(crsComponent.getString("COMP_NM")));
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return components;
    }

    public List<Component> getByName(String componentName) {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components") + " where COMP_NM = '"
                + componentName + "'";
        CachedRowSet crsComponent = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                LOGGER.warn(MessageFormat.format("component.version=no implementations for component {0}.", componentName));
                return components;
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", componentName));
            }
            crsComponent.next();
            String componentId = crsComponent.getString("COMP_ID");
            String queryComponentVersions = "select COMP_VRS_NB from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + " where COMP_ID = "
                    + SQLTools.GetStringForSQL(componentId);
            CachedRowSet crsComponentVersions = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentVersions, "reader");
            while (crsComponentVersions.next()) {
                get(componentName, crsComponentVersions.getLong("COMP_VRS_NB")).ifPresent(components::add);
            }
            crsComponentVersions.close();
            crsComponent.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return components;
    }

    public Optional<Component> get(String componentName, long versionNumber) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_NM = "
                + SQLTools.GetStringForSQL(componentName);
        CachedRowSet crsComponent = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                LOGGER.warn(MessageFormat.format("component.version=found multiple implementations for component {0}. Returning first implementation.", componentName));
            }
            crsComponent.next();
            String componentId = crsComponent.getString("COMP_ID");

            // get version
            Optional<ComponentVersion> componentVersion = componentVersionConfiguration.getComponentVersion(componentId, versionNumber);
            if (!componentVersion.isPresent()) {
                return Optional.empty();
            }

            // get parameters
            String queryComponentParameters = "select COMP_PAR_NM, COMP_PAR_VAL from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters")
                    + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + versionNumber;
            CachedRowSet crsComponentParameters = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentParameters, "reader");
            List<ComponentParameter> componentParameters = new ArrayList<>();
            while (crsComponentParameters.next()) {
                componentParameters.add(new ComponentParameter(crsComponentParameters.getString("COMP_PAR_NM"),
                        crsComponentParameters.getString("COMP_PAR_VAL")));
            }

            // get attributes
            String queryComponentAttributes = "select ENV_NM, COMP_ATT_NM, COMP_ATT_VAL from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                    + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + versionNumber;
            CachedRowSet crsComponentAttributes = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentAttributes, "reader");
            List<ComponentAttribute> componentAttributes = new ArrayList<>();
            while (crsComponentAttributes.next()) {
                componentAttributes.add(new ComponentAttribute(crsComponentAttributes.getString("ENV_NM"),
                        crsComponentAttributes.getString("COMP_ATT_NM"),
                        crsComponentAttributes.getString("COMP_ATT_VAL")));
            }
            String componentType = crsComponent.getString("COMP_TYP_NM");
            String componentDescription = crsComponent.getString("COMP_DSC");
            crsComponent.close();
            crsComponentParameters.close();
            crsComponentAttributes.close();
            return Optional.of(new Component(componentId,
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

    public boolean exists(Component component)  {
        try {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_NM = "
                + SQLTools.GetStringForSQL(component.getName());
        CachedRowSet crsComponent = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        if (crsComponent.size() == 0) {
            return false;
        }
        crsComponent.next();
        String componentId = crsComponent.getString("COMP_ID");

        // get version
        ComponentVersionConfiguration componentVersionConfiguration = new ComponentVersionConfiguration();
        Optional<ComponentVersion> componentVersion = componentVersionConfiguration.getComponentVersion(componentId, component.getVersion().getNumber());
        return componentVersion.isPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components") + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters") + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes") + ";");
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(queries);
    }

    public void deleteByName(String componentName) throws ComponentDoesNotExistException, SQLException {
        for (Component component : getByName(componentName)) {
            delete(component);
        }
    }

    public void delete(Component component) throws ComponentDoesNotExistException, SQLException {
        //TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        if (!exists(component)) {
            throw new ComponentDoesNotExistException(MessageFormat.format("Component {0}-{1} is not present in the repository so cannot be deleted",
                    component.getName(), component.getVersion().getNumber()));
        }

        List<String> deleteQuery = getDeleteStatement(component);
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(deleteQuery);
    }

    private List<String> getDeleteStatement(Component component) {
        List<String> queries = new ArrayList<>();
        // delete parameters
        String deleteParametersQuery = "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";";
        queries.add(deleteParametersQuery);
        String deleteAttributesQuery = "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";";
        queries.add(deleteAttributesQuery);
        String deleteVersionQuery = "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";";
        queries.add(deleteVersionQuery);
        // delete component info if last version
        String countQuery = "SELECT COUNT(DISTINCT COMP_VRS_NB ) AS total_versions FROM "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND "
                + " COMP_VRS_NB != " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";";
        CachedRowSet crs = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                String deleteComponentQuery = "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components") +
                        " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getName()) + ";";
                queries.add(deleteComponentQuery);
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }

        return queries;
    }

    public void insert(Component component) throws ComponentAlreadyExistsException, SQLException {
        // TODO handle component ID
        // TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        if (exists(component)) {
            throw new ComponentAlreadyExistsException(MessageFormat.format("Component {0}-{1} already exists", component.getName(), component.getVersion().getNumber()));
        }
        List<String> insertStatement = getInsertStatement(component);
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(insertStatement);

    }

    private List<String> getInsertStatement(Component component) {
        // TODO: check id generation
        List<String> queries = new ArrayList<>();


        if (!exists(component.getName())) {
            String componentQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                    .getTableNameByLabel("Components") +
                    " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(component.getId()) + "," +
                    SQLTools.GetStringForSQL(component.getType()) + "," +
                    SQLTools.GetStringForSQL(component.getName()) + "," +
                    SQLTools.GetStringForSQL(component.getDescription()) + ");";
            queries.add(componentQuery);
        }

        // add version
        String componentVersionQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") +
                " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(component.getId()) + "," +
                SQLTools.GetStringForSQL(component.getVersion().getNumber()) + "," +
                SQLTools.GetStringForSQL(component.getVersion().getDescription()) + ");";
        queries.add(componentVersionQuery);

        // add Parameters

        for (ComponentParameter parameter : component.getParameters()) {
            String componentParameterQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                    .getTableNameByLabel("ComponentParameters") +
                    " (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) VALUES (" +
                    SQLTools.GetStringForSQL(component.getId()) + "," +
                    SQLTools.GetStringForSQL(component.getVersion().getNumber()) + "," +
                    SQLTools.GetStringForSQL(parameter.getName()) + "," +
                    SQLTools.GetStringForSQL(parameter.getValue()) + ");";
            queries.add(componentParameterQuery);
        }

        // add attributes
        for (ComponentAttribute attribute : component.getAttributes()) {
            String componentAttributeQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                    .getTableNameByLabel("ComponentAttributes") +
                    " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) VALUES (" +
                    SQLTools.GetStringForSQL(component.getId()) + "," +
                    SQLTools.GetStringForSQL(component.getVersion().getNumber()) + "," +
                    SQLTools.GetStringForSQL(attribute.getEnvironment()) + "," +
                    SQLTools.GetStringForSQL(attribute.getName()) + "," +
                    SQLTools.GetStringForSQL(attribute.getValue()) + ");";
            queries.add(componentAttributeQuery);
        }
        return queries;
    }

    private boolean exists(String name) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components")
                + " where COMP_NM = "
                + SQLTools.GetStringForSQL(name);
        CachedRowSet crsComponent = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        return crsComponent.size() != 0;
    }

    public void update(Component component) throws ComponentDoesNotExistException, SQLException {
        //TODO fix logggin
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        try {
            delete(component);
            insert(component);
        } catch (ComponentDoesNotExistException e) {
            //TODO fix logging
            //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Component {0}-{1} is not present in the repository so cannot be updated",component.getName(), component.getVersion().getNumber()),Level.TRACE);
            throw e;
            // throw new ComponentDoesNotExistException(MessageFormat.format(
            //        "Component {0}-{1} is not present in the repository so cannot be updated", component.getName(),  component.getVersion().getNumber()));

        } catch (ComponentAlreadyExistsException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
            //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Component {0}-{1} is not deleted correctly during update. {2}",component.getName(), component.getVersion().getNumber(), e.toString()),Level.WARN);
        }
    }


    private long getLatestVersion(String componentName) {
        long componentVersionNumber = -1;
        CachedRowSet crsComponentVersion = null;
        String queryComponentVersion = "select max(COMP_VRS_NB) as \"MAX_VRS_NB\" from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + " a inner join "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Components")
                + " b on a.COMP_ID = b.COMP_ID where b.COMP_NM = " + SQLTools.GetStringForSQL(componentName) + ";";
        crsComponentVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            while (crsComponentVersion.next()) {
                componentVersionNumber = crsComponentVersion.getLong("MAX_VRS_NB");
            }
            crsComponentVersion.close();
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }

        if (componentVersionNumber == -1) {
            throw new RuntimeException("No component version found for Component (NAME) " + componentName);
        }

        return componentVersionNumber;
    }

    public Optional<Component> get(String componentName) {
        return get(componentName, this.getLatestVersion(componentName));
    }
}