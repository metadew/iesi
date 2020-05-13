package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.service.metadata.MetadataFieldService;
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
    public void delete(ActionParameterTraceKey actionParameterTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterTrace {0}.", actionParameterTraceKey.toString()));
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
    public void insert(ActionParameterTrace actionParameterTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", actionParameterTrace.getMetadataKey().toString()));
        String insertStatement = insertStatement(actionParameterTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void insert(List<ActionParameterTrace> actionParameterTraces) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTraces {0}.", actionParameterTraces.stream().map(ActionParameterTrace::getMetadataKey).collect(Collectors.toList()).toString()));
        List<String> insertQueries = new ArrayList<>();
        for (ActionParameterTrace actionParameterTrace : actionParameterTraces) {
            insertQueries.add(insertStatement(actionParameterTrace));
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
                SQLTools.GetStringForSQL(MetadataFieldService.getInstance().truncateAccordingToConfiguration("ActionParameterTraces", "ACTION_PAR_VAL" ,actionParameterTrace.getValue())) + ");";
    }

    @Override
    public void update(ActionParameterTrace actionParameterTrace) {
        LOGGER.trace(MessageFormat.format("Updating ActionParameterTrace {0}.", actionParameterTrace.getMetadataKey().toString()));
        String updateStatement = updateStatement(actionParameterTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ActionParameterTrace actionParameterTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " SET ACTION_PAR_VAL = " + SQLTools.GetStringForSQL(MetadataFieldService.getInstance().truncateAccordingToConfiguration("ActionParameterTraces", "ACTION_PAR_VAL", actionParameterTrace.getValue())) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getProcessId()) +
                " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getActionId()) +
                " AND ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterTrace.getMetadataKey().getName()) + ";";
    }
}