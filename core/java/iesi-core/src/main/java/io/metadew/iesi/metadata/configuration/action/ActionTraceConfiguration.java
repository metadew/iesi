package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionTrace;
import io.metadew.iesi.metadata.definition.action.key.ActionTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionTraceConfiguration extends Configuration<ActionTrace, ActionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    public ActionTraceConfiguration(MetadataControl metadataControl) {
        super(metadataControl);
    }

    @Override
    public Optional<ActionTrace> get(ActionTraceKey actionTraceKey) throws SQLException {
        String query = "SELECT ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getActionId()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", actionTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ActionTrace(actionTraceKey,
                cachedRowSet.getLong("ACTION_NB"),
                cachedRowSet.getString("ACTION_TYP_NM"),
                cachedRowSet.getString("ACTION_NM"),
                cachedRowSet.getString("ACTION_DSC"),
                cachedRowSet.getString("COMP_NM"),
                cachedRowSet.getString("ITERATION_VAL"),
                cachedRowSet.getString("CONDITION_VAL"),
                cachedRowSet.getInt("RETRIES_VAL"),
                cachedRowSet.getString("EXP_ERR_FL"),
                cachedRowSet.getString("STOP_ERR_FL")));
    }

    @Override
    public List<ActionTrace> getAll() throws SQLException {
        List<ActionTrace> actionTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            actionTraces.add(new ActionTrace(new ActionTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("ACTION_ID")),
                    cachedRowSet.getLong("ACTION_NB"),
                    cachedRowSet.getString("ACTION_TYP_NM"),
                    cachedRowSet.getString("ACTION_NM"),
                    cachedRowSet.getString("ACTION_DSC"),
                    cachedRowSet.getString("COMP_NM"),
                    cachedRowSet.getString("ITERATION_VAL"),
                    cachedRowSet.getString("CONDITION_VAL"),
                    cachedRowSet.getInt("RETRIES_VAL"),
                    cachedRowSet.getString("EXP_ERR_FL"),
                    cachedRowSet.getString("STOP_ERR_FL")));
        }
        return actionTraces;
    }

    @Override
    public void delete(ActionTraceKey actionTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", actionTraceKey.toString()));
        if (!exists(actionTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ActionTrace {0} does not exists", actionTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionTraceKey actionTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getActionId()) + ";";
    }

    @Override
    public void insert(ActionTrace actionTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", actionTrace.getMetadataKey().toString()));
        if (exists(actionTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }
    private String insertStatement(ActionTrace actionTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
                " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getNumber()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getType()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getName()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getDescription()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getComponent()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getIteration()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getCondition()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getRetries()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getErrorStop()) + ");";
    }
}