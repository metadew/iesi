package io.metadew.iesi.metadata.configuration.execution.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.service.metadata.MetadataFieldService;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ScriptExecutionRequestParameterConfiguration extends Configuration<ScriptExecutionRequestParameter, ScriptExecutionRequestParameterKey> {

    private static ScriptExecutionRequestParameterConfiguration INSTANCE;

    public synchronized static ScriptExecutionRequestParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutionRequestParameterConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptExecutionRequestParameterConfiguration() {
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
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() +
            " (ID, SCRIPT_EXEC_REQ_ID, NAME, VALUE) VALUES (:id, :scriptId, :name, :value)";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() +
            " SET NAME= :name, VALUE= :value WHERE ID = :id ; ";
    private static final String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " WHERE SCRIPT_EXEC_REQ_ID =  :id  ;";
    private static final String delete = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptExecutionRequestParameters").getName() + " WHERE ID =  :id  ;";


    @Override
    public Optional<ScriptExecutionRequestParameter> get(ScriptExecutionRequestParameterKey scriptExecutionRequestKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptExecutionRequestParameter> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ScriptExecutionRequestParameterKey scriptExecutionRequestKey) {
        log.trace(MessageFormat.format("Deleting {0}.", scriptExecutionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }


    @Override
    public void insert(ScriptExecutionRequestParameter scriptExecutionRequest) {
        log.trace(MessageFormat.format("Inserting ScriptExecutionRequest {0}.", scriptExecutionRequest.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptExecutionRequest.getMetadataKey().getId())
                .addValue("scriptId", scriptExecutionRequest.getScriptExecutionRequestKey().getId())
                .addValue("name", scriptExecutionRequest.getName())
                .addValue("value", MetadataFieldService.getInstance()
                        .truncateAccordingToConfiguration("ScriptExecutionRequestParameters", "VALUE", scriptExecutionRequest.getValue()));
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public void deleteByScriptExecutionRequest(ScriptExecutionRequestKey executionRequestKey) {
        log.trace(MessageFormat.format("Deleting ScriptExecutionRequest by ExecutionKey {0}.", executionRequestKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void update(ScriptExecutionRequestParameter executionRequest) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", executionRequest.getName())
                .addValue("value", MetadataFieldService.getInstance()
                        .truncateAccordingToConfiguration("ScriptExecutionRequestParameters", "VALUE", executionRequest.getValue()))
                .addValue("id", executionRequest.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);

    }
}
