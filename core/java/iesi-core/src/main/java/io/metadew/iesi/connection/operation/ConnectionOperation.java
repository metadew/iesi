package io.metadew.iesi.connection.operation;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.ArtifactoryConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.LinuxHostConnection;
import io.metadew.iesi.connection.host.WindowsHostConnection;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ConnectionOperation {

    private final FrameworkControl frameworkControl;
    private final FrameworkCrypto frameworkCrypto = SpringContext.getBean(FrameworkCrypto.class);

    public ConnectionOperation(FrameworkControl frameworkControl) {
        this.frameworkControl = frameworkControl;
    }

    public HostConnection getHostConnection(Connection connection) {
        List<String> missingMandatoryFields = new ArrayList<>();

        HostConnection hostConnection = null;
        if (connection.getType().equalsIgnoreCase("host.windows")) {
            String hostName = "";
            String tempPath = "";

            for (ConnectionParameter connectionParameter : connection.getParameters()) {
                if (connectionParameter.getName().equalsIgnoreCase("host")) {
                    hostName = (connectionParameter.getValue());
                    hostName = frameworkControl.resolveConfiguration(hostName);
                } else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
                    tempPath = connectionParameter.getValue();
                    tempPath = frameworkControl.resolveConfiguration(tempPath);
                }
            }

            // Check Mandatory Parameters
            ConnectionType connectionType = this.getConnectionType(connection.getType());
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isMandatory()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("host")) {
                        if (hostName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("host");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("temppath")) {
                        if (tempPath.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("tempPath");
                    }
                }
            }

            if (!missingMandatoryFields.isEmpty()) {
                String message = "Mandatory fields missing for windows host connection " + connection.getMetadataKey().getName();
                throw new RuntimeException(message);
            }

            // Decrypt Parameters
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isEncrypted()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("host")) {
                        hostName = frameworkCrypto.decryptIfNeeded(hostName);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("temppath")) {
                        tempPath = frameworkCrypto.decryptIfNeeded(tempPath);
                    }
                }
            }

            hostConnection = new WindowsHostConnection(hostName, tempPath);
        } else if (connection.getType().equalsIgnoreCase("host.linux") || connection.getType().equalsIgnoreCase("host.unix")) {
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
                    hostName = frameworkControl.resolveConfiguration(hostName);
                } else if (connectionParameter.getName().equalsIgnoreCase("port")) {
                    portNumber = Integer.parseInt(connectionParameter.getValue());
                } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
                    userName = connectionParameter.getValue();
                    userName = frameworkControl.resolveConfiguration(userName);
                } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
                    userPassword = connectionParameter.getValue();
                    userPassword = frameworkControl
                            .resolveConfiguration(userPassword);
                } else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
                    tempPath = connectionParameter.getValue();
                    tempPath = frameworkControl.resolveConfiguration(tempPath);
                } else if (connectionParameter.getName().equalsIgnoreCase("simulateterminal")) {
                    terminalFlag = connectionParameter.getValue();
                    terminalFlag = frameworkControl
                            .resolveConfiguration(terminalFlag);
                } else if (connectionParameter.getName().equalsIgnoreCase("jumphostconnections")) {
                    jumpHostConnectionName = connectionParameter.getValue();
                    jumpHostConnectionName = frameworkControl
                            .resolveConfiguration(jumpHostConnectionName);
                } else if (connectionParameter.getName().equalsIgnoreCase("allowlocalhostexecution")) {
                    allowLocalhostExecution = connectionParameter.getValue();
                    allowLocalhostExecution = frameworkControl
                            .resolveConfiguration(allowLocalhostExecution);
                }
            }

            // Check Mandatory Parameters
            ConnectionType connectionType = this.getConnectionType(connection.getType());
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isMandatory()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("host")) {
                        if (hostName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("host");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("port")) {
                        if (portNumber == 0)
                            missingMandatoryFields.add("port");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("user")) {
                        if (userName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("user");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("password")) {
                        if (userPassword.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("password");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("temppath")) {
                        if (tempPath.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("tempPath");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("simulateterminal")) {
                        if (terminalFlag.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("simulateTerminal");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("jumphostconnections")) {
                        if (jumpHostConnectionName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("jumphostConnections");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("allowlocalhostexecution")) {
                        if (allowLocalhostExecution.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("allowLocalhostExecution");
                    }
                }
            }

            if (!missingMandatoryFields.isEmpty()) {
                String message = "Mandatory fields missing for linux host connection " + connection.getMetadataKey().getName();
                throw new RuntimeException(message);
            }

            // Decrypt Parameters
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isEncrypted()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("host")) {
                        hostName = frameworkCrypto.decryptIfNeeded(hostName);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("user")) {
                        userName = frameworkCrypto.decryptIfNeeded(userName);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("password")) {
                        userPassword = frameworkCrypto.decryptIfNeeded(userPassword);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("temppath")) {
                        tempPath = frameworkCrypto.decryptIfNeeded(tempPath);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("simulateterminal")) {
                        terminalFlag = frameworkCrypto.decryptIfNeeded(terminalFlag);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("jumphostconnections")) {
                        jumpHostConnectionName = frameworkCrypto
                                .decryptIfNeeded(jumpHostConnectionName);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("allowLocalhostexecution")) {
                        allowLocalhostExecution = frameworkCrypto
                                .decryptIfNeeded(allowLocalhostExecution);
                    }
                }
            }

            hostConnection = new LinuxHostConnection(hostName, portNumber, userName,
                    userPassword, tempPath, terminalFlag, jumpHostConnectionName, allowLocalhostExecution);
        }

        return hostConnection;
    }

    public ArtifactoryConnection getArtifactoryConnection(Connection connection) {
        List<String> missingMandatoryFields = new ArrayList<>();

        ArtifactoryConnection artifactoryConnection = null;
        if (connection.getType().equalsIgnoreCase("repo.artifactory")) {
            String connectionURL = "";
            String userName = "";
            String userPassword = null;
            String repositoryName = "";

            for (ConnectionParameter connectionParameter : connection.getParameters()) {
                if (connectionParameter.getName().equalsIgnoreCase("url")) {
                    connectionURL = (connectionParameter.getValue());
                    connectionURL = frameworkControl
                            .resolveConfiguration(connectionURL);
                } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
                    userName = connectionParameter.getValue();
                    userName = frameworkControl.resolveConfiguration(userName);
                } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
                    userPassword = connectionParameter.getValue();
                    userPassword = frameworkControl
                            .resolveConfiguration(userPassword);
                } else if (connectionParameter.getName().equalsIgnoreCase("repository")) {
                    repositoryName = connectionParameter.getValue();
                    repositoryName = frameworkControl
                            .resolveConfiguration(repositoryName);
                }
            }

            // Check Mandatory Parameters
            ConnectionType connectionType = this.getConnectionType(connection.getType());
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isMandatory()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("url")) {
                        if (connectionURL.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("url");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("user")) {
                        if (userName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("user");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("password")) {
                        if (userPassword.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("password");
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("repository")) {
                        if (repositoryName.trim().equalsIgnoreCase(""))
                            missingMandatoryFields.add("repository");
                    }
                }
            }

            if (!missingMandatoryFields.isEmpty()) {
                String message = "Mandatory fields missing for artifactory connection " + connection.getMetadataKey().getName();
                throw new RuntimeException(message);
            }

            // Decrypt Parameters
            for (Map.Entry<String, ConnectionTypeParameter> connectionTypeParameter : connectionType.getParameters().entrySet()) {
                if (connectionTypeParameter.getValue().isEncrypted()) {
                    if (connectionTypeParameter.getKey().equalsIgnoreCase("url")) {
                        connectionURL = frameworkCrypto.decryptIfNeeded(connectionURL);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("user")) {
                        userName = frameworkCrypto.decryptIfNeeded(userName);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("password")) {
                        userPassword = frameworkCrypto.decryptIfNeeded(userPassword);
                    } else if (connectionTypeParameter.getKey().equalsIgnoreCase("repository")) {
                        repositoryName = frameworkCrypto.decryptIfNeeded(repositoryName);
                    }
                }
            }

            artifactoryConnection = new ArtifactoryConnection(connectionURL, userName, userPassword, repositoryName);

        }

        return artifactoryConnection;
    }

    public boolean isOnLocalConnection(HostConnection hostConnection) {
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            return hostConnection.getHostName().equalsIgnoreCase(localHostName);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration();
        return connectionTypeConfiguration.getConnectionType(connectionTypeName);
    }

}
