package io.metadew.iesi.connection.database.connection.sqlite;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;

import java.util.regex.Pattern;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

    private final Pattern caseWhenPattern = Pattern.compile("WHEN\\s(?<condition>[\\s_\\w\\.]+?)\\sTHEN\\s(?<result>[\\w\\.]+)\\s");
    private final Pattern conditionPattern = Pattern.compile("(?<left>[\\w\\._]+)\\s(?<right>.+)");

    private static String type = "sqlite";

    public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public SqliteDatabaseConnection(String fileName) {
        super(type, getConnectionUrl(fileName), "", "");
    }

    public static String getConnectionUrl(String fileName) {
        return "jdbc:sqlite:" + fileName;
    }

}
