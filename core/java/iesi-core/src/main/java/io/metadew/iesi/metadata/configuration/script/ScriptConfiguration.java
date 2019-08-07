package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ScriptAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.ListObject;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger();

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
        LOGGER.info(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
        List<Script> scripts = new ArrayList<>();
        String queryScript = "select SCRIPT_ID from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                " where SCRIPT_NM = "
                + SQLTools.GetStringForSQL(scriptName) + ";";
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

            System.out.println(StackTrace.toString());
        }
        return scripts;
    }

    public void deleteScript(Script script) throws ScriptDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (!exists(script)) {
            throw new ScriptDoesNotExistException(
                    MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be deleted",
                            script.getName(), script.getVersion().getNumber()));
        }

        List<String> deleteQuery = getDeleteStatement(script);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeBatch(deleteQuery);
    }

    public void deleteScriptByName(String scriptName) throws ScriptDoesNotExistException {
        for (Script script : getScriptByName(scriptName)) {
            deleteScript(script);
        }
    }

    public void insertScript(Script script) throws ScriptAlreadyExistsException {
       LOGGER.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (exists(script)) {
            throw new ScriptAlreadyExistsException(MessageFormat.format(
                    "Script {0}-{1} already exists", script.getName(), script.getVersion().getNumber()));
        }
        List<String> insertStatement = getInsertStatement(script);
        this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeBatch(insertStatement);
    }

    public void updateScript(Script script) throws ScriptDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Updating script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
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
        	LOGGER.warn(MessageFormat.format("Script {0}-{1} is not deleted correctly during update. {2}",script.getName(), script.getVersion().getNumber(), e.toString()));
        }
    }

    private List<String> getInsertStatement(Script script) {
        List<String> queries = new ArrayList<>();
        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(this.getFrameworkInstance());
        ScriptParameterConfiguration scriptParameterConfiguration = new ScriptParameterConfiguration(this.getFrameworkInstance());
        ActionConfiguration actionConfiguration = new ActionConfiguration(this.getFrameworkInstance());

        // TODO: discuss script types
        if (getScriptByName(script.getName()).size() == 0) {
            String sql = "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                    " (SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(script.getId()) + "," +
                    SQLTools.GetStringForSQL(script.getType() == null ? "script" : script.getType()) + "," +
                    SQLTools.GetStringForSQL(script.getName()) + "," +
                    SQLTools.GetStringForSQL(script.getDescription()) + ");";
            queries.add(sql);
        }

        // add version
        queries.add(scriptVersionConfiguration.getInsertStatement(script.getId(), script.getVersion()));

        // add Parameters
        for (ScriptParameter scriptParameter : script.getParameters()) {
            queries.add(scriptParameterConfiguration.getInsertStatement(script.getId(), script.getVersion().getNumber(), scriptParameter));
        }

        // add actions
        for (Action action : script.getActions()) {
            queries.addAll(actionConfiguration.getInsertStatement(script.getId(), script.getVersion().getNumber(), action));
        }
        return queries;
    }

    private List<String> getDeleteStatement(Script script) {
        List<String> queries = new ArrayList<>();

        // delete parameters
        queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(script.getVersion().getNumber()) + ";");
        // delete version
        queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(script.getVersion().getNumber()) + ";");

        // delete actions
        for (Action action : script.getActions()) {
            queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Actions") +
                    " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) +
                    " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(script.getVersion().getNumber()) +
                    " AND ACTION_ID = " + SQLTools.GetStringForSQL(action.getId()) + ";");
            queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters") +
                    " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) +
                    " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(script.getVersion().getNumber()) +
                    " AND ACTION_ID = " + SQLTools.GetStringForSQL(action.getId()) + ";");
        }

        // delete script info if last version
        String countQuery = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB ) AS total_versions FROM "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) + " AND "
                + " SCRIPT_VRS_NB != " + SQLTools.GetStringForSQL(script.getVersion().getNumber()) + ";";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                queries.add("DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                        " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) + ";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queries;
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
        LOGGER.debug(MessageFormat.format("Fetching latest version for script {0}.", scriptName));
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
            LOGGER.info("exception=" + e);
            LOGGER.info("exception.stacktrace=" + StackTrace);

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
        LOGGER.debug(MessageFormat.format("Fetching script {0}-{1}.", scriptName, versionNumber));
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        CachedRowSet crsScript = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        ActionConfiguration actionConfiguration = new ActionConfiguration(this.getFrameworkInstance());
        ScriptVersionConfiguration scriptVersionConfiguration = new ScriptVersionConfiguration(
                this.getFrameworkInstance());
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                // frameworkExecution.getFrameworkLog().log(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptName, versionNumber), Level.DEBUG);
            }
            crsScript.next();
            String scriptId = crsScript.getString("SCRIPT_ID");

            // Get the version
            Optional<ScriptVersion> scriptVersion = scriptVersionConfiguration.getScriptVersion(scriptId, versionNumber);
            if (!scriptVersion.isPresent()) {
                // frameworkExecution.getFrameworkLog().log(MessageFormat.format("Cannot find version {1} for script {0}.", scriptName, versionNumber), Level.WARN);
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
                    LOGGER.debug(MessageFormat.format("Cannot retrieve action {0} for script {1}-{2}.", crsActions.getString("ACTION_ID"), scriptName, versionNumber));
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

            System.out.println(StackTrace.toString());
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + StackTrace);

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