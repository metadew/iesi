package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.ActionParameterTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ActionParameterTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionParameterTraceConfiguration extends Configuration<ActionParameterTrace, ActionParameterTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();


    public ActionParameterTraceConfiguration(MetadataControl metadataControl) {
        super(metadataControl);
    }

    @Override
    public Optional<ActionParameterTrace> get(ActionParameterTraceKey actionParameterTraceKey) throws SQLException {
        String query = "SELECT ACTION_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionParameterTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getName()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ActionParameterTrace {0}. Returning first implementation", actionParameterTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ActionParameterTrace(actionParameterTraceKey, cachedRowSet.getString("ACTION_PAR_VAL")));
    }

    @Override
    public List<ActionParameterTrace> getAll() throws SQLException {
        List<ActionParameterTrace> actionParameterTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("ACTION_ID"),
                    cachedRowSet.getString("ACTION_PAR_NM")),
                    cachedRowSet.getString("ACTION_PAR_VAL")));
        }
        return actionParameterTraces;
    }

    @Override
    public void delete(ActionParameterTraceKey actionParameterTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterTrace {0}.", actionParameterTraceKey.toString()));
        if (!exists(actionParameterTraceKey)) {
            throw new ActionParameterTraceDoesNotExistException(MessageFormat.format(
                    "ActionParameterTrace {0} does not exists", actionParameterTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionParameterTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);

    }

    private String deleteStatement(ActionParameterTraceKey actionParameterTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionParameterTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getName()) + ";";
    }

    @Override
    public void insert(ActionParameterTrace actionParameterTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", actionParameterTrace.getMetadataKey().toString()));
        if (exists(actionParameterTrace.getMetadataKey())) {
            throw new ActionParameterTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionParameterTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionParameterTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionParameterTrace actionParameterTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getValue()) + ");";
    }
}