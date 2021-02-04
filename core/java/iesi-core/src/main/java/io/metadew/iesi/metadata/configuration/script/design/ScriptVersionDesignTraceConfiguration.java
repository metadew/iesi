package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.design.ScriptVersionDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptVersionDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptVersionDesignTraceConfiguration extends Configuration<ScriptVersionDesignTrace, ScriptVersionDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptVersionDesignTraceConfiguration INSTANCE;

    public synchronized static ScriptVersionDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptVersionDesignTraceConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
    }

    @Override
    public Optional<ScriptVersionDesignTrace> get(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptVersionDesignTrace {0}. Returning first implementation", scriptVersionDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptVersionDesignTrace(scriptVersionDesignTraceKey,
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptVersionDesignTrace> getAll() {
        try {
            List<ScriptVersionDesignTrace> scriptVersionDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptVersionDesignTraces.add(new ScriptVersionDesignTrace(new ScriptVersionDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")));
            }
            return scriptVersionDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersionDesignTrace {0}.", scriptVersionDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptVersionDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
    }

    public boolean exists(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    @Override
    public void insert(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersionDesignTrace {0}.", scriptVersionDesignTrace.toString()));
        String insertStatement = insertStatement(scriptVersionDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.getStringForSQL(scriptVersionDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(scriptVersionDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(scriptVersionDesignTrace.getScriptVersionNumber()) + "," +
                SQLTools.getStringForSQL(scriptVersionDesignTrace.getScriptVersionDescription()) + ");";
    }

    @Override
    public void update(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptVersionDesignTrace {0}.", scriptVersionDesignTrace.toString()));
        String updateStatement = updateStatement(scriptVersionDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " SET SCRIPT_VRS_DSC = " + SQLTools.getStringForSQL(scriptVersionDesignTrace.getScriptVersionDescription()) + ", " +
                "SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionDesignTrace.getScriptVersionNumber()) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(scriptVersionDesignTrace.getMetadataKey().getProcessId()) + ";";
    }
}