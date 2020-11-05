package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
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

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptExecutionConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    private final static String query = "SELECT ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() +
            " WHERE ID = :id ;";
    private static final String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " (ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS) " +
            "VALUES (:id, :requestId, :runId, :value, :start, :end) ;";
    private final static String getAll = "SELECT ID, SCRPT_REQUEST_ID, RUN_ID, ST_NM, STRT_TMS, END_TMS FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() +
            " ;";
    private static final String delete = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " WHERE ID = :id  ;";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() +
            " SET SCRPT_REQUEST_ID = :id, RUN_ID = :runId, ST_NM = :value, STRT_TMS = :start, END_TMS = :end " +
            "  WHERE ID = :requestId ; ";
    private static final String deleteByScriptExecutionRequestKey = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutions").getName() + " WHERE SCRPT_REQUEST_ID = :id  ;";

    @Override
    public Optional<ScriptExecution> get(ScriptExecutionKey scriptExecutionRequestKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ScriptExecutionExtractor())));
    }

    @Override
    public List<ScriptExecution> getAll() {
        return namedParameterJdbcTemplate.query(getAll, new ScriptExecutionExtractor());
    }


    @Override
    public void delete(ScriptExecutionKey scriptExecutionKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecution {0}.", scriptExecutionKey.toString()));
        if (!exists(scriptExecutionKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionKey.getId());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ScriptExecution scriptExecutionRequest) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptExecution {0}.", scriptExecutionRequest.toString()));
        if (exists(scriptExecutionRequest.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(scriptExecutionRequest);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequest.getMetadataKey().getId())
                .addValue("requestId", scriptExecutionRequest.getScriptExecutionRequestKey().getId())
                .addValue("runId", scriptExecutionRequest.getRunId())
                .addValue("value", scriptExecutionRequest.getScriptRunStatus().value())
                .addValue("start", scriptExecutionRequest.getStartTimestamp())
                .addValue("end", scriptExecutionRequest.getEndTimestamp());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public void deleteByScriptExecutionRequestKey(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptExecution by ExecutionKey {0}.", scriptExecutionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteByScriptExecutionRequestKey,
                sqlParameterSource);
    }

    @Override
    public void update(ScriptExecution scriptExecution) {
        if (!exists(scriptExecution.getMetadataKey())) {
            throw new MetadataDoesNotExistException(scriptExecution);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecution.getScriptExecutionRequestKey().getId())
                .addValue("runId", scriptExecution.getRunId())
                .addValue("value", scriptExecution.getScriptRunStatus().value())
                .addValue("start", scriptExecution.getStartTimestamp())
                .addValue("end", scriptExecution.getEndTimestamp())
                .addValue("requestId", scriptExecution.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}
