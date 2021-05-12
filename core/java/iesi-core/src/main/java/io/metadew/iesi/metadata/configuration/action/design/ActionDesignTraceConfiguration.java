package io.metadew.iesi.metadata.configuration.action.design;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
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
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
    }

    @Override
    public Optional<ActionDesignTrace> get(ActionDesignTraceKey actionDesignTraceKey) {
        try {
            String query = "SELECT ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getActionId()) + ";";
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
                    SQLTools.getStringFromSQLClob(cachedRowSet, "CONDITION_VAL"),
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
                        SQLTools.getStringFromSQLClob(cachedRowSet, "CONDITION_VAL"),
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
    public void delete(ActionDesignTraceKey actionDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionDesignTrace {0}.", actionDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(actionDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionDesignTraceKey actionDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.getStringForSQL(actionDesignTraceKey.getActionId()) + ";";
    }

    @Override
    public void insert(ActionDesignTrace actionDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ActionDesignTrace {0}.", actionDesignTrace.toString()));
        String insertStatement = insertStatement(actionDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionDesignTrace actionDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
                " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getNumber()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getType()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getName()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getDescription()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getComponent()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getIteration()) + "," +
                SQLTools.getStringForSQLClob(actionDesignTrace.getCondition(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getRetries()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getErrorExpected()) + "," +
                SQLTools.getStringForSQL(actionDesignTrace.getErrorStop()) + ");";
    }

    @Override
    public void update(ActionDesignTrace actionDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ActionDesignTrace {0}.", actionDesignTrace.getMetadataKey().toString()));
        String updateStatement = updateStatement(actionDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ActionDesignTrace actionDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionDesignTraces") +
                " SET ACTION_NB = " + SQLTools.getStringForSQL(actionDesignTrace.getNumber()) + "," +
                "ACTION_TYP_NM = " + SQLTools.getStringForSQL(actionDesignTrace.getType()) + "," +
                "ACTION_NM = " + SQLTools.getStringForSQL(actionDesignTrace.getName()) + "," +
                "ACTION_DSC = " + SQLTools.getStringForSQL(actionDesignTrace.getDescription()) + "," +
                "COMP_NM = " + SQLTools.getStringForSQL(actionDesignTrace.getComponent()) + "," +
                "ITERATION_VAL = " + SQLTools.getStringForSQL(actionDesignTrace.getIteration()) + "," +
                "CONDITION_VAL = " + SQLTools.getStringForSQLClob(actionDesignTrace.getCondition(),
                getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                        .findFirst()
                        .orElseThrow(RuntimeException::new)) + "," +
                "RETRIES_VAL = " + SQLTools.getStringForSQL(actionDesignTrace.getRetries()) + "," +
                "EXP_ERR_FL = " + SQLTools.getStringForSQL(actionDesignTrace.getErrorExpected()) + "," +
                "STOP_ERR_FL =" + SQLTools.getStringForSQL(actionDesignTrace.getErrorStop()) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getProcessId()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionDesignTrace.getMetadataKey().getActionId()) + ";";
    }

}