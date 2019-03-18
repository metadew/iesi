package io.metadew.iesi.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.database.ScriptRunner;

/**
 * Connection object for databases. This is extended depending on the database type.
 * 
 * @author peter.billen
 *
 */
public class DatabaseConnection {

	private String type = "";
	private String driver = "";
	private String connectionURL = "";
	private String userName = "";
	private String userPassword = null;

	private Connection connection;

	public DatabaseConnection() {

	}

	public DatabaseConnection(String type, String connectionURL, String userName, String userPassword) {
		super();
		this.setType(type);
		this.setConnectionURL(connectionURL);
		this.setUserName(userName);
		this.setUserPassword(userPassword);

		// Derive driver
		this.deriveDriver();
	}

	// Derive Driver
	private void deriveDriver() {
		if (this.getType().equalsIgnoreCase("oracle")) {
			this.setDriver("oracle.jdbc.driver.OracleDriver");
		} else if (this.getType().equalsIgnoreCase("mysql")) {
			this.setDriver("com.mysql.jdbc.Driver");
		} else if (this.getType().equalsIgnoreCase("netezza")) {
			this.setDriver("org.netezza.Driver");
		} else if (this.getType().equalsIgnoreCase("postgresql")) {
			this.setDriver("org.postgresql.Driver");
		} else if (this.getType().equalsIgnoreCase("sqlite")) {
			this.setDriver("org.sqlite.JDBC");
		} else if (this.getType().equalsIgnoreCase("teradata")) {
			this.setDriver("com.teradata.jdbc.TeraDriver");
		}
	}

	// Illegal character manipulation
	private String removeIllgegalCharactersForSingleQuery(String input) {
		input = input.trim();
		if (input.endsWith(";")) {
			input = input.substring(0, input.length() - 1);
		}
		return input;
	}

	// Database interactions
	public CachedRowSet executeQuery(String query) {

		// Remove illegal characters at the end
		query = this.removeIllgegalCharactersForSingleQuery(query);

		CachedRowSet crs = null;

		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
			throw new RuntimeException(e.getMessage());
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(this.getConnectionURL(), this.getUserName(),
					this.getUserPassword());
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

		if (connection != null) {
			try {
				Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);

				try {
					ResultSet rs = statement.executeQuery(query);
					crs = RowSetProvider.newFactory().createCachedRowSet();
					crs.populate(rs);
					rs.close();
				} catch (Exception e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Query Actions Failed");
					throw new RuntimeException(e.getMessage());
				}

				statement.close();

			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Database actions Failed");
			} finally {
				// Close the connection
				try {
					connection.close();
				} catch (SQLException e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Connection Close Failed");
					throw new RuntimeException(e.getMessage());
				}
			}

		} else {
			System.out.println("Connection lost");
		}

		return crs;
	}

	public CachedRowSet executeQueryLimitRows(String query, int limit) {
		// Remove illegal characters at the end
		query = this.removeIllgegalCharactersForSingleQuery(query);

		CachedRowSet crs = null;

		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
			throw new RuntimeException(e.getMessage());
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(this.getConnectionURL(), this.getUserName(),
					this.getUserPassword());
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

		if (connection != null) {
			try {
				Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				statement.setMaxRows(limit);

				try {
					ResultSet rs = statement.executeQuery(query);
					crs = RowSetProvider.newFactory().createCachedRowSet();
					crs.populate(rs);
					rs.close();
				} catch (Exception e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Query Actions Failed");
					throw new RuntimeException(e.getMessage());
				}

				statement.close();

			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Database actions Failed");
			} finally {
				// Close the connection
				try {
					connection.close();
				} catch (SQLException e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Connection Close Failed");
					throw new RuntimeException(e.getMessage());
				}
			}

		} else {
			System.out.println("Connection lost");
		}

		return crs;
	}

	public void executeUpdate(String query) {
		// Remove illegal characters at the end
		query = this.removeIllgegalCharactersForSingleQuery(query);

		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
			throw new RuntimeException(e.getMessage());
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(this.getConnectionURL(), this.getUserName(),
					this.getUserPassword());
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

		if (connection != null) {
			try {
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
				statement.close();
			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Database Actions Failed");
				throw new RuntimeException(e.getMessage());
			} finally {
				// Close the connection
				try {
					connection.close();
				} catch (SQLException e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Connection Close Failed");
					throw new RuntimeException(e.getMessage());
				}
			}

		} else {
			System.out.println("Connection lost");
		}
	}

	@SuppressWarnings("finally")
	public SqlScriptResult executeScript(String fileName) {
		SqlScriptResult dcSQLScriptResult = null;
		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
			throw new RuntimeException(e.getMessage());
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(this.getConnectionURL(), this.getUserName(),
					this.getUserPassword());
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

		if (connection != null) {
			try {
				//
				ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

				InputStreamReader reader = null;

				try {
					reader = new InputStreamReader(new FileInputStream(fileName));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}

				try {
					dcSQLScriptResult = scriptRunner.runScript(reader);
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}

			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Database Actions Failed");
				throw new RuntimeException(e.getMessage());
			} finally {
				// Close the connection
				try {
					connection.close();
					return dcSQLScriptResult;
				} catch (SQLException e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Connection Close Failed");
					throw new RuntimeException(e.getMessage());
				}
			}

		} else {
			System.out.println("Connection lost");
			throw new RuntimeException("Connection lost");
		}
	}

	@SuppressWarnings("finally")
	public SqlScriptResult executeScript(InputStream inputStream) {
		SqlScriptResult dcSQLScriptResult = null;
		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(this.getConnectionURL(), this.getUserName(),
					this.getUserPassword());
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

		if (connection != null) {
			try {
				//
				ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

				InputStreamReader reader = null;

				try {
					reader = new InputStreamReader(inputStream);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}

				try {
					dcSQLScriptResult = scriptRunner.runScript(reader);
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}

			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Database Actions Failed");
				throw new RuntimeException(e.getMessage());
			} finally {
				// Close the connection
				try {
					connection.close();
					return dcSQLScriptResult;
				} catch (Exception e) {
					StringWriter StackTrace = new StringWriter();
					e.printStackTrace(new PrintWriter(StackTrace));
					System.out.println("Connection Close Failed");
					throw new RuntimeException(e.getMessage());
				}
			}

		} else {
			System.out.println("Connection lost");
			throw new RuntimeException("Connection lost");
		}
	}

	public PreparedStatement createLivePreparedStatement(String sqlStatement) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = this.getConnection().prepareStatement(sqlStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preparedStatement;
	}

	public void createLiveConnection() {

		try {
			Class.forName(this.getDriver());
		} catch (ClassNotFoundException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("JDBC Driver Not Available");
			throw new RuntimeException(e.getMessage());
		}

		try {
			this.setConnection(
					DriverManager.getConnection(this.getConnectionURL(), this.getUserName(), this.getUserPassword()));
		} catch (SQLException e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			System.out.println("Connection Failed");
			throw new RuntimeException(e.getMessage());
		}

	}

	public void closeLiveConnection() {

		if (this.getConnection() != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				StringWriter StackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(StackTrace));
				System.out.println("Connection Close Failed");
				throw new RuntimeException(e.getMessage());
			}

		} else {
			System.out.println("Connection lost");
		}
	}

	// Getters and Setters
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type.toLowerCase();
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
