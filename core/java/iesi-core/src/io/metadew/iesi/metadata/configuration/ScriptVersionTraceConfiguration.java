package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptVersionTraceConfiguration {

    private ScriptVersion scriptVersion;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ScriptVersionTraceConfiguration(ScriptVersion scriptVersion, FrameworkExecution frameworkExecution) {
        this.setScriptVersion(scriptVersion);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ScriptVersionTraceConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public String getInsertStatement(String runId, long processId, String scriptId) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptDesignVersionTraces");
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}