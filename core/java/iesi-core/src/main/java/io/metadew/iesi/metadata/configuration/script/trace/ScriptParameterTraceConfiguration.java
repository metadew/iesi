package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.trace.ScriptParameterTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptParameterTraceKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptParameterTraceConfiguration extends Configuration<ScriptParameterTrace, ScriptParameterTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptParameterTraceConfiguration INSTANCE;

    public synchronized static ScriptParameterTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptParameterTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptParameterTraceConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
    }

    @Override
    public Optional<ScriptParameterTrace> get(ScriptParameterTraceKey scriptParameterTraceKey) {
        try {String query = "SELECT SCRIPT_VRS_NB, SCRIPT_PAR_VAL FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptParameterTrace {0}. Returning first implementation", scriptParameterTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptParameterTrace(scriptParameterTraceKey,
                SQLTools.getStringFromSQLClob(cachedRowSet, "SCRIPT_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptParameterTrace> getAll() {
        try {
            List<ScriptParameterTrace> scriptParameterTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptParameterTraces.add(new ScriptParameterTrace(new ScriptParameterTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("SCRIPT_PAR_NM")),
                        SQLTools.getStringFromSQLClob(cachedRowSet, "SCRIPT_PAR_VAL")));
            }
            return scriptParameterTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptParameterTraceKey scriptParameterTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptParameterTrace {0}.", scriptParameterTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptParameterTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterTraceKey scriptParameterTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameterTrace scriptParameterTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameterTrace {0}.", scriptParameterTrace.getMetadataKey().toString()));
        String insertStatement = insertStatement(scriptParameterTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptParameterTrace scriptParameterTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getScriptParameterName()) + "," +
                SQLTools.getStringForSQLClob(scriptParameterTrace.getScriptParameterValue(),
                        getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                .findFirst()
                                .orElseThrow(RuntimeException::new)) + ");";
    }

    @Override
    public void update(ScriptParameterTrace scriptParameterTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptParameterTrace {0}.", scriptParameterTrace.toString()));
        String updateStatement = updateStatement(scriptParameterTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptParameterTrace scriptParameterTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " SET SCRIPT_PAR_VAL = " + SQLTools.getStringForSQLClob(scriptParameterTrace.getScriptParameterValue(),
                getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                        .findFirst()
                        .orElseThrow(RuntimeException::new)) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getProcessId()) +
                " AND SCRIPT_PAR_NM = " + SQLTools.getStringForSQL(scriptParameterTrace.getMetadataKey().getScriptParameterName()) + ";";
    }
}
