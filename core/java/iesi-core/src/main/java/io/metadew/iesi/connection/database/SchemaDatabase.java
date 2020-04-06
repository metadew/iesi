package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class SchemaDatabase extends Database {

    private String schema;

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, String schema) {
        super(schemaDatabaseConnection);
        schemaDatabaseConnection.setSchema(schema);
        this.schema = schema;
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection) {
        this(schemaDatabaseConnection, null);
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, int initialPoolSize, int maximalPoolSize,  String schema) {
        super(schemaDatabaseConnection, initialPoolSize, maximalPoolSize);
        schemaDatabaseConnection.setSchema(schema);
        this.schema = schema;
    }

    public SchemaDatabase(SchemaDatabaseConnection schemaDatabaseConnection, int initialPoolSize, int maximalPoolSize) {
        this(schemaDatabaseConnection, initialPoolSize, maximalPoolSize, null);
    }
   
    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
