package io.metadew.iesi.connection.database.connection;

import com.zaxxer.hikari.HikariConfig;
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

    public HikariConfig configure(HikariConfig hikariConfig) {
        hikariConfig.setJdbcUrl(getConnectionURL());
        hikariConfig.setUsername(getUserName());
        hikariConfig.setPassword(getUserPassword());
        hikariConfig.setAutoCommit(false);
        if (getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(getConnectionInitSql());
        }
        getSchema().ifPresent(hikariConfig::setSchema);
        return hikariConfig;
    }

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