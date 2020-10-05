package io.metadew.iesi.connection.database.connection;

import com.zaxxer.hikari.HikariConfig;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class SchemaDatabaseConnectionService<T extends SchemaDatabaseConnection> extends DatabaseConnectionService<T> implements ISchemaDatabaseConnectionService<T> {

    public HikariConfig configure(T schemaDatabaseConnection, HikariConfig hikariConfig) {
        hikariConfig.setJdbcUrl(schemaDatabaseConnection.getConnectionURL());
        hikariConfig.setUsername(schemaDatabaseConnection.getUserName());
        hikariConfig.setPassword(schemaDatabaseConnection.getUserPassword());
        hikariConfig.setAutoCommit(true);
        //hikariConfig.setAutoCommit(false);
        if (schemaDatabaseConnection.getConnectionInitSql() != null) {
            hikariConfig.setConnectionInitSql(schemaDatabaseConnection.getConnectionInitSql());
        }
        //schemaDatabaseConnection.getSchema().ifPresent(hikariConfig::setSchema);
        return hikariConfig;
    }

}