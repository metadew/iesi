package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Postgresql databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class PostgresqlDatabaseConnection extends DatabaseConnection {

    private static String type = "postgresql";
    private String schema;

    public PostgresqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public PostgresqlDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                        String userPassword) {
        super(type, "jdbc:postgresql://" + hostName + ":" + portNumber + "/" + databaseName, userName, userPassword);
    }


    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    private Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        Optional<String> schema = getSchema();
        if (schema.isPresent()) {
            connection.setSchema(schema.get());
        }
        return connection;
    }
}
