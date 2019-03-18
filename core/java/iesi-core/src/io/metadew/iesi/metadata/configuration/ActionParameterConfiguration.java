package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;

public class ActionParameterConfiguration {

	private ActionParameter actionParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ActionParameterConfiguration(ActionParameter actionParameter, FrameworkExecution frameworkExecution) {
		this.setActionParameter(actionParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ActionParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String scriptName, long scriptVersionNumber, String actionName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ActionParameters");
		sql += " (ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Actions"), "ACTION_ID", "where ACTION_NM = '"+ actionName + "' and SCRIPT_ID = (" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Scripts"), "SCRIPT_ID", "SCRIPT_NM", scriptName)) + ") and SCRIPT_VRS_NB =" + scriptVersionNumber + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getActionParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getActionParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
	}

	public ActionParameter getActionParameter(long actionId, String actionParameterName) {
		ActionParameter actionParameter = new ActionParameter();
		CachedRowSet crsActionParameter = null;
		String queryActionParameter = "select ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ActionParameters")
				+ " where ACTION_ID = " + actionId + " and ACTION_PAR_NM = '" + actionParameterName + "'";
		crsActionParameter = this.getFrameworkExecution().getMetadataControl().getDesignRepositoryConfiguration().executeQuery(queryActionParameter);
		try {
			while (crsActionParameter.next()) {
				actionParameter.setName(actionParameterName);
				actionParameter.setValue(crsActionParameter.getString("ACTION_PAR_VAL"));
			}
			crsActionParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return actionParameter;
	}

	// Getters and Setters
	public ActionParameter getActionParameter() {
		return actionParameter;
	}

	public void setActionParameter(ActionParameter actionParameter) {
		this.actionParameter = actionParameter;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}


}