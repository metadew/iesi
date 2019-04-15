package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import io.metadew.iesi.metadata.definition.ListObject;

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
		String query = "select * from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections")
				+ " order by CONN_NM ASC";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(query);
		ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(frameworkExecution);
		try {
			while (crs.next()) {
				String connectionName = crs.getString("CONN_NM");
				String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters")
						+ " where CONN_NM = '" + connectionName + "'";
				CachedRowSet crsConnectionParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryConnectionParameters);

				String queryEnvironment = "select distinct ENV_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters")
						+ " where CONN_NM = '"
						+  connectionName + "' order by ENV_NM ASC";
				CachedRowSet crsEnvironment = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(queryEnvironment);
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

	public List<Connection> getConnectionsByEnvironment(String environmentName) {
		List<Connection> connections = new ArrayList<>();

		String connectionsByEnvironmentQuery = "select distinct CONN_NM from "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters")
				+ " where ENV_NM = '" + environmentName + "'";
		CachedRowSet connectionsByEnvironment = this.getFrameworkExecution().getMetadataControl()
				.getConnectivityRepositoryConfiguration().executeQuery(connectionsByEnvironmentQuery);
		try {
			while (connectionsByEnvironment.next()) {
				connections.add(getConnection(connectionsByEnvironment.getString("CONN_NM"), environmentName));
			}
			connectionsByEnvironment.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connections;
	}

	public void deleteConnection(Connection connection) {
		String deleteQuery = getDeleteQuery(connection);
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(deleteQuery);
	}

	public String getDeleteQuery(Connection connection) {
		String deleteQuery = "";

		deleteQuery += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters");
		deleteQuery += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName())
				+ "AND ENV_NM = "
				+ SQLTools.GetStringForSQL(connection.getEnvironment());
		deleteQuery += ";";
		deleteQuery += "\n";

		// If this was the last remaining connection with name CONN_NM, remove entirely from connections
		String countQuery = "SELECT COUNT(DISTINCT ENV_NM ) AS total_environments FROM "
				+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections")
				+ " WHERE ENV_NM != "
				+ connection.getEnvironment() + ";";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(countQuery);

		try {
			if (crs.next() && Integer.parseInt(crs.getString("total_environments")) == 0) {
				deleteQuery += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections");
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

	public void deleteConnectionByName(String connectionName) {
		String deleteQuery = getDeleteConnectionByNameQuery(connectionName);
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(deleteQuery);
	}

	public String getDeleteConnectionByNameQuery(String connectionName) {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections");
		sql += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ConnectionParameters");
		sql += " WHERE CONN_NM = "
				+ SQLTools.GetStringForSQL(connection.getName());
		sql += ";";
		sql += "\n";
		return sql;
	}

	public void insertConnection(Connection connection) {
		deleteConnection(connection);
		String insertQuery = getInsertQuery(connection);
		this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeUpdate(insertQuery);
	}

	private String getInsertQuery(Connection connection) {
		String sql = "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.getMetadataTableConfiguration().getTableName("Connections");
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
		String sqlParameters = this.getParameterInsertQuery(connection);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertQuery(Connection connection) {
		String result = "";

		for (ConnectionParameter connectionParameter : connection.getParameters()) {
			ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
					connectionParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += connectionParameterConfiguration.getInsertStatement(connection.getName(), connection.getEnvironment());
		}

		return result;
	}



	// Insert
	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("ConnectionParameters");
			sql += " WHERE CONN_NM = " + SQLTools.GetStringForSQL(this.getConnection().getName());
			sql += " AND ENV_NM = " + SQLTools.GetStringForSQL(this.getConnection().getEnvironment());
			sql += ";";
			sql += "\n";
			sql += "DELETE FROM " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
					.getMetadataTableConfiguration().getTableName("Connections");
			sql += " WHERE CONN_NM = " + SQLTools.GetStringForSQL(this.getConnection().getName());
			sql += ";";
			sql += "\n";
		}

		sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.getMetadataTableConfiguration().getTableName("Connections");
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
		if (!sqlParameters.equals("")) {
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
			if (!result.equals(""))
				result += "\n";
			result += connectionParameterConfiguration.getInsertStatement(this.getConnection().getName(),
					this.getConnection().getEnvironment());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Connection getConnection(String connectionName, String environmentName) {
		Connection connection = new Connection();
		CachedRowSet crsConnection = null;
		String queryConnection = "select CONN_NM, CONN_TYP_NM, CONN_DSC from " + this.getFrameworkExecution()
				.getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections")
				+ " where CONN_NM = '" + connectionName + "'";
		crsConnection = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
				.executeQuery(queryConnection);
		ConnectionParameterConfiguration connectionParameterConfiguration = new ConnectionParameterConfiguration(
				this.getFrameworkExecution());
		try {
			while (crsConnection.next()) {
				connection.setName(connectionName);
				connection.setType(crsConnection.getString("CONN_TYP_NM"));
				connection.setDescription(crsConnection.getString("CONN_DSC"));
				connection.setEnvironment(environmentName);

				// Get parameters
				CachedRowSet crsConnectionParameters = null;
				String queryConnectionParameters = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
						+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
								.getMetadataTableConfiguration().getTableName("ConnectionParameters")
						+ " where CONN_NM = '" + connectionName + "'and ENV_NM = '" + environmentName + "'";
				crsConnectionParameters = this.getFrameworkExecution().getMetadataControl()
						.getConnectivityRepositoryConfiguration().executeQuery(queryConnectionParameters);
				List<ConnectionParameter> connectionParameterList = new ArrayList();
				while (crsConnectionParameters.next()) {
					connectionParameterList.add(connectionParameterConfiguration.getConnectionParameter(connectionName,
							environmentName, crsConnectionParameters.getString("CONN_PAR_NM")));
				}
				connection.setParameters(connectionParameterList);
				crsConnectionParameters.close();
			}
			crsConnection.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (connection.getName() == null) {
			throw new RuntimeException("No Connection found" + connectionName);
		}

		return connection;
	}

	public ListObject getConnections(String environmentName) {
		List<Connection> connectionList = new ArrayList<>();
		String query = "select CONN_NM from " + this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().getMetadataTableConfiguration().getTableName("Connections")
				+ " order by CONN_NM ASC";
		CachedRowSet crs = this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration().executeQuery(query);
		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
		try {
			String connectionName = "";
			while (crs.next()) {
				connectionName = crs.getString("CONN_NM");

				if (environmentName.trim().equals("")) {
					CachedRowSet crsEnvironment = null;
					String queryEnvironment = "select distinct ENV_NM from "
							+ this.getFrameworkExecution().getMetadataControl().getConnectivityRepositoryConfiguration()
									.getMetadataTableConfiguration().getTableName("ConnectionParameters")
							+ " where CONN_NM = '" + connectionName + "' order by ENV_NM ASC";
					crsEnvironment = this.getFrameworkExecution().getMetadataControl()
							.getConnectivityRepositoryConfiguration().executeQuery(queryEnvironment);

					String tempEnvironmentName = "";
					while (crsEnvironment.next()) {
						tempEnvironmentName = crsEnvironment.getString("ENV_NM");
						connectionList.add(connectionConfiguration.getConnection(connectionName, tempEnvironmentName));
					}
					crsEnvironment.close();

				} else {
					connectionList.add(connectionConfiguration.getConnection(connectionName, environmentName));
				}
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Connection()), connectionList);
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