package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ScriptConfiguration {

    private Script script;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ScriptConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public ScriptConfiguration(Script script, FrameworkExecution frameworkExecution) {
        this.setScript(script);
        this.verifyVersionExists();
        this.setFrameworkExecution(frameworkExecution);
    }

    // Checks
    private void verifyVersionExists() {
        if (this.getScript().getVersion() == null) {
            this.getScript().setVersion(new ScriptVersion());
        }
    }

    private boolean verifyScriptConfigurationExists(String scriptName) {
        Script script = new Script();
        CachedRowSet crsScript = null;
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        crsScript = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        try {
            while (crsScript.next()) {
                script.setId(crsScript.getString("SCRIPT_ID"));
                script.setType(crsScript.getString("SCRIPT_TYP_NM"));
                script.setName(scriptName);
                script.setDescription(crsScript.getString("SCRIPT_DSC"));
            }
            crsScript.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (script.getName() == null || script.getName().equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    // Insert
    public String getInsertStatement() {
        String sql = "";
        this.getScript().setId(IdentifierTools.getScriptIdentifier(this.getScript().getName()));

        if (this.exists()) {
            sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
        }

        if (!this.verifyScriptConfigurationExists(this.getScript().getName())) {
            sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts");
            sql += " (SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) ";
            sql += "VALUES ";
            sql += "(";
            sql += SQLTools.GetStringForSQL(this.getScript().getId());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getScript().getType());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getScript().getName());
            sql += ",";
            sql += SQLTools.GetStringForSQL(this.getScript().getDescription());
            sql += ")";
            sql += ";";
        }

        // add ScriptVersion
        String sqlVersion = this.getVersionInsertStatements();
        if (!sqlVersion.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlVersion;
        }

        // add Actions
        String sqlActions = this.getActionInsertStatements();
        if (!sqlActions.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlActions;
        }

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements();
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getVersionInsertStatements() {
        String result = "";

        if (this.getScript().getVersion() == null)
            return result;

        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(
                this.getScript().getVersion(), this.getFrameworkExecution());
        result += scriptVersionConfiguration.getInsertStatement(this.getScript());

        return result;
    }

    private String getActionInsertStatements() {
        String result = "";
        int counter = 0;

        if (this.getScript().getActions() == null)
            return result;

        for (Action action : this.getScript().getActions()) {
            counter++;
            ActionConfiguration actionConfiguration = new ActionConfiguration(action, this.getFrameworkExecution());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += actionConfiguration.getInsertStatement(this.getScript(), counter);
        }

        return result;
    }

    private String getParameterInsertStatements() {
        String result = "";

        if (this.getScript().getParameters() == null)
            return result;

        for (ScriptParameter scriptParameter : this.getScript().getParameters()) {
            ScriptParameterConfiguration scriptParameterConfiguration = new ScriptParameterConfiguration(
                    this.getScript().getVersion(), scriptParameter, this.getFrameworkExecution());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += scriptParameterConfiguration.getInsertStatement(this.getScript());
        }

        return result;
    }

    private long getLatestVersion(String scriptName) {
        long scriptVersionNumber = -1;
        CachedRowSet crsScriptVersion = null;
        String queryScriptVersion = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") + " a inner join "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts")
                + " b on a.script_id = b.script_id where b.script_nm = '" + scriptName + "'";
        crsScriptVersion = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            while (crsScriptVersion.next()) {
                scriptVersionNumber = crsScriptVersion.getLong("MAX_VRS_NB");
            }
            crsScriptVersion.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (scriptVersionNumber == -1) {
            throw new RuntimeException("No script version found for Script (NAME) " + scriptName);
        }

        return scriptVersionNumber;
    }

    public Script getScript(String scriptName) {
        return this.getScript(scriptName, this.getLatestVersion(scriptName));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Script getScript(String scriptName, long scriptVersionNumber) {
        Script script = new Script();
        CachedRowSet crsScript = null;
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        crsScript = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        ActionConfiguration actionConfiguration = new ActionConfiguration(this.getFrameworkExecution());
        ScriptParameterConfiguration scriptParameterConfiguration = new ScriptParameterConfiguration(
                this.getFrameworkExecution());
        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(
                this.getFrameworkExecution());
        try {
            while (crsScript.next()) {
                script.setId(crsScript.getString("SCRIPT_ID"));
                script.setType(crsScript.getString("SCRIPT_TYP_NM"));
                script.setName(scriptName);
                script.setDescription(crsScript.getString("SCRIPT_DSC"));

                // Get the version
                ScriptVersion scriptVersion = scriptVersionConfiguration.getScriptVersion(script.getId(), scriptVersionNumber);
                script.setVersion(scriptVersion);

                // Get the actions
                List<Action> actionList = new ArrayList();
                String queryActions = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB from "
                        + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions")
                        + " where SCRIPT_ID = '" + script.getId() + "' and SCRIPT_VRS_NB = " + scriptVersionNumber
                        + " order by ACTION_NB asc ";
                CachedRowSet crsActions = null;
                crsActions = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryActions, "reader");
                while (crsActions.next()) {
                    actionList.add(actionConfiguration.getAction(script, crsActions.getString("ACTION_ID")));
                }
                script.setActions(actionList);
                crsActions.close();

                // Get parameters
                CachedRowSet crsScriptParameters = null;
                String queryScriptParameters = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM from "
                        + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                        + " where SCRIPT_ID = '" + script.getId() + "' and SCRIPT_VRS_NB = " + scriptVersionNumber;
                crsScriptParameters = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
                        .executeQuery(queryScriptParameters, "reader");
                List<ScriptParameter> scriptParameterList = new ArrayList();
                while (crsScriptParameters.next()) {
                    scriptParameterList.add(scriptParameterConfiguration.getScriptParameter(script.getId(),
                            scriptVersionNumber, crsScriptParameters.getString("SCRIPT_PAR_NM")));
                }
                script.setParameters(scriptParameterList);
                crsScriptParameters.close();

            }
            crsScript.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        if (script.getName() == null || script.getName().equalsIgnoreCase("")) {
            throw new RuntimeException("script.error.notfound");
        }

        return script;
    }

    // Get
    public ListObject getScripts() {
        List<Script> scriptList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " order by SCRIPT_NM ASC";
        crs = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkExecution());
        try {
            String scriptName = "";
            while (crs.next()) {
                scriptName = crs.getString("SCRIPT_NM");
                scriptList.add(scriptConfiguration.getScript(scriptName));
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(
                FrameworkObjectConfiguration.getFrameworkObjectType(new Script()),
                scriptList);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}