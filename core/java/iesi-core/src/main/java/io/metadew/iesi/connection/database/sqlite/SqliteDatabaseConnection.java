package io.metadew.iesi.connection.database.sqlite;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;

import java.nio.file.Path;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

    private static String type = "sqlite";

    public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
    }

    public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
        this(connectionURL, userName, userPassword, null);
    }

    public SqliteDatabaseConnection(String fileName, String initSql) {
        this(getConnectionUrl(fileName), "", "", initSql);
    }

    public SqliteDatabaseConnection(Path fileName, String initSql) {
        this(getConnectionUrl(fileName.toString()), "", "", initSql);
    }

    private static String getConnectionUrl(String fileName) {
        return "jdbc:sqlite:" + fileName;
    }

}
