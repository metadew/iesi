package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.definition.ScriptVersion;

public class ScriptVersionConfiguration {

	private ScriptVersion scriptVersion;
	private FrameworkExecution frameworkExecution;

	// Constructors	
	public ScriptVersionConfiguration(ScriptVersion scriptVersion, FrameworkExecution frameworkExecution) {
		this.setScriptVersion(scriptVersion);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ScriptVersionConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(Script script) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
		sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(script.getId());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getScriptVersion().getNumber());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getScriptVersion().getDescription());
		sql += ")";
		sql += ";";

		return sql;
	}

	
	public String getDefaultInsertStatement(String scriptName) {
		String sql = "";

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
		sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts"), "SCRIPT_ID", "where SCRIPT_NM = '"+ scriptName) + "')";
		sql += ",";
		sql += SQLTools.GetStringForSQL("0");
		sql += ",";
		sql += SQLTools.GetStringForSQL("Default scriptVersion");
		sql += ")";
		sql += ";";

		return sql;
	}

	public ScriptVersion getScriptVersion(String scriptId, long scriptVersionNumber) {
		ScriptVersion scriptVersion = new ScriptVersion();
		CachedRowSet crsScriptVersion = null;
		String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
				+ " where SCRIPT_ID = '" + scriptId + "' and SCRIPT_VRS_NB = " + scriptVersionNumber;
		crsScriptVersion = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
		try {
			while (crsScriptVersion.next()) {
				scriptVersion.setNumber(scriptVersionNumber);
				scriptVersion.setDescription(crsScriptVersion.getString("SCRIPT_VRS_DSC"));
			}
			crsScriptVersion.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return scriptVersion;
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