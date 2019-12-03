package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.exception.ActionParameterAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
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

public class ActionParameterConfiguration extends Configuration<ActionParameter, ActionParameterKey> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static ActionParameterConfiguration INSTANCE;

    public synchronized static ActionParameterConfiguration getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ActionParameterConfiguration();
        }
        return INSTANCE;
    }


    public ActionParameterConfiguration() {
    }

    @Override
    public Optional<ActionParameter> get(ActionParameterKey metadataKey) {
        return get(metadataKey.getScriptId(), metadataKey.getScriptVersionNumber(),
                metadataKey.getActionId(), metadataKey.getActionName());
    }

    @Override
    public List<ActionParameter> getAll() {
        List<ActionParameter> actionParameters = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ActionParameters")
                + " order by ACTION_ID ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ActionParameterKey actionParameterKey = new ActionParameterKey(
                        crs.getString("SCRIPT_ID"),
                        crs.getLong("SCRIPT_VRS_NB"),
                        crs.getString("ACTION_NM"),
                        crs.getString("ACTION_PAR_NM"));
                actionParameters.add(new ActionParameter(actionParameterKey, crs.getString("ACTION_PAR_VAL")));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return actionParameters;
    }

    @Override
    public void delete(ActionParameterKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameter {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException("ActionParameter", metadataKey);
        }
        String deleteStatement = deleteStatement(metadataKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public String deleteStatement(ActionParameterKey metadataKey){
        return "DELETE FROM " + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters") +
                " WHERE SCRIPT_ID = " + SQLTools.GetStringForSQL(metadataKey.getScriptId()) +
                " AND SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(metadataKey.getScriptVersionNumber()) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(metadataKey.getActionId()) +
                " AND ACTION_PAR_NM = " + SQLTools.GetStringForSQL(metadataKey.getActionName()) + ";";
    }

    @Override
    public void insert(ActionParameter metadata) throws MetadataAlreadyExistsException {
        ActionParameterKey actionParameterKey = metadata.getMetadataKey();
        insert(actionParameterKey.getScriptId(), actionParameterKey.getScriptVersionNumber(),
                actionParameterKey.getActionId(), metadata);
    }

    public String getInsertStatement(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(actionId) + "," +
                SQLTools.GetStringForSQL(actionParameter.getName()) + "," +
                SQLTools.GetStringForSQL(actionParameter.getValue()) + ");";
    }

    public Optional<ActionParameter> get(String scriptId, long scriptVersionNumber, String actionId, String actionParameterName) {
        try {
            String queryActionParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                    + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber)
                    + " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " and ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterName) + ";";
            CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository()
                    .executeQuery(queryActionParameter, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ActionParameter {0}-{1}-{2}-{4}. Returning first implementation", scriptId, scriptVersionNumber, actionId, actionParameterName));
            }
            cachedRowSet.next();
            ActionParameterKey actionParameterKey = new ActionParameterKey(scriptId, scriptVersionNumber, actionId, actionParameterName);
            return Optional.of(new ActionParameter(actionParameterKey, cachedRowSet.getString("ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void insert(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) throws ActionParameterAlreadyExistsException {
            LOGGER.trace(MessageFormat.format("Inserting ActionParameter {0}-{1}-{2}-{3}.", scriptId, scriptVersionNumber, actionId, actionParameter.getName()));
            if (exists(scriptId, scriptVersionNumber, actionId, actionParameter)) {
                throw new ActionParameterAlreadyExistsException(MessageFormat.format(
                        "ActionParameter {0}-{1}-{2}-{3} already exists", scriptId, scriptVersionNumber, actionId, actionParameter.getName()));
            }
            String insertQuery = "INSERT INTO " + MetadataControl.getInstance().getDesignMetadataRepository()
                    .getTableNameByLabel("ActionParameters") +
                    " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                    SQLTools.GetStringForSQL(scriptId) + "," +
                    scriptVersionNumber + "," +
                    SQLTools.GetStringForSQL(actionId) + "," +
                    SQLTools.GetStringForSQL(actionParameter.getName()) + "," +
                    SQLTools.GetStringForSQL(actionParameter.getValue()) + ");";
            MetadataControl.getInstance().getDesignMetadataRepository().executeUpdate(insertQuery);
    }

    private boolean exists(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) {
        String query = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                + MetadataControl.getInstance().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " and SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptVersionNumber)
                + " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " and ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameter.getName()) + ";";
        CachedRowSet cachedRowSet = MetadataControl.getInstance().getDesignMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

}