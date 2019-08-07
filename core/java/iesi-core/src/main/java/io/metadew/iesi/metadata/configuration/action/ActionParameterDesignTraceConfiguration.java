package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.ActionParameterTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ActionParameterTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterDesignTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionParameterDesignTraceConfiguration extends Configuration<ActionParameterDesignTrace, ActionParameterDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    public ActionParameterDesignTraceConfiguration(MetadataControl metadataControl) {
        super(metadataControl);
    }

    @Override
    public Optional<ActionParameterDesignTrace> get(ActionParameterDesignTraceKey actionParameterDesignTraceKey) throws SQLException {
        String query = "SELECT ACTION_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ActionParameterTrace {0}. Returning first implementation", actionParameterDesignTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ActionParameterDesignTrace(actionParameterDesignTraceKey, cachedRowSet.getString("ACTION_PAR_VAL")));
    }

    @Override
    public List<ActionParameterDesignTrace> getAll() throws SQLException {
        List<ActionParameterDesignTrace> actionParameterTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            actionParameterTraces.add(new ActionParameterDesignTrace(new ActionParameterDesignTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("ACTION_ID"),
                    cachedRowSet.getString("ACTION_PAR_NM")),
                    cachedRowSet.getString("ACTION_PAR_VAL")));
        }
        return actionParameterTraces;
    }

    @Override
    public void delete(ActionParameterDesignTraceKey actionParameterDesignTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterTrace {0}.", actionParameterDesignTraceKey.toString()));
        if (!exists(actionParameterDesignTraceKey)) {
            throw new ActionParameterTraceDoesNotExistException(MessageFormat.format(
                    "ActionParameterTrace {0} does not exists", actionParameterDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionParameterDesignTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
    }

    @Override
    public void insert(ActionParameterDesignTrace actionParameterDesignTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", actionParameterDesignTrace.toString()));
        if (exists(actionParameterDesignTrace.getMetadataKey())) {
            throw new ActionParameterTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionParameterDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionParameterDesignTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    public String insertStatement(ActionParameterDesignTrace actionParameterDesignTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getValue()) + ");";
    }
}