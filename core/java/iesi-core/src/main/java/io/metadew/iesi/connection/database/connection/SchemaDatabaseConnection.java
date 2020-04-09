package io.metadew.iesi.connection.database.connection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SchemaDatabaseConnection extends DatabaseConnection {

    @Setter
    private String schema;

    public SchemaDatabaseConnection(String type, String connectionURL, String userName, String userPassword, String connectionInitSql) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
        this.schema = null;
    }
    public SchemaDatabaseConnection(String type, String connectionURL, String userName, String userPassword, String connectionInitSql, String schema) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
        this.schema = schema;
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }
}