package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.design.ScriptLabelDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptLabelDesignTraceConfiguration extends Configuration<ScriptLabelDesignTrace, ScriptLabelDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptLabelDesignTraceConfiguration INSTANCE;

    public synchronized static ScriptLabelDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptLabelDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptLabelDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptLabelDesignTrace> get(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getProcessId()) + " AND " +
                    " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getScriptLabelKey().getId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptLabelDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptLabelDesignTrace(scriptLabelDesignTraceKey,
                    new ScriptKey(cachedRowSet.getString("SCRIPT_ID"), cachedRowSet.getLong("SCRIPT_VRS_NB")),
                    cachedRowSet.getString("NAME"),
                    cachedRowSet.getString("VALUE")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptLabelDesignTrace> getAll() {
        try {
            List<ScriptLabelDesignTrace> scriptLabelDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_LBL_ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptLabelDesignTraces.add(new ScriptLabelDesignTrace(
                        new ScriptLabelDesignTraceKey(cachedRowSet.getString("RUN_ID"),cachedRowSet.getLong("PRC_ID"),new ScriptLabelKey(cachedRowSet.getString("SCRIPT_LBL_ID"))),
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
    public void delete(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting {0}.", scriptLabelDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptLabelDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    public boolean exists(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey) {
        String query = "SELECT * FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getProcessId()) + " AND " +
                " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getScriptLabelKey().getId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String deleteStatement(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getProcessId()) + " AND " +
                " SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTraceKey.getScriptLabelKey().getId()) + ";";
    }

    @Override
    public void insert(ScriptLabelDesignTrace scriptParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptLabelDesignTrace {0}.", scriptParameterDesignTrace.toString()));
        String insertStatement = insertStatement(scriptParameterDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptLabelDesignTrace scriptLabelDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_LBL_ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE) VALUES (" +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getScriptLabelKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getScriptKey().getScriptId()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getScriptKey().getScriptVersion()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getName()) + ", " +
                SQLTools.GetStringForSQL(scriptLabelDesignTrace.getValue()) + ");";
    }

    @Override
    public void update(ScriptLabelDesignTrace scriptParameterDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptLabelDesignTrace {0}.", scriptParameterDesignTrace.toString()));
        String updateStatement = updateStatement(scriptParameterDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptLabelDesignTrace scriptLabelDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptLabelDesignTraces") +
                " SET " +
                " SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getScriptKey().getScriptId()) + ", " +
                " SCRIPT_VRS_NB = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getScriptKey().getScriptVersion()) + ", " +
                " NAME = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getName()) + ", " +
                " VALUE = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getValue()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getProcessId()) +
                " AND SCRIPT_LBL_ID = " + SQLTools.GetStringForSQL(scriptLabelDesignTrace.getMetadataKey().getScriptLabelKey().getId()) + ";";
    }
}
