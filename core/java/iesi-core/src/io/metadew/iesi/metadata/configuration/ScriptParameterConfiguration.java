package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.definition.ScriptParameter;
import io.metadew.iesi.metadata.definition.ScriptVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ScriptParameterConfiguration {

    private ScriptVersion scriptVersion;
    private ScriptParameter scriptParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptParameterConfiguration(ScriptVersion scriptVersion, ScriptParameter scriptParameter, FrameworkInstance frameworkInstance) {
        this.setScriptVersion(scriptVersion);
        this.setScriptParameter(scriptParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(String scriptId, long scriptVersionNumber, ScriptParameter scriptParameter) {
        return "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ScriptParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(this.getScriptParameter().getName()) + "," +
                SQLTools.GetStringForSQL(this.getScriptParameter().getValue()) + ");";
    }


    public String getInsertStatement(Script script) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ScriptParameters");
        sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("Scripts"), "SCRIPT_ID", "where SCRIPT_NM = '" + script.getName()) + "')";
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

    public ScriptParameter getScriptParameter(String scriptId, long scriptVersionNumber, String scriptParameterName) {
        ScriptParameter scriptParameter = new ScriptParameter();
        CachedRowSet crsScriptParameter = null;
        String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + "' and SCRIPT_VRS_NB = " + scriptVersionNumber + " and SCRIPT_PAR_NM = '" + scriptParameterName + "'";
        crsScriptParameter = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptParameter, "reader");
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