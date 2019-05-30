package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ComponentAttributeConfiguration {

    private ComponentVersion componentVersion;
    private ComponentAttribute componentAttribute;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ComponentAttributeConfiguration(ComponentVersion componentVersion, ComponentAttribute componentAttribute, FrameworkInstance frameworkInstance) {
        this.setComponentVersion(componentVersion);
        this.setComponentAttribute(componentAttribute);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ComponentAttributeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String componentName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes");
        sql += " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '" + componentName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentAttribute().getEnvironment());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentAttribute().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getComponentAttribute().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ComponentAttribute getComponentAttribute(long componentId, String componentAttributeName, long componentVersionNumber) {
        ComponentAttribute componentAttribute = new ComponentAttribute();
        CachedRowSet crsComponentAttribute = null;
        String queryComponentAttribute = "select COMP_ID, COMP_ATT_NM, ENV_NM, COMP_ATT_VAL from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
                + " where COMP_ID = " + SQLTools.GetStringForSQL(componentId) + " and COMP_ATT_NM = '" + componentAttributeName + "'" + " and COMP_VRS_NB = " + componentVersionNumber;
        crsComponentAttribute = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentAttribute, "reader");
        try {
            while (crsComponentAttribute.next()) {
                componentAttribute.setName(componentAttributeName);
                componentAttribute.setEnvironment(crsComponentAttribute.getString("ENV_NM"));
                componentAttribute.setValue(crsComponentAttribute.getString("COMP_ATT_VAL"));
            }
            crsComponentAttribute.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return componentAttribute;
    }

    // Getters and Setters
    public ComponentAttribute getComponentAttribute() {
        return componentAttribute;
    }

    public void setComponentAttribute(ComponentAttribute componentAttribute) {
        this.componentAttribute = componentAttribute;
    }

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