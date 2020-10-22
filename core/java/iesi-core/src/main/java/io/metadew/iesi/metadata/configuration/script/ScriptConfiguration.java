package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.ActionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
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

public class ScriptConfiguration extends Configuration<Script, ScriptKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    private static ScriptConfiguration INSTANCE;

    public synchronized static ScriptConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptConfiguration();
        }
        return INSTANCE;
    }

    private ScriptConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ActionConfiguration.getInstance().init(metadataRepository);
//        ScriptVersionConfiguration.getInstance().init(metadataRepository);
        ScriptLabelConfiguration.getInstance().init(metadataRepository);
        ScriptParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Script> get(ScriptKey scriptKey) {
        // had to change this to only get the script, because the script doesn't have version as id
        // return get(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber());
        LOGGER.trace(MessageFormat.format("Fetching script {0}-{1}.", scriptKey.getScriptId(), scriptKey.getScriptVersion()));
        String queryScript = "select SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC from "
                + getMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_ID = "
                + SQLTools.GetStringForSQL(scriptKey.getScriptId()) + ";";
        CachedRowSet crsScript = getMetadataRepository().executeQuery(queryScript, "reader");
        try {
            if (crsScript.size() == 0) {
                return Optional.empty();
            } else if (crsScript.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptKey.getScriptId(), scriptKey.getScriptVersion()));
            }
            crsScript.next();

            // Get the version
            Optional<ScriptVersion> scriptVersion = ScriptVersionConfiguration.getInstance().get(new ScriptVersionKey(new ScriptKey(scriptKey.getScriptId(), scriptKey.getScriptVersion())));
            if (!scriptVersion.isPresent()) {
                return Optional.empty();
            }

            // Get the actions
            List<Action> actions = ActionConfiguration.getInstance().getByScript(scriptKey);

            // Get parameters
            List<ScriptParameter> scriptParameters = ScriptParameterConfiguration.getInstance().getByScript(scriptKey);

            // Get labels
            List<ScriptLabel> scriptLabels = ScriptLabelConfiguration.getInstance().getByScript(scriptKey);

            Script script = new Script(scriptKey, crsScript.getString("SCRIPT_NM"), crsScript.getString("SCRIPT_DSC"),
                    scriptVersion.get(), scriptParameters, actions, scriptLabels);
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

    public boolean exists(ScriptKey scriptKey) {
        try {
            String query = "SELECT SCRIPT_ID FROM " + getMetadataRepository().getTableNameByLabel("Scripts") +
                    " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptKey.getScriptId()) + ";";
            CachedRowSet crsScript = getMetadataRepository().executeQuery(query, "reader");
            if (crsScript.size() == 0) {
                return false;
            }
            crsScript.next();
            return ScriptVersionConfiguration.getInstance().exists(new ScriptVersionKey(scriptKey));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String scriptName) {
        String query = "SELECT SCRIPT_ID FROM " + getMetadataRepository().getTableNameByLabel("Scripts") +
                " WHERE SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + ";";
        CachedRowSet crsScript = getMetadataRepository().executeQuery(query, "reader");
        return crsScript.size() > 0;
    }

    @Override
    public List<Script> getAll() {
        List<Script> scripts = new ArrayList<>();
        String queryScript = "select * from "
                + getMetadataRepository().getTableNameByLabel("Scripts")
                + " order by SCRIPT_NM ASC";
        CachedRowSet crsScript = getMetadataRepository().executeQuery(queryScript, "reader");

        try {
            while (crsScript.next()) {
                String scriptId = crsScript.getString("SCRIPT_ID");
                List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptId(scriptId);
                for (ScriptVersion scriptVersion : scriptVersions) {
                    get(new ScriptKey(scriptId, scriptVersion.getNumber())).ifPresent(scripts::add);
                }
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
    public void delete(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Deleting script {0}-{1}.", scriptKey.toString()));
        if (!exists(scriptKey)) {
            throw new MetadataDoesNotExistException(scriptKey);
        }
        ScriptVersionKey scriptVersionKey = new ScriptVersionKey(new ScriptKey(scriptKey.getScriptId(), scriptKey.getScriptVersion()));
        ScriptVersionConfiguration.getInstance().delete(scriptVersionKey);
        ActionConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptParameterConfiguration.getInstance().deleteByScript(scriptKey);
        ScriptLabelConfiguration.getInstance().deleteByScript(scriptKey);
        getDeleteStatement(scriptKey)
                .ifPresent(getMetadataRepository()::executeUpdate);
    }

    public List<Script> getByName(String scriptName) {
        try {
            LOGGER.trace(MessageFormat.format("Fetching scripts by name ''{0}''", scriptName));
            List<Script> scripts = new ArrayList<>();
            String queryScript = "select SCRIPT_ID from " + getMetadataRepository().getTableNameByLabel("Scripts") +
                    " where SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptName) + ";";
            CachedRowSet crsScript = getMetadataRepository().executeQuery(queryScript, "reader");
            if (crsScript.size() == 0) {
                return scripts;
            } else if (crsScript.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Script {0}. Returning first implementation", scriptName));
            }
            crsScript.next();
            List<ScriptVersion> scriptVersions = ScriptVersionConfiguration.getInstance().getByScriptId(crsScript.getString("SCRIPT_ID"));

            for (ScriptVersion scriptVersion : scriptVersions) {
                get(new ScriptKey(crsScript.getString("SCRIPT_ID"), scriptVersion.getNumber())).ifPresent(scripts::add);
            }
            crsScript.close();
            return scripts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByName(String scriptName) {
        for (Script script : getByName(scriptName)) {
            delete(script.getMetadataKey());
        }
    }

    public void insert(Script script) {
        LOGGER.trace(MessageFormat.format("Inserting script {0}-{1}.", script.getName(), script.getVersion().getNumber()));
        if (exists(script)) {
            throw new MetadataAlreadyExistsException(script);
        }

        // add Parameters
        for (ScriptParameter scriptParameter : script.getParameters()) {
            ScriptParameterConfiguration.getInstance().insert(scriptParameter);
        }

        // add Parameters
        for (ScriptLabel scriptLabel : script.getLabels()) {
            ScriptLabelConfiguration.getInstance().insert(scriptLabel);
        }

        // add version
        ScriptVersionConfiguration.getInstance().insert(script.getVersion());
        // add actions
        for (Action action : script.getActions()) {
            ActionConfiguration.getInstance().insert(action);
        }

        getMetadataRepository().executeUpdate(getInsertStatement(script));
    }

    private String getInsertStatement(Script script) {
        if (!exists(script)) {
            return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Scripts") +
                    " (SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(script.getMetadataKey().getScriptId()) + "," +
                    SQLTools.GetStringForSQL(script.getName()) + "," +
                    SQLTools.GetStringForSQL(script.getDescription()) + ");";
        } else {
            return "UPDATE " + getMetadataRepository().getTableNameByLabel("Scripts") + " SET " +
                    "SCRIPT_DSC = " + SQLTools.GetStringForSQL(script.getDescription()) +
                    " WHERE " +
                    "SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getMetadataKey().getScriptId());
        }
    }

    private Optional<String> getDeleteStatement(ScriptKey scriptVersionKey) {

//        // delete parameters
//        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameters") +
//                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptId()) + " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionKey.getVersionNumber()) + ";");

        // delete script info if last version
        String countQuery = "SELECT COUNT(DISTINCT SCRIPT_VRS_NB) AS total_versions FROM "
                + getMetadataRepository().getTableNameByLabel("ScriptVersions")
                + " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptId()) + " AND "
                + " SCRIPT_VRS_NB != " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptVersion()) + ";";
        CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

        try {
            if (crs.next() && Integer.parseInt(crs.getString("total_versions")) == 0) {
                return Optional.of("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Scripts") +
                        " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptVersionKey.getScriptId()) + ";");
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
            return Optional.empty();
        }
    }


    public Optional<Script> getLatestVersion(String scriptName) {
        Optional<ScriptVersion> latestVersion = ScriptVersionConfiguration.getInstance().getLatestVersionNumber(IdentifierTools.getScriptIdentifier(scriptName));
        if (latestVersion.isPresent()) {
            return get(new ScriptKey(latestVersion.get().getScriptId(), latestVersion.get().getNumber()));
        } else {
            return Optional.empty();
        }
    }

//    public Optional<Script> get(String scriptId, long versionNumber) {
//        LOGGER.trace(MessageFormat.format("Fetching script {0}-{1}.", scriptId, versionNumber));
//        String queryScript = "select SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC from "
//                + getMetadataRepository().getTableNameByLabel("Scripts") + " where SCRIPT_ID = "
//                + SQLTools.GetStringForSQL(scriptId) + ";";
//        CachedRowSet crsScript = getMetadataRepository().executeQuery(queryScript, "reader");
//        try {
//            if (crsScript.size() == 0) {
//                return Optional.empty();
//            } else if (crsScript.size() > 1) {
//                LOGGER.warn(MessageFormat.format("Found multiple implementations for script {0}-{1}. Returning first implementation", scriptId, versionNumber));
//            }
//            crsScript.next();
//
//            // Get the version
//            Optional<ScriptVersion> scriptVersion = ScriptVersionConfiguration.getInstance().get(new ScriptVersionKey(scriptId, versionNumber));
//            if (!scriptVersion.isPresent()) {
//                return Optional.empty();
//            }
//
//            // Get the actions
//            List<Action> actions = new ArrayList<>();
//            String queryActions = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB from "
//                    + getMetadataRepository().getTableNameByLabel("Actions")
//                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(versionNumber)
//                    + " order by ACTION_NB asc ";
//            CachedRowSet crsActions = getMetadataRepository().executeQuery(queryActions, "reader");
//
//            while (crsActions.next()) {
//                Optional<Action> action = ActionConfiguration.getInstance().get(scriptId, scriptVersion.get().getNumber(), crsActions.getString("ACTION_ID"));
//                if (action.isPresent()) {
//                    actions.add(action.get());
//                } else {
//                    LOGGER.debug(MessageFormat.format("Cannot retrieve action {0} for script {1}-{2}.", crsActions.getString("ACTION_ID"), scriptId, versionNumber));
//                }
//            }
//            crsActions.close();
//
//            // Get parameters
//            String queryScriptParameters = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from "
//                    + getMetadataRepository().getTableNameByLabel("ScriptParameters")
//                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(versionNumber);
//            CachedRowSet crsScriptParameters = getMetadataRepository()
//                    .executeQuery(queryScriptParameters, "reader");
//            List<ScriptParameter> scriptParameters = new ArrayList<>();
//            while (crsScriptParameters.next()) {
//                ScriptParameterKey scriptParameterKey = new ScriptParameterKey(scriptId, scriptVersion.get().getNumber(),
//                        crsScriptParameters.getString("SCRIPT_PAR_NM"));
//                scriptParameters.add(new ScriptParameter(scriptParameterKey,
//                        crsScriptParameters.getString("SCRIPT_PAR_VAL")));
//            }
//            crsScriptParameters.close();
//            Script script = new Script(scriptId, crsScript.getString("SCRIPT_NM"), crsScript.getString("SCRIPT_DSC"),
//                    scriptVersion.get(), scriptParameters, actions);
//            crsScript.close();
//            return Optional.of(script);
//        } catch (Exception e) {
//            StringWriter StackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(StackTrace));
//
//            System.out.println(StackTrace.toString());
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + StackTrace);
//
//            return Optional.empty();
//        }
//    }
}