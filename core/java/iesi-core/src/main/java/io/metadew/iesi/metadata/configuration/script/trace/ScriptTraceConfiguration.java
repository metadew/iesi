package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.trace.ScriptTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptTraceConfiguration extends Configuration<ScriptTrace, ScriptTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptTraceConfiguration INSTANCE;

    public synchronized static ScriptTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptTrace> get(ScriptTraceKey scriptTraceKey) {
        try {
            String query = "SELECT PARENT_PRC_ID, SCRIPT_NM, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionParameter {0}. Returning first implementation", scriptTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptTrace(scriptTraceKey,
                    cachedRowSet.getString("SCRIPT_ID"),
                    cachedRowSet.getLong("PARENT_PRC_ID"),
                    cachedRowSet.getString("SCRIPT_NM"),
                    cachedRowSet.getString("SCRIPT_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptTrace> getAll() {
        try {
            List<ScriptTrace> scriptTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptTraces.add(new ScriptTrace(new ScriptTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getString("SCRIPT_DSC")));

            }
            return scriptTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptTraceKey scriptTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptTraceKey scriptTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptTrace scriptTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptTrace {0}.", scriptTrace.getMetadataKey().toString()));
        String insertStatement = insertStatement(scriptTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptTrace scriptTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptTraces") +
                " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptTrace.getParentProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptTrace.getScriptId()) + "," +
                SQLTools.GetStringForSQL(scriptTrace.getScriptName()) + "," +
                SQLTools.GetStringForSQL(scriptTrace.getScriptDescription()) + ");";
    }

    @Override
    public void update(ScriptTrace scriptTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptTrace {0}.", scriptTrace.toString()));
        String updateStatement = updateStatement(scriptTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptTrace scriptTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptTraces") +
                " SET PARENT_PRC_ID = " + SQLTools.GetStringForSQL(scriptTrace.getParentProcessId()) + "," +
                "SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptTrace.getScriptId()) + "," +
                "SCRIPT_NM = " + SQLTools.GetStringForSQL(scriptTrace.getScriptName()) + "," +
                "SCRIPT_DSC = " + SQLTools.GetStringForSQL(scriptTrace.getScriptDescription()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getProcessId()) + ";";
    }
}