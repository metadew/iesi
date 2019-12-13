package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.exception.ActionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.exception.ActionDoesNotExistException;
import io.metadew.iesi.metadata.configuration.action.exception.ActionParameterAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.repository.MetadataRepository;
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

public class ActionConfiguration extends Configuration<Action, ActionKey> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static ActionConfiguration INSTANCE;

    public synchronized static ActionConfiguration getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ActionConfiguration();
        }
        return INSTANCE;
    }

    private ActionConfiguration() {
    }

    public void init(MetadataRepository metadataRepository){
        setMetadataRepository(metadataRepository);
        ActionParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Action> get(ActionKey metadataKey) {
        return get(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber(), metadataKey.getActionId());
    }

    @Override
    public List<Action> getAll() {
        List<Action> actions = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("Actions")
                + " order by ACTION_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ActionKey actionKey = new ActionKey(crs.getString("SCRIPT_ID"), crs.getLong("SCRIPT_VRS_NB"),
                        crs.getString("ACTION_ID"));
                List<ActionParameter> actionParameters = getAllLinkedActionParameters(actionKey);
                actions.add(new Action(actionKey,
                        crs.getLong("ACTION_NB"),
                        crs.getString("ACTION_TYP_NM"),
                        crs.getString("ACTION_NM"),
                        crs.getString("ACTION_DSC"),
                        crs.getString("COMP_NM"),
                        crs.getString("CONDITION_VAL"),
                        crs.getString("ITERATION_VAL"),
                        crs.getString("EXP_ERR_FL"),
                        crs.getString("STOP_ERR_FL"),
                        crs.getString("RETRIES_VAL"),
                        actionParameters
                ));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actions;
    }

    private List<ActionParameter> getAllLinkedActionParameters(ActionKey actionKey){
        List<ActionParameter> actionParameters = new ArrayList<>();
        try {
            String query = "select ACTION_PAR_NM, ACTION_PAR_VAL from " +
                    getMetadataRepository().getTableNameByLabel("ActionParameters") +
                    " WHERE" +
                    " SCRIPT_ID  = " + SQLTools.GetStringForSQL(actionKey.getScriptId()) + " AND" +
                    " SCRIPT_VRS_NB  = " + actionKey.getScriptVersionNumber() + " AND" +
                    " ACTION_ID = " + SQLTools.GetStringForSQL(actionKey.getActionId()) + ";";
            CachedRowSet crsActionParameters = getMetadataRepository().executeQuery(query, "reader");
            while (crsActionParameters.next()) {
                ActionParameterKey actionParameterKey = new ActionParameterKey(actionKey.getScriptId(), actionKey.getScriptVersionNumber(),
                        actionKey.getActionId(), crsActionParameters.getString("ACTION_PAR_NM"));
                ActionParameter actionParameter =
                        new ActionParameter(actionParameterKey, crsActionParameters.getString("ACTION_PAR_VAL"));
                actionParameters.add(actionParameter);
            }
            crsActionParameters.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actionParameters;
    }

    @Override
    public void delete(ActionKey metadataKey) throws MetadataDoesNotExistException {
        delete(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber(), metadataKey.getActionId());
    }

    @Override
    public void insert(Action metadata) throws MetadataAlreadyExistsException {
        ActionKey actionKey = metadata.getMetadataKey();
        insert(actionKey.getScriptId(), actionKey.getScriptVersionNumber(), metadata);
    }


    public Optional<Action> get(Script script, String actionId) {
        return get(script.getId(), script.getVersion().getNumber(), actionId);
    }

    public Optional<Action> get(String scriptId, long scriptVersionNumber, String actionId) {
    	LOGGER.trace(MessageFormat.format("Fetching action {0}.", actionId));
        String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL, RETRIES_VAL from "
                + getMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet crsAction = getMetadataRepository().executeQuery(queryAction, "reader");
        try {
            if (crsAction.size() == 0) {
                return Optional.empty();
            } else if (crsAction.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for action {0}. Returning first implementation", actionId));
            }
            crsAction.next();
            // Get parameters
            String queryActionParameters = "select ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ActionParameters")
                    + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
            CachedRowSet crsActionParameters = getMetadataRepository().executeQuery(queryActionParameters, "reader");
            List<ActionParameter> actionParameters = new ArrayList<>();
            while (crsActionParameters.next()) {
                ActionParameterKey actionParameterKey = new ActionParameterKey(scriptId, scriptVersionNumber, actionId, crsActionParameters.getString("ACTION_PAR_NM"));
                actionParameters.add(new ActionParameter(actionParameterKey,
                        crsActionParameters.getString("ACTION_PAR_VAL")));
            }
            ActionKey actionKey = new ActionKey(scriptId, scriptVersionNumber, actionId);
            Action action = new Action(actionKey,
                    crsAction.getLong("ACTION_NB"),
                    crsAction.getString("ACTION_TYP_NM"),
                    crsAction.getString("ACTION_NM"),
                    crsAction.getString("ACTION_DSC"),
                    crsAction.getString("COMP_NM"),
                    crsAction.getString("CONDITION_VAL"),
                    crsAction.getString("ITERATION_VAL"),
                    crsAction.getString("EXP_ERR_FL"),
                    crsAction.getString("STOP_ERR_FL"),
                    crsAction.getString("RETRIES_VAL"),
                    actionParameters
            );
            crsActionParameters.close();
            crsAction.close();
            return Optional.of(action);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + StackTrace);

            return Optional.empty();
        }
    }

    public List<String> getInsertStatement(String scriptId, long scriptVersionNumber, Action action) {
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + getMetadataRepository()
                .getTableNameByLabel("Actions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                SQLTools.GetStringForSQL(scriptVersionNumber) + "," +
                SQLTools.GetStringForSQL(action.getId()) + "," +
                SQLTools.GetStringForSQL(action.getNumber()) + "," +
                SQLTools.GetStringForSQL(action.getType()) + "," +
                SQLTools.GetStringForSQL(action.getName()) + "," +
                SQLTools.GetStringForSQL(action.getDescription()) + "," +
                SQLTools.GetStringForSQL(action.getComponent()) + "," +
                SQLTools.GetStringForSQL(action.getIteration()) + "," +
                SQLTools.GetStringForSQL(action.getCondition()) + "," +
                SQLTools.GetStringForSQL(action.getRetries()) + "," +
                SQLTools.GetStringForSQL(action.getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(action.getErrorStop()) + ");");
        for (ActionParameter actionParameter : action.getParameters()) {
            queries.add(ActionParameterConfiguration.getInstance().getInsertStatement(scriptId, scriptVersionNumber, action.getId(), actionParameter));
        }
        return queries;
    }

    public void insert(String scriptId, long scriptVersionNumber, Action action) throws ActionAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting action {0}-{1}-{2}.", scriptId, scriptVersionNumber, action.getId()));
        if (exists(scriptId, scriptVersionNumber, action)) {
            throw new ActionAlreadyExistsException(MessageFormat.format(
                    "Action {0}-{1}-{2} already exists", scriptId, scriptVersionNumber, action.getId()));
        }
        for (ActionParameter actionParameter : action.getParameters()) {
            try {
                ActionParameterConfiguration.getInstance().insert(scriptId, scriptVersionNumber, action.getId(), actionParameter);
            } catch (ActionParameterAlreadyExistsException e) {
                LOGGER.warn(e.getMessage() + ". Skipping");
            }
        }
        String query = "INSERT INTO " + getMetadataRepository()
                .getTableNameByLabel("Actions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                SQLTools.GetStringForSQL(scriptVersionNumber) + "," +
                SQLTools.GetStringForSQL(action.getId()) + "," +
                SQLTools.GetStringForSQL(action.getNumber()) + "," +
                SQLTools.GetStringForSQL(action.getType()) + "," +
                SQLTools.GetStringForSQL(action.getName()) + "," +
                SQLTools.GetStringForSQL(action.getDescription()) + "," +
                SQLTools.GetStringForSQL(action.getComponent()) + "," +
                SQLTools.GetStringForSQL(action.getIteration()) + "," +
                SQLTools.GetStringForSQL(action.getCondition()) + "," +
                SQLTools.GetStringForSQL(action.getRetries()) + "," +
                SQLTools.GetStringForSQL(action.getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(action.getErrorStop()) + ");";
        getMetadataRepository().executeUpdate(query);
    }

    public boolean exists(String scriptId, long scriptVersionNumber, Action action) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID from "
                + getMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(action.getId()) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public boolean exists(String scriptId, long scriptVersionNumber, String actionId) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID from "
                + getMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void delete(String scriptId, long scriptVersionNumber, String actionId) throws ActionDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Action {0}-{1}-{2}.", scriptId, scriptVersionNumber, actionId));
        if (!exists(scriptId, scriptVersionNumber, actionId)) {
            throw new ActionDoesNotExistException(MessageFormat.format("Action {0}-{1}-{2} does not exists", scriptId, scriptVersionNumber, actionId));
        }
        List<String> deleteStatement = deleteStatement(scriptId, scriptVersionNumber, actionId);
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(String scriptId, long scriptVersionNumber, String actionId) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Actions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + ";");
        return queries;
    }

    public void deleteActionsFromScript(String scriptId, long scriptVersionNumber) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Actions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) + ";");
        getMetadataRepository().executeBatch(queries);
    }

}