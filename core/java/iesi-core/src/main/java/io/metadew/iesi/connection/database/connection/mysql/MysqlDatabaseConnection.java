package io.metadew.iesi.connection.database.connection.mysql;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;

/**
 * Connection object for MySQL databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class MysqlDatabaseConnection extends DatabaseConnection {

    private static String type = "mysql";

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String schemaName, String userName,
                                   String userPassword) {
        this("jdbc:mysql://" + hostName + ":" + portNumber + "/" + schemaName, userName, userPassword);
    }


}
