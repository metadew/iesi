package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ScriptLabelConfiguration extends Configuration<ScriptLabel, ScriptLabelKey> {

    private static ScriptLabelConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptLabelConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptLabelConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptLabelConfiguration() {
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

    private static final String deleteByScript = "DELETE FROM   "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " where SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version ; ";
    private static final String insert = "INSERT INTO  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " (ID, SCRIPT_ID, SCRIPT_VRS_NB, NAME, VALUE) VALUES (:id, :scriptId, :version, :name, :value); ";
    private static final String deleteStatement = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptLabels").getName() +
            " WHERE ID = :id ; ";

    @Override
    public Optional<ScriptLabel> get(ScriptLabelKey scriptLabelKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptLabel> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ScriptLabelKey scriptLabelKey) {
        LOGGER.trace(MessageFormat.format("Deleting {0}.", scriptLabelKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptLabelKey.getId());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void insert(ScriptLabel scriptLabel) {
        LOGGER.trace(MessageFormat.format("Inserting {0}.", scriptLabel.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptLabel.getMetadataKey().getId())
                .addValue("scriptId", scriptLabel.getScriptKey().getScriptId())
                .addValue("version", scriptLabel.getScriptKey().getScriptVersion())
                .addValue("name", scriptLabel.getName())
                .addValue("value", scriptLabel.getValue());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }


    public void deleteByScript(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("version", scriptKey.getScriptVersion());
        namedParameterJdbcTemplate.update(
                deleteByScript,
                sqlParameterSource);
    }
}