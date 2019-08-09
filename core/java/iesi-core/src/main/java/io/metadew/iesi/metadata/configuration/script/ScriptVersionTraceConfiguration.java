package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.action.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptVersionTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionTraceKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptVersionTraceConfiguration extends Configuration<ScriptVersionTrace, ScriptVersionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    public ScriptVersionTraceConfiguration(MetadataControl metadataControl) {
        super(metadataControl);
    }


    @Override
    public Optional<ScriptVersionTrace> get(ScriptVersionTraceKey scriptVersionTraceKey) throws SQLException {
        String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptVersionTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        if (cachedRowSet.size() == 0) {
            return Optional.empty();
        } else if (cachedRowSet.size() > 1) {
            LOGGER.info(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptVersionTraceKey.toString()));
        }
        cachedRowSet.next();
        return Optional.of(new ScriptVersionTrace(scriptVersionTraceKey,
                cachedRowSet.getLong("SCRIPT_VRS_NB"),
                cachedRowSet.getString("SCRIPT_VRS_DSC")));
    }

    @Override
    public List<ScriptVersionTrace> getAll() throws SQLException {
        List<ScriptVersionTrace> scriptVersionTraces = new ArrayList<>();
        String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionTraces") + ";";
        CachedRowSet cachedRowSet = getMetadataControl().getTraceMetadataRepository().executeQuery(query, "reader");
        while (cachedRowSet.next()) {
            scriptVersionTraces.add(new ScriptVersionTrace(new ScriptVersionTraceKey(
                    cachedRowSet.getString("RUN_ID"),
                    cachedRowSet.getLong("PRC_ID")),
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        }
        return scriptVersionTraces;
    }

    @Override
    public void delete(ScriptVersionTraceKey scriptVersionTraceKey) throws MetadataDoesNotExistException, SQLException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptVersionTraceKey.toString()));
        if (!exists(scriptVersionTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptVersionTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptVersionTraceKey);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionTraceKey scriptTraceKey) {
        return "DELETE FROM " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
                " PRC_ID = "  + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptVersionTrace scriptVersionTrace) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", scriptVersionTrace.getMetadataKey().toString()));
        if (exists(scriptVersionTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptVersionTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptVersionTrace);
        getMetadataControl().getTraceMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionTrace scriptVersionTrace) {
        return "INSERT INTO " + getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptVersionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getScriptVersionNumber()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getScriptVersionDescription()) + ");";
    }
}