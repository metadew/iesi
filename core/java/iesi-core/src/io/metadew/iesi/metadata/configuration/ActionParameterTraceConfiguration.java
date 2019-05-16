package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;

public class ActionParameterTraceConfiguration {

	private ActionParameter actionParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ActionParameterTraceConfiguration(ActionParameter actionParameter, FrameworkExecution frameworkExecution) {
		this.setActionParameter(actionParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ActionParameterTraceConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String runId, long processId, String actionId) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionDesignParameterTraces");
		sql += " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(runId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(processId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(actionId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getActionParameter().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getActionParameter().getValue());
		sql += ")";
		sql += ";";

		return sql;
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