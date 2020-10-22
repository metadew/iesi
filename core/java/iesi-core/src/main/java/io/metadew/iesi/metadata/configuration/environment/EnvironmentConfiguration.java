package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
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

public class EnvironmentConfiguration extends Configuration<Environment, EnvironmentKey> {

    private static EnvironmentConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static EnvironmentConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private EnvironmentConfiguration() {
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
        EnvironmentParameterConfiguration.getInstance().init(metadataRepository);
    }

    private static final String queryAll = "select ENV_NM, ENV_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " order by ENV_NM ASC";
    private static final String queryEnvironment = "select ENV_NM, ENV_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " where ENV_NM = :name";
    private static final String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " WHERE ENV_NM = :name";
    private static final String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " (ENV_NM, ENV_DSC) VALUES (:name, :description );";
    private static final String deleteAllEnvironments = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + ";";
    private static final String deleteAllEnvironmentParameters = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName() + ";";
    private static final String exists = "select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " where ENV_NM = :name";


    @Override
    public Optional<Environment> get(EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey.getName());
        Optional<Environment> environment = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryEnvironment,
                        sqlParameterSource,
                        new EnvironmentConfigurationExtractor())));
        if (!environment.isPresent()) {
            return Optional.empty();
        }
        List<EnvironmentParameter> environmentParameters = EnvironmentParameterConfiguration.getInstance().getByEnvironment(environmentKey);
        Environment environment1 = new Environment(environmentKey);
        environment1.setDescription(environment.get().getDescription());
        environment1.setParameters(environmentParameters);
        return Optional.of(environment1);
    }

    @Override
    public List<Environment> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new EnvironmentConfigurationExtractor());
    }

    @Override
    public void delete(EnvironmentKey environmentKey) {
        LOGGER.trace(MessageFormat.format("Deleting Environment {0}.", environmentKey.toString()));
        if (!exists(environmentKey)) {
            throw new MetadataDoesNotExistException(environmentKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey);
        EnvironmentParameterConfiguration.getInstance().deleteByEnvironment(environmentKey);
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }

    @Override
    public void insert(Environment environment) {
        LOGGER.trace(MessageFormat.format("Inserting Environment {0}.", environment.getMetadataKey().toString()));
        if (exists(environment.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(environment.getMetadataKey());
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environment.getName())
                .addValue("description", environment.getDescription());
        for (EnvironmentParameter environmentParameter : environment.getParameters()) {
            EnvironmentParameterConfiguration.getInstance().insert(environmentParameter);
        }
        namedParameterJdbcTemplate.update(
                insertStatement,
                sqlParameterSource);
    }

    public boolean exists(EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey.getName());
        List<Environment> environments = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new EnvironmentConfigurationExtractor());
        if (environments.size() == 0) {
            return false;
        } else if (environments.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", environmentKey.toString()));
        }
        return true;
    }

    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAllEnvironments,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteAllEnvironmentParameters,
                sqlParameterSource);
    }
}