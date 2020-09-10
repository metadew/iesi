package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.design.ScriptParameterDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptParameterDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptParameterDesignTraceConfiguration extends Configuration<ScriptParameterDesignTrace, ScriptParameterDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptParameterDesignTraceConfiguration INSTANCE;

    public synchronized static ScriptParameterDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptParameterDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptParameterDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptParameterDesignTrace> get(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptParameterDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptParameterDesignTrace(scriptParameterDesignTraceKey,
                    cachedRowSet.getString("SCRIPT_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptParameterDesignTrace> getAll() {
        try {
            List<ScriptParameterDesignTrace> scriptParameterDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptParameterDesignTraces.add(new ScriptParameterDesignTrace(new ScriptParameterDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("SCRIPT_PAR_NM")),
                        cachedRowSet.getString("SCRIPT_PAR_VAL")));
            }
            return scriptParameterDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptParameterDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptParameterDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public boolean exists(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) {
        String query = "SELECT * FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String deleteStatement(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getScriptParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameterDesignTrace scriptParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameterDesignTrace {0}.", scriptParameterDesignTrace.toString()));
        String insertStatement = insertStatement(scriptParameterDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptParameterDesignTrace scriptParameterDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getScriptParameterName()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getScriptParameterValue()) + ");";
    }

    @Override
    public void update(ScriptParameterDesignTrace scriptParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptParameterDesignTrace {0}.", scriptParameterDesignTrace.toString()));
        String updateStatement = updateStatement(scriptParameterDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptParameterDesignTrace scriptParameterDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " SET SCRIPT_PAR_VAL = " + SQLTools.GetStringForSQL(scriptParameterDesignTrace.getScriptParameterValue()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getProcessId()) +
                " AND SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getScriptParameterName()) + ";";
    }
}
