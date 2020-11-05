package io.metadew.iesi.metadata.configuration.execution;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ExecutionRequestLabelConfiguration extends Configuration<ExecutionRequestLabel, ExecutionRequestLabelKey> {

    private static ExecutionRequestLabelConfiguration INSTANCE;

    public synchronized static ExecutionRequestLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExecutionRequestLabelConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ExecutionRequestLabelConfiguration() {
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

    private static final String delete = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() + " WHERE ID = :id  ;";
    private final static String insert = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequestLabels").getName() +
            " (ID, REQUEST_ID, NAME, VALUE) VALUES (:id, :requestId, :name, :value)";
    private final static String update = "UPDATE "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() +
            " SET REQUEST_ID = :id, NAME = :name, VALUE = :value  WHERE REQUEST_ID = :id ; ";
    private static final String deleteByExecutionRequest = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AuthenticatedExecutionRequests").getName() + " WHERE REQUEST_ID = :id  ;";

    @Override
    public Optional<ExecutionRequestLabel> get(ExecutionRequestLabelKey executionRequestLabelKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExecutionRequestLabel> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ExecutionRequestLabelKey executionRequestLabelKey) {
        log.trace(MessageFormat.format("Deleting {0}.", executionRequestLabelKey.toString()));
        if (!exists(executionRequestLabelKey)) {
            throw new MetadataDoesNotExistException(executionRequestLabelKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestLabelKey.getId());
        namedParameterJdbcTemplate.update(
                delete,
                sqlParameterSource);
    }

    @Override
    public void insert(ExecutionRequestLabel executionRequestLabel) {
        log.trace(MessageFormat.format("Inserting {0}.", executionRequestLabel.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestLabel.getMetadataKey().getId())
                .addValue("requestId", executionRequestLabel.getMetadataKey().getId())
                .addValue("name", executionRequestLabel.getMetadataKey().getId())
                .addValue("value", executionRequestLabel.getMetadataKey().getId());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);

    }

    public void deleteByExecutionRequest(ExecutionRequestKey executionRequestKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestKey.getId());
        namedParameterJdbcTemplate.update(
                deleteByExecutionRequest,
                sqlParameterSource);
    }

    public void update(ExecutionRequestLabel executionRequestLabel) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", executionRequestLabel.getExecutionRequestKey().getId())
                .addValue("name", executionRequestLabel.getName())
                .addValue("value", executionRequestLabel.getValue());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}