package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;

public class ActionParameterTraceConfiguration {

    private ActionParameter actionParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ActionParameterTraceConfiguration(ActionParameter actionParameter, FrameworkInstance frameworkInstance) {
        this.setActionParameter(actionParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ActionParameterTraceConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String runId, long processId, Script script, String actionId) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionDesignParameterTraces");
        sql += " (RUN_ID, PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) ";
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}