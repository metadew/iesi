package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EqualsAndHashCode
@ToString
@Getter
public abstract class Database {

    private static final int DEFAULT_INITIAL_POOL_SIZE = 4;
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final Logger LOGGER = LogManager.getLogger();
    private final int initialPoolSize;
    private final int maximalPoolSize;

    private DatabaseConnection databaseConnection;
    @EqualsAndHashCode.Exclude
    private HikariDataSource connectionPool;

    public Database(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.maximalPoolSize = DEFAULT_MAX_POOL_SIZE;
        this.initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
        if (DatabaseHandlerImpl.getInstance().isInitializeConnectionPool(this)) {
            this.connectionPool = DatabaseHandlerImpl.getInstance().createConnectionPool(this, databaseConnection);;
        }
    }

    public Database(DatabaseConnection databaseConnection, int initialPoolSize, int maximalPoolSize) {
        this.databaseConnection = databaseConnection;
        this.initialPoolSize = initialPoolSize;
        this.maximalPoolSize = maximalPoolSize;
        if (DatabaseHandlerImpl.getInstance().isInitializeConnectionPool(this)) {
            this.connectionPool = DatabaseHandlerImpl.getInstance().createConnectionPool(this, databaseConnection);;
        }
    }

}