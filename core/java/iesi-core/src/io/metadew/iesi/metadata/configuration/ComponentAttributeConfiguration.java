package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentVersion;

public class ComponentAttributeConfiguration {

	private ComponentVersion componentVersion;
	private ComponentAttribute componentAttribute;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ComponentAttributeConfiguration(ComponentVersion componentVersion, ComponentAttribute componentAttribute, FrameworkExecution frameworkExecution) {
		this.setComponentVersion(componentVersion);
		this.setComponentAttribute(componentAttribute);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ComponentAttributeConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String componentName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes");
		sql += " (COMP_ID, COMP_VRS_NB, ENV_NM, COMP_ATT_NM, COMP_ATT_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Components"), "COMP_ID", "where COMP_NM = '"+ componentName) + "')";
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
		String queryComponentAttribute = "select COMP_ID, COMP_ATT_NM, ENV_NM, COMP_ATT_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ComponentAttributes")
				+ " where COMP_ID = " + componentId + " and COMP_ATT_NM = '" + componentAttributeName + "'" + " and COMP_VRS_NB = " + componentVersionNumber;
		crsComponentAttribute = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryComponentAttribute, "reader");
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}