package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptParameterTraceConfiguration {

    private ScriptVersion scriptVersion;
    private ScriptParameter scriptParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptParameterTraceConfiguration(ScriptVersion scriptVersion, ScriptParameter scriptParameter, FrameworkInstance frameworkInstance) {
        this.setScriptVersion(scriptVersion);
        this.setScriptParameter(scriptParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptParameterTraceConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String runId, long processId, String scriptId) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getTraceMetadataRepository()
                .getTableNameByLabel("ScriptDesignParameterTraces");
        sql += " (RUN_ID, PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(runId);
        sql += ",";
        sql += SQLTools.GetStringForSQL(processId);
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptId);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScriptVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScriptParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScriptParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    // Getters and Setters
    public ScriptParameter getScriptParameter() {
        return scriptParameter;
    }

    public void setScriptParameter(ScriptParameter scriptParameter) {
        this.scriptParameter = scriptParameter;
    }

    public ScriptVersion getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ScriptVersion scriptVersion) {
        this.scriptVersion = scriptVersion;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}