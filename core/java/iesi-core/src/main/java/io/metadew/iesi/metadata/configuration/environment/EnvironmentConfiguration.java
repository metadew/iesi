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

    private static final String queryAll = "select Environments.ENV_NM as Environments_ENV_NM  " +
            ", Environments.ENV_DSC as Environments_ENV_DSC , EnvironmentParameters.ENV_NM as EnvironmentParameters_ENV_NM, EnvironmentParameters.ENV_PAR_NM as EnvironmentParameters_ENV_PAR_NM, EnvironmentParameters.ENV_PAR_VAL as EnvironmentParameters_ENV_PAR_VAL from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName()
            + " Environments  LEFT OUTER JOIN "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()
            + " EnvironmentParameters on Environments.ENV_NM=EnvironmentParameters.ENV_NM order by Environments.ENV_NM ASC";
    private static final String queryEnvironment = "select Environments.ENV_NM as Environments_ENV_NM, Environments.ENV_DSC as Environments_ENV_DSC , EnvironmentParameters.ENV_NM as EnvironmentParameters_ENV_NM, EnvironmentParameters.ENV_PAR_NM as EnvironmentParameters_ENV_PAR_NM, EnvironmentParameters.ENV_PAR_VAL as EnvironmentParameters_ENV_PAR_VAL from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " Environments  "
            + " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()
            + " EnvironmentParameters on Environments.ENV_NM=EnvironmentParameters.ENV_NM  where Environments.ENV_NM = :name";
    private static final String deleteStatement = "DELETE FROM "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " WHERE ENV_NM = :name";
    private static final String insertStatement = "INSERT INTO "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName()
            + " (ENV_NM, ENV_DSC) VALUES (:name, :description );";
    private static final String deleteAllEnvironments = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + ";";
    private static final String deleteAllEnvironmentParameters = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName() + ";";
    private static final String exists = "select Environments.ENV_NM as Environments_ENV_NM, Environments.ENV_DSC as Environments_ENV_DSC , EnvironmentParameters.ENV_NM as EnvironmentParameters_ENV_NM, EnvironmentParameters.ENV_PAR_NM as EnvironmentParameters_ENV_PAR_NM, EnvironmentParameters.ENV_PAR_VAL as EnvironmentParameters_ENV_PAR_VAL from "
            + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " Environments  "
            + " LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()
            + " EnvironmentParameters on Environments.ENV_NM=EnvironmentParameters.ENV_NM  where Environments.ENV_NM = :name";
    private static final String update = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName() + " " +
            "SET ENV_DSC= :description WHERE ENV_NM= :name;";

    @Override
    public Optional<Environment> get(EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey.getName());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryEnvironment,
                        sqlParameterSource,
                        new EnvironmentConfigurationExtractor())));
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
                .addValue("name", environmentKey.getName());
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
        return environments.size() >= 1;
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

    public void update(Environment environment) {
        LOGGER.trace(MessageFormat.format("Inserting Environment {0}.", environment.getMetadataKey().toString()));
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environment.getName())
                .addValue("description", environment.getDescription());
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
        EnvironmentParameterConfiguration.getInstance().deleteByEnvironment(environment.getMetadataKey());
        for (EnvironmentParameter environmentParameter : environment.getParameters()) {
            EnvironmentParameterConfiguration.getInstance().insert(environmentParameter);
        }
    }
}