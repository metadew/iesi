package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentVersion;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Optional;

public class ComponentVersionConfiguration {

    private ComponentVersion componentVersion;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ComponentVersionConfiguration(ComponentVersion componentVersion, FrameworkExecution frameworkExecution) {
        this.setComponentVersion(componentVersion);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ComponentVersionConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String componentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
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

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL("0");
        sql += ",";
        sql += SQLTools.GetStringForSQL("Default componentVersion");
        sql += ")";
        sql += ";";

        return sql;
    }


    public Optional<ComponentVersion> getComponentVersion(long componentId, long componentVersionNumber) {
        ComponentVersion componentVersion = null;
        String queryComponentVersion = "select COMP_ID, COMP_VRS_NB, COMP_VRS_DSC from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentVersions")
                + " where COMP_ID = " + componentId + " and COMP_VRS_NB = " + componentVersionNumber;
        CachedRowSet crsComponentVersion = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentVersion, "reader");
        try {
            if (crsComponentVersion.size() == 0) {
                return Optional.empty();
            } else if (crsComponentVersion.size() == 1) {
                crsComponentVersion.next();
                componentVersion = new ComponentVersion(componentVersionNumber, crsComponentVersion.getString("COMP_VRS_DSC"));
            } else {
                frameworkExecution.getFrameworkLog().log(MessageFormat.format("component.version=found multiple descriptions for component id {0} version {1}. " +
                        "Returning first implementation.", componentId, componentVersion), Level.WARN);
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}