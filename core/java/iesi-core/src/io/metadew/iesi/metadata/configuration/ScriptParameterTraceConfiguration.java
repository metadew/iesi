package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptParameterTraceConfiguration {

	private ScriptVersion scriptVersion;
	private ScriptParameter scriptParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ScriptParameterTraceConfiguration(ScriptVersion scriptVersion, ScriptParameter scriptParameter, FrameworkExecution frameworkExecution) {
		this.setScriptVersion(scriptVersion);
		this.setScriptParameter(scriptParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ScriptParameterTraceConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String runId, long processId, String scriptId) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository()
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
	
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public ScriptVersion getScriptVersion() {
		return scriptVersion;
	}

	public void setScriptVersion(ScriptVersion scriptVersion) {
		this.scriptVersion = scriptVersion;
	}

}