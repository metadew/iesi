package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.*;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptConfiguration extends MetadataConfiguration {

    private Script script;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptConfiguration(Script script, FrameworkInstance frameworkInstance) {
        this.setScript(script);
        this.verifyVersionExists();
        this.setFrameworkInstance(frameworkInstance);
    }

    // Abstract method implementations
	@Override
	public List<Script> getAllObjects() {
		return this.getAllScripts();
	}

    // Checks
    private void verifyVersionExists() {
        if (this.getScript().getVersion() == null) {
            this.getScript().setVersion(new ScriptVersion());
        }
    }

    public boolean exists(String scriptName, long versionNumber) {
        return getScript(scriptName, versionNumber).isPresent();
    }

    public boolean exists(String scriptName) {
        return getScriptByName(scriptName).isEmpty();
    }

    public boolean exists(Script script) {
        return exists(script.getName(), script.getVersion().getNumber());
    }

    public List<Script> getAllScripts() {
        List<Script> scripts = new ArrayList<>();
        String queryScript = "select SCRIPT_ID, SCRIPT_NM from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts");
        CachedRowSet crsScript = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");

        try {
            while (crsScript.next()) {
                scripts.addAll(getScriptByName(crsScript.getString("SCRIPT_NM")));
            }
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return scripts;
    }

    public List<Script> getScriptByName(String scriptName) {
        List<Script> scripts = new ArrayList<>();
        String queryScript = "select SCRIPT_ID from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        CachedRowSet crsScript = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        try {
            if (crsScript.size() == 0) {
                return scripts;
            } else if (crsScript.size() > 1) {
                // TODO: log;
            }
            crsScript.next();
            String queryScriptVersions = "select SCRIPT_VRS_NB from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") + " where SCRIPT_ID = "
                    + SQLTools.GetStringForSQL(crsScript.getString("SCRIPT_ID"));
            CachedRowSet crsScriptVersions = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptVersions, "reader");
            while (crsScriptVersions.next()) {
                Optional<Script> script = getScript(scriptName, crsScriptVersions.getLong("SCRIPT_VRS_NB"));
                if (script.isPresent()) {
                    scripts.add(getScript(scriptName, crsScriptVersions.getLong("SCRIPT_VRS_NB")).get());
                } else {
                    // TODO: log
                }
            }
            crsScriptVersions.close();
            crsScript.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return scripts;
    }

    public void deleteScript(Script script) throws ScriptDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting script {0}-{1}.", script.getName(), script.getVersion().getNumber()), Level.TRACE);
        if (!exists(script)) {
            throw new ScriptDoesNotExistException(
                    MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be deleted",
                            script.getName(), script.getVersion().getNumber()));
        }

        String deleteQuery = getDeleteStatement(script);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeUpdate(deleteQuery);
    }

    public void deleteScriptByName(String scriptName) throws ScriptDoesNotExistException {
        for (Script script : getScriptByName(scriptName)) {
            deleteScript(script);
        }
    }

    public void insertScript(Script script) throws ScriptAlreadyExistsException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()), Level.TRACE);
        if (exists(script)) {
            throw new ScriptAlreadyExistsException(MessageFormat.format(
                    "Script {0}-{1} already exists", script.getName(), script.getVersion().getNumber()));
        }
        String insertStatement = getInsertStatement(script);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeUpdate(insertStatement);
    }

    public void updateScript(Script script) throws ScriptDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating script {0}-{1}.", script.getName(), script.getVersion().getNumber()), Level.TRACE);
        try {
            deleteScript(script);
            insertScript(script);
        } catch (ScriptDoesNotExistException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be updated",script.getName(), script.getVersion().getNumber()),Level.TRACE);
            throw e;
            // throw new ComponentDoesNotExistException(MessageFormat.format(
            //        "Component {0}-{1} is not present in the repository so cannot be updated", component.getName(),  component.getVersion().getNumber()));

        } catch (ScriptAlreadyExistsException e) {
        	// TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Script {0}-{1} is not deleted correctly during update. {2}",script.getName(), script.getVersion().getNumber(), e.toString()),Level.WARN);
        }
    }

    private String getInsertStatement(Script script) {
        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(this.getFrameworkInstance());
        ScriptParameterConfiguration scriptParameterConfiguration = new ScriptParameterConfiguration(this.getFrameworkInstance());
        ActionConfiguration actionConfiguration = new ActionConfiguration(this.getFrameworkInstance());
        StringBuilder sql = new StringBuilder();

        if (getScriptByName(script.getName()).size() == 0) {
            sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts"));
            sql.append(" (SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (");
            sql.append(SQLTools.GetStringForSQL(script.getId())).append(",");
            sql.append(SQLTools.GetStringForSQL(script.getType())).append(",");
            sql.append(SQLTools.GetStringForSQL(script.getName())).append(",");
            sql.append(SQLTools.GetStringForSQL(script.getDescription())).append(");");
        }
        // add version
        sql.append(scriptVersionConfiguration.getInsertStatement(script.getId(), script.getVersion()));

        // add Parameters
        for (ScriptParameter scriptParameter :script.getParameters()) {
            sql.append(scriptParameterConfiguration.getInsertStatement(script.getId(), script.getVersion().getNumber(), scriptParameter));
            sql.append("\n");
        }

        // add actions
        for (Action action : script.getActions()) {
            sql.append(actionConfiguration.getInsertStatement(script.getId(), script.getVersion().getNumber(), action));
            sql.append("\n");
        }

        return sql.toString();
    }

    private String getDeleteStatement(Script script) {
        // delete parameters
        StringBuilder deleteQuery = new StringBuilder("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters"));
        deleteQuery.append(" WHERE SCRIPT_ID = ").append(SQLTools.GetStringForSQL(script.getId())).append(" AND SCRIPT_VRS_NB = ").append(SQLTools.GetStringForSQL(script.getVersion().getNumber())).append(";\n");
        // delete version
        deleteQuery.append("DELETE FROM ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions"));
        deleteQuery.append(" WHERE SCRIPT_ID = ").append(SQLTools.GetStringForSQL(script.getId())).append(" AND SCRIPT_VRS_NB = ").append(SQLTools.GetStringForSQL(script.getVersion().getNumber())).append(";\n");

        // delete actions
        for (Action action : script.getActions()) {
            deleteQuery.append("DELETE FROM ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions"));
            deleteQuery.append(" WHERE ACTION_ID = ").append(SQLTools.GetStringForSQL(action.getId())).append(";\n");

            deleteQuery.append("DELETE FROM ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters"));
            deleteQuery.append(" WHERE ACTION_ID = ").append(SQLTools.GetStringForSQL(action.getId())).append(";\n");

        }

        // delete script info if last version
        String countQuery = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB ) AS total_versions FROM "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID != " + SQLTools.GetStringForSQL(script.getId()) + ";";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                deleteQuery.append("DELETE FROM ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts"));
                deleteQuery.append(" WHERE SCRIPT_ID = ").append(SQLTools.GetStringForSQL(script.getId())).append(";\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deleteQuery.toString();
    }

    private boolean verifyScriptConfigurationExists(String scriptName) {
        Script script = new Script();
        CachedRowSet crsScript = null;
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        crsScript = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
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
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
            sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions");
            sql += " WHERE SCRIPT_ID = '" + this.getScript().getId() + "'";
            sql += " AND SCRIPT_VRS_NB = " + this.getScript().getVersion().getNumber();
            sql += ";";
            sql += "\n";
        }

        if (!this.verifyScriptConfigurationExists(this.getScript().getName())) {
            sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts");
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
                this.getScript().getVersion(), this.getFrameworkInstance());
        result += scriptVersionConfiguration.getInsertStatement(this.getScript().getName());

        return result;
    }

    private String getActionInsertStatements() {
        String result = "";
        int counter = 0;

        if (this.getScript().getActions() == null)
            return result;

        for (Action action : this.getScript().getActions()) {
            counter++;
            ActionConfiguration actionConfiguration = new ActionConfiguration(action, this.getFrameworkInstance());
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
                    this.getScript().getVersion(), scriptParameter, this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += scriptParameterConfiguration.getInsertStatement(this.getScript());
        }

        return result;
    }

    private Optional<Long> getLatestVersion(String scriptName) {
        // TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Fetching latest version for script {0}.", scriptName), Level.DEBUG);
        String queryScriptVersion = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") + " a inner join "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts")
                + " b on a.script_id = b.script_id where b.script_nm = '" + scriptName + "'";
        CachedRowSet crsScriptVersion = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
        try {
            if (crsScriptVersion.size() == 0) {
                crsScriptVersion.close();
                return Optional.empty();
            } else {
                crsScriptVersion.next();
                long latestScriptVersion = crsScriptVersion.getLong("MAX_VRS_NB");
                crsScriptVersion.close();
                return Optional.of(latestScriptVersion);
            }
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
    }

    public Optional<Script> getScript(String scriptName) {
        Optional<Long> latestVersion = getLatestVersion(scriptName);
        if (latestVersion.isPresent()) {
            return getScript(scriptName, latestVersion.get());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Script> getScript(String scriptName, long versionNumber) {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Fetching script {0}-{1}.", scriptName, versionNumber), Level.DEBUG);
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        CachedRowSet crsScript = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        ActionConfiguration actionConfiguration = new ActionConfiguration(this.getFrameworkInstance());
        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(
                this.getFrameworkInstance());
        try {
            if (crsScript.size() == 0) {
                throw new RuntimeException("script.error.notfound");
            } else if (crsScript.size() > 1) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", script.getName(), script.getVersion().getNumber()), Level.DEBUG);
            }
            crsScript.next();
            String scriptId = crsScript.getString("SCRIPT_ID");

            // Get the version
            Optional<ScriptVersion> scriptVersion = scriptVersionConfiguration.getScriptVersion(scriptId, versionNumber);
            if (!scriptVersion.isPresent()) {
                //TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Cannot find version {1} for script {0}.", script.getName(), script.getVersion().getNumber()), Level.WARN);
                return Optional.empty();
            }

            // Get the actions
            List<Action> actions = new ArrayList<>();
            String queryActions = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + versionNumber
                    + " order by ACTION_NB asc ";
            CachedRowSet crsActions = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryActions, "reader");

            while (crsActions.next()) {
                Optional<Action> action = actionConfiguration.getAction(scriptId, scriptVersion.get().getNumber(), crsActions.getString("ACTION_ID"));
                if (action.isPresent()) {
                    actions.add(action.get());
                } else {
                    //TODO fix logging
                	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Cannot retreive action {0} for script {1}-{2}.", crsActions.getString("ACTION_ID"), scriptName, versionNumber), Level.DEBUG);
                }
            }
            crsActions.close();

            // Get parameters
            String queryScriptParameters = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + versionNumber;
            CachedRowSet crsScriptParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                    .executeQuery(queryScriptParameters, "reader");
            List<ScriptParameter> scriptParameters = new ArrayList<>();
            while (crsScriptParameters.next()) {
                scriptParameters.add(new ScriptParameter(crsScriptParameters.getString("SCRIPT_PAR_NM"),
                        crsScriptParameters.getString("SCRIPT_PAR_VAL")));
            }
            crsScriptParameters.close();
            Script script = new Script(scriptId, crsScript.getString("SCRIPT_TYP_NM"), scriptName, crsScript.getString("SCRIPT_DSC"),
                    scriptVersion.get(), scriptParameters, actions);
            crsScript.close();
            return Optional.of(script);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
    }


    // Get
    public ListObject getScripts() {
        List<Script> scriptList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " order by SCRIPT_NM ASC";
        crs = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(query, "reader");
        ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkInstance());
        try {
            String scriptName = "";
            while (crs.next()) {
                scriptName = crs.getString("SCRIPT_NM");
                scriptList.add(scriptConfiguration.getScript(scriptName).get());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}