package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.action.exception.ActionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.exception.*;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private final ActionConfiguration actionConfiguration;
    private final ScriptVersionConfiguration scriptVersionConfiguration;
    private final ScriptParameterConfiguration scriptParameterConfiguration;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ScriptConfiguration() {
        this.actionConfiguration = new ActionConfiguration();
        this.scriptVersionConfiguration = new ScriptVersionConfiguration();
        this.scriptParameterConfiguration = new ScriptParameterConfiguration();
    }

    @Override
    public Optional<Script> get(ScriptKey metadataKey) {
        return get(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
    }

    public boolean exists(String scriptName, long versionNumber) {
        try {
            String query = "SELECT SCRIPT_ID FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                    " WHERE SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + ";";
            CachedRowSet crsScript = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
            if (crsScript.size() == 0) {
                return false;
            }
            crsScript.next();
            String scriptId = crsScript.getString("SCRIPT_ID");

            // Get the version
            Optional<ScriptVersion> scriptVersion = scriptVersionConfiguration.getScriptVersion(scriptId, versionNumber);
            return scriptVersion.isPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String scriptName) {
        String query = "SELECT SCRIPT_ID FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                " WHERE SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + ";";
        CachedRowSet crsScript = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return crsScript.size() > 0;
    }

    public boolean exists(Script script) {
        return exists(script.getName(), script.getVersion().getNumber());
    }

    @Override
    public List<Script> getAll() {
        List<Script> scripts = new ArrayList<>();
        String queryScript = "select SCRIPT_ID, SCRIPT_NM from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts");
        CachedRowSet crsScript = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScript, "reader");

        try {
            while (crsScript.next()) {
                scripts.addAll(getByName(crsScript.getString("SCRIPT_NM")));
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return scripts;
    }

    @Override
    public void delete(ScriptKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting script {0}-{1}.", metadataKey.toString()));
        if (!exists(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber())) {
            throw new ScriptDoesNotExistException(
                    MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be deleted",
                            metadataKey.toString()));
        }

        try {
            scriptVersionConfiguration.delete(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
        } catch (ScriptVersionDoesNotExistException e) {
            LOGGER.warn(e.getMessage() + ". Skipping");
        }

        actionConfiguration.deleteActionsFromScript(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
        List<String> deleteQuery = getDeleteStatement(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(deleteQuery);
    }

    private List<Script> getByName(String scriptName) {
        try {
            LOGGER.trace(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
            List<Script> scripts = new ArrayList<>();
            String queryScript = "select SCRIPT_ID from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                    " where SCRIPT_NM = "
                    + SQLTools.GetStringForSQL(scriptName) + ";";
            CachedRowSet crsScript = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScript, "reader");
            if (crsScript.size() == 0) {
                return scripts;
            } else if (crsScript.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Script {0}. Returning first implementation", scriptName));
            }
            crsScript.next();
            String queryScriptVersions = "select SCRIPT_VRS_NB from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") + " where SCRIPT_ID = "
                    + SQLTools.GetStringForSQL(crsScript.getString("SCRIPT_ID"));
            CachedRowSet crsScriptVersions = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScriptVersions, "reader");
            while (crsScriptVersions.next()) {
                get(scriptName, crsScriptVersions.getLong("SCRIPT_VRS_NB")).ifPresent(scripts::add);
            }
            crsScriptVersions.close();
            crsScript.close();
            return scripts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Script script) throws ScriptDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (!exists(script)) {
            throw new ScriptDoesNotExistException(
                    MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be deleted",
                            script.getName(), script.getVersion().getNumber()));
        }

        try {
            scriptVersionConfiguration.delete(script.getId(), script.getVersion().getNumber());
        } catch (ScriptVersionDoesNotExistException e) {
            LOGGER.warn(e.getMessage() + ". Skipping");
        }

        actionConfiguration.deleteActionsFromScript(script.getId(), script.getVersion().getNumber());
        List<String> deleteQuery = getDeleteStatement(script.getId(), script.getVersion().getNumber());
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(deleteQuery);
    }

    public void deleteByName(String scriptName) throws ScriptDoesNotExistException, SQLException {
        for (Script script : getByName(scriptName)) {
            delete(script);
        }
    }

    public void insert(Script script) throws ScriptAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (exists(script)) {
            throw new ScriptAlreadyExistsException(MessageFormat.format(
                    "Script {0}-{1} already exists", script.getName(), script.getVersion().getNumber()));
        }

        // add Parameters
        for (ScriptParameter scriptParameter : script.getParameters()) {
            try {
                scriptParameterConfiguration.insert(script.getId(), script.getVersion().getNumber(), scriptParameter);
            } catch (ScriptParameterAlreadyExistsException e) {
                LOGGER.warn(e.getMessage() + ".skipping");
            }
        }

        // add version
        try {
            scriptVersionConfiguration.insert(script.getId(), script.getVersion());
        } catch (ScriptVersionAlreadyExistsException e) {
            LOGGER.warn(e.getMessage() + ".skipping");
        }

        // add actions
        for (Action action : script.getActions()) {
            try {
                actionConfiguration.insert(script.getId(), script.getVersion().getNumber(), action);
            } catch (ActionAlreadyExistsException e) {
                LOGGER.warn(e.getMessage() + ". Skipping");
            }
        }

        List<String> insertStatement = getInsertStatement(script);
        MetadataControl.getInstance().getDesignMetadataRepository().executeBatch(insertStatement);
    }

    public void update(Script script) throws ScriptDoesNotExistException{
        LOGGER.trace(MessageFormat.format("Updating script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        try {
            delete(script);
            insert(script);
        } catch (ScriptDoesNotExistException e) {
            LOGGER.warn(MessageFormat.format("Script {0}-{1} is not present in the repository so cannot be updated", script.getName(), script.getVersion().getNumber()));
            throw e;
        } catch (ScriptAlreadyExistsException e) {
            LOGGER.warn(MessageFormat.format("Script {0}-{1} is not deleted correctly during update. {2}", script.getName(), script.getVersion().getNumber(), e.toString()));
        }
    }

    private List<String> getInsertStatement(Script script) {
        List<String> queries = new ArrayList<>();

        if (!exists(script.getName())) {
            String sql = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                    " (SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(script.getId()) + "," +
                    SQLTools.GetStringForSQL(script.getType() == null ? "script" : script.getType()) + "," +
                    SQLTools.GetStringForSQL(script.getName()) + "," +
                    SQLTools.GetStringForSQL(script.getDescription()) + ");";
            queries.add(sql);
        }
        return queries;
    }

    private List<String> getDeleteStatement(String scriptId, Long scriptVersionNumber) {
        List<String> queries = new ArrayList<>();

        // delete parameters
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";");

        // delete script info if last version
        String countQuery = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND "
                + " SCRIPT_VRS_NB != " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";";
        CachedRowSet crs = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") +
                        " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + ";");
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }

        return queries;
    }


    private Optional<Long> getLatestVersion(String scriptName) {
        LOGGER.trace(MessageFormat.format("Fetching latest version for script {0}.", scriptName));
        String queryScriptVersion = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptVersions") + " a inner join "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts")
                + " b on a.script_id = b.script_id where b.script_nm = '" + scriptName + "'";
        CachedRowSet crsScriptVersion = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScriptVersion, "reader");
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

    public Optional<Script> get(String scriptName) {
        Optional<Long> latestVersion = getLatestVersion(scriptName);
        if (latestVersion.isPresent()) {
            return get(scriptName, latestVersion.get());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Script> get(String scriptName, long versionNumber) {
        LOGGER.trace(MessageFormat.format("Fetching script {0}-{1}.", scriptName, versionNumber));
        String queryScript = "select SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_NM = '"
                + scriptName + "'";
        CachedRowSet crsScript = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryScript, "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptName, versionNumber));
            }
            crsScript.next();
            String scriptId = crsScript.getString("SCRIPT_ID");

            // Get the version
            Optional<ScriptVersion> scriptVersion = scriptVersionConfiguration.getScriptVersion(scriptId, versionNumber);
            if (!scriptVersion.isPresent()) {
                return Optional.empty();
            }

            // Get the actions
            List<Action> actions = new ArrayList<>();
            String queryActions = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Actions")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + versionNumber
                    + " order by ACTION_NB asc ";
            CachedRowSet crsActions = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryActions, "reader");

            while (crsActions.next()) {
                Optional<Action> action = actionConfiguration.get(scriptId, scriptVersion.get().getNumber(), crsActions.getString("ACTION_ID"));
                if (action.isPresent()) {
                    actions.add(action.get());
                } else {
                    LOGGER.debug(MessageFormat.format("Cannot retrieve action {0} for script {1}-{2}.", crsActions.getString("ACTION_ID"), scriptName, versionNumber));
                }
            }
            crsActions.close();

            // Get parameters
            String queryScriptParameters = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ScriptParameters")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + versionNumber;
            CachedRowSet crsScriptParameters = MetadataControl.getInstance().getDesignMetadataRepository()
                    .executeQuery(queryScriptParameters, "reader");
            List<ScriptParameter> scriptParameters = new ArrayList<>();
            while (crsScriptParameters.next()) {
                ScriptParameterKey scriptParameterKey = new ScriptParameterKey(scriptId, scriptVersion.get().getNumber(),
                        crsScriptParameters.getString("SCRIPT_PAR_NM"));
                scriptParameters.add(new ScriptParameter(scriptParameterKey,
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
}