package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameterDesignTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterDesignTraceKey;
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

    public ScriptParameterDesignTraceConfiguration() {
        super();
    }

    @Override
    public Optional<ScriptParameterDesignTrace> get(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) throws SQLException {
        String query = "SELECT SCRIPT_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptParameterDesignTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptParameterDesignTrace(scriptParameterDesignTraceKey,
                cachedRowSet.getString("SCRIPT_PAR_VAL")));
    }

    @Override
    public List<ScriptParameterDesignTrace> getAll() throws SQLException {
        List<ScriptParameterDesignTrace> scriptParameterDesignTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            scriptParameterDesignTraces.add(new ScriptParameterDesignTrace(new ScriptParameterDesignTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID"),
                    cachedRowSet.getString("SCRIPT_PAR_NM")),
                    cachedRowSet.getString("SCRIPT_PAR_VAL")));
        }
        return scriptParameterDesignTraces;
    }

    @Override
    public void delete(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptParameterDesignTraceKey.toString()));
        if (!exists(scriptParameterDesignTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptParameterDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptParameterDesignTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getProcessId()) + " AND " +
                " SCRIPT_PAR_NM = "  + SQLTools.GetStringForSQL(scriptParameterDesignTraceKey.getScriptParameterName()) + ";";
    }

    @Override
    public void insert(ScriptParameterDesignTrace scriptParameterDesignTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameterDesignTrace {0}.", scriptParameterDesignTrace.toString()));
        if (exists(scriptParameterDesignTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptParameterDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptParameterDesignTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptParameterDesignTrace scriptParameterDesignTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptParameterDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getMetadataKey().getScriptParameterName()) + "," +
                SQLTools.GetStringForSQL(scriptParameterDesignTrace.getScriptParameterValue()) + ");";
    }
}