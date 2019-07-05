package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ComponentVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class ComponentVersionConfiguration {

    private ComponentVersion componentVersion;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ComponentVersionConfiguration(ComponentVersion componentVersion, FrameworkInstance frameworkInstance) {
        this.setComponentVersion(componentVersion);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentVersionConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String componentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentVersion().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    public String getDefaultInsertStatement(String componentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL("0");
        sql += ",";
        sql += SQLTools.GetStringForSQL("Default componentVersion");
        sql += ")";
        sql += ";";

        return sql;
    }


    public Optional<ComponentVersion> getComponentVersion(String componentId, long componentVersionNumber) {
        ComponentVersion componentVersion = null;
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_VRS_NB = " + componentVersionNumber;
        CachedRowSet crsComponentVersion = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() == 1) {
                crsComponentVersion.next();
                componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            } else {
            	//TODO fix logging
                //frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple descriptions for component id {0} version {1}. " + "Returning first implementation.", componentId, componentVersion), Level.WARN);
                crsComponentVersion.next();
                componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            }
            crsComponentVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
        return Optional.of(componentVersion);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public ComponentVersion getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(ComponentVersion componentVersion) {
        this.componentVersion = componentVersion;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}