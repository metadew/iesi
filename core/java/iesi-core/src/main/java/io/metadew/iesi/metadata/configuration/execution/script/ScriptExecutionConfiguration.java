package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
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
    private static ScriptExecutionConfiguration INSTANCE;

    public synchronized static ScriptExecutionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionConfiguration();
        }
        return INSTANCE;
    }

    private ScriptExecutionConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());}

    @Override
    public Optional<ScriptExecution> get(ScriptExecutionKey scriptExecutionRequestKey) {
        try {
            String query = "SELECT ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS " +
                    "FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                    " WHERE ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptExecution {0}. Returning first implementation", scriptExecutionRequestKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptExecution(scriptExecutionRequestKey,
                    new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                    cachedRowSet.getString("RUN_ID"),
                    ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
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
            String query = "SELECT ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutions.add(new ScriptExecution(new ScriptExecutionKey(
                        cachedRowSet.getString("ID")),
                        new ScriptExecutionRequestKey(cachedRowSet.getString("SCRPT_REQUEST_ID")),
                        cachedRowSet.getString("RUN_ID"),
                        ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
                        SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
            }
            return scriptExecutions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptExecutionKey scriptExecutionKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecution {0}.", scriptExecutionKey.toString()));
        if (!exists(scriptExecutionKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionKey);
        }
        List<String> deleteStatement = deleteStatement(scriptExecutionKey);
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionKey scriptExecutionKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " WHERE " +
                " ID = " + SQLTools.getStringForSQL(scriptExecutionKey.getId()) + ";");
        return queries;
    }

    @Override
    public void insert(ScriptExecution scriptExecutionRequest) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptExecution {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(scriptExecutionRequest);
        }
        String insertStatement = insertStatement(scriptExecutionRequest);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptExecution scriptExecution) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " (ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS) VALUES (" +
                SQLTools.getStringForSQL(scriptExecution.getMetadataKey().getId()) + "," +
                SQLTools.getStringForSQL(scriptExecution.getScriptExecutionRequestKey().getId()) + ", " +
                SQLTools.getStringForSQL(scriptExecution.getRunId()) + ", " +
                SQLTools.getStringForSQL(scriptExecution.getScriptRunStatus().value()) + ", " +
                SQLTools.getStringForSQL(scriptExecution.getStartTimestamp()) + ", " +
                SQLTools.getStringForSQL(scriptExecution.getEndTimestamp()) + ");";
    }

    public List<ScriptExecution> getByScriptExecutionRequest(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        try {
            List<ScriptExecution> scriptExecutions = new ArrayList<>();
            String query = "SELECT ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                    " WHERE SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";";

            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptExecutions.add(new ScriptExecution(new ScriptExecutionKey(
                        cachedRowSet.getString("ID")),
                        scriptExecutionRequestKey,
                        cachedRowSet.getString("RUN_ID"),
                        ScriptRunStatus.valueOf(cachedRowSet.getString("ST_NM")),
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
        getMetadataRepository().executeBatch(deleteStatement);
    }

    private List<String> deleteStatement(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") +
                " WHERE SCRPT_REQUEST_ID = " + SQLTools.getStringForSQL(scriptExecutionRequestKey.getId()) + ";");
        return queries;
    }

    @Override
    public void update(ScriptExecution scriptExecution) {
        if (!exists(scriptExecution.getMetadataKey())) {
            throw new MetadataDoesNotExistException(scriptExecution);
        }
        String updateStatement = updateStatement(scriptExecution);
        getMetadataRepository().executeUpdate(updateStatement);
    }

    public String updateStatement(ScriptExecution scriptExecution) {
        return "UPDATE " + getMetadataRepository().getTableNameByLabel("ScriptExecutions") + " SET " +
                "SCRPT_REQUEST_ID= " + SQLTools.getStringForSQL(scriptExecution.getScriptExecutionRequestKey().getId()) + ", " +
                "RUN_ID=" + SQLTools.getStringForSQL(scriptExecution.getRunId()) + ", " +
                "ST_NM=" + SQLTools.getStringForSQL(scriptExecution.getScriptRunStatus().value()) + ", " +
                "STRT_TMS=" + SQLTools.getStringForSQL(scriptExecution.getStartTimestamp()) + ", " +
                "END_TMS=" + SQLTools.getStringForSQL(scriptExecution.getEndTimestamp()) +
                " WHERE ID = " + SQLTools.getStringForSQL(scriptExecution.getMetadataKey().getId()) + ";";
    }
}
