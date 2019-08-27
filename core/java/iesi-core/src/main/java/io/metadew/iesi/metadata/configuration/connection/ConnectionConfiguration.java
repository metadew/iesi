package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteJDBCLoader;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConnectionConfiguration extends MetadataConfiguration {

    private final ConnectionParameterConfiguration connectionParameterConfiguration;
    private static final Logger LOGGER = LogManager.getLogger();

    public ConnectionConfiguration() {
        connectionParameterConfiguration = new ConnectionParameterConfiguration();
    }

    @Override
    public List<Connection> getAllObjects() {
        return this.getAll();
    }

    public List<Connection> getAll() {
        List<Connection> connections = new ArrayList<>();
        String query = "select * from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
                + " order by CONN_NM ASC";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String connectionName = crs.getString("CONN_NM");

                String queryEnvironment = "select distinct ENV_NM from "
                        + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                        + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + " order by ENV_NM ASC;";
                CachedRowSet crsEnvironment = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
                while (crsEnvironment.next()) {
                    List<ConnectionParameter> connectionParameters = new ArrayList<>();
                    String environmentName = crsEnvironment.getString("ENV_NM");

                    String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
                            + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                            + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + " and ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
                    CachedRowSet crsConnectionParameters = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnectionParameters, "reader");

                    while (crsConnectionParameters.next()) {
                        connectionParameters.add(connectionParameterConfiguration.getConnectionParameter(
                                connectionName,
                                environmentName,
                                crsConnectionParameters.getString("CONN_PAR_NM")));
                    }
                    connections.add(new Connection(
                            connectionName,
                            crs.getString("CONN_TYP_NM"),
                            crs.getString("CONN_DSC"),
                            environmentName,
                            connectionParameters));
                    crsConnectionParameters.close();
                }
                crsEnvironment.close();
            }
            crs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connections;
    }

    public List<Connection> getByName(String connectionName) {
        return getAll().stream()
                .filter(connection -> connection.getName().equals(connectionName))
                .collect(Collectors.toList());
    }

    public List<Connection> getByEnvironment(String environmentName) {
        List<Connection> connections = new ArrayList<>();
        String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet connectionsByEnvironment = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(connectionsByEnvironmentQuery, "reader");
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

    public Optional<Connection> get(String connectionName, String environmentName) {
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
        crsConnection = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnection, "reader");
        try {
            if (crsConnection.size() == 0) {
                return Optional.empty();
            } else if (crsConnection.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for connection {0}. Returning first implementation", connectionName));
            }
            crsConnection.next();

            // Get parameters
            String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
                    + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                    + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + " and ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
            CachedRowSet crsConnectionParameters = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnectionParameters, "reader");
            List<ConnectionParameter> connectionParameters = new ArrayList<>();
            boolean containsParameters = false;
            // TODO: if no parameters are found for environment_name, it does not exist?!
            while (crsConnectionParameters.next()) {
                containsParameters = true;
                connectionParameters.add(connectionParameterConfiguration.getConnectionParameter(connectionName, environmentName, crsConnectionParameters.getString("CONN_PAR_NM")));
            }
            crsConnectionParameters.close();

            if (containsParameters) {
                Connection connection = new Connection(connectionName,
                        crsConnection.getString("CONN_TYP_NM"),
                        crsConnection.getString("CONN_DSC"),
                        environmentName,
                        connectionParameters);
                crsConnection.close();
                return Optional.of(connection);
            } else {
                crsConnection.close();
                return Optional.empty();
            }
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }
    }

    public boolean exists(Connection connection) throws SQLException {
        return exists(connection.getName(), connection.getEnvironment());
    }

    public boolean exists(String connectionName) {
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
        crsConnection = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnection, "reader");
        return crsConnection.size() > 0;
    }

    public boolean exists(String connectionName, String environmentName) throws SQLException {
        CachedRowSet crsConnection;
        String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + ";";
        crsConnection = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnection, "reader");
        if (crsConnection.size() == 0) {
            return false;
        } else if (crsConnection.size() > 1) {
            LOGGER.warn(MessageFormat.format("Found multiple implementations for connection {0}. Returning first implementation", connectionName));
        }
        crsConnection.next();

        // Get parameters
        String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where CONN_NM = " + SQLTools.GetStringForSQL(connectionName) + " and ENV_NM = " + SQLTools.GetStringForSQL(environmentName) + ";";
        CachedRowSet crsConnectionParameters = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnectionParameters, "reader");
        return crsConnectionParameters.size() > 0;
    }

    public void delete(String name, String environment) throws ConnectionDoesNotExistException, SQLException {
        // TODO fix logging
        //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        if (!exists(name, environment)) {
            throw new ConnectionDoesNotExistException(
                    MessageFormat.format("Connection {0}-{1} is not present in the repository so cannot be updated",
                            name, environment));

        }
        List<String> deleteQuery = getDeleteQuery(name, environment);
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(deleteQuery);


        delete(get(name, environment)
                .orElseThrow(() -> new ConnectionDoesNotExistException(
                        MessageFormat.format("Connection {0}-{1} is not present in the repository so cannot be updated",
                                name, environment))));
    }


    public void delete(Connection connection) throws ConnectionDoesNotExistException, SQLException {
        delete(connection.getName(), connection.getEnvironment());
    }

    public List<String> getDeleteQuery(String name, String environment) throws SQLException {
        List<String> queries = new ArrayList<>();

        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name)
                + "AND ENV_NM = " + SQLTools.GetStringForSQL(environment) + ";");

        // If this was the last remaining connection with name CONN_NM, remove entirely from connections
        String countQuery = "SELECT COUNT(DISTINCT ENV_NM ) AS total_environments FROM "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " WHERE ENV_NM != " + SQLTools.GetStringForSQL(environment) + ";";
        CachedRowSet crs = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(countQuery, "reader");

        if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
            queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") +
                    " WHERE CONN_NM = " + SQLTools.GetStringForSQL(name) + ";");
        }

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
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(deleteQuery);
    }

    public List<String> getDeleteByNameQuery(String connectionName) {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") +
                " WHERE CONN_NM = "
                + SQLTools.GetStringForSQL(connectionName) + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " WHERE CONN_NM = "
                + SQLTools.GetStringForSQL(connectionName) + ";");
        return queries;
    }

    public void deleteAll() {
        // TODO fix logging
        //frameworkExecution.getFrameworkLog().log("Deleting all connections", Level.TRACE);
        List<String> query = getDeleteAllStatement();
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(query);
    }

    private List<String> getDeleteAllStatement() {
        List<String> queries = new ArrayList<>();
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("Connections") + ";");
        queries.add("DELETE FROM " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") + ";");

        return queries;
    }

    public Connection insert(Connection connection) throws ConnectionAlreadyExistsException, SQLException {
        // frameworkInstance.getFrameworkLog().log(MessageFormat.format("Inserting connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        if (exists(connection)) {
            throw new ConnectionAlreadyExistsException(MessageFormat.format(
                    "Connection {0}-{1} already exists", connection.getName(), connection.getEnvironment()));
        }
        List<String> insertQuery = getInsertQuery(connection);
        MetadataControl.getInstance().getConnectivityMetadataRepository().executeBatch(insertQuery);
        return connection;
    }

    private List<String> getInsertQuery(Connection connection) {
        List<String> queries = new ArrayList<>();
        if (getByName(connection.getName()).size() == 0) {
            queries.add("INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository()
                    .getTableNameByLabel("Connections") + " (CONN_NM, CONN_TYP_NM, CONN_DSC) VALUES (" +
                    SQLTools.GetStringForSQL(connection.getName()) + "," +
                    SQLTools.GetStringForSQL(connection.getType()) + "," +
                    SQLTools.GetStringForSQL(connection.getDescription()) + ");");
        }

        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            queries.add(connectionParameterConfiguration.getInsertStatement(connection.getName(), connection.getEnvironment(), connectionParameter));
        }

        return queries;
    }

    public Connection update(Connection connection) throws ConnectionDoesNotExistException, ConnectionAlreadyExistsException, SQLException {
        // frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating connection {0}-{1}.", connection.getScriptName(), connection.getEnvironment()), Level.TRACE);
        try {
            delete(connection);
            return insert(connection);
        } catch (ConnectionDoesNotExistException e) {
            // TODO fix logging
            //frameworkExecution.getFrameworkLog().log(MessageFormat.format("Connection {0}-{1} is not present in the repository so cannot be updated",connection.getScriptName(), connection.getEnvironment()),Level.TRACE);
            throw new ConnectionDoesNotExistException(MessageFormat.format(
                    "Connection {0}-{1} is not present in the repository so cannot be updated", connection.getName()));

        } catch (ConnectionAlreadyExistsException e) {
            // frameworkExecution.getFrameworkLog().log(MessageFormat.format("Connection {0}-{1} is not deleted correctly during update. {2}",connection.getScriptName(), connection.getEnvironment(), e.toString()),Level.WARN);
            throw e;
        }
    }
}