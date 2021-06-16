package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
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

    public synchronized static ActionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionConfiguration();
        }
        return INSTANCE;
    }

    private ActionConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<Action> get(ActionKey actionKey) {
        LOGGER.trace(MessageFormat.format("Fetching action {0}.", actionKey.toString()));
        String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL, RETRIES_VAL from "
                + getMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.getStringForSQL(actionKey.getActionId()) +
                " AND SCRIPT_ID = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptVersion()) +
                " AND DELETED_AT = 'NA' ;";
        CachedRowSet crsAction = getMetadataRepository().executeQuery(queryAction, "reader");
        try {
            if (crsAction.size() == 0) {
                return Optional.empty();
            } else if (crsAction.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for action {0}. Returning first implementation", actionKey.toString()));
            }
            crsAction.next();

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
                    ActionParameterConfiguration.getInstance().getByAction(actionKey)
            );
            crsAction.close();
            return Optional.of(action);
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + StackTrace);

            return Optional.empty();
        }
    }

    @Override
    public List<Action> getAll() {
        List<Action> actions = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("Actions") + " WHERE DELETED_AT = 'NA'";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ActionKey actionKey = new ActionKey(new ScriptKey(crs.getString("SCRIPT_ID"), crs.getLong("SCRIPT_VRS_NB")),
                        crs.getString("ACTION_ID"));
                List<ActionParameter> actionParameters = ActionParameterConfiguration.getInstance().getByAction(actionKey);
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
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actions;
    }

    @Override
    public void delete(ActionKey actionKey) {
        LOGGER.trace(MessageFormat.format("Deleting Action {0}.", actionKey.toString()));
        if (!exists(actionKey)) {
            throw new MetadataDoesNotExistException(actionKey);
        }
        ActionParameterConfiguration.getInstance().deleteByAction(actionKey);
        getMetadataRepository().executeUpdate(deleteStatement(actionKey));
    }

    @Override
    public void insert(Action action) {
        LOGGER.trace(MessageFormat.format("Inserting action {0}.", action.toString()));
        if (exists(action)) {
            throw new MetadataAlreadyExistsException(action);
        }
        for (ActionParameter actionParameter : action.getParameters()) {
            ActionParameterConfiguration.getInstance().insert(actionParameter);
        }
        String query = "INSERT INTO " + getMetadataRepository()
                .getTableNameByLabel("Actions") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL, DELETED_AT) VALUES (" +
                SQLTools.getStringForSQL(action.getMetadataKey().getScriptKey().getScriptId()) + "," +
                SQLTools.getStringForSQL(action.getMetadataKey().getScriptKey().getScriptVersion()) + "," +
                SQLTools.getStringForSQL(action.getMetadataKey().getActionId()) + "," +
                SQLTools.getStringForSQL(action.getNumber()) + "," +
                SQLTools.getStringForSQL(action.getType()) + "," +
                SQLTools.getStringForSQL(action.getName()) + "," +
                SQLTools.getStringForSQL(action.getDescription()) + "," +
                SQLTools.getStringForSQL(action.getComponent()) + "," +
                SQLTools.getStringForSQL(action.getIteration()) + "," +
                SQLTools.getStringForSQL(action.getCondition()) + "," +
                SQLTools.getStringForSQL(action.getRetries()) + "," +
                SQLTools.getStringForSQL(action.getErrorExpected()) + "," +
                SQLTools.getStringForSQL(action.getErrorStop()) + ", 'NA' );";
        getMetadataRepository().executeUpdate(query);
    }

    public boolean exists(ActionKey actionKey) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID from "
                + getMetadataRepository().getTableNameByLabel("Actions")
                + " WHERE ACTION_ID = " + SQLTools.getStringForSQL(actionKey.getActionId()) +
                " AND SCRIPT_ID = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptVersion()) +
                " AND DELETED_AT = 'NA';";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String deleteStatement(ActionKey actionKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("Actions") +
                " WHERE SCRIPT_ID = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(actionKey.getScriptKey().getScriptVersion()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionKey.getActionId()) + ";";
    }

    public List<Action> getByScript(ScriptKey scriptKey) {
        List<Action> actions = new ArrayList<>();
        String query = "select * from " + getMetadataRepository().getTableNameByLabel("Actions") +
                " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " and DELETED_AT = 'NA' "
                + " order by ACTION_NB ASC" + ";";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ActionKey actionKey = new ActionKey(scriptKey, crs.getString("ACTION_ID"));
                List<ActionParameter> actionParameters = ActionParameterConfiguration.getInstance().getByAction(actionKey);
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
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actions;
    }

    public void deleteByScript(ScriptKey scriptKey) {
        LOGGER.trace(MessageFormat.format("Deleting actions for script {0}", scriptKey.toString()));
        ActionParameterConfiguration.getInstance().deleteByScript(scriptKey);
        getMetadataRepository().executeUpdate("UPDATE " + getMetadataRepository().getTableNameByLabel("Actions") +
                " SET DELETED_AT = " + SQLTools.getStringForSQL(scriptKey.getDeletedAt()) +
                " where SCRIPT_ID = " + SQLTools.getStringForSQL(scriptKey.getScriptId()) +
                " and SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptKey.getScriptVersion()) +
                " and DELETED_AT = 'NA' ;");
    }

}