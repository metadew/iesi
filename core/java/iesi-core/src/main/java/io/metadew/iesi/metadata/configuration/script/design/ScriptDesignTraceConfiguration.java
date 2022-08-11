package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptDesignTraceKey;
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
public class ScriptDesignTraceConfiguration extends Configuration<ScriptDesignTrace, ScriptDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ScriptDesignTraceConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }


    @PostConstruct
    private void ScriptDesignTraceConfiguration() {
        setMetadataRepository(metadataRepositoryConfiguration.getTraceMetadataRepository());
    }

    @Override
    public Optional<ScriptDesignTrace> get(ScriptDesignTraceKey scriptDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_ID, PARENT_PRC_ID, SCRIPT_NM, SECURITY_GROUP_NAME, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptDesignTrace {0}. Returning first implementation", scriptDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptDesignTrace(scriptDesignTraceKey,
                    cachedRowSet.getString("SCRIPT_ID"),
                    cachedRowSet.getLong("PARENT_PRC_ID"),
                    cachedRowSet.getString("SCRIPT_NM"),
                    cachedRowSet.getString("SCRIPT_DSC"),
                    cachedRowSet.getString("SECURITY_GROUP_NAME")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptDesignTrace> getAll() {
        try {
            List<ScriptDesignTrace> scriptDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SECURITY_GROUP_NAME, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptDesignTraces.add(new ScriptDesignTrace(new ScriptDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getString("SCRIPT_DSC"),
                        cachedRowSet.getString("SECURITY_GROUP_NAME")));
            }
            return scriptDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptDesignTraceKey scriptDesignTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptDesignTrace {0}.", scriptDesignTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptDesignTraceKey scriptDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptDesignTrace scriptDesignTrace) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptDesignParameterTrace {0}.", scriptDesignTrace.toString()));
        String insertStatement = insertStatement(scriptDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public boolean exists(ScriptDesignTraceKey scriptDesignTraceKey) {
        String query = "SELECT * FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String insertStatement(ScriptDesignTrace scriptDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SECURITY_GROUP_NAME, SCRIPT_DSC) VALUES (" +
                SQLTools.getStringForSQL(scriptDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getParentProcessId()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getScriptId()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getScriptName()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getSecurityGroupName()) + "," +
                SQLTools.getStringForSQL(scriptDesignTrace.getScriptDescription()) + ");";
    }

    @Override
    public void update(ScriptDesignTrace scriptDesignTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptDesignParameterTrace {0}.", scriptDesignTrace.toString()));
        String updateStatement = updateStatement(scriptDesignTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptDesignTrace scriptDesignTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " SET PARENT_PRC_ID = " + SQLTools.getStringForSQL(scriptDesignTrace.getParentProcessId()) + "," +
                "SCRIPT_ID = " + SQLTools.getStringForSQL(scriptDesignTrace.getScriptId()) + "," +
                "SCRIPT_NM = " + SQLTools.getStringForSQL(scriptDesignTrace.getScriptName()) + "," +
                "SECURITY_GROUP_NAME = " + SQLTools.getStringForSQL(scriptDesignTrace.getSecurityGroupName()) + "," +
                "SCRIPT_DSC = " + SQLTools.getStringForSQL(scriptDesignTrace.getScriptDescription()) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(scriptDesignTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(scriptDesignTrace.getMetadataKey().getProcessId()) + ";";
    }
}