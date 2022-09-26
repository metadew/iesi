package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ActionTraceConfiguration extends Configuration<ActionTrace, ActionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ActionTraceConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getTraceMetadataRepository());
    }

    @Override
    public Optional<ActionTrace> get(ActionTraceKey actionTraceKey) {
        try {
            String query = "SELECT ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(actionTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.getStringForSQL(actionTraceKey.getActionId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", actionTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionTrace(actionTraceKey,
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
    public List<ActionTrace> getAll() {
        try {
            List<ActionTrace> actionTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
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
                        SQLTools.getStringFromSQLClob(cachedRowSet, "CONDITION_VAL"),
                        cachedRowSet.getInt("RETRIES_VAL"),
                        cachedRowSet.getString("EXP_ERR_FL"),
                        cachedRowSet.getString("STOP_ERR_FL")));
            }
            return actionTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionTraceKey actionTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", actionTraceKey.toString()));
        String deleteStatement = deleteStatement(actionTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionTraceKey actionTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(actionTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.getStringForSQL(actionTraceKey.getActionId()) + ";";
    }

    @Override
    public void insert(ActionTrace actionTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ActionTrace {0}.", actionTrace.getMetadataKey().toString()));
        String insertStatement = insertStatement(actionTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionTrace actionTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
                " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.getStringForSQL(actionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(actionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(actionTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.getStringForSQL(actionTrace.getNumber()) + "," +
                SQLTools.getStringForSQL(actionTrace.getType()) + "," +
                SQLTools.getStringForSQL(actionTrace.getName()) + "," +
                SQLTools.getStringForSQL(actionTrace.getDescription()) + "," +
                SQLTools.getStringForSQL(actionTrace.getComponent()) + "," +
                SQLTools.getStringForSQL(actionTrace.getIteration()) + "," +
                SQLTools.getStringForSQLClob(actionTrace.getCondition(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + "," +
                SQLTools.getStringForSQL(actionTrace.getRetries()) + "," +
                SQLTools.getStringForSQL(actionTrace.getErrorExpected()) + "," +
                SQLTools.getStringForSQL(actionTrace.getErrorStop()) + ");";
    }

    @Override
    public void update(ActionTrace actionTrace) {
        LOGGER.trace(MessageFormat.format("Updating ActionTrace {0}.", actionTrace.getMetadataKey().toString()));
        String updateStatement = updateStatement(actionTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ActionTrace actionTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionTraces") +
                " SET ACTION_NB = " + SQLTools.getStringForSQL(actionTrace.getNumber()) + "," +
                "ACTION_TYP_NM = " + SQLTools.getStringForSQL(actionTrace.getType()) + "," +
                "ACTION_NM = " + SQLTools.getStringForSQL(actionTrace.getName()) + "," +
                "ACTION_DSC = " + SQLTools.getStringForSQL(actionTrace.getDescription()) + "," +
                "COMP_NM = " + SQLTools.getStringForSQL(actionTrace.getComponent()) + "," +
                "ITERATION_VAL = " + SQLTools.getStringForSQL(actionTrace.getIteration()) + "," +
                "CONDITION_VAL = " +
                SQLTools.getStringForSQLClob(actionTrace.getCondition(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + "," +
                "RETRIES_VAL = " + SQLTools.getStringForSQL(actionTrace.getRetries()) + "," +
                "EXP_ERR_FL = " + SQLTools.getStringForSQL(actionTrace.getErrorExpected()) + "," +
                "STOP_ERR_FL =" + SQLTools.getStringForSQL(actionTrace.getErrorStop()) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(actionTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(actionTrace.getMetadataKey().getProcessId()) +
                " AND ACTION_ID= " + SQLTools.getStringForSQL(actionTrace.getMetadataKey().getActionId()) + ";";
    }


//    public void insert(List<ActionTrace> actionTraces) throws MetadataAlreadyExistsException, SQLException {
//        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTraces {0}.", actionTraces.stream().map(ActionTrace::getMetadataKey).collect(Collectors.toList()).toString()));
//        List<String> insertQueries = new ArrayList<>();
//        for (ActionTrace actionTrace : actionTraces) {
//            insertQueries.add(insertStatement(actionTrace));
//        }
//        getMetadataRepository().executeBatch(insertQueries);
//    }
}