package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.List;

public class DbDb2ConnectionOperation {

    private boolean missingMandatoryFields;
    private List<String> missingMandatoryFieldsList;

    public DbDb2ConnectionOperation() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Database getDatabase(Connection connection)  {
//        this.setMissingMandatoryFieldsList(new ArrayList());
//
//        Database database = null;
//
//        String hostName = "";
//        String portNumberTemp = "";
//        int portNumber = 0;
//        String databaseName = "";
//        String userName = "";
//        String userPassword = "";
//
//        for (ConnectionParameter connectionParameter : connection.getParameters()) {
//            if (connectionParameter.getName().equalsIgnoreCase("host")) {
//                hostName = (connectionParameter.getValue());
//                hostName = FrameworkControl.getInstance().resolveConfiguration(hostName);
//            } else if (connectionParameter.getName().equalsIgnoreCase("port")) {
//                portNumberTemp = connectionParameter.getValue();
//                portNumberTemp = FrameworkControl.getInstance().resolveConfiguration(portNumberTemp);
//            } else if (connectionParameter.getName().equalsIgnoreCase("database")) {
//                databaseName = connectionParameter.getValue();
//                databaseName = FrameworkControl.getInstance().resolveConfiguration(databaseName);
//            } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
//                userName = connectionParameter.getValue();
//                userName = FrameworkControl.getInstance().resolveConfiguration(userName);
//            } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
//                userPassword = connectionParameter.getValue();
//                userPassword = FrameworkControl.getInstance().resolveConfiguration(userPassword);
//            }
//        }
//
//        // Check Mandatory Parameters
//        this.setMissingMandatoryFields(false);
//        ConnectionType connectionType = ConnectionTools.getConnectionType(connection.getType());
//        for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//            if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
//                if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                    if (hostName.trim().equalsIgnoreCase(""))
//                        this.addMissingField("host");
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
//                    if (portNumberTemp.trim().equalsIgnoreCase(""))
//                        this.addMissingField("port");
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
//                    if (databaseName.trim().equalsIgnoreCase(""))
//                        this.addMissingField("path");
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                    if (userName.trim().equalsIgnoreCase(""))
//                        this.addMissingField("user");
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                    if (userPassword.trim().equalsIgnoreCase(""))
//                        this.addMissingField("password");
//                }
//            }
//        }
//
//        if (this.isMissingMandatoryFields()) {
//            String message = "Mandatory fields missing for connection " + connection.getName();
//            throw new RuntimeException(message);
//        }
//
//        // Decrypt Parameters
//        for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
//            if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
//                if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
//                    hostName = FrameworkCrypto.getInstance().decrypt(hostName);
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
//                    portNumberTemp = FrameworkCrypto.getInstance().decrypt(portNumberTemp);
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
//                    databaseName = FrameworkCrypto.getInstance().decrypt(databaseName);
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
//                    userName = FrameworkCrypto.getInstance().decrypt(userName);
//                } else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
//                    userPassword = FrameworkCrypto.getInstance().decrypt(userPassword);
//                }
//            }
//        }
//
//        // Convert port number
//        if (!portNumberTemp.isEmpty()) {
//            portNumber = Integer.parseInt(portNumberTemp);
//        }
//
//        Db2DatabaseConnection db2DatabaseConnection = new Db2DatabaseConnection(hostName, portNumber, databaseName, userName, userPassword);
//        db2DatabaseConnection.setSchema(databaseName);
//        database = new Db2Database(db2DatabaseConnection, databaseName);

        return null;
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