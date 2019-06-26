package io.metadew.iesi.connection.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.ArtifactoryConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.MysqlDatabase;
import io.metadew.iesi.connection.database.NetezzaDatabase;
import io.metadew.iesi.connection.database.OracleDatabase;
import io.metadew.iesi.connection.database.PostgresqlDatabase;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.*;
import io.metadew.iesi.connection.host.LinuxHostConnection;
import io.metadew.iesi.connection.host.WindowsHostConnection;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.definition.ConnectionTypeParameter;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionOperation {

	private FrameworkExecution frameworkExecution;
	private boolean missingMandatoryFields;
	private List<String> missingMandatoryFieldsList;

	public ConnectionOperation(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Methods
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Database getDatabase(Connection connection) {
		this.setMissingMandatoryFieldsList(new ArrayList());

		Database database = null;
		try {
			if (connection.getType().equalsIgnoreCase("db.oracle")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String tnsAlias = "";
				String userName = "";
				String userPassword = null;
				String serviceName = "";

				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
						hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
						portNumberTemp = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(portNumberTemp);
					} else if (connectionParameter.getName().equalsIgnoreCase("tnsalias")) {
						tnsAlias = connectionParameter.getValue();
						tnsAlias = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(tnsAlias);
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
						userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
						userPassword = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(userPassword);
					} else if (connectionParameter.getName().equalsIgnoreCase("service")) {
						serviceName = connectionParameter.getValue();
						serviceName = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(serviceName);
					}
				}

				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equalsIgnoreCase(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equalsIgnoreCase(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("tnsalias")) {
							if (tnsAlias.trim().equalsIgnoreCase(""))
								this.addMissingField("tnsalias");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equalsIgnoreCase(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equalsIgnoreCase(""))
								this.addMissingField("password");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("service")) {
							if (userPassword.trim().equalsIgnoreCase(""))
								this.addMissingField("service");
						}
					}
				}

				// Addition for combined mandatory behavour of SERVICE_NM and TNS_ALIAS
				if (tnsAlias.trim().equalsIgnoreCase("") && serviceName.trim().equalsIgnoreCase("")) {
					this.addMissingField("service");
					this.addMissingField("tnsalias");
				}

				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}

				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("tnsalias")) {
							tnsAlias = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tnsAlias);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("service")) {
							serviceName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(serviceName);
						}
					}
				}

				// Convert port number
				if (!portNumberTemp.isEmpty()) {
					portNumber = Integer.parseInt(portNumberTemp);
				}
				
				OracleDatabaseConnection oracleDatabaseConnection;
				if (tnsAlias != null && tnsAlias.isEmpty()) {
					oracleDatabaseConnection = new TnsAliasOracleDatabaseConnection(hostName, portNumber, tnsAlias,
							userName, userPassword);
				} else {
					oracleDatabaseConnection = new ServiceNameOracleDatabaseConnection(hostName, portNumber,
							serviceName, userName, userPassword);
				}
				// TODO: schema as parameter to add to Oracle database
				database = new OracleDatabase(oracleDatabaseConnection, "");
			} else if (connection.getType().equalsIgnoreCase("db.netezza")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String databaseName = "";
				String userName = "";
				String userPassword = "";

				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
						hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
						portNumberTemp = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(portNumberTemp);
					} else if (connectionParameter.getName().equalsIgnoreCase("database")) {
						databaseName = connectionParameter.getValue();
						databaseName = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(databaseName);
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
						userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
						userPassword = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(userPassword);
					}
				}

				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equalsIgnoreCase(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equalsIgnoreCase(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							if (databaseName.trim().equalsIgnoreCase(""))
								this.addMissingField("database");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equalsIgnoreCase(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equalsIgnoreCase(""))
								this.addMissingField("password");
						}
					}
				}

				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}

				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							databaseName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(databaseName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						}
					}
				}

				// Convert port number
				if (!portNumberTemp.isEmpty()) {
					portNumber = Integer.parseInt(portNumberTemp);
				}

				NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(hostName,
						portNumber, databaseName, userName, userPassword);
				database = new NetezzaDatabase(netezzaDatabaseConnection, "");
			} else if (connection.getType().equalsIgnoreCase("db.postgresql")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String databaseName = "";
				String userName = "";
				String userPassword = "";

				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
						hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
						portNumberTemp = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(portNumberTemp);
					} else if (connectionParameter.getName().equalsIgnoreCase("database")) {
						databaseName = connectionParameter.getValue();
						databaseName = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(databaseName);
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
						userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
						userPassword = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(userPassword);
					}
				}

				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equalsIgnoreCase(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equalsIgnoreCase(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							if (databaseName.trim().equalsIgnoreCase(""))
								this.addMissingField("database");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equalsIgnoreCase(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equalsIgnoreCase(""))
								this.addMissingField("password");
						}
					}
				}

				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}

				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							databaseName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(databaseName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						}
					}
				}

				// Convert port number
				if (!portNumberTemp.isEmpty()) {
					portNumber = Integer.parseInt(portNumberTemp);
				}

				PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(hostName,
						portNumber, databaseName, userName, userPassword);
				database = new PostgresqlDatabase(postgresqlDatabaseConnection, "");
			} else if (connection.getType().equalsIgnoreCase("db.mysql")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String schemaName = "";
				String userName = "";
				String userPassword = "";

				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
						hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
						portNumberTemp = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(portNumberTemp);
					} else if (connectionParameter.getName().equalsIgnoreCase("schema")) {
						schemaName = connectionParameter.getValue();
						schemaName = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(schemaName);
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
						userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
						userPassword = this.getFrameworkExecution().getFrameworkControl()
								.resolveConfiguration(userPassword);
					}
				}

				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equalsIgnoreCase(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equalsIgnoreCase(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("schema")) {
							if (schemaName.trim().equalsIgnoreCase(""))
								this.addMissingField("schema");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equalsIgnoreCase(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equalsIgnoreCase(""))
								this.addMissingField("password");
						}
					}
				}

				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}

				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("schema")) {
							schemaName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(schemaName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						}
					}
				}

				// Convert port number
				if (!portNumberTemp.isEmpty()) {
					portNumber = Integer.parseInt(portNumberTemp);
				}
				
				MysqlDatabaseConnection mysqlDatabaseConnection = new MysqlDatabaseConnection(hostName, portNumber,
						schemaName, userName, userPassword);
				database = new MysqlDatabase(mysqlDatabaseConnection);
			} else if (connection.getType().equalsIgnoreCase("db.sqlite")) {
				String filePath = "";
				String fileName = "";

				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("filepath")) {
						filePath = (connectionParameter.getValue());
						filePath = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(filePath);
					} else if (connectionParameter.getName().equalsIgnoreCase("filename")) {
						fileName = connectionParameter.getValue();
						fileName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(fileName);
					}
				}

				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("filepath")) {
							if (filePath.trim().equalsIgnoreCase(""))
								this.addMissingField("filePath");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("filename")) {
							if (fileName.trim().equalsIgnoreCase(""))
								this.addMissingField("fileName");
						}
					}
				}

				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}

				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("filepath")) {
							filePath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(filePath);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("filename")) {
							fileName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(fileName);
						}
					}
				}

				SqliteDatabaseConnection sqliteDatabaseConnection = new SqliteDatabaseConnection(
						FilenameUtils.normalize(filePath + File.separator + fileName));
				database = new SqliteDatabase(sqliteDatabaseConnection);
			} else if (connection.getType().equalsIgnoreCase("db.h2")) {
				DbH2ConnectionOperation dbH2ConnectionOperation = new DbH2ConnectionOperation(
						this.getFrameworkExecution());
				database = dbH2ConnectionOperation.getDatabase(connection);
			} else if (connection.getType().equalsIgnoreCase("db.mariadb")) {
				DbMariadbConnectionOperation dbMariadbConnectionOperation = new DbMariadbConnectionOperation(
						this.getFrameworkExecution());
				database = dbMariadbConnectionOperation.getDatabase(connection);
			} else if (connection.getType().equalsIgnoreCase("db.mssql")) {
				DbMssqlConnectionOperation dbMssqlConnectionOperation = new DbMssqlConnectionOperation(
						this.getFrameworkExecution());
				database = dbMssqlConnectionOperation.getDatabase(connection);
			} else if (connection.getType().equalsIgnoreCase("db.presto")) {
				DbPrestoConnectionOperation dbPrestoConnectionOperation = new DbPrestoConnectionOperation(
						this.getFrameworkExecution());
				database = dbPrestoConnectionOperation.getDatabase(connection);
			} else if (connection.getType().equalsIgnoreCase("db.dremio")) {
				DbDremioConnectionOperation dbDremioConnectionOperation = new DbDremioConnectionOperation(
						this.getFrameworkExecution());
				database = dbDremioConnectionOperation.getDatabase(connection);
			} else if (connection.getType().equalsIgnoreCase("db.drill")) {
				DbDrillConnectionOperation dbDrillConnectionOperation = new DbDrillConnectionOperation(
						this.getFrameworkExecution());
				database = dbDrillConnectionOperation.getDatabase(connection);
			} else {
				String message = "Database type is not (yet) supported: " + connection.getType();
				throw new RuntimeException(message);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return database;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HostConnection getHostConnection(Connection connection) {
		this.setMissingMandatoryFieldsList(new ArrayList());

		ObjectMapper objectMapper = new ObjectMapper();
		HostConnection hostConnection = null;
		if (connection.getType().equalsIgnoreCase("host.windows")) {
			String hostName = "";
			String tempPath = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("host")) {
					hostName = (connectionParameter.getValue());
					hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
				} else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
					tempPath = connectionParameter.getValue();
					tempPath = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(tempPath);
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						if (hostName.trim().equalsIgnoreCase(""))
							this.addMissingField("host");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						if (tempPath.trim().equalsIgnoreCase(""))
							this.addMissingField("tempPath");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						tempPath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tempPath);
					}
				}
			}

			WindowsHostConnection windowsHostConnection = new WindowsHostConnection(hostName, tempPath);
			hostConnection = objectMapper.convertValue(windowsHostConnection, HostConnection.class);
		} else if (connection.getType().equalsIgnoreCase("host.linux")) {
			String hostName = "";
			int portNumber = 0;
			String userName = "";
			String userPassword = null;
			String tempPath = "";
			String terminalFlag = "";
			String jumpHostConnectionName = "";
			String allowLocalhostExecution = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("host")) {
					hostName = (connectionParameter.getValue());
					hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
				} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
					portNumber = Integer.parseInt(connectionParameter.getValue());
				} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
					userName = connectionParameter.getValue();
					userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
				} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
					userPassword = connectionParameter.getValue();
					userPassword = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(userPassword);
				} else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
					tempPath = connectionParameter.getValue();
					tempPath = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(tempPath);
				} else if (connectionParameter.getName().equalsIgnoreCase("simulateterminal")) {
					terminalFlag = connectionParameter.getValue();
					terminalFlag = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(terminalFlag);
				} else if (connectionParameter.getName().equalsIgnoreCase("jumphostconnections")) {
					jumpHostConnectionName = connectionParameter.getValue();
					jumpHostConnectionName = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(jumpHostConnectionName);
				} else if (connectionParameter.getName().equalsIgnoreCase("allowlocalhostexecution")) {
					allowLocalhostExecution = connectionParameter.getValue();
					allowLocalhostExecution = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(allowLocalhostExecution);
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						if (hostName.trim().equalsIgnoreCase(""))
							this.addMissingField("host");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
						if (portNumber == 0)
							this.addMissingField("port");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						if (userName.trim().equalsIgnoreCase(""))
							this.addMissingField("user");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						if (userPassword.trim().equalsIgnoreCase(""))
							this.addMissingField("password");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						if (tempPath.trim().equalsIgnoreCase(""))
							this.addMissingField("tempPath");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
						if (terminalFlag.trim().equalsIgnoreCase(""))
							this.addMissingField("simulateTerminal");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
						if (jumpHostConnectionName.trim().equalsIgnoreCase(""))
							this.addMissingField("jumphostConnections");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("allowlocalhostexecution")) {
						if (allowLocalhostExecution.trim().equalsIgnoreCase(""))
							this.addMissingField("allowLocalhostExecution");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						tempPath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tempPath);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
						terminalFlag = this.getFrameworkExecution().getFrameworkCrypto().decrypt(terminalFlag);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
						jumpHostConnectionName = this.getFrameworkExecution().getFrameworkCrypto()
								.decrypt(jumpHostConnectionName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("allowLocalhostexecution")) {
						allowLocalhostExecution = this.getFrameworkExecution().getFrameworkCrypto()
								.decrypt(allowLocalhostExecution);
					}
				}
			}

			LinuxHostConnection linuxHostConnection = new LinuxHostConnection(hostName, portNumber, userName,
					userPassword, tempPath, terminalFlag, jumpHostConnectionName, allowLocalhostExecution);
			hostConnection = objectMapper.convertValue(linuxHostConnection, HostConnection.class);
		}

		return hostConnection;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArtifactoryConnection getArtifactoryConnection(Connection connection) {
		this.setMissingMandatoryFieldsList(new ArrayList());

		ArtifactoryConnection artifactoryConnection = null;
		if (connection.getType().equalsIgnoreCase("repo.artifactory")) {
			String connectionURL = "";
			String userName = "";
			String userPassword = null;
			String repositoryName = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("url")) {
					connectionURL = (connectionParameter.getValue());
					connectionURL = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(connectionURL);
				} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
					userName = connectionParameter.getValue();
					userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
				} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
					userPassword = connectionParameter.getValue();
					userPassword = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(userPassword);
				} else if (connectionParameter.getName().equalsIgnoreCase("repository")) {
					repositoryName = connectionParameter.getValue();
					repositoryName = this.getFrameworkExecution().getFrameworkControl()
							.resolveConfiguration(repositoryName);
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
						if (connectionURL.trim().equalsIgnoreCase(""))
							this.addMissingField("url");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						if (userName.trim().equalsIgnoreCase(""))
							this.addMissingField("user");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						if (userPassword.trim().equalsIgnoreCase(""))
							this.addMissingField("password");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
						if (repositoryName.trim().equalsIgnoreCase(""))
							this.addMissingField("repository");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
						connectionURL = this.getFrameworkExecution().getFrameworkCrypto().decrypt(connectionURL);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
						repositoryName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(repositoryName);
					}
				}
			}

			artifactoryConnection = new ArtifactoryConnection(connectionURL, userName, userPassword, repositoryName);

		}

		return artifactoryConnection;
	}

	public boolean isOnLocalConnection(HostConnection hostConnection) {
		boolean result = false;

		try {
			String localHostName = InetAddress.getLocalHost().getHostName();
			if (hostConnection.getHostName().equalsIgnoreCase(localHostName))
				result = true;
		} catch (UnknownHostException e) {
			result = false;
		}

		return result;
	}

	public ConnectionType getConnectionType(String connectionTypeName) {
		ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration(
				this.getFrameworkExecution().getFrameworkInstance());
		ConnectionType connectionType = null;

		try {
			connectionType = connectionTypeConfiguration.getConnectionType(connectionTypeName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return connectionType;
	}

	protected void addMissingField(String fieldName) {
		this.setMissingMandatoryFields(true);
		this.getMissingMandatoryFieldsList().add(fieldName);
	}

	// Getters and Setters
	public boolean isMissingMandatoryFields() {
		return missingMandatoryFields;
	}

	public void setMissingMandatoryFields(boolean missingMandatoryFields) {
		this.missingMandatoryFields = missingMandatoryFields;
	}

	public List<String> getMissingMandatoryFieldsList() {
		return missingMandatoryFieldsList;
	}

	public void setMissingMandatoryFieldsList(List<String> missingMandatoryFieldsList) {
		this.missingMandatoryFieldsList = missingMandatoryFieldsList;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
}
