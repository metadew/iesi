package io.metadew.iesi.connection.database.mysql;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for MySQL databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class MysqlDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "mysql";

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
    }

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql, String schema) {
        super(type, connectionURL, userName, userPassword, connectionInitSql, schema);
    }

    //    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
//        super(type, connectionURL, userName, userPassword, null);
//    }
//
    public MysqlDatabaseConnection(String hostName, int portNumber, String schemaName, String userName,
                                   String userPassword, String connectionInitSql) {
        this("jdbc:mysql://" + hostName + ":" + portNumber + "/" + schemaName, userName, userPassword, connectionInitSql, schemaName);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String userName,
                                   String userPassword, String connectionInitSql) {
        this("jdbc:mysql://" + hostName + ":" + portNumber + "/", userName, userPassword, connectionInitSql);
    }


}
