package io.metadew.iesi.connection.database.connection.h2;

public class H2ServerDatabaseConnection extends H2DatabaseConnection {

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword);
    }

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword, String schema) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword, schema);
    }

}
