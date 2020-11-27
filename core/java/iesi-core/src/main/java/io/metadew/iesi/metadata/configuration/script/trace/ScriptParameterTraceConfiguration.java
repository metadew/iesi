package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.trace.ScriptParameterTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptParameterTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptParameterTraceConfiguration extends Configuration<ScriptParameterTrace, ScriptParameterTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptParameterTraceConfiguration instance;

    public static synchronized ScriptParameterTraceConfiguration getInstance() {
        if (instance == null) {
            instance = new ScriptParameterTraceConfiguration();
        }
        return instance;
    }

    private ScriptParameterTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }


    @Override
    public Optional<ScriptParameterTrace> get(ScriptParameterTraceKey scriptParameterTraceKey) {
        try {String query = "SELECT SCRIPT_VRS_NB, SCRIPT_PAR_VAL FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(String.format("Found multiple implementations for ScriptParameterTrace %s. Returning first implementation", scriptParameterTraceKey));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptParameterTrace(scriptParameterTraceKey,
                cachedRowSet.getString("SCRIPT_PAR_VAL")));
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
                        cachedRowSet.getString("SCRIPT_PAR_VAL")));
            }
            return scriptParameterTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptParameterTraceKey scriptParameterTraceKey) {
        LOGGER.trace(String.format("Deleting ScriptParameterTrace %s.", scriptParameterTraceKey));
        String deleteStatement = deleteStatement(scriptParameterTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterTraceKey scriptParameterTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameterTrace scriptParameterTrace) {
        LOGGER.trace(String.format("Inserting ScriptParameterTrace %s.", scriptParameterTrace));
        String insertStatement = insertStatement(scriptParameterTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptParameterTrace scriptParameterTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getScriptParameterName()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getScriptParameterValue()) + ");";
    }

    @Override
    public void update(ScriptParameterTrace scriptParameterTrace) {
        LOGGER.trace(String.format("Updating ScriptParameterTrace %s.", scriptParameterTrace));
        String updateStatement = updateStatement(scriptParameterTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptParameterTrace scriptParameterTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " SET SCRIPT_PAR_VAL = " + SQLTools.GetStringForSQL(scriptParameterTrace.getScriptParameterValue()) +
                " WHERE RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getProcessId()) +
                " AND SCRIPT_PAR_NM = " + SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getScriptParameterName()) + ";";
    }
}
