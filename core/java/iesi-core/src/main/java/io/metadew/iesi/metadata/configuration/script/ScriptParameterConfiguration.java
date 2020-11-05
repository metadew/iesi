package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ScriptParameterConfiguration extends Configuration<ScriptParameter, ScriptParameterKey> {

    private static ScriptParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptParameterConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptParameterConfiguration() {
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

    private static final String deleteByScript = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version ; ";
    private static final String insert = "INSERT INTO  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES  (:id, :version, :parameter, :value); ";
    private static final String deleteStatement = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version AND SCRIPT_PAR_NM = :parameter ; ";

    @Override
    public Optional<ScriptParameter> get(ScriptParameterKey scriptParameterKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptParameter> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ScriptParameterKey scriptParameterKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptParameter {0}.", scriptParameterKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptParameterKey.getScriptKey().getScriptId())
                .addValue("version", scriptParameterKey.getScriptKey().getScriptVersion())
                .addValue("parameter", scriptParameterKey.getParameterName());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }


    @Override
    public void insert(ScriptParameter scriptParameter) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptParameter {0}.", scriptParameter.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptParameter.getMetadataKey().getScriptKey().getScriptId())
                .addValue("version", scriptParameter.getMetadataKey().getScriptKey().getScriptVersion())
                .addValue("parameter", scriptParameter.getMetadataKey().getParameterName())
                .addValue("value", scriptParameter.getValue());
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