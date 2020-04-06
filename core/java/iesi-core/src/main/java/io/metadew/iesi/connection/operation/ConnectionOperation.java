package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.ArtifactoryConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.metadata.configuration.type.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ConnectionOperation {

    private boolean missingMandatoryFields;
    private List<String> missingMandatoryFieldsList;

    // TODO: remove
    public ConnectionOperation() {
    }


    public HostConnection getHostConnection(Connection connection) {
//        this.setMissingMandatoryFieldsList(new ArrayList<>());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        HostConnection hostConnection = null;
//        if (connection.getType().equalsIgnoreCase("host.windows")) {
//            String hostName = "";
//            String tempPath = "";
//
//            for (ConnectionParameter connectionParameter : connection.getParameters()) {
//                if (connectionParameter.getName().equalsIgnoreCase("host")) {
//                    hostName = (connectionParameter.getValue());
//                    hostName = FrameworkControl.getInstance().resolveConfiguration(hostName);
//                } else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
//                    tempPath = connectionParameter.getValue();
//                    tempPath = FrameworkControl.getInstance().resolveConfiguration(tempPath);
//                }
//            }
//
//            // Check Mandatory Parameters
//            this.setMissingMandatoryFields(false);
//            ConnectionType connectionType = this.getConnectionType(connection.getType());
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                        if (hostName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("host");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
//                        if (tempPath.trim().equalsIgnoreCase(""))
//                            this.addMissingField("tempPath");
//                    }
//                }
//            }
//
//            if (this.isMissingMandatoryFields()) {
//                String message = "Mandatory fields missing for windows host connection " + connection.getName();
//                throw new RuntimeException(message);
//            }
//
//            // Decrypt Parameters
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                        hostName = FrameworkCrypto.getInstance().decrypt(hostName);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
//                        tempPath = FrameworkCrypto.getInstance().decrypt(tempPath);
//                    }
//                }
//            }
//
//            WindowsHostConnection windowsHostConnection = new WindowsHostConnection(hostName, tempPath);
//            hostConnection = objectMapper.convertValue(windowsHostConnection, HostConnection.class);
//        } else if (connection.getType().equalsIgnoreCase("host.linux")) {
//            String hostName = "";
//            int portNumber = 0;
//            String userName = "";
//            String userPassword = null;
//            String tempPath = "";
//            String terminalFlag = "";
//            String jumpHostConnectionName = "";
//            String allowLocalhostExecution = "";
//
//            for (ConnectionParameter connectionParameter : connection.getParameters()) {
//                if (connectionParameter.getName().equalsIgnoreCase("host")) {
//                    hostName = (connectionParameter.getValue());
//                    hostName = FrameworkControl.getInstance().resolveConfiguration(hostName);
//                } else if (connectionParameter.getName().equalsIgnoreCase("port")) {
//                    portNumber = Integer.parseInt(connectionParameter.getValue());
//                } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
//                    userName = connectionParameter.getValue();
//                    userName = FrameworkControl.getInstance().resolveConfiguration(userName);
//                } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
//                    userPassword = connectionParameter.getValue();
//                    userPassword = FrameworkControl.getInstance()
//                            .resolveConfiguration(userPassword);
//                } else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
//                    tempPath = connectionParameter.getValue();
//                    tempPath = FrameworkControl.getInstance().resolveConfiguration(tempPath);
//                } else if (connectionParameter.getName().equalsIgnoreCase("simulateterminal")) {
//                    terminalFlag = connectionParameter.getValue();
//                    terminalFlag = FrameworkControl.getInstance()
//                            .resolveConfiguration(terminalFlag);
//                } else if (connectionParameter.getName().equalsIgnoreCase("jumphostconnections")) {
//                    jumpHostConnectionName = connectionParameter.getValue();
//                    jumpHostConnectionName = FrameworkControl.getInstance()
//                            .resolveConfiguration(jumpHostConnectionName);
//                } else if (connectionParameter.getName().equalsIgnoreCase("allowlocalhostexecution")) {
//                    allowLocalhostExecution = connectionParameter.getValue();
//                    allowLocalhostExecution = FrameworkControl.getInstance()
//                            .resolveConfiguration(allowLocalhostExecution);
//                }
//            }
//
//            // Check Mandatory Parameters
//            this.setMissingMandatoryFields(false);
//            ConnectionType connectionType = this.getConnectionType(connection.getType());
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                        if (hostName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("host");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
//                        if (portNumber == 0)
//                            this.addMissingField("port");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                        if (userName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("user");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                        if (userPassword.trim().equalsIgnoreCase(""))
//                            this.addMissingField("password");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
//                        if (tempPath.trim().equalsIgnoreCase(""))
//                            this.addMissingField("tempPath");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
//                        if (terminalFlag.trim().equalsIgnoreCase(""))
//                            this.addMissingField("simulateTerminal");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
//                        if (jumpHostConnectionName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("jumphostConnections");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("allowlocalhostexecution")) {
//                        if (allowLocalhostExecution.trim().equalsIgnoreCase(""))
//                            this.addMissingField("allowLocalhostExecution");
//                    }
//                }
//            }
//
//            if (this.isMissingMandatoryFields()) {
//                String message = "Mandatory fields missing for linux host connection " + connection.getName();
//                throw new RuntimeException(message);
//            }
//
//            // Decrypt Parameters
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                        hostName = FrameworkCrypto.getInstance().decrypt(hostName);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                        userName = FrameworkCrypto.getInstance().decrypt(userName);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                        userPassword = FrameworkCrypto.getInstance().decrypt(userPassword);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
//                        tempPath = FrameworkCrypto.getInstance().decrypt(tempPath);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
//                        terminalFlag = FrameworkCrypto.getInstance().decrypt(terminalFlag);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
//                        jumpHostConnectionName = FrameworkCrypto.getInstance()
//                                .decrypt(jumpHostConnectionName);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("allowLocalhostexecution")) {
//                        allowLocalhostExecution = FrameworkCrypto.getInstance()
//                                .decrypt(allowLocalhostExecution);
//                    }
//                }
//            }
//
//            LinuxHostConnection linuxHostConnection = new LinuxHostConnection(hostName, portNumber, userName,
//                    userPassword, tempPath, terminalFlag, jumpHostConnectionName, allowLocalhostExecution);
//            hostConnection = objectMapper.convertValue(linuxHostConnection, HostConnection.class);
//        }

        return null;
    }

    public ArtifactoryConnection getArtifactoryConnection() {
//        this.setMissingMandatoryFieldsList(new ArrayList<>());
//
//        ArtifactoryConnection artifactoryConnection = null;
//        if (connection.getType().equalsIgnoreCase("repo.artifactory")) {
//            String connectionURL = "";
//            String userName = "";
//            String userPassword = null;
//            String repositoryName = "";
//
//            for (ConnectionParameter connectionParameter : connection.getParameters()) {
//                if (connectionParameter.getName().equalsIgnoreCase("url")) {
//                    connectionURL = (connectionParameter.getValue());
//                    connectionURL = FrameworkControl.getInstance()
//                            .resolveConfiguration(connectionURL);
//                } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
//                    userName = connectionParameter.getValue();
//                    userName = FrameworkControl.getInstance().resolveConfiguration(userName);
//                } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
//                    userPassword = connectionParameter.getValue();
//                    userPassword = FrameworkControl.getInstance()
//                            .resolveConfiguration(userPassword);
//                } else if (connectionParameter.getName().equalsIgnoreCase("repository")) {
//                    repositoryName = connectionParameter.getValue();
//                    repositoryName = FrameworkControl.getInstance()
//                            .resolveConfiguration(repositoryName);
//                }
//            }
//
//            // Check Mandatory Parameters
//            this.setMissingMandatoryFields(false);
//            ConnectionType connectionType = this.getConnectionType(connection.getType());
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
//                        if (connectionURL.trim().equalsIgnoreCase(""))
//                            this.addMissingField("url");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                        if (userName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("user");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                        if (userPassword.trim().equalsIgnoreCase(""))
//                            this.addMissingField("password");
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
//                        if (repositoryName.trim().equalsIgnoreCase(""))
//                            this.addMissingField("repository");
//                    }
//                }
//            }
//
//            if (this.isMissingMandatoryFields()) {
//                String message = "Mandatory fields missing for artifactory connection " + connection.getName();
//                throw new RuntimeException(message);
//            }
//
//            // Decrypt Parameters
//            for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//                if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
//                    if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
//                        connectionURL = FrameworkCrypto.getInstance().decrypt(connectionURL);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                        userName = FrameworkCrypto.getInstance().decrypt(userName);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                        userPassword = FrameworkCrypto.getInstance().decrypt(userPassword);
//                    } else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
//                        repositoryName = FrameworkCrypto.getInstance().decrypt(repositoryName);
//                    }
//                }
//            }
//
//            artifactoryConnection = new ArtifactoryConnection(connectionURL, userName, userPassword, repositoryName);
//
//        }

        return null;
    }

    @Deprecated
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
        ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration();
        return connectionTypeConfiguration.getConnectionType(connectionTypeName);
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

}
