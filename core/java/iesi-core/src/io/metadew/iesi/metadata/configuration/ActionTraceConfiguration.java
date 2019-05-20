package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.tools.IdentifierTools;

public class ActionTraceConfiguration {

	private Action action;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ActionTraceConfiguration(Action action, FrameworkExecution frameworkExecution) {
		this.setAction(action);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ActionTraceConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String runId, long processId, Script script, int actionNumber) {
		String actionId = IdentifierTools.getActionIdentifier(this.getAction().getName());
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository()
				.getTableNameByLabel("ActionDesignTraces");
		sql += " (RUN_ID, PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(runId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(processId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(script.getId());
		sql += ",";
		sql += SQLTools.GetStringForSQL(script.getVersion().getNumber());
		sql += ",";
		sql += SQLTools.GetStringForSQL(actionId);
		sql += ",";
		sql += SQLTools.GetStringForSQL(actionNumber);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getDescription());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getComponent());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getIteration());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getCondition());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getRetries());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getErrorExpected());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getErrorStop());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(runId, processId, script, actionId);
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}
	
	private String getParameterInsertStatements(String runId, long processId, Script script, String actionId) {
		String result = "";

		for (ActionParameter actionParameter : this.getAction().getParameters()) {
			ActionParameterTraceConfiguration actionParameterTraceConfiguration = new ActionParameterTraceConfiguration(actionParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += actionParameterTraceConfiguration.getInsertStatement(runId, processId, script, actionId);
		}

		return result;
	}

	// Getters and Setters
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
	

	
}