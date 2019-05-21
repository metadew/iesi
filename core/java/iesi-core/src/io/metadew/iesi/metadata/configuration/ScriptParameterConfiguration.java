package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptParameterConfiguration {

	private ScriptVersion scriptVersion;
	private ScriptParameter scriptParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ScriptParameterConfiguration(ScriptVersion scriptVersion, ScriptParameter scriptParameter, FrameworkExecution frameworkExecution) {
		this.setScriptVersion(scriptVersion);
		this.setScriptParameter(scriptParameter);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ScriptParameterConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String scriptName) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
				.getTableNameByLabel("ScriptParameters");
		sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
				.getTableNameByLabel("Scripts"), "SCRIPT_ID", "where SCRIPT_NM = '"+ scriptName) + "')";
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

	public ScriptParameter getScriptParameter(long scriptId, long scriptVersionNumber, String scriptParameterName) {
		ScriptParameter scriptParameter = new ScriptParameter();
		CachedRowSet crsScriptParameter = null;
		String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
				+ " where SCRIPT_ID = " + scriptId + " and SCRIPT_VRS_NB = " + scriptVersionNumber + " and SCRIPT_PAR_NM = '" + scriptParameterName + "'";
		crsScriptParameter = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptParameter, "reader");
		try {
			while (crsScriptParameter.next()) {
				scriptParameter.setName(scriptParameterName);
				scriptParameter.setValue(crsScriptParameter.getString("SCRIPT_PAR_VAL"));
			}
			crsScriptParameter.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return scriptParameter;
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