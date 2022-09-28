package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.trace.ScriptVersionTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptVersionTraceKey;
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
public class ScriptVersionTraceConfiguration extends Configuration<ScriptVersionTrace, ScriptVersionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    public ScriptVersionTraceConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getTraceMetadataRepository());
    }

    @Override
    public Optional<ScriptVersionTrace> get(ScriptVersionTraceKey scriptVersionTraceKey) {
        try {
            String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.getStringForSQL(scriptVersionTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.getStringForSQL(scriptVersionTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ScriptVersionTrace {0}. Returning first implementation", scriptVersionTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptVersionTrace(scriptVersionTraceKey,
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptVersionTrace> getAll() {
        try {
            List<ScriptVersionTrace> scriptVersionTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptVersionTraces.add(new ScriptVersionTrace(new ScriptVersionTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")));
            }
            return scriptVersionTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptVersionTraceKey scriptVersionTraceKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersionTrace {0}.", scriptVersionTraceKey.toString()));
        String deleteStatement = deleteStatement(scriptVersionTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionTraceKey scriptTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.getStringForSQL(scriptTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.getStringForSQL(scriptTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptVersionTrace scriptVersionTrace) {
        LOGGER.trace(MessageFormat.format("Inserting scriptVersionTrace {0}.", scriptVersionTrace.getMetadataKey().toString()));
        String insertStatement = insertStatement(scriptVersionTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionTrace scriptVersionTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.getStringForSQL(scriptVersionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.getStringForSQL(scriptVersionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.getStringForSQL(scriptVersionTrace.getScriptVersionNumber()) + "," +
                SQLTools.getStringForSQL(scriptVersionTrace.getScriptVersionDescription()) + ");";
    }

    @Override
    public void update(ScriptVersionTrace scriptVersionTrace) {
        LOGGER.trace(MessageFormat.format("Updating ScriptVersionTrace {0}.", scriptVersionTrace.toString()));
        String updateStatement = updateStatement(scriptVersionTrace);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    private String updateStatement(ScriptVersionTrace scriptVersionTrace) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " SET SCRIPT_VRS_DSC = " + SQLTools.getStringForSQL(scriptVersionTrace.getScriptVersionDescription()) + ", " +
                "SCRIPT_VRS_NB = " + SQLTools.getStringForSQL(scriptVersionTrace.getScriptVersionNumber()) +
                " WHERE RUN_ID = " + SQLTools.getStringForSQL(scriptVersionTrace.getMetadataKey().getRunId()) +
                " AND PRC_ID = " + SQLTools.getStringForSQL(scriptVersionTrace.getMetadataKey().getProcessId()) + ";";
    }
}