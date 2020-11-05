package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ScriptExecutionRequestImpersonationConfiguration extends Configuration<ScriptExecutionRequestImpersonation, ScriptExecutionRequestImpersonationKey> {

    private static ScriptExecutionRequestImpersonationConfiguration INSTANCE;

    public synchronized static ScriptExecutionRequestImpersonationConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionRequestImpersonationConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptExecutionRequestImpersonationConfiguration() {
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

    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() +
            " (ID, SCRIPT_EXEC_REQ_ID, IMP_ID) VALUES (:id, :scriptId, :impId)";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() +
            " SET SCRIPT_EXEC_REQ_ID = :scriptId, IMP_ID = :impId WHERE ID = :id ; ";
    private static final String deleteByImpersonation = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " WHERE IMP_ID =  :id  ;";
    private static final String deleteByScriptExecutionRequest = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " WHERE SCRIPT_EXEC_REQ_ID =  :id  ;";
    private static final String delete = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestImpersonations").getName() + " WHERE ID =  :id  ;";

    @Override
    public Optional<ScriptExecutionRequestImpersonation> get(ScriptExecutionRequestImpersonationKey scriptExecutionRequestKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptExecutionRequestImpersonation> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ScriptExecutionRequestImpersonationKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting {0}.", scriptExecutionRequestKey.toString()));
        if (!exists(scriptExecutionRequestKey)) {
            throw new MetadataDoesNotExistException(scriptExecutionRequestKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ScriptExecutionRequestImpersonation scriptExecutionRequest) {
        log.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequest.getMetadataKey().getId())
                .addValue("scriptId", scriptExecutionRequest.getScriptExecutionRequestKey().getId())
                .addValue("impId", scriptExecutionRequest.getImpersonationKey().getName());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public void deleteByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequestImpersonation by {0}.", executionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteByScriptExecutionRequest,
                sqlParameterSource);
    }

    public void deleteByImpersonation(ImpersonationKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequestImpersonation by {0}.", executionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getName());
        namedParameterJdbcTemplate.update(
                deleteByImpersonation,
                sqlParameterSource);
    }

    @Override
    public void update(ScriptExecutionRequestImpersonation executionRequest) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("scriptId", executionRequest.getScriptExecutionRequestKey().getId())
                .addValue("impId", executionRequest.getImpersonationKey().getName())
                .addValue("id", executionRequest.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}
