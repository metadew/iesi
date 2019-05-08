package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.exception.ConnectionAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ConnectionDoesNotExistException;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import io.metadew.iesi.metadata.definition.ListObject;
import org.apache.logging.log4j.Level;

public class ConnectionConfiguration {

	private FrameworkExecution frameworkExecution;
	private Connection connection;

	// Constructors
	public ConnectionConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	public ConnectionConfiguration(Connection connection, FrameworkExecution frameworkExecution) {
		this.setConnection(connection);
		this.setFrameworkExecution(frameworkExecution);
	}

	public List<Connection> getConnections() {
		List<Connection> connections = new ArrayList<>();
		String query = "select * from " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
				+ " order by CONN_NM ASC";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
		ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(frameworkExecution);
		try {
			while (crs.next()) {
				String connectionName = crs.getString("CONN_NM");
				String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
						+ " where CONN_NM = '" + connectionName + "'";
				CachedRowSet crsConnectionParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityMetadataRepository().executeQuery(queryConnectionParameters, "reader");

				String queryEnvironment = "select distinct ENV_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
						+ " where CONN_NM = '"
						+  connectionName + "' order by ENV_NM ASC";
				CachedRowSet crsEnvironment = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");
				while (crsEnvironment.next()) {
					List<ConnectionParameter> connectionParameters = new ArrayList<>();
					String environmentName = crsEnvironment.getString("ENV_NM");
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
				}
				crsConnectionParameters.close();
				crsEnvironment.close();
			}
			crs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connections;
	}

	public List<Connection> getConnectionByName(String connectionName) {
		return getConnections().stream().filter(connection -> connection.getName().equals(connectionName)).collect(Collectors.toList());
	}

	public List<Connection> getConnectionsByEnvironment(String environmentName) {
		List<Connection> connections = new ArrayList<>();

		String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
				+ " where ENV_NM = '" + environmentName + "'";
		CachedRowSet connectionsByEnvironment = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityMetadataRepository().executeQuery(connectionsByEnvironmentQuery, "reader");
		try {
			while (connectionsByEnvironment.next()) {
				getConnection(connectionsByEnvironment.getString("CONN_NM"), environmentName).ifPresent(connections::add);
			}
			connectionsByEnvironment.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connections;
	}

	public Optional<Connection> getConnection(String connectionName, String environmentName) {
		Connection connection = null;
		CachedRowSet crsConnection;
		String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + this.getFrameworkExecution()
				.getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections")
				+ " where CONN_NM = '" + connectionName + "'";
		crsConnection = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
				.executeQuery(queryConnection, "reader");
		ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsConnection.next()) {
				// TODO: if no parameters are found for environment_name, it does not exist?!
				// Get parameters
				String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
						.getTableNameByLabel("ConnectionParameters")
						+ " where CONN_NM = '" + connectionName + "'and ENV_NM = '" + environmentName + "'";
				CachedRowSet crsConnectionParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityMetadataRepository().executeQuery(queryConnectionParameters, "reader");
				List<ConnectionParameter> connectionParameters = new ArrayList<>();
				boolean containsParameters = false;
				while (crsConnectionParameters.next()) {
					containsParameters = true;
					connectionParameters.add(connectionParameterConfiguration.getConnectionParameter(connectionName,
							environmentName, crsConnectionParameters.getString("CONN_PAR_NM")));
				}
				if (containsParameters) {
					connection = new Connection(connectionName,
							crsConnection.getString("CONN_TYP_NM"),
							crsConnection.getString("CONN_DSC"),
							environmentName,
							connectionParameters);
				}
				crsConnectionParameters.close();
			}
			crsConnection.close();
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return Optional.ofNullable(connection);
	}

	public boolean exists(Connection connection) {
		return getConnection(connection.getName(), connection.getEnvironment()).isPresent();
	}

	public boolean exists(String connectionName) {
		return !getConnectionByName(connectionName).isEmpty();
	}

	public void deleteConnection(Connection connection) throws ConnectionDoesNotExistException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Deleting connection {0}-{1}.", connection.getName(), connection.getEnvironment()), Level.TRACE);
		if (!exists(connection)) {
			throw new ConnectionDoesNotExistException(
					MessageFormat.format("Connection {0}-{1} is not present in the repositoryCoordinator so cannot be updated",
							connection.getName(), connection.getEnvironment()));

		}
		String deleteQuery = getDeleteQuery(connection);
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(deleteQuery);
	}

	public String getDeleteQuery(Connection connection) {
		String deleteQuery = "";

		deleteQuery += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
		deleteQuery += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName())
				+ "AND ENV_NM = "
				+ SQLTools.GetStringForSQL(connection.getEnvironment());
		deleteQuery += ";";
		deleteQuery += "\n";

		// If this was the last remaining connection with name CONN_NM, remove entirely from connections
		String countQuery = "SELECT COUNT(DISTINCT ENV_NM ) AS total_environments FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
				+ " WHERE ENV_NM != '"
				+ connection.getEnvironment() + "';";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(countQuery, "reader");

		try {
			if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
				deleteQuery += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections");
				deleteQuery += " WHERE CONN_NM = "
						+ SQLTools.GetStringForSQL(connection.getName());
				deleteQuery += ";";
				deleteQuery += "\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return deleteQuery;
	}

	public void deleteConnectionByName(String connectionName) throws ConnectionDoesNotExistException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Deleting connection {0}.", connectionName), Level.TRACE);
		if (!exists(connectionName)) {
			throw new ConnectionDoesNotExistException(
					MessageFormat.format("Connection {0} is not present in the repositoryCoordinator so cannot be updated",
							connectionName, connection.getEnvironment()));

		}
		String deleteQuery = getDeleteConnectionByNameQuery(connectionName);
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(deleteQuery);
	}

	public String getDeleteConnectionByNameQuery(String connectionName) {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections");
		sql += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
		sql += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName());
		sql += ";";
		sql += "\n";
		return sql;
	}

	public void deleteAllConnections() {
		frameworkExecution.getFrameworkLog().log("Deleting all connections", Level.TRACE);
		String query = getDeleteAllStatement();
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
	}

	private String getDeleteAllStatement() {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections");
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
		sql += ";";
		sql += "\n";

		return sql;
	}

	public void insertConnection(Connection connection) throws ConnectionAlreadyExistsException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Inserting connection {0}-{1}.", connection.getName(), connection.getEnvironment()), Level.TRACE);
		if (exists(connection)) {
			throw new ConnectionAlreadyExistsException(MessageFormat.format(
					"Connection {0}-{1} already exists", connection.getName(), connection.getEnvironment()));
		}
		String insertQuery = getInsertQuery(connection);
		this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(insertQuery);
	}

	private String getInsertQuery(Connection connection) {
		String sql = "";
		if (getConnectionByName(connection.getName()).size() == 0) {
			sql = "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
					.getTableNameByLabel("Connections");
			sql += " (CONN_NM, CONN_TYP_NM, CONN_DSC) ";
			sql += "VALUES ";
			sql += "(";
			sql += SQLTools.GetStringForSQL(connection.getName());
			sql += ",";
			sql += SQLTools.GetStringForSQL(connection.getType());
			sql += ",";
			sql += SQLTools.GetStringForSQL(connection.getDescription());
			sql += ")";
			sql += ";";
		}

		// add Parameters
		String sqlParameters = this.getParameterInsertQuery(connection);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertQuery(Connection connection) {
		StringBuilder result = new StringBuilder();

		for (ConnectionParameter connectionParameter : connection.getParameters()) {
			ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
					connectionParameter, this.getFrameworkExecution());
			if (!result.toString().equals(""))
				result.append("\n");
			result.append(connectionParameterConfiguration.getInsertStatement(connection.getName(), connection.getEnvironment()));
		}

		return result.toString();
	}

	public void updateConnection(Connection connection) throws ConnectionDoesNotExistException {
		frameworkExecution.getFrameworkLog().log(MessageFormat.format(
				"Updating connection {0}-{1}.", connection.getName(), connection.getEnvironment()), Level.TRACE);
		try {
			deleteConnection(connection);
			insertConnection(connection);
		} catch (ConnectionDoesNotExistException e) {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format(
					"Connection {0}-{1} is not present in the repositoryCoordinator so cannot be updated",
					connection.getName(), connection.getEnvironment()),
					Level.TRACE);
			throw new ConnectionDoesNotExistException(MessageFormat.format(
					"Connection {0}-{1} is not present in the repositoryCoordinator so cannot be updated", connection.getName()));

		} catch (ConnectionAlreadyExistsException e) {
			frameworkExecution.getFrameworkLog().log(MessageFormat.format(
					"Connection {0}-{1} is not deleted correctly during update. {2}",
					connection.getName(), connection.getEnvironment(), e.toString()),
					Level.WARN);
		}
	}

	// Insert

	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += "DELETE FROM "
					+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
			sql += " WHERE CONN_NM = " + SQLTools.GetStringForSQL(this.getConnection().getName());
			sql += " AND ENV_NM = " + SQLTools.GetStringForSQL(this.getConnection().getEnvironment());
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM "
					+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Connections");
			sql += " WHERE CONN_NM = " + SQLTools.GetStringForSQL(this.getConnection().getName());
			sql += ";";
			sql += "\n";
		}

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
				.getTableNameByLabel("Connections");
		sql += " (CONN_NM, CONN_TYP_NM, CONN_DSC) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(this.getConnection().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getConnection().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getConnection().getDescription());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements();
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements() {
		String result = "";

		for (ConnectionParameter connectionParameter : this.getConnection().getParameters()) {
			ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
					connectionParameter, this.getFrameworkExecution());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += connectionParameterConfiguration.getInsertStatement(this.getConnection().getName(),
					this.getConnection().getEnvironment());
		}

		return result;
	}


	public ListObject getConnections(String environmentName) {
		List<Connection> connections = new ArrayList<>();
		CachedRowSet crs;
		String query = "select CONN_NM from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
				.getTableNameByLabel("Connections")
				+ " order by CONN_NM ASC";
		crs = this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
		try {
			String connectionName = "";
			while (crs.next()) {
				connectionName = crs.getString("CONN_NM");

				if (environmentName.trim().equalsIgnoreCase("")) {
					CachedRowSet crsEnvironment = null;
					String queryEnvironment = "select distinct ENV_NM from "
							+ this.getFrameworkExecution().getMetadataControl().getConnectivityMetadataRepository()
							.getTableNameByLabel("ConnectionParameters")
							+ " where CONN_NM = '" + connectionName + "' order by ENV_NM ASC";
					crsEnvironment = this.getFrameworkExecution().getMetadataControl()
							.getConnectivityMetadataRepository().executeQuery(queryEnvironment, "reader");

					String tempEnvironmentName = "";
					while (crsEnvironment.next()) {
						tempEnvironmentName = crsEnvironment.getString("ENV_NM");
						connectionConfiguration.getConnection(connectionName, tempEnvironmentName).ifPresent(connections::add);
					}
					crsEnvironment.close();

				} else {

					connectionConfiguration.getConnection(connectionName, environmentName).ifPresent(connections::add);
				}
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Connection()), connections);
	}

	// Exists
	public boolean exists() {
		return true;
	}

	// Getters and Setters
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}