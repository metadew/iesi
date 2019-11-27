package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.trace.exception.ActionParameterTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.trace.exception.ActionParameterTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionParameterTraceConfiguration extends Configuration<ActionParameterTrace, ActionParameterTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionParameterTraceConfiguration INSTANCE;

    public synchronized static ActionParameterTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionParameterTraceConfiguration();
        }
        return INSTANCE;
    }

    private ActionParameterTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionParameterTrace> get(ActionParameterTraceKey actionParameterTraceKey) {
        try {
            String query = "SELECT ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getActionId()) + " AND " +
                    " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionParameterTrace {0}. Returning first implementation", actionParameterTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionParameterTrace(actionParameterTraceKey, cachedRowSet.getString("ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionParameterTrace> getAll() {
        try {
            List<ActionParameterTrace> actionParameterTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionParameterTraces.add(new ActionParameterTrace(new ActionParameterTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID"),
                        cachedRowSet.getString("ACTION_PAR_NM")),
                        cachedRowSet.getString("ACTION_PAR_VAL")));
            }
            return actionParameterTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionParameterTraceKey actionParameterTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterTrace {0}.", actionParameterTraceKey.toString()));
        if (!exists(actionParameterTraceKey)) {
            throw new ActionParameterTraceDoesNotExistException(MessageFormat.format(
                    "ActionParameterTrace {0} does not exists", actionParameterTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionParameterTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);

    }

    private String deleteStatement(ActionParameterTraceKey actionParameterTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterTraceKey.getName()) + ";";
    }

    @Override
    public void insert(ActionParameterTrace actionParameterTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", actionParameterTrace.getMetadataKey().toString()));
        if (exists(actionParameterTrace.getMetadataKey())) {
            throw new ActionParameterTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionParameterTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionParameterTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void insert(List<ActionParameterTrace> actionParameterTraces) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTraces {0}.", actionParameterTraces.stream().map(ActionParameterTrace::getMetadataKey).collect(Collectors.toList()).toString()));
        List<String> insertQueries = new ArrayList<>();
        for (ActionParameterTrace actionParameterTrace : actionParameterTraces) {
            if (exists(actionParameterTrace.getMetadataKey())) {
                LOGGER.info(MessageFormat.format("ActionParameterTrace {0} already exists. Skipping", actionParameterTrace.getMetadataKey().toString()));
            } else {
                insertQueries.add(insertStatement(actionParameterTrace));
            }
        }
        getMetadataRepository().executeBatch(insertQueries);
    }

    private String insertStatement(ActionParameterTrace actionParameterTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(actionParameterTrace.getValue()) + ");";
    }
}