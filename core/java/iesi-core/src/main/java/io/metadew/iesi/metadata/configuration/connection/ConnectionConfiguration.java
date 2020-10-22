
package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConnectionConfiguration extends Configuration<Connection, ConnectionKey> {

    private static ConnectionConfiguration INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized static ConnectionConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionConfiguration();
        }
        return INSTANCE;
    }

    private static final String queryAll = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " order by CONN_NM ASC;";
    private static final String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " WHERE CONN_NM  = :name;";
    private static final String countQuery = "SELECT COUNT(DISTINCT ENV_NM) AS total_environments FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName() +
            " WHERE ENV_NM != :name AND CONN_NM  = :connectionName;";
    private static final String getDeleteQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " WHERE CONN_NM = :name;";
    private static final String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " (CONN_NM, CONN_TYP_NM, CONN_DSC) VALUES (:name, :type,:description);";
    private static final String getAll = "SELECT * FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " WHERE CONN_NM = :name;";
    private static final String queryConnectionParameters = "select DISTINCT ENV_NM from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName() +
            " WHERE CONN_NM  = :name;";
    private static final String deleteAllConnections = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() + " ;";
    private static final String deleteAllConnectionParameters = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName() + " ;";
    private static final String getDeleteByNameQueryConnections = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName()
            + " WHERE CONN_NM = :connectionName ;";
    private static final String getDeleteByNameQueryConnectionParameters = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName()
            + " WHERE CONN_NM = :connectionName ;";
    private static final String update = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " SET CONN_TYP_NM = :type , CONN_DSC = :description WHERE CONN_NM = :name ;";
    private final static String exists = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            " where CONN_NM = :name";
    private final static String getByEnvironment = "select distinct CONN_NM from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ConnectionParameters").getName() +
            " where ENV_NM = :environment";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ConnectionConfiguration() {
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
        ConnectionParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Connection> get(ConnectionKey connectionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", connectionKey.getName());
        Optional<Connection> connections = Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ConnectionConfigurationExtractor())));
        List<ConnectionParameter> connectionParameters = ConnectionParameterConfiguration.getInstance().getByConnection(connectionKey);
        if (connectionParameters.isEmpty()) {
            return Optional.empty();
        } else {
            Connection connection = new Connection(connectionKey);
            connection.setType(connections.get().getType());
            connection.setDescription(connections.get().getDescription());
            connection.setParameters(connectionParameters);
            return Optional.of(connection);
        }
    }

    @Override
    public List<Connection> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new ConnectionConfigurationExtractor());
    }

    @Override
    public void delete(ConnectionKey connectionKey) {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", connectionKey.toString()));
        if (!exists(connectionKey)) {
            throw new MetadataDoesNotExistException(connectionKey);
        }
        ConnectionParameterConfiguration.getInstance().deleteByConnection(connectionKey);
        getDeleteQuery(connectionKey);
    }

    private void getDeleteQuery(ConnectionKey connectionKey) {
        SqlParameterSource sqlParameterSourceTotal = new MapSqlParameterSource()
                .addValue("name", connectionKey.getEnvironmentKey().getName())
                .addValue("connectionName", connectionKey.getName());
        int connectionParameters = namedParameterJdbcTemplate.query(
                countQuery,
                sqlParameterSourceTotal, new ConnectionConfigurationExtractorTotal());
        if (connectionParameters == 0) {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("name", connectionKey.getName());
            namedParameterJdbcTemplate.update(
                    getDeleteQuery,
                    sqlParameterSource);
        } else {
            Optional.empty();
        }
    }

    @Override
    public void insert(Connection connection) {
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", connection.getMetadataKey().toString()));
        if (exists(connection.getMetadataKey())) {
            throw new MetadataAlreadyExistsException(connection);
        }
        connection.getParameters()
                .forEach(connectionParameter -> ConnectionParameterConfiguration.getInstance().insert(connectionParameter));
        insertStatement(connection);
    }

    private void insertStatement(Connection connection) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", connection.getMetadataKey().getName());
        List<Connection> connections = namedParameterJdbcTemplate.query(getAll, sqlParameterSource, new ConnectionConfigurationExtractor());
        if (connections.size() == 0) {
            SqlParameterSource sqlParameterSource2 = new MapSqlParameterSource()
                    .addValue("name", connection.getMetadataKey().getName())
                    .addValue("type", connection.getType())
                    .addValue("description", connection.getDescription());
            namedParameterJdbcTemplate.update(
                    insertStatement,
                    sqlParameterSource2);
        } else {
            Optional.empty();
        }
    }

    public List<Connection> getByName(String connectionName) {
        List<Connection> connectionList = namedParameterJdbcTemplate.query(query,
                new ConnectionConfigurationExtractor());
        if (connectionList.size() == 0) {
            return connectionList;
        } else if (connectionList.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionName));
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", connectionName);
        List<ConnectionParameter> connectionParameters = namedParameterJdbcTemplate.query(
                queryConnectionParameters,
                sqlParameterSource, new ConnectionParameterExtractor());
        get(new ConnectionKey(connectionName, (EnvironmentKey) connectionParameters))
                .ifPresent(connectionList::add);
        return connectionList;
    }

    public List<Connection> getByEnvironment(String environmentName) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("environment", environmentName);
        List<Connection> connections = new ArrayList<>();
        List<ConnectionParameter> connectionParameters = namedParameterJdbcTemplate.query(
                getByEnvironment,
                sqlParameterSource, new ConnectionParameterExtractor());
        get(new ConnectionKey(connectionParameters.get(0).getName(), environmentName))
                .ifPresent(connections::add);
        return connections;
    }

    public void deleteAll() {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        namedParameterJdbcTemplate.update(
                deleteAllConnections,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteAllConnectionParameters,
                sqlParameterSource);
    }

    public void deleteByName(String connectionName) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("connectionName", connectionName);
        namedParameterJdbcTemplate.update(
                getDeleteByNameQueryConnections,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                getDeleteByNameQueryConnectionParameters,
                sqlParameterSource);
    }

    public boolean exists(String connectionName) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", connectionName);
        List<Connection> connections = namedParameterJdbcTemplate.query(
                exists,
                sqlParameterSource,
                new ConnectionConfigurationExtractor());
        return connections.size() > 0;
    }

    public void update(Connection connection) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("type", connection.getType())
                .addValue("description", connection.getDescription())
                .addValue("name", connection.getName());
        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            ConnectionParameterConfiguration.getInstance().update(connectionParameter);
        }
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}