package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.exception.ScriptExecutionRequestAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.execution.script.exception.ScriptExecutionRequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionStatus;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptExecutionConfiguration extends Configuration<ScriptExecution, ScriptExecutionKey> {

    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ScriptExecutionConfiguration() {
        super();
    }


    @Override
    public Optional<ScriptExecution> get(ScriptExecutionKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT SCRIPT_EXEC_ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS " +
                    "FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                    " WHERE SCRIPT_EXEC_ID = " + SQLTools.GetStringForSQL(scriptExecutionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptExecution {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptExecution(scriptExecutionRequestKey,
                    new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                    cachedRowSet.getString("RUN_ID"),
                    ScriptExecutionStatus.valueOf(cachedRowSet.getString("ST_NM")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                    SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptExecution> getAll() {
        try {
            List<ScriptExecution> scriptExecutions = new ArrayList<>();
            String query = "SELECT SCRIPT_EXEC_ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") + ";";

            CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutions.add(new ScriptExecution(new ScriptExecutionKey(
                        cachedRowSet.getString("SCRIPT_EXEC_ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                        cachedRowSet.getString("RUN_ID"),
                        ScriptExecutionStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
            }
            return scriptExecutions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptExecutionKey scriptExecutionKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecution {0}.", scriptExecutionKey.toString()));
        if (!exists(scriptExecutionKey)) {
            throw new ScriptExecutionRequestDoesNotExistException(MessageFormat.format(
                    "ScriptExecution {0} does not exists", scriptExecutionKey.toString()));
        }
        List<String> deleteStatement = deleteStatement(scriptExecutionKey);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionKey scriptExecutionKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " WHERE " +
                " SCRIPT_EXEC_ID = " + SQLTools.GetStringForSQL(scriptExecutionKey.getId()) + ";");
        return queries;
    }

    @Override
    public void insert(ScriptExecution scriptExecutionRequest) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptExecution {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new ScriptExecutionRequestAlreadyExistsException(MessageFormat.format(
                    "ScriptExecution {0} already exists", scriptExecutionRequest.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptExecutionRequest);
        getMetadataControl().getExecutionServerMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptExecution scriptExecution) {
        return "INSERT INTO " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " (SCRIPT_EXEC_ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS) VALUES (" +
                SQLTools.GetStringForSQL(scriptExecution.getMetadataKey().getId()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScriptExecutionRequestKey().getId()) + ", " +
                SQLTools.GetStringForSQL(scriptExecution.getRunId()) + ", " +
                SQLTools.GetStringForSQL(scriptExecution.getScriptExecutionStatus().value()) + ", " +
                SQLTools.GetStringForSQL(scriptExecution.getStartTimestamp()) + ", " +
                SQLTools.GetStringForSQL(scriptExecution.getEndTimestamp()) + ");";
    }

    public List<ScriptExecution> getByScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        try {
            List<ScriptExecution> scriptExecutions = new ArrayList<>();
            String query = "SELECT SCRIPT_EXEC_ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM " +
                    getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                    " WHERE SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(scriptExecutionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataControl().getExecutionServerMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutions.add(new ScriptExecution(new ScriptExecutionKey(
                        cachedRowSet.getString("SCRIPT_EXEC_ID")),
                        scriptExecutionRequestKey,
                        cachedRowSet.getString("RUN_ID"),
                        ScriptExecutionStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
            }
            return scriptExecutions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByScriptExecutionRequestKey(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecution by ExecutionKey {0}.", scriptExecutionRequestKey.toString()));
        List<String> deleteStatement = deleteStatement(scriptExecutionRequestKey);
        getMetadataControl().getExecutionServerMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataControl().getExecutionServerMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " WHERE SCRPT_REQUEST_ID = " + SQLTools.GetStringForSQL(scriptExecutionRequestKey.getId()) + ";");
        return queries;
    }
}
