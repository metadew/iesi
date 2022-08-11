package io.metadew.iesi.metadata.configuration.action.design;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionParameterDesignTraceConfiguration extends Configuration<ActionParameterDesignTrace, ActionParameterDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ActionParameterDesignTraceConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getTraceMetadataRepository());
    }

    @Override
    public Optional<ActionParameterDesignTrace> get(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        try {
            String query = "SELECT ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                    " ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionParameterDesignTrace {0}. Returning first implementation", actionParameterDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionParameterDesignTrace(actionParameterDesignTraceKey,
                    SQLTools.getStringFromSQLClob(cachedRowSet, "ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionParameterDesignTrace> getAll() {
        try {
            List<ActionParameterDesignTrace> actionParameterTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionParameterTraces.add(new ActionParameterDesignTrace(new ActionParameterDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID"),
                        cachedRowSet.getString("ACTION_PAR_NM")),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "ACTION_PAR_VAL")));
            }
            return actionParameterTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterDesignTrace {0}.", actionParameterDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(actionParameterDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
    }

    @Override
    public void insert(ActionParameterDesignTrace actionParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterDesignTrace {0}.", actionParameterDesignTrace.toString()));
        String insertStatement = insertStatement(actionParameterDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }


    public void insert(List<ActionParameterDesignTrace> actionParameterDesignTraces) {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterDesignTraces {0}.", actionParameterDesignTraces.stream().map(ActionParameterDesignTrace::getMetadataKey).collect(Collectors.toList()).toString()));
        List<String> insertQueries = new ArrayList<>();
        for (ActionParameterDesignTrace actionParameterDesignTrace : actionParameterDesignTraces) {
            insertQueries.add(insertStatement(actionParameterDesignTrace));
        }
        getMetadataRepository().executeBatch(insertQueries);
    }

    public String insertStatement(ActionParameterDesignTrace actionParameterDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getName()) + "," +
                SQLTools.getStringForSQLClob(actionParameterDesignTrace.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + ");";
    }

    @Override
    public void update(ActionParameterDesignTrace actionParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ActionParameterDesignTrace {0}.", actionParameterDesignTrace.getMetadataKey().toString()));
        String updateStatement = updateStatement(actionParameterDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ActionParameterDesignTrace actionParameterDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " SET ACTION_PAR_VAL = " +
                SQLTools.getStringForSQLClob(actionParameterDesignTrace.getValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getProcessId()) +
                " AND ACTION_ID = " + SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getActionId()) +
                " AND ACTION_PAR_NM = " + SQLTools.getStringForSQL(actionParameterDesignTrace.getMetadataKey().getName()) + ";";
    }
}