package io.metadew.iesi.connection.database.connection.h2;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for H2 databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class H2DatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "h2";

    public H2DatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public H2DatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

    @Override
    public String getDriver() {
        return "org.h2.Driver";
    }

    public Connection getConnection() {
        try {
            Connection connection = super.getConnection();

            Optional<String> schema = getSchema();
            if (schema.isPresent()) {
                connection.createStatement().execute("SET SCHEMA " + schema.get());
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
