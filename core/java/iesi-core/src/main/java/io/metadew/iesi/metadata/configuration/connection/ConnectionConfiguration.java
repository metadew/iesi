package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.tools.SQLTools;
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

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
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
            " WHERE ENV_NM != :name AND CONN_NM = :connectionName;";
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
            "SET CONN_TYP_NM = :type , CONN_DSC = :description WHERE CONN_NM = :name ;";
    private final static String exists = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Connections").getName() +
            "where CONN_NM = :name";

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
//        ConnectionParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Connection> get(ConnectionKey connectionKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", connectionKey.getName());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(namedParameterJdbcTemplate.query(
                        query,
                        sqlParameterSource,
                        new ConnectionConfigurationExtractor())));

//        try {connectionKey.getName()
//            String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " +
//                    getMetadataRepository().getTableNameByLabel("Connections") +
//                    " WHERE " +
//                    " CONN_NM  = " + SQLTools.GetStringForSQL(connectionKey.getName()) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
//            if (cachedRowSet.size() == 0) {
//                return Optional.empty();
//            } else if (cachedRowSet.size() > 1) {
//                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionKey.toString()));
//            }
//            cachedRowSet.next();
//            List<ConnectionParameter> connectionParameters = ConnectionParameterConfiguration.getInstance().getByConnection(connectionKey);
//            if (connectionParameters.isEmpty()) {
//                return Optional.empty();
//            } else {
//                return Optional.of(new Connection(connectionKey, cachedRowSet.getString("CONN_TYP_NM"),
//                        cachedRowSet.getString("CONN_DSC"), connectionParameters));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public List<Connection> getAll() {
        return namedParameterJdbcTemplate.query(queryAll, new ConnectionConfigurationExtractor());
//        List<Connection> connections = new ArrayList<>();
//        String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
//                + " order by CONN_NM ASC";
//        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
//        try {
//            while (crs.next()) {
//                String queryConnectionParameters = "select DISTINCT ENV_NM from "
//                        + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
//                        + " where CONN_NM = " + SQLTools.GetStringForSQL(crs.getString("CONN_NM")) + ";";
//                CachedRowSet environmentCachedRowSet = getMetadataRepository().executeQuery(queryConnectionParameters, "reader");
//                while (environmentCachedRowSet.next()) {
//                    ConnectionKey connectionKey = new ConnectionKey(crs.getString("CONN_NM"), environmentCachedRowSet.getString("ENV_NM"));
//                    connections.add(new Connection(
//                            connectionKey,
//                            crs.getString("CONN_TYP_NM"),
//                            crs.getString("CONN_DSC"),
//                            ConnectionParameterConfiguration.getInstance().getByConnection(connectionKey)));
//                }
//            }
//            crs.close();
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.warn("exception=" + e.getMessage());
//            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
//        }
//        return connections;
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
        try {
            // If this was the last remaining connection with name CONN_NM, remove entirely from connections
//            String countQuery = "SELECT COUNT(DISTINCT ENV_NM) AS total_environments FROM "
//                    + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
//                    + " WHERE ENV_NM != " + SQLTools.GetStringForSQL(connectionKey.getEnvironmentKey().getName()) +
//                    " AND CONN_NM = " + SQLTools.GetStringForSQL(connectionKey.getName()) + ";";
//            CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

            List<ConnectionParameter> connectionParameters = namedParameterJdbcTemplate.query(countQuery,
                    new ConnectionParameterExtractor());

            if (connectionParameters.size() == 0) {
                SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                        .addValue("name", connectionKey.getName());
                namedParameterJdbcTemplate.update(
                        getDeleteQuery,
                        sqlParameterSource);
//                return Optional.of("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
//                        " WHERE CONN_NM = " + SQLTools.GetStringForSQL(connectionKey.getName()) + ";");
            } else {
                Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        List<Connection> connections = new ArrayList<>();

        String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
                + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet connectionsByEnvironment = getMetadataRepository().executeQuery(connectionsByEnvironmentQuery, "reader");
        try {
            while (connectionsByEnvironment.next()) {
                get(new ConnectionKey(connectionsByEnvironment.getString("CONN_NM"), environmentName))
                        .ifPresent(connections::add);
            }
            connectionsByEnvironment.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        return connections.size() >= 1;
    }

    public void update(Connection connection) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("type", connection.getType())
                .addValue("description", connection.getDescription())
                .addValue("name", connection.getMetadataKey().getName());
        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            ConnectionParameterConfiguration.getInstance().update(connectionParameter);
        }
        namedParameterJdbcTemplate.update(
                update,
                sqlParameterSource);
    }
}