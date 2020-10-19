package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
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


public class EnvironmentParameterConfiguration extends Configuration<EnvironmentParameter, EnvironmentParameterKey> {

    private static EnvironmentParameterConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static EnvironmentParameterConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EnvironmentParameterConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private EnvironmentParameterConfiguration() {
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

    private static final String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()  + " WHERE " + " ENV_NM = :name AND ENV_PAR_NM = :parameterName ;";
    private static final String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName() + " (ENV_NM, ENV_PAR_NM, ENV_PAR_VAL) VALUES (:env_nm,:env_par_nm,:env_par_val);";
    private static final String deleteByEnvironment = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Environments").getName()  + " WHERE ENV_NM= :name";
    private static final String queryEnvironmentParameter = "select ENV_NM, ENV_PAR_NM, ENV_PAR_VAL from  " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()  + " WHERE ENV_NM= :name ; ";
    private static final String queryAll ="select * from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("EnvironmentParameters").getName()  + " order by ENV_NM ASC";

    @Override
    public boolean exists(EnvironmentParameterKey environmentParameterKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentParameterKey.getEnvironmentKey().getName())
                .addValue("parameterName", environmentParameterKey.getParameterName());
        List<EnvironmentParameter> environments = namedParameterJdbcTemplate.query(
                queryEnvironmentParameter,
                sqlParameterSource,
                new EnvironmentParameterExtractor());
        return environments.size() >= 1;
    }

    @Override
    public Optional<EnvironmentParameter> get(EnvironmentParameterKey metadataKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadataKey.getEnvironmentKey().getName())
                .addValue("parameterName", metadataKey.getParameterName());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        queryEnvironmentParameter,
                        sqlParameterSource,
                        new EnvironmentParameterExtractor())));
    }

    public void deleteByEnvironment(EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey.getName());
        namedParameterJdbcTemplate.update(
                deleteByEnvironment,
                sqlParameterSource);
    }
    @Override
    public List<EnvironmentParameter> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new EnvironmentParameterExtractor());
    }

    @Override
    public void delete(EnvironmentParameterKey metadataKey) {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new MetadataDoesNotExistException(metadataKey);
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", metadataKey.getEnvironmentKey().getName())
                .addValue("parameterName", metadataKey.getParameterName());
        namedParameterJdbcTemplate.update(
                deleteStatement,
                sqlParameterSource);
    }


    @Override
    public void insert(EnvironmentParameter metadata) {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", metadata.getMetadataKey().toString()));
        if (exists(metadata.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(metadata.getMetadataKey());
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("env_nm", metadata.getMetadataKey().getEnvironmentKey().getName())
                .addValue("env_par_nm", metadata.getName())
                .addValue("env_par_val", metadata.getValue());
        namedParameterJdbcTemplate.update(
                insertQuery,
                sqlParameterSource);
    }

    public List<EnvironmentParameter> getByEnvironment(EnvironmentKey environmentKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", environmentKey.getName());
      return  namedParameterJdbcTemplate.query(queryEnvironmentParameter,sqlParameterSource, new EnvironmentParameterExtractor());

    }

}