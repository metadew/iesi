package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersionDesignTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionDesignTraceKey;
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

    public ScriptVersionDesignTraceConfiguration() {
        super();
    }

    @Override
    public Optional<ScriptVersionDesignTrace> get(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) throws SQLException {
        String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptVersionDesignTrace {0}. Returning first implementation", scriptVersionDesignTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptVersionDesignTrace(scriptVersionDesignTraceKey,
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SCRIPT_VRS_DSC")));
    }

    @Override
    public List<ScriptVersionDesignTrace> getAll() throws SQLException {
        List<ScriptVersionDesignTrace> scriptVersionDesignTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            scriptVersionDesignTraces.add(new ScriptVersionDesignTrace(new ScriptVersionDesignTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID")),
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        }
        return scriptVersionDesignTraces;
    }

    @Override
    public void delete(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptVersionDesignTraceKey.toString()));
        if (!exists(scriptVersionDesignTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptVersionDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptVersionDesignTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptVersionDesignTrace scriptVersionDesignTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting scriptVersionDesignTrace {0}.", scriptVersionDesignTrace.toString()));
        if (exists(scriptVersionDesignTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptVersionDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptVersionDesignTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getScriptVersionNumber()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getScriptVersionDescription()) + ");";
    }
}