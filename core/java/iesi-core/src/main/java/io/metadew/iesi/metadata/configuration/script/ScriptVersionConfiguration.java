package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class ScriptVersionConfiguration extends Configuration<ScriptVersion, ScriptVersionKey> {

    private static ScriptVersionConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ScriptVersionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ScriptVersionConfiguration() {
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

    private static final String getLatestVersionNumber = "select max(SCRIPT_VRS_NB) as \"MAX_VRS_NB\" from   "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " where SCRIPT_ID = :id  ; ";
    private static final String insert = "INSERT INTO  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " (SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (:id, :version, :description); ";
    private static final String deleteStatement = "DELETE FROM  "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ScriptVersions").getName() +
            " WHERE SCRIPT_ID = :id AND SCRIPT_VRS_NB = :version ; ";

    @Override
    public Optional<ScriptVersion> get(ScriptVersionKey scriptVersionKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ScriptVersion> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ScriptVersionKey scriptVersionKey) {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersion {0}.", scriptVersionKey.toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptVersionKey.getScriptKey().getScriptId())
                .addValue("version", scriptVersionKey.getScriptKey().getScriptVersion());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    public Optional<Script> getLatestVersionNumber(String scriptId) {
        LOGGER.trace(MessageFormat.format("Fetching latest version for script {0}.", scriptId));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptId);
        Long scriptVersions = namedParameterJdbcTemplate.query(
                getLatestVersionNumber,
                sqlParameterSource,
                new ScriptVersionExtractorTotal());
        System.out.println(scriptVersions);
        if (scriptVersions == 0) {
            return Optional.empty();
        } else {
            return ScriptConfiguration.getInstance().get(new ScriptKey(scriptId, scriptVersions));
        }
    }

    @Override
    public void insert(ScriptVersion scriptVersion) {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersion {0}-{1}.", scriptVersion.getScriptId(), scriptVersion.getNumber()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", scriptVersion.getMetadataKey().getScriptKey().getScriptId())
                .addValue("version", scriptVersion.getMetadataKey().getScriptKey().getScriptVersion())
                .addValue("description", scriptVersion.getDescription());
        namedParameterJdbcTemplate.update(
                insert,
                sqlParameterSource);
    }
}