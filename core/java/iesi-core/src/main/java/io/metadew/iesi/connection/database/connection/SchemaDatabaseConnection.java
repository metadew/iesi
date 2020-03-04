package io.metadew.iesi.connection.database.connection;

import lombok.Setter;

import java.util.Optional;

/**
 * Connection object for databases. This is extended depending on the database
 * type.
 *
 * @author peter.billen
 */
public abstract class SchemaDatabaseConnection extends DatabaseConnection {

    @Setter
    private String schema;

    public SchemaDatabaseConnection(String type, String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
        this.schema = null;
    }
    public SchemaDatabaseConnection(String type, String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword);
        this.schema = schema;
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }
}