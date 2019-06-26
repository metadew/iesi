package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptVersionTraceConfiguration {

    private ScriptVersion scriptVersion;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptVersionTraceConfiguration(ScriptVersion scriptVersion, FrameworkInstance frameworkInstance) {
        this.setScriptVersion(scriptVersion);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptVersionTraceConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String runId, long processId, String scriptId) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignVersionTraces");
        sql += " (RUN_ID, PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) ";
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
        sql += SQLTools.GetStringForSQL(this.getScriptVersion().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    // Getters and Setters
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