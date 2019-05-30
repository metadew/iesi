package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptVersion;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class ScriptVersionConfiguration {

    private ScriptVersion scriptVersion;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptVersionConfiguration(ScriptVersion scriptVersion, FrameworkInstance frameworkInstance) {
        this.setScriptVersion(scriptVersion);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptVersionConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String scriptId, ScriptVersion scriptVersion) {
        return "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + ", " +
                scriptVersion.getNumber() + ", " +
                scriptVersion.getDescription() + ");";
    }

    // Insert
    public String getInsertStatement(String scriptName) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
        sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts"), "SCRIPT_ID", "where SCRIPT_NM = '" + scriptName) + "')";
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

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
        sql += " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts"), "SCRIPT_ID", "where SCRIPT_NM = '" + scriptName) + "')";
        sql += ",";
        sql += SQLTools.GetStringForSQL("0");
        sql += ",";
        sql += SQLTools.GetStringForSQL("Default scriptVersion");
        sql += ")";
        sql += ";";

        return sql;
    }

    public Optional<ScriptVersion> getScriptVersion(String scriptId, long scriptVersionNumber) {
        String queryScriptVersion = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC from " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + scriptVersionNumber;
        CachedRowSet crsScriptVersion = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                return Optional.empty();
            } else if (crsScriptVersion.size() > 1) {
                //TODO: log
            }
            crsScriptVersion.next();
            ScriptVersion scriptVersion = new ScriptVersion(scriptId, scriptVersionNumber, crsScriptVersion.getString("SCRIPT_VRS_DSC"));
            crsScriptVersion.close();
            return Optional.of(scriptVersion);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
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