package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameterTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
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

    public ScriptParameterTraceConfiguration() {
        super();
    }

    @Override
    public Optional<ScriptParameterTrace> get(ScriptParameterTraceKey scriptParameterTraceKey) throws SQLException {
        String query = "SELECT SCRIPT_VRS_NB, SCRIPT_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = "  + SQLTools.GetStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptParameterTrace {0}. Returning first implementation", scriptParameterTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptParameterTrace(scriptParameterTraceKey,
                cachedRowSet.getString("SCRIPT_PAR_VAL")));
    }

    @Override
    public List<ScriptParameterTrace> getAll() throws SQLException {
        List<ScriptParameterTrace> scriptParameterTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            scriptParameterTraces.add(new ScriptParameterTrace(new ScriptParameterTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("SCRIPT_PAR_NM")),
                    cachedRowSet.getString("SCRIPT_PAR_VAL")));
        }
        return scriptParameterTraces;
    }

    @Override
    public void delete(ScriptParameterTraceKey scriptParameterTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptParameterTraceKey.toString()));
        if (!exists(scriptParameterTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptParameterTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptParameterTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterTraceKey scriptParameterTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptParameterTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = "  + SQLTools.GetStringForSQL(scriptParameterTraceKey.getScriptParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameterTrace scriptParameterTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", scriptParameterTrace.getMetadataKey().toString()));
        if (exists(scriptParameterTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptParameterTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptParameterTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptParameterTrace scriptParameterTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getMetadataKey().getScriptParameterName()) + "," +
                SQLTools.GetStringForSQL(scriptParameterTrace.getScriptParameterValue()) + ");";
    }
}