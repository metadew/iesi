package io.metadew.iesi.connection.database.connection.mysql;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection object for MySQL databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class MysqlDatabaseConnection extends SchemaDatabaseConnection {

    private static Logger LOGGER = LogManager.getLogger();
    private static String type = "mysql";

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String schemaName, String userName,
                                   String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, schemaName), userName, userPassword);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String schemaName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:mysql://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (!schemaName.isEmpty()) {
            connectionUrl.append("/");
            connectionUrl.append(schemaName);
        }

        return connectionUrl.toString();
    }

    @Override
    public String getDriver() {
        return "com.mysql.jdbc.Driver";
    }

    public Connection getConnection() {
        try {
            Connection connection = super.getConnection();
            //Setting this sql_mode to avoid issues with zero dates
            connection.createStatement().execute("SET SQL_MODE='ALLOW_INVALID_DATES';");
            return connection;
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }
}
