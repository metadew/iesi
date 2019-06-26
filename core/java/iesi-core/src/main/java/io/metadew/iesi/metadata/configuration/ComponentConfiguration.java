package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.ComponentVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentConfiguration extends MetadataConfiguration {

    private Component component;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ComponentConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentConfiguration(Component component, FrameworkInstance frameworkInstance) {
        this.setComponent(component);
        this.verifyVersionExists();
        this.setFrameworkInstance(frameworkInstance);
    }
    
    // Abstract method implementations
	@Override
	public List<Component> getAllObjects() {
		return this.getComponents();
	}

    // Checks
    private void verifyVersionExists() {
        if (this.getComponent().getVersion() == null) {
            this.getComponent().setVersion(new ComponentVersion());
            this.getComponent().getVersion().setNumber(0);
            this.getComponent().getVersion().setDescription("Default version");
        }
    }

    private boolean verifyComponentConfigurationExists(String componentName) {
        Component component = new Component();
        CachedRowSet crsComponent = null;
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components") + " where COMP_NM = '"
                + componentName + "'";
        crsComponent = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            while (crsComponent.next()) {
                component.setId(crsComponent.getString("COMP_ID"));
                component.setType(crsComponent.getString("COMP_TYP_NM"));
                component.setName(componentName);
                component.setDescription(crsComponent.getString("COMP_DSC"));
            }
            crsComponent.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (component.getName() == null || component.getName().equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }


    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select COMP_NM from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
        CachedRowSet crsComponent = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponent, "reader");

        try {
            while (crsComponent.next()) {
                System.out.println("Getting component " + crsComponent.getString("COMP_NM"));
                components.addAll(getComponentsByName(crsComponent.getString("COMP_NM")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    public List<Component> getComponentsByName(String componentName) {
        List<Component> components = new ArrayList<>();
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components") + " where COMP_NM = '"
                + componentName + "'";
        CachedRowSet crsComponent = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponent, "reader");
        try {
            if (crsComponent.size() == 0) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=no implementations for component {0}.", componentName), Level.WARN);
                return components;
            } else if (crsComponent.size() > 1) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple implementations for component {0}." +" Returning first implementation.", componentName), Level.WARN);
            }
            crsComponent.next();
            String componentId = crsComponent.getString("COMP_ID");
            String queryComponentVersions = "select COMP_VRS_NB from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + " where COMP_ID = "
                    + SQLTools.GetStringForSQL(componentId);
            CachedRowSet crsComponentVersions = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentVersions, "reader");
            while (crsComponentVersions.next()) {
                System.out.println("Component Version " + crsComponentVersions.getLong("COMP_VRS_NB"));
                getComponent(componentName, crsComponentVersions.getLong("COMP_VRS_NB")).ifPresent(components::add);
            }
            crsComponentVersions.close();
            crsComponent.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    public Optional<Component> getComponent(String componentName, long versionNumber) {
        String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components") + " where COMP_NM = '"
                + componentName + "'";
        CachedRowSet crsComponent = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponent, "reader");

        try {
            if (crsComponent.size() == 0) {
                return Optional.empty();
            } else if (crsComponent.size() > 1) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple implementations for component {0}." +"Returning first implementation.", componentName), Level.WARN);
            }
            crsComponent.next();
            String componentId = crsComponent.getString("COMP_ID");

            // get version
            ComponentVersionConfiguration componentVersionConfiguration = new ComponentVersionConfiguration(
                    this.getFrameworkInstance());
            Optional<ComponentVersion> componentVersion = componentVersionConfiguration.getComponentVersion(componentId, versionNumber);
            if (!componentVersion.isPresent()) {
            	//TODO fix logging
                //frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple implementations for component {0}." +"Returning first implementation.", componentName), Level.WARN);
                return Optional.empty();
            }

            // get parameters
            String queryComponentParameters = "select COMP_PAR_NM, COMP_PAR_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters")
                    + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + versionNumber;
            CachedRowSet crsComponentParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentParameters, "reader");
            List<ComponentParameter> componentParameters = new ArrayList<>();
            while (crsComponentParameters.next()) {
                componentParameters.add(new ComponentParameter(crsComponentParameters.getString("COMP_PAR_NM"),
                        crsComponentParameters.getString("COMP_PAR_VAL")));
            }

            // get attributes
            String queryComponentAttributes = "select ENV_NM, COMP_ATT_NM, COMP_ATT_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                    + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + "' and COMP_VRS_NB = " + versionNumber;
            CachedRowSet crsComponentAttributes = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentAttributes, "reader");
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
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("action.error=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("action.stacktrace=" + StackTrace, Level.INFO);

            System.out.println("ERROR");
            return Optional.empty();
        }
    }

    public boolean exists(Component component) {
        return getComponent(component.getName(), component.getVersion().getNumber()).isPresent();
    }

    public void deleteComponents() {
        String deleteQuery = "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components") + ";\n";
        deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + ";\n";
        deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters") + ";\n";
        deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes") + ";\n";
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeUpdate(deleteQuery);
    }

    public void deleteComponentByName(String componentName) throws ComponentDoesNotExistException {
        for (Component component : getComponentsByName(componentName)) {
            deleteComponent(component);
        }
    }

    public void deleteComponent(Component component) throws ComponentDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        if (!exists(component)) {
            throw new ComponentDoesNotExistException(
                    MessageFormat.format("Component {0}-{1} is not present in the repository so cannot be deleted",
                            component.getName(), component.getVersion().getNumber()));
        }

        String deleteQuery = getDeleteStatement(component);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeUpdate(deleteQuery);
    }


    private String getDeleteStatement(Component component) {
        // delete parameters
        String deleteQuery = "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters");
        deleteQuery += " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";\n";
        // delete attributes
        deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes");
        deleteQuery += " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";\n";
        // delete version
        deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
        deleteQuery += " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getId()) + " AND COMP_VRS_NB = " + SQLTools.GetStringForSQL(component.getVersion().getNumber()) + ";\n";

        // delete component info if last version
        String countQuery = "SELECT COUNT(DISTINCT COMP_VRS_NB ) AS total_versions FROM "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " WHERE COMP_ID != " + SQLTools.GetStringForSQL(component.getId()) + ";";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                deleteQuery += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
                deleteQuery += " WHERE COMP_ID = " + SQLTools.GetStringForSQL(component.getName()) + ";\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deleteQuery;
    }

    public void insertComponent(Component component) throws ComponentAlreadyExistsException {
        // TODO handle component ID
        // TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        if (exists(component)) {
            throw new ComponentAlreadyExistsException(MessageFormat.format(
                    "Component {0}-{1} already exists", component.getName(), component.getVersion().getNumber()));
        }
        String insertStatement = getInsertStatement(component);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeUpdate(insertStatement);

    }

    private String getInsertStatement(Component component) {
        // TODO: check id generation
        StringBuilder sql = new StringBuilder();


        if (getComponentsByName(component.getName()).size() == 0) {
            sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                    .getTableNameByLabel("Components"));
            sql.append(" (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) VALUES (");
            sql.append(SQLTools.GetStringForSQL(component.getId())).append(",");
            sql.append(SQLTools.GetStringForSQL(component.getType())).append(",");
            sql.append(SQLTools.GetStringForSQL(component.getName())).append(",");
            sql.append(SQLTools.GetStringForSQL(component.getDescription())).append(");\n");
        }

        // add version
        sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ComponentVersions"));
        sql.append(" (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) VALUES (");
        sql.append(SQLTools.GetStringForSQL(component.getId())).append(",");
        sql.append(SQLTools.GetStringForSQL(component.getVersion().getNumber())).append(",");
        sql.append(SQLTools.GetStringForSQL(component.getVersion().getDescription())).append(");\n");

        // add Parameters

        for (ComponentParameter parameter : component.getParameters()) {
            sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                    .getTableNameByLabel("ComponentParameters"));
            sql.append(" (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) VALUES (");
            sql.append(SQLTools.GetStringForSQL(component.getId())).append(",");
            sql.append(SQLTools.GetStringForSQL(component.getVersion().getNumber())).append(",");
            sql.append(SQLTools.GetStringForSQL(parameter.getName())).append(",");
            sql.append(SQLTools.GetStringForSQL(parameter.getValue())).append(");\n");
        }

        // add attributes
        for (ComponentAttribute attribute : component.getAttributes()) {
            sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                    .getTableNameByLabel("ComponentAttributes"));
            sql.append(" (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) VALUES (");
            sql.append(SQLTools.GetStringForSQL(component.getId())).append(",");
            sql.append(SQLTools.GetStringForSQL(component.getVersion().getNumber())).append(",");
            sql.append(SQLTools.GetStringForSQL(attribute.getEnvironment())).append(",");
            sql.append(SQLTools.GetStringForSQL(attribute.getName())).append(",");
            sql.append(SQLTools.GetStringForSQL(attribute.getValue())).append(");\n");
        }

        return sql.toString();
    }

    public void updateComponent(Component component) throws ComponentDoesNotExistException {
        //TODO fix logggin
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating component {0}-{1}.", component.getName(), component.getVersion().getNumber()), Level.TRACE);
        try {
            deleteComponent(component);
            insertComponent(component);
        } catch (ComponentDoesNotExistException e) {
        	//TODO fix logging
            //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Component {0}-{1} is not present in the repository so cannot be updated",component.getName(), component.getVersion().getNumber()),Level.TRACE);
            throw e;
            // throw new ComponentDoesNotExistException(MessageFormat.format(
            //        "Component {0}-{1} is not present in the repository so cannot be updated", component.getName(),  component.getVersion().getNumber()));

        } catch (ComponentAlreadyExistsException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Component {0}-{1} is not deleted correctly during update. {2}",component.getName(), component.getVersion().getNumber(), e.toString()),Level.WARN);
        }
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes");
            sql += " WHERE COMP_ID in (";
            sql += "select COMP_ID FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
            sql += " WHERE COMP_NM = "
                    + SQLTools.GetStringForSQL(this.getComponent().getName());
            sql += ")";
            sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters");
            sql += " WHERE COMP_ID in (";
            sql += "select COMP_ID FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
            sql += " WHERE COMP_NM = "
                    + SQLTools.GetStringForSQL(this.getComponent().getName());
            sql += ")";
            sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
            sql += " WHERE COMP_ID in (";
            sql += "select COMP_ID FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
            sql += " WHERE COMP_NM = "
                    + SQLTools.GetStringForSQL(this.getComponent().getName());
            sql += ")";
            sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
            sql += ";";
            sql += "\n";

            /*
             * sql += "DELETE FROM " +
             * this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableConfig().getCFG_COMP(); sql
             * += " WHERE COMP_NM = " +
             * this.getFrameworkInstance().getSqlTools().GetStringForSQL(this.getComponent().
             * getName()); sql += ";"; sql += "\n";
             */
        }

        if (!this.verifyComponentConfigurationExists(this.getComponent().getName())) {
            sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components");
            sql += " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) ";
            sql += "VALUES ";
            sql += "(";
            sql += "(" + SQLTools.GetNextIdStatement(
                    this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID") + ")";
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getComponent().getType());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getComponent().getName());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getComponent().getDescription());
            sql += ")";
            sql += ";";
        }

        // add Versions
        String sqlVersions = this.getVersionInsertStatements();
        if (!sqlVersions.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlVersions;
        }

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        // add Attributes
        String sqlAttributes = this.getAttributeInsertStatements();
        if (!sqlAttributes.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlAttributes;
        }

        return sql;
    }

    private String getAttributeInsertStatements() {
        String result = "";

        if (this.getComponent().getAttributes() == null)
            return result;

        for (ComponentAttribute componentAttribute : this.getComponent().getAttributes()) {
            ComponentAttributeConfiguration componentAttributeConfiguration = new ComponentAttributeConfiguration(
                    this.getComponent().getVersion(), componentAttribute, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += componentAttributeConfiguration.getInsertStatement(this.getComponent().getName());
        }

        return result;
    }

    private String getVersionInsertStatements() {
        String result = "";

        if (this.getComponent().getVersion() == null)
            return result;

        ComponentVersionConfiguration componentVersionConfiguration = new ComponentVersionConfiguration(
                this.getComponent().getVersion(), this.getFrameworkInstance());
        result += componentVersionConfiguration.getInsertStatement(this.getComponent().getName());

        return result;
    }

    private String getParameterInsertStatements() {
        String result = "";

        if (this.getComponent().getParameters() == null)
            return result;

        for (ComponentParameter componentParameter : this.getComponent().getParameters()) {
            ComponentParameterConfiguration componentParameterConfiguration = new ComponentParameterConfiguration(
                    this.getComponent().getVersion(), componentParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += componentParameterConfiguration.getInsertStatement(this.getComponent().getName());
        }

        return result;
    }

    private long getLatestVersion(String componentName) {
        long componentVersionNumber = -1;
        CachedRowSet crsComponentVersion = null;
        String queryComponentVersion = "select max(COMP_VRS_NB) as \"MAX_VRS_NB\" from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions") + " a inner join "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components")
                + " b on a.COMP_ID = b.COMP_ID where b.COMP_NM = '" + componentName + "'";
        crsComponentVersion = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            while (crsComponentVersion.next()) {
                componentVersionNumber = crsComponentVersion.getLong("MAX_VRS_NB");
            }
            crsComponentVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (componentVersionNumber == -1) {
            throw new RuntimeException("No component version found for Component (NAME) " + componentName);
        }

        return componentVersionNumber;
    }

    public Optional<Component> getComponent(String componentName) {
        return this.getComponent(componentName, this.getLatestVersion(componentName));
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}