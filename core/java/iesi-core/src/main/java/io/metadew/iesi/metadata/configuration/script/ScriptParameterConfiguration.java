package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
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

    private static final String queryScriptParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " where SCRIPT_ID = :id and SCRIPT_VRS_NB = :version and SCRIPT_PAR_NM = :parameter ; ";
    private static final String getAll = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " order by SCRIPT_ID ASC ; ";
    private static final String getByScript = "select * from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " WHERE SCRIPT_ID = :id  and SCRIPT_VRS_NB = :version;";
    private static final String deleteByScript = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version ; ";
    private static final String insert = "INSERT INTO  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL) VALUES  (:id, :version, :parameter, :value); ";
    private static final String deleteStatement = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version AND SCRIPT_PAR_NM = :parameter ; ";
    private static final String exists = "select SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_PAR_NM, SCRIPT_PAR_VAL from   "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptParameters").getName() +
            " where SCRIPT_ID = :id and SCRIPT_VRS_NB = :version and SCRIPT_PAR_NM = :parameter ; ";

    @Override
    public Optional<ScriptParameter> get(ScriptParameterKey scriptParameterKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptParameterKey.getScriptKey().getScriptId())
                .addValue("version", scriptParameterKey.getScriptKey().getScriptVersion())
                .addValue("parameter", scriptParameterKey.getParameterName());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryScriptParameter,
                        sqlParameterSource,
                        new ScriptParameterExtractor())));
    }

    @Override
    public List<ScriptParameter> getAll() {
        return namedParameterJdbcTemplate.query(
                getAll,
                new ScriptParameterExtractor());
    }

    @Override
    public void delete(ScriptParameterKey scriptParameterKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptParameter {0}.", scriptParameterKey.toString()));
        if (!exists(scriptParameterKey)) {
            throw new MetadataDoesNotExistException(scriptParameterKey);
        }
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
        if (exists(scriptParameter)) {
            throw new MetadataAlreadyExistsException(scriptParameter);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptParameter.getMetadataKey().getScriptKey().getScriptId())
                .addValue("version", scriptParameter.getMetadataKey().getScriptKey().getScriptVersion())
                .addValue("parameter", scriptParameter.getMetadataKey().getParameterName())
                .addValue("value", scriptParameter.getValue());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }

    public boolean exists(ScriptParameterKey scriptParameterKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptParameterKey.getScriptKey().getScriptId())
                .addValue("version", scriptParameterKey.getScriptKey().getScriptVersion())
                .addValue("parameter", scriptParameterKey.getParameterName());
        List<ScriptParameter> scriptParameters = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new ScriptParameterExtractor());
        return scriptParameters.size() >= 1;
    }

    public void deleteByScript(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("version", scriptKey.getScriptVersion());

        namedParameterJdbcTemplate.update(
                deleteByScript,
                sqlParameterSource);
    }

    public List<ScriptParameter> getByScript(ScriptKey scriptKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptKey.getScriptId())
                .addValue("version", scriptKey.getScriptVersion());
        return namedParameterJdbcTemplate.query(
                getByScript,
                sqlParameterSource,
                new ScriptParameterExtractor());
    }
}