package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.connection.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.connection.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    private ConnectionConfiguration() {
    }


    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
        ConnectionParameterConfiguration.getInstance().init(metadataRepository);
    }

    @Override
    public Optional<Connection> get(ConnectionKey metadataKey) {
        try {
            String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " +
                    getMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.GetStringForSQL(metadataKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", metadataKey.toString()));
            }
            cachedRowSet.next();
            List<ConnectionParameter> connectionParameters = getAllLinkedConnectionParameters(metadataKey);
            if (connectionParameters.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(new Connection(metadataKey, cachedRowSet.getString("CONN_TYP_NM"),
                        cachedRowSet.getString("CONN_DSC"), connectionParameters));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Connection> get(ConnectionKey metadataKey, String environment) {
        try {
            String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " +
                    getMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.GetStringForSQL(metadataKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", metadataKey.toString()));
            }
            cachedRowSet.next();
            List<ConnectionParameter> connectionParameters = getAllLinkedConnectionParameters(metadataKey);
            if (connectionParameters.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(new Connection(metadataKey, cachedRowSet.getString("CONN_TYP_NM"), cachedRowSet.getString("CONN_DSC"), connectionParameters));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Connection> get(String connectionName, String environmentName){
        return get(new ConnectionKey(connectionName, environmentName));
    }

    private List<ConnectionParameter> getAllLinkedConnectionParameters(ConnectionKey connectionKey) {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        try {
            String query = "select CONN_PAR_NM, CONN_PAR_VAL from " +
                    getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.GetStringForSQL(connectionKey.getName()) + " AND " +
                    " ENV_NM = " + SQLTools.GetStringForSQL(connectionKey.getEnvironmentKey().getName()) + ";";
            CachedRowSet crsConnectionParameters = getMetadataRepository().executeQuery(query, "reader");
            while (crsConnectionParameters.next()) {
                ConnectionParameter connectionParameter =
                        new ConnectionParameter(new ConnectionParameterKey(connectionKey.getName(), connectionKey.getEnvironmentKey().getName(),
                                crsConnectionParameters.getString("CONN_PAR_NM")), crsConnectionParameters.getString("CONN_PAR_VAL"));
                connectionParameters.add(connectionParameter);
            }
            crsConnectionParameters.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connectionParameters;
    }

    private List<ConnectionParameter> getAllLinkedConnectionParametersByName(String connectionName) {
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        try {
            String query = "select CONN_PAR_NM, ENV_NM, CONN_PAR_VAL from " +
                    getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE " +
                    " CONN_NM  = " + SQLTools.GetStringForSQL(connectionName) + ";";
            CachedRowSet crsConnectionParameters = getMetadataRepository().executeQuery(query, "reader");
            while (crsConnectionParameters.next()) {
                ConnectionParameter connectionParameter =
                        new ConnectionParameter(connectionName, crsConnectionParameters.getString("ENV_NM"),
                                crsConnectionParameters.getString("CONN_PAR_NM"), crsConnectionParameters.getString("CONN_PAR_VAL"));
                connectionParameters.add(connectionParameter);
            }
            crsConnectionParameters.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connectionParameters;
    }

    @Override
    public List<Connection> getAll() {
        List<Connection> connections = new ArrayList<>();
        String query = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " order by CONN_NM ASC";
        CachedRowSet crs = getMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                ConnectionKey connectionKey = new ConnectionKey(crs.getString("CONN_NM"), "");
                List<ConnectionParameter> connectionParameters =
                        getAllLinkedConnectionParametersByName(crs.getString("CONN_NM"));
                connections.add(new Connection(
                        connectionKey,
                        crs.getString("CONN_TYP_NM"),
                        crs.getString("CONN_DSC"),
                        connectionParameters));
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connections;
    }

    @Override
    public void delete(ConnectionKey metadataKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting Connection {0}.", metadataKey.toString()));
        if (!exists(metadataKey)) {
            throw new ConnectionDoesNotExistException(MessageFormat.format(
                    "Connection {0} does not exists", metadataKey.toString()));
        }
        List<String> deleteStatements = getDeleteQuery(metadataKey.getName(), metadataKey.getEnvironmentKey().getName());
        getMetadataRepository().executeBatch(deleteStatements);
    }

    private List<String> getDeleteQuery(String name, String environment) {
        try {
            List<String> queries = new ArrayList<>();

            queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                    " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name)
                    + "AND ENV_NM = " + SQLTools.GetStringForSQL(environment) + ";");

            // If this was the last remaining connection with name CONN_NM, remove entirely from connections
            String countQuery = "SELECT COUNT(DISTINCT ENV_NM ) AS total_environments FROM "
                    + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " WHERE ENV_NM != " + SQLTools.GetStringForSQL(environment) + ";";
            CachedRowSet crs = getMetadataRepository().executeQuery(countQuery, "reader");

            if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
                queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                        " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name) + ";");
            }

            return queries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getDeleteQueryByName(String name) {

        List<String> queries = new ArrayList<>();

        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name) + ";");


        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name) + ";");

        return queries;
    }

    @Override
    public void insert(Connection connection) throws MetadataAlreadyExistsException {
        // frameworkInstance.getFrameworkLog().log(MessageFormat.format("Inserting connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        LOGGER.trace(MessageFormat.format("Inserting Connection {0}.", connection.getMetadataKey().toString()));
        if (exists(connection.getMetadataKey())) {
            throw new ConnectionAlreadyExistsException(MessageFormat.format(
                    "Connection {0} already exists", connection.getMetadataKey().toString()));
        }
        List<String> insertQuery = getInsertQuery(connection);
        getMetadataRepository().executeBatch(insertQuery);
    }

    public String insertStatement(Connection connection) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("Connections") +
                " (CONN_NM, CONN_TYP_NM, CONN_DSC) VALUES (" +
                SQLTools.GetStringForSQL(connection.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(connection.getType()) + "," +
                SQLTools.GetStringForSQL(connection.getDescription()) + ");";
    }

    private List<String> getInsertQuery(Connection connection) {
        List<String> queries = new ArrayList<>();
        if (!exists(connection.getMetadataKey())) {
            queries.add(insertStatement(connection));
        }
        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            queries.add(ConnectionParameterConfiguration.getInstance().insertStatement(connectionParameter));
        }

        return queries;
    }

    public List<Connection> getByName(String connectionName) {
        List<Connection> connections = new ArrayList<>();
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
        crsConnection = getMetadataRepository().executeQuery(queryConnection, "reader");

        try {
            crsConnection.next();
            if (crsConnection.size() == 0) {
                return connections;
            } else if (crsConnection.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for Connection {0}. Returning first implementation", connectionName));
            }

            // Get parameters
            String queryConnectionParameters = "select DISTINCT ENV_NM from "
                    + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
            CachedRowSet crsConnectionEnvironments = getMetadataRepository().executeQuery(queryConnectionParameters, "reader");
            while (crsConnectionEnvironments.next()) {
                get(connectionName, crsConnectionEnvironments.getString("ENV_NM")).ifPresent(connections::add);
            }
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exeption=" + e.getMessage());
            LOGGER.info("exception.stacktrace=" + stackTrace.toString());
        }
        return connections;
    }

    public List<Connection> getByEnvironment(String environmentName) {
        List<Connection> connections = new ArrayList<>();

        String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
                + getMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet connectionsByEnvironment = getMetadataRepository().executeQuery(connectionsByEnvironmentQuery, "reader");
        try {
            while (connectionsByEnvironment.next()) {
                get(connectionsByEnvironment.getString("CONN_NM"), environmentName).ifPresent(connections::add);
            }
            connectionsByEnvironment.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connections;
    }

    public void deleteAll() {
        List<String> query = getDeleteAllStatement();
        getMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") + ";");
        return queries;
    }

    public void deleteByName(String connectionName) throws ConnectionDoesNotExistException {
        // TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting connection {0}.", connectionName), Level.TRACE);
        if (!exists(connectionName)) {
            throw new ConnectionDoesNotExistException(
                    MessageFormat.format("Connection {0} is not present in the repository so cannot be updated",
                            connectionName));

        }
        List<String> deleteQuery = getDeleteByNameQuery(connectionName);
        getMetadataRepository().executeBatch(deleteQuery);
    }

    public List<String> getDeleteByNameQuery(String connectionName) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("Connections") +
                " WHERE CONN_NM = "
                + SQLTools.GetStringForSQL(connectionName) + ";");
        queries.add("DELETE FROM " + getMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = "
                + SQLTools.GetStringForSQL(connectionName) + ";");
        return queries;
    }

    public boolean exists(String connectionName) {
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + getMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
        crsConnection = getMetadataRepository().executeQuery(queryConnection, "reader");
        return crsConnection.size() > 0;
    }
}