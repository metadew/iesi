package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.trace.ScriptLabelTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptLabelTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptLabelTraceConfiguration extends Configuration<ScriptLabelTrace, ScriptLabelTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptLabelTraceConfiguration INSTANCE;

    public synchronized static ScriptLabelTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptLabelTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptLabelTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptLabelTrace> get(ScriptLabelTraceKey scriptLabelTraceKey) {
        try {
            String query = "SELECT SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getProcessId()) + " AND " +
                    " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getScriptLabelKey().getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptLabelTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptLabelTrace(scriptLabelTraceKey,
                    new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                    cachedRowSet.getString("NAME"),
                    cachedRowSet.getString("VALUE")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptLabelTrace> getAll() {
        try {
            List<ScriptLabelTrace> scriptLabelDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_LBL_ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptLabelDesignTraces.add(new ScriptLabelTrace(
                        new ScriptLabelTraceKey(cachedRowSet.getString("RUN_ID"),cachedRowSet.getLong("PRC_ID"),new ScriptLabelKey(cachedRowSet.getString("SCRIPT_LBL_ID"))),
                        new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                        cachedRowSet.getString("NAME"),
                        cachedRowSet.getString("VALUE")));
            }
            return scriptLabelDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptLabelTraceKey scriptLabelTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting {0}.", scriptLabelTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptLabelTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public boolean exists(ScriptLabelTraceKey scriptLabelTraceKey) {
        String query = "SELECT * FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getProcessId()) + " AND " +
                " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getScriptLabelKey().getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String deleteStatement(ScriptLabelTraceKey scriptLabelTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getProcessId()) + " AND " +
                " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelTraceKey.getScriptLabelKey().getId()) + ";";
    }

    @Override
    public void insert(ScriptLabelTrace scriptParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptLabelTrace {0}.", scriptParameterDesignTrace.toString()));
        String insertStatement = insertStatement(scriptParameterDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptLabelTrace scriptLabelTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_LBL_ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE) VALUES (" +
                SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getScriptLabelKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelTrace.getScriptKey().getScriptId()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelTrace.getScriptKey().getScriptVersion()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelTrace.getName()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelTrace.getValue()) + ");";
    }

    @Override
    public void update(ScriptLabelTrace scriptLabelTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptLabelTrace {0}.", scriptLabelTrace.toString()));
        String updateStatement = updateStatement(scriptLabelTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptLabelTrace scriptLabelTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptLabelTraces") +
                " SET " +
                " SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptLabelTrace.getScriptKey().getScriptId()) + ", " +
                " SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptLabelTrace.getScriptKey().getScriptVersion()) + ", " +
                " NAME = " + SQLTools.GetStringForSQL(scriptLabelTrace.getName()) + ", " +
                " VALUE = " + SQLTools.GetStringForSQL(scriptLabelTrace.getValue()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getProcessId()) +
                " AND SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelTrace.getMetadataKey().getScriptLabelKey().getId()) + ";";
    }
}
