package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.action.ActionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionParameterAlreadyExistsException;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionConfiguration {

    private final ActionParameterConfiguration actionParameterConfiguration;
    private final static Logger LOGGER = LogManager.getLogger();

    public ActionConfiguration() {
    	this.actionParameterConfiguration = new ActionParameterConfiguration();
    }


    public Optional<Action> get(Script script, String actionId) {
        return get(script.getId(), script.getVersion().getNumber(), actionId);
    }

    public Optional<Action> get(String scriptId, long scriptVersionNumber, String actionId) {
    	LOGGER.trace(MessageFormat.format("Fetching action {0}.", actionId));
        String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL, RETRIES_VAL from "
                + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet crsAction = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryAction, "reader");
        try {
            if (crsAction.size() == 0) {
                return Optional.empty();
            } else if (crsAction.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for action {0}. Returning first implementation", actionId));
            }
            crsAction.next();
            // Get parameters
            String queryActionParameters = "select ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                    + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
            CachedRowSet crsActionParameters = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(queryActionParameters, "reader");
            List<ActionParameter> actionParameters = new ArrayList<>();
            while (crsActionParameters.next()) {
                actionParameters.add(new ActionParameter(crsActionParameters.getString("ACTION_PAR_NM"),
                        crsActionParameters.getString("ACTION_PAR_VAL")));
            }
            Action action = new Action(actionId,
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
        queries.add("INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
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
            queries.add(actionParameterConfiguration.getInsertStatement(scriptId, scriptVersionNumber, action.getId(), actionParameter));
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
                actionParameterConfiguration.insert(scriptId, scriptVersionNumber, action.getId(), actionParameter);
            } catch (ActionParameterAlreadyExistsException e) {
                LOGGER.warn(e.getMessage() + ". Skipping");
            }
        }
        String query = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
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
        MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(query);
    }

    public boolean exists(String scriptId, long scriptVersionNumber, Action action) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID from "
                + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(action.getId()) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public boolean exists(String scriptId, long scriptVersionNumber, String actionId) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID from "
                + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    public void delete(String scriptId, long scriptVersionNumber, String actionId) throws ActionDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Action {0}-{1}-{2}.", scriptId, scriptVersionNumber, actionId));
        if (!exists(scriptId, scriptVersionNumber, actionId)) {
            throw new ActionDoesNotExistException(MessageFormat.format("Action {0}-{1}-{2} does not exists", scriptId, scriptVersionNumber, actionId));
        }
        List<String> deleteStatement = deleteStatement(scriptId, scriptVersionNumber, actionId);
        MetadataControl.getInstance().getTraceMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(String scriptId, long scriptVersionNumber, String actionId) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("Actions") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + ";");
        return queries;
    }

}