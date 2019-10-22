package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.design.exception.ScriptDesignTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.design.exception.ScriptDesignTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.script.design.ScriptDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptDesignTraceConfiguration extends Configuration<ScriptDesignTrace, ScriptDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptDesignTraceConfiguration INSTANCE;

    public synchronized static ScriptDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptDesignTrace> get(ScriptDesignTraceKey scriptDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_ID, PARENT_PRC_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
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
                    cachedRowSet.getString("SCRIPT_TYP_NM"),
                    cachedRowSet.getString("SCRIPT_NM"),
                    cachedRowSet.getString("SCRIPT_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptDesignTrace> getAll() {
        try {
            List<ScriptDesignTrace> scriptDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptDesignTraces.add(new ScriptDesignTrace(new ScriptDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getString("SCRIPT_ID"),
                        cachedRowSet.getLong("PARENT_PRC_ID"),
                        cachedRowSet.getString("SCRIPT_TYP_NM"),
                        cachedRowSet.getString("SCRIPT_NM"),
                        cachedRowSet.getString("SCRIPT_DSC")));
            }
            return scriptDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptDesignTraceKey scriptDesignTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptDesignTrace {0}.", scriptDesignTraceKey.toString()));
        if (!exists(scriptDesignTraceKey)) {
            throw new ScriptDesignTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptDesignTraceKey scriptDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptDesignTrace scriptDesignTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptDesignParameterTrace {0}.", scriptDesignTrace.toString()));
        if (exists(scriptDesignTrace.getMetadataKey())) {
            throw new ScriptDesignTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public boolean exists(ScriptDesignTraceKey scriptDesignTraceKey) {
        String query = "SELECT * FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    private String insertStatement(ScriptDesignTrace scriptDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptDesignTraces") +
                " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getParentProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getScriptId()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getScriptType()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getScriptName()) + "," +
                SQLTools.GetStringForSQL(scriptDesignTrace.getScriptDescription()) + ");";
    }
}