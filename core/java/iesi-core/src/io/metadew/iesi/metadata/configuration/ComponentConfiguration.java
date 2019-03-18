package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.ComponentVersion;

public class ComponentConfiguration {

	private Component component;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ComponentConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public ComponentConfiguration(Component component, FrameworkExecution frameworkExecution) {
		this.setComponent(component);
		this.verifyVersionExists();
		this.setFrameworkExecution(frameworkExecution);
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
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components") + " where COMP_NM = '"
				+ componentName + "'";
		crsComponent = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryComponent);
		try {
			while (crsComponent.next()) {
				component.setId(crsComponent.getLong("COMP_ID"));
				component.setType(crsComponent.getString("COMP_TYP_NM"));
				component.setName(componentName);
				component.setDescription(crsComponent.getString("COMP_DSC"));
			}
			crsComponent.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (component.getName() == null || component.getName().equals("")) {
			return false;
		} else {
			return true;
		}
	}

	// Insert
	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentAttributes");
			sql += " WHERE COMP_ID in (";
			sql += "select COMP_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components");
			sql += " WHERE COMP_NM = "
					+ SQLTools.GetStringForSQL(this.getComponent().getName());
			sql += ")";
			sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentParameters");
			sql += " WHERE COMP_ID in (";
			sql += "select COMP_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components");
			sql += " WHERE COMP_NM = "
					+ SQLTools.GetStringForSQL(this.getComponent().getName());
			sql += ")";
			sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentVersions");
			sql += " WHERE COMP_ID in (";
			sql += "select COMP_ID FROM " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components");
			sql += " WHERE COMP_NM = "
					+ SQLTools.GetStringForSQL(this.getComponent().getName());
			sql += ")";
			sql += " AND COMP_VRS_NB = " + this.getComponent().getVersion().getNumber();
			sql += ";";
			sql += "\n";

			/*
			 * sql += "DELETE FROM " +
			 * this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getTableConfig().getCFG_COMP(); sql
			 * += " WHERE COMP_NM = " +
			 * this.getFrameworkExecution().getSqlTools().GetStringForSQL(this.getComponent().
			 * getName()); sql += ";"; sql += "\n";
			 */
		}

		if (!this.verifyComponentConfigurationExists(this.getComponent().getName())) {
			sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components");
			sql += " (COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC) ";
			sql += "VALUES ";
			sql += "(";
			sql += "(" + SQLTools.GetNextIdStatement(
					this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components"), "COMP_ID") + ")";
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
		if (!sqlVersions.equals("")) {
			sql += "\n";
			sql += sqlVersions;
		}

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements();
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		// add Attributes
		String sqlAttributes = this.getAttributeInsertStatements();
		if (!sqlAttributes.equals("")) {
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
					this.getComponent().getVersion(), componentAttribute, this.getFrameworkExecution());
			if (!result.equals(""))
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
				this.getComponent().getVersion(), this.getFrameworkExecution());
		result += componentVersionConfiguration.getInsertStatement(this.getComponent().getName());

		return result;
	}

	private String getParameterInsertStatements() {
		String result = "";

		if (this.getComponent().getParameters() == null)
			return result;

		for (ComponentParameter componentParameter : this.getComponent().getParameters()) {
			ComponentParameterConfiguration componentParameterConfiguration = new ComponentParameterConfiguration(
					this.getComponent().getVersion(), componentParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += componentParameterConfiguration.getInsertStatement(this.getComponent().getName());
		}

		return result;
	}
	
	private long getLatestVersion(String componentName) {
		long componentVersionNumber = -1;
		CachedRowSet crsComponentVersion = null;
		String queryComponentVersion = "select max(COMP_VRS_NB) as \"MAX_VRS_NB\" from "
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentVersions") + " a inner join "
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components")
				+ " b on a.COMP_ID = b.COMP_ID where b.COMP_NM = '" + componentName + "'";
		crsComponentVersion = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryComponentVersion);
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

	public Component getComponent(String componentName) {
		return this.getComponent(componentName, this.getLatestVersion(componentName));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Component getComponent(String componentName, long componentVersionNumber) {
		Component component = new Component();
		CachedRowSet crsComponent = null;
		String queryComponent = "select COMP_ID, COMP_TYP_NM, COMP_NM, COMP_DSC from "
				+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Components") + " where COMP_NM = '"
				+ componentName + "'";
		crsComponent = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryComponent);
		ComponentAttributeConfiguration componentAttributeConfiguration = new ComponentAttributeConfiguration(
				this.getFrameworkExecution());
		ComponentVersionConfiguration componentVersionConfiguration = new ComponentVersionConfiguration(
				this.getFrameworkExecution());
		ComponentParameterConfiguration componentParameterConfiguration = new ComponentParameterConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsComponent.next()) {
				component.setId(crsComponent.getLong("COMP_ID"));
				component.setType(crsComponent.getString("COMP_TYP_NM"));
				component.setName(componentName);
				component.setDescription(crsComponent.getString("COMP_DSC"));

				// Get the version
				ComponentVersion componentVersion = componentVersionConfiguration.getComponentVersion(component.getId(),
						componentVersionNumber);
				component.setVersion(componentVersion);

				// Get parameters
				CachedRowSet crsComponentParameters = null;
				String queryComponentParameters = "select COMP_ID, COMP_PAR_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentParameters")
						+ " where COMP_ID = " + component.getId() + " and COMP_VRS_NB = " + componentVersionNumber;
				crsComponentParameters = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
						.executeQuery(queryComponentParameters);
				List<ComponentParameter> componentParameterList = new ArrayList();
				while (crsComponentParameters.next()) {
					componentParameterList.add(componentParameterConfiguration.getComponentParameter(component.getId(),
							crsComponentParameters.getString("COMP_PAR_NM"), componentVersionNumber));
				}
				component.setParameters(componentParameterList);
				crsComponentParameters.close();

				// Get attributes
				CachedRowSet crsComponentAttributes = null;
				String queryComponentAttributes = "select COMP_ID, COMP_ATT_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ComponentAttributes")
						+ " where COMP_ID = " + component.getId() + " and COMP_VRS_NB = " + componentVersionNumber;
				crsComponentAttributes = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration()
						.executeQuery(queryComponentAttributes);
				List<ComponentAttribute> componentAttributeList = new ArrayList();
				while (crsComponentAttributes.next()) {
					componentAttributeList.add(componentAttributeConfiguration.getComponentAttribute(component.getId(),
							crsComponentAttributes.getString("COMP_ATT_NM"), componentVersionNumber));
				}
				component.setAttributes(componentAttributeList);
				crsComponentAttributes.close();
			}
			crsComponent.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (component.getName() == null || component.getName().equals("")) {
			throw new RuntimeException("Component (NAME) " + componentName + " does not exist");
		}

		return component;
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}