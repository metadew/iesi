package io.metadew.iesi.connection.database.connection.oracle;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Oracle databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class OracleDatabaseConnection extends SchemaDatabaseConnection {

    private static Logger LOGGER = LogManager.getLogger();
    private static String type = "oracle";

    public OracleDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public OracleDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public static String getConnectionUrl(String hostName, int portNumber, String serviceName, String tnsAlias) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:oracle:thin:");
        if (!serviceName.isEmpty()) {
            connectionUrl.append(":@//");
            connectionUrl.append(hostName);

            if (portNumber > 0) {
                connectionUrl.append(":");
                connectionUrl.append(portNumber);
            }

            connectionUrl.append("/");
            connectionUrl.append(serviceName);
        } else if (!tnsAlias.isEmpty()) {
            connectionUrl.append(hostName);

            if (portNumber > 0) {
                connectionUrl.append(":");
                connectionUrl.append(portNumber);
            }

            connectionUrl.append(":");
            connectionUrl.append(tnsAlias);
        } else {
            throw new RuntimeException("Unable to build connection url");
        }

        return connectionUrl.toString();
    }


    @Override
    public String getDriver() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    public Connection getConnection() {
        try {
            Connection connection = super.getConnection();
            Optional<String> schema = getSchema();
            if (schema.isPresent()) {
                LOGGER.info(getConnectionURL() + ":setting schema to " + schema.get());
                connection.createStatement()
                        .execute("alter session set current_schema=" + schema.get());
                // connection.setSchema(schema.get());
            }
            connection.createStatement().execute("alter session set nls_timestamp_format='YYYY-MM-DD\"T\" HH24:MI:SS:FF'");
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
