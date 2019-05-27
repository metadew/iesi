package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.H2DatabaseConnection;
import io.metadew.iesi.connection.tools.ConnectionTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.definition.ConnectionTypeParameter;

import java.util.ArrayList;
import java.util.List;

public class DbH2ConnectionOperation {

    private FrameworkExecution frameworkExecution;
    private boolean missingMandatoryFields;
    private List<String> missingMandatoryFieldsList;

    public DbH2ConnectionOperation(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Database getDatabase(Connection connection) {
        this.setMissingMandatoryFieldsList(new ArrayList());

        Database database = null;

        String hostName = "";
        String portNumberTemp = "";
        int portNumber = 0;
        String pathName = "";
        String fileName = "";
        String userName = "";
        String userPassword = "";

        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            if (connectionParameter.getName().equalsIgnoreCase("host")) {
                hostName = (connectionParameter.getValue());
                hostName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(hostName);
            } else if (connectionParameter.getName().equalsIgnoreCase("port")) {
                portNumberTemp = connectionParameter.getValue();
                portNumberTemp = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(portNumberTemp);
            } else if (connectionParameter.getName().equalsIgnoreCase("path")) {
                pathName = connectionParameter.getValue();
                pathName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(pathName);
            } else if (connectionParameter.getName().equalsIgnoreCase("file")) {
                fileName = connectionParameter.getValue();
                fileName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(fileName);
            } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
                userName = connectionParameter.getValue();
                userName = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userName);
            } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
                userPassword = connectionParameter.getValue();
                userPassword = this.getFrameworkExecution().getFrameworkControl().resolveConfiguration(userPassword);
            }
        }

        // Check Mandatory Parameters
        this.setMissingMandatoryFields(false);
        ConnectionType connectionType = ConnectionTools.getConnectionType(this.getFrameworkExecution(), connection.getType());
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
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("path")) {
                    pathName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(pathName);
                } else if (connectionTypeParameter.getName().equalsIgnoreCase("file")) {
                    fileName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(fileName);
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

        H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(hostName, portNumber, pathName, fileName, userName, userPassword);
        database = new H2Database(h2DatabaseConnection, "");
        return database;
    }

    protected void addMissingField(String fieldName) {
        this.setMissingMandatoryFields(true);
        this.getMissingMandatoryFieldsList().add(fieldName);
    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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