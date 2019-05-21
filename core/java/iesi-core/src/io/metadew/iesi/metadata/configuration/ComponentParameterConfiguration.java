package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.ComponentVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ComponentParameterConfiguration {

    private ComponentVersion componentVersion;
    private ComponentParameter componentParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ComponentParameterConfiguration(ComponentVersion componentVersion, ComponentParameter componentParameter, FrameworkExecution frameworkExecution) {
        this.setComponentVersion(componentVersion);
        this.setComponentParameter(componentParameter);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ComponentParameterConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String componentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters");
        sql += " (COMP_ID, COMP_VRS_NB, COMP_PAR_NM, COMP_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ComponentParameter getComponentParameter(long componentId, String componentParameterName, long componentVersionNumber) {
        ComponentParameter componentParameter = new ComponentParameter();
        CachedRowSet crsComponentParameter = null;
        String queryComponentParameter = "select COMP_ID, COMP_PAR_NM, COMP_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentParameters")
                + " where COMP_ID = " + componentId + " and COMP_PAR_NM = '" + componentParameterName + "'" + " and COMP_VRS_NB = " + componentVersionNumber;
        crsComponentParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentParameter, "reader");
        try {
            while (crsComponentParameter.next()) {
                componentParameter.setName(componentParameterName);
                componentParameter.setValue(crsComponentParameter.getString("COMP_PAR_VAL"));
            }
            crsComponentParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return componentParameter;
    }

    // Getters and Setters
    public ComponentParameter getComponentParameter() {
        return componentParameter;
    }

    public void setComponentParameter(ComponentParameter componentParameter) {
        this.componentParameter = componentParameter;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ComponentVersion getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(ComponentVersion componentVersion) {
        this.componentVersion = componentVersion;
    }

}