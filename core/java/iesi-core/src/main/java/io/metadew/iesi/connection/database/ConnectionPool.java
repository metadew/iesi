package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;

public class ConnectionPool {
    private ConnectionPool() {

    }

    /*
     * Expects a config in the following format
     *
     * poolName = "test pool"
     * jdbcUrl = ""
     * maximumPoolSize = 10
     * minimumIdle = 2
     * username = ""
     * password = ""
     * cachePrepStmts = true
     * prepStmtCacheSize = 256
     * prepStmtCacheSqlLimit = 2048
     * useServerPrepStmts = true
     *
     * Let HikariCP bleed out here on purpose
     */
    public static HikariDataSource getDataSource(String poolName, String jdbcUrl, String username, String password, int maximumPoolSize, int minimumIdle, String connectionInitSql) {

        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName(poolName);
        jdbcConfig.setMaximumPoolSize(maximumPoolSize);
        jdbcConfig.setMinimumIdle(minimumIdle);
        jdbcConfig.setJdbcUrl(jdbcUrl);
        jdbcConfig.setUsername(username);
        jdbcConfig.setPassword(password);
        jdbcConfig.setAutoCommit(false);
        if (connectionInitSql != null) {
            jdbcConfig.setConnectionInitSql(connectionInitSql);
        }
        return new HikariDataSource(jdbcConfig);
    }

    public static HikariDataSource getDataSource(String poolName, String jdbcUrl, String username, String password, int maximumPoolSize, int minimumIdle, String connectionInitSql, String schema) {

        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName(poolName);
        jdbcConfig.setMaximumPoolSize(maximumPoolSize);
        jdbcConfig.setMinimumIdle(minimumIdle);
        jdbcConfig.setJdbcUrl(jdbcUrl);
        jdbcConfig.setUsername(username);
        jdbcConfig.setPassword(password);
        jdbcConfig.setAutoCommit(false);
        jdbcConfig.setSchema(schema);
        if (connectionInitSql != null) {
            jdbcConfig.setConnectionInitSql(connectionInitSql);
        }
        return new HikariDataSource(jdbcConfig);
    }
}
// {{end:poolFactory}}