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

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql, String database) {
        super(type, connectionURL, userName, userPassword, connectionInitSql, database);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String database, String userName,
                                   String userPassword, String connectionInitSql) {
        this("jdbc:mysql://" + hostName + ":" + portNumber + "/" + database, userName, userPassword, connectionInitSql, database);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String userName,
                                   String userPassword, String connectionInitSql) {
        this("jdbc:mysql://" + hostName + ":" + portNumber + "/", userName, userPassword, connectionInitSql);
    }


}
