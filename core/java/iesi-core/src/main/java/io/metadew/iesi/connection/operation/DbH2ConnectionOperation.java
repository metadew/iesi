package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
import io.metadew.iesi.connection.tools.ConnectionTools;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;

import java.util.ArrayList;
import java.util.List;

public class DbH2ConnectionOperation {

    private boolean missingMandatoryFields;
    private List<String> missingMandatoryFieldsList;

    // TODO: Remove Database should be subtype of Connection leading to H2DB to be subclass of DB...
    public DbH2ConnectionOperation() {
    }

    public Database getDatabase(Connection connection) {
        this.setMissingMandatoryFieldsList(new ArrayList<>());

        Database database;

        String hostName = "";
        String type = "";
        String portNumberTemp = "";
        int portNumber = 0;
        String pathName = "";
        String fileName = "";
        String userName = "";
        String userPassword = "";
        String databaseName = "";

        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            if (connectionParameter.getName().equalsIgnoreCase("host")) {
                hostName = (connectionParameter.getValue());
                hostName = FrameworkControl.getInstance().resolveConfiguration(hostName);
            }
            if (connectionParameter.getName().equalsIgnoreCase("type")) {
                type = connectionParameter.getValue();
                type = FrameworkControl.getInstance().resolveConfiguration(type);
            }
            if (connectionParameter.getName().equalsIgnoreCase("name")) {
                databaseName = connectionParameter.getValue();
                databaseName = FrameworkControl.getInstance().resolveConfiguration(databaseName);
            } else if (connectionParameter.getName().equalsIgnoreCase("port")) {
                portNumberTemp = connectionParameter.getValue();
                portNumberTemp = FrameworkControl.getInstance().resolveConfiguration(portNumberTemp);
            } else if (connectionParameter.getName().equalsIgnoreCase("path")) {
                pathName = connectionParameter.getValue();
                pathName = FrameworkControl.getInstance().resolveConfiguration(pathName);
            } else if (connectionParameter.getName().equalsIgnoreCase("file")) {
                fileName = connectionParameter.getValue();
                fileName = FrameworkControl.getInstance().resolveConfiguration(fileName);
            } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
                userName = connectionParameter.getValue();
                userName = FrameworkControl.getInstance().resolveConfiguration(userName);
            } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
                userPassword = connectionParameter.getValue();
                userPassword = FrameworkControl.getInstance().resolveConfiguration(userPassword);
            }
        }

        // Check Mandatory Parameters
        this.setMissingMandatoryFields(false);
        ConnectionType connectionType = ConnectionTools.getConnectionType(connection.getType());
        for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
            if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
                if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
                    if (hostName.trim().equalsIgnoreCase(""))
                        this.addMissingField("host");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
                    if (portNumberTemp.trim().equalsIgnoreCase(""))
                        this.addMissingField("port");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("path")) {
                    if (pathName.trim().equalsIgnoreCase(""))
                        this.addMissingField("path");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("file")) {
                    if (fileName.trim().equalsIgnoreCase(""))
                        this.addMissingField("file");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
                    if (userName.trim().equalsIgnoreCase(""))
                        this.addMissingField("user");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
                    if (userPassword.trim().equalsIgnoreCase(""))
                        this.addMissingField("password");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("type")) {
                    if (type.trim().equalsIgnoreCase(""))
                        this.addMissingField("type");
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("name")) {
                    if (type.trim().equalsIgnoreCase(""))
                        this.addMissingField("name");
                }
            }
        }

        if (this.isMissingMandatoryFields()) {
            String message = "Mandatory fields missing for connection " + connection.getMetadataKey().getName();
            throw new RuntimeException(message);
        }

        // Decrypt Parameters
        for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
            if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
                if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
                    hostName = FrameworkCrypto.getInstance().decrypt(hostName);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
                    portNumberTemp = FrameworkCrypto.getInstance().decrypt(portNumberTemp);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("path")) {
                    pathName = FrameworkCrypto.getInstance().decrypt(pathName);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("file")) {
                    fileName = FrameworkCrypto.getInstance().decrypt(fileName);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
                    userName = FrameworkCrypto.getInstance().decrypt(userName);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
                    userPassword = FrameworkCrypto.getInstance().decrypt(userPassword);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("type")) {
                    type = FrameworkCrypto.getInstance().decrypt(type);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("name")) {
                    databaseName = FrameworkCrypto.getInstance().decrypt(databaseName);
                }
            }
        }

        // Convert port number
        if (!portNumberTemp.isEmpty()) {
            portNumber = Integer.parseInt(portNumberTemp);
        }
        H2DatabaseConnection h2DatabaseConnection;
        switch (type) {
            case "embedded":
                h2DatabaseConnection = new H2EmbeddedDatabaseConnection(fileName, userName, userPassword);
                break;
            case "server":
                h2DatabaseConnection = new H2ServerDatabaseConnection(hostName, portNumber, fileName, userName, userPassword);
                break;
            case "memory":
                h2DatabaseConnection = new H2MemoryDatabaseConnection(databaseName, userName, userPassword);
                break;
            default:
                throw new RuntimeException();
        }
        database = new H2Database(h2DatabaseConnection, "");
        return database;
    }

    protected void addMissingField(String fieldName) {
        this.setMissingMandatoryFields(true);
        this.getMissingMandatoryFieldsList().add(fieldName);
    }

    public List<String> getMissingMandatoryFieldsList() {
        return missingMandatoryFieldsList;
    }

    public void setMissingMandatoryFieldsList(List<String> missingMandatoryFieldsList) {
        this.missingMandatoryFieldsList = missingMandatoryFieldsList;
    }

    public boolean isMissingMandatoryFields() {
        return missingMandatoryFields;
    }

    public void setMissingMandatoryFields(boolean missingMandatoryFields) {
        this.missingMandatoryFields = missingMandatoryFields;
    }

}