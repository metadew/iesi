package io.metadew.iesi.metadata.configuration.action.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.design.exception.ActionDesignTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.design.exception.ActionDesignTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.design.ScriptDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.key.ActionDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionDesignTraceConfiguration extends Configuration<ActionDesignTrace, ActionDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionDesignTraceConfiguration INSTANCE;

    public synchronized static ActionDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ActionDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionDesignTrace> get(ActionDesignTraceKey actionDesignTraceKey) {
        try {
            String query = "SELECT ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getActionId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ActionDesignTrace {0}. Returning first implementation", actionDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionDesignTrace(actionDesignTraceKey,
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionDesignTrace> getAll() {
        try {
            List<ActionDesignTrace> actionDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionDesignTraces.add(new ActionDesignTrace(new ActionDesignTraceKey(
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
            return actionDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionDesignTraceKey actionDesignTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionDesignTrace {0}.", actionDesignTraceKey.toString()));
        if (!exists(actionDesignTraceKey)) {
            throw new ActionDesignTraceDoesNotExistException(MessageFormat.format(
                    "ActionTrace {0} does not exists", actionDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionDesignTraceKey actionDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionDesignTraceKey.getActionId()) + ";";
    }

    @Override
    public void insert(ActionDesignTrace actionDesignTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionDesignTrace {0}.", actionDesignTrace.toString()));
        if (exists(actionDesignTrace.getMetadataKey())) {
            throw new ActionDesignTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionDesignTrace actionDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
                " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(actionDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getNumber()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getType()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getName()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getDescription()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getComponent()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getIteration()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getCondition()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getRetries()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(actionDesignTrace.getErrorStop()) + ");";
    }
}