package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Database {

    private static final int DEFAULT_INITIAL_POOL_SIZE = 4;
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final Logger LOGGER = LogManager.getLogger();
    private final int initialPoolSize;
    private final int maximalPoolSize;

    private DatabaseConnection databaseConnection;
    private HikariDataSource connectionPool;

    public Database(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.maximalPoolSize = DEFAULT_MAX_POOL_SIZE;
        this.initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
        if (isInitializeConnectionPool()) {
            initializeConnectionPool(databaseConnection);
        }
    }

    public boolean isInitializeConnectionPool() {
        return true;
    }

    public Database(DatabaseConnection databaseConnection, int initialPoolSize, int maximalPoolSize) {
        this.databaseConnection = databaseConnection;
        this.initialPoolSize = initialPoolSize;
        this.maximalPoolSize = maximalPoolSize;
        if (isInitializeConnectionPool()) {
            initializeConnectionPool(databaseConnection);
        }
    }

    private void initializeConnectionPool(DatabaseConnection databaseConnection) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(UUID.randomUUID().toString());
        hikariConfig.setMaximumPoolSize(maximalPoolSize);
        hikariConfig.setMinimumIdle(initialPoolSize);
        hikariConfig.setAutoCommit(false);
        databaseConnection.configure(hikariConfig);
        connectionPool = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                LOGGER.info("sql.exception=" + e);
                LOGGER.debug("sql.exception.stacktrace=" + stackTrace.toString());
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    public abstract String getSystemTimestampExpression();

    public abstract String getAllTablesQuery(String pattern);

    public Connection getLiveConnection() {
        return databaseConnection.getConnection();
    }

    public void executeUpdate(String query) {
        Connection connection = null;
        try {
            connection = getConnection();
            this.databaseConnection.executeUpdate(query, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("sql.exception=" + e);
            LOGGER.debug("sql.exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            LOGGER.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    public CachedRowSet executeQuery(String query) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection();
            cachedRowSet = databaseConnection.executeQuery(query, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            LOGGER.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
        return cachedRowSet;
    }

    public void executeBatch(List<String> queries) {
        Connection connection = null;
        try {
            connection = getConnection();
            databaseConnection.executeBatch(queries, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("exception.sql=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    public CachedRowSet executeQuery(String query, Connection connection) {
        try {
            return this.databaseConnection.executeQuery(query, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            LOGGER.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection();
            cachedRowSet = databaseConnection.executeQueryLimitRows(query, limit, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            LOGGER.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
        return cachedRowSet;
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit, Connection connection) {
        try {
            return this.databaseConnection.executeQueryLimitRows(query, limit, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            LOGGER.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(String filename) {
        Connection connection = null;
        SqlScriptResult sqlScriptResult;
        try {
            connection = getConnection();
            sqlScriptResult = databaseConnection.executeScript(filename, connection);
            connection.commit();
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(String filename, Connection connection) {
        try {
            return this.databaseConnection.executeScript(filename, connection);
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(InputStream inputStream) {
        Connection connection = null;
        SqlScriptResult sqlScriptResult;
        try {
            connection = getConnection();
            sqlScriptResult = databaseConnection.executeScript(inputStream, connection);
            connection.commit();
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(InputStream inputStream, Connection connection) {
        try {
            return this.databaseConnection.executeScript(inputStream, connection);
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection();
            cachedRowSet = databaseConnection.executeProcedure(sqlProcedure, sqlParameters, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            releaseConnection(connection);
        }
        return cachedRowSet;
    }

    // TODO: remove
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        StringBuilder createQuery = new StringBuilder();
        // StringBuilder fieldComments = new StringBuilder();
        String tableName = tableNamePrefix + table.getName();

        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
        int counter = 1;
        for (Map.Entry<String, MetadataField> field : table.getFields().entrySet()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(field.getKey());

            int tabNumber = 1;
            if (field.getKey().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getKey().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(field.getValue()));
            /*
             * TODO create comment syntax inside subclasses returning stringbuilder rather
             * than just a boolean
             *
             * if (addComments() && field.getDescription().isPresent()) {
             * fieldComments.append("\nCOMMENT ON COLUMN ").append(tableName).append(".").
             * append(field.getScriptName())
             * .append(" IS '").append(field.getDescription().get()).append("';"); }
             */
            counter++;
        }

        createQuery.append("\n)\n").append(createQueryExtras()).append(";");
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public String getDeleteStatement(MetadataTable table) {
        return "delete from " + table.getName() + ";";
    }

    public String getDropStatement(MetadataTable table) {
        return "drop table " + table.getName() + ";";
    }

    public String getCreateStatement(MetadataTable table) {
        StringBuilder createQuery = new StringBuilder();
        // StringBuilder fieldComments = new StringBuilder();
        String tableName = table.getName();
        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
        int counter = 1;
        for (Map.Entry<String, MetadataField> field : table.getFields().entrySet()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(field.getKey());

            int tabNumber = 1;
            if (field.getKey().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getKey().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(field.getValue()));
            /*
             * TODO create comment syntax inside subclasses returning stringbuilder rather
             * than just a boolean
             *
             * if (addComments() && field.getDescription().isPresent()) {
             * fieldComments.append("\nCOMMENT ON COLUMN ").append(tableName).append(".").
             * append(field.getScriptName())
             * .append(" IS '").append(field.getDescription().get()).append("';"); }
             */
            counter++;
        }
        getPrimaryKeyConstraints(table).ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));

        createQuery.append("\n);\n");
        createQuery.append(createQueryExtras());
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    Optional<String> getPrimaryKeyConstraints(MetadataTable metadataTable) {
        Map<String, MetadataField> primaryKeyMetadataFields = metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getValue().isPrimaryKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT pk_" + metadataTable.getName() + " PRIMARY KEY (" + String.join(", ", primaryKeyMetadataFields.keySet()) + ")");
        }
    }

    public void dropTable(MetadataTable table) {
        executeUpdate(getDropStatement(table));
    }

    public void cleanTable(MetadataTable table) {
        executeUpdate(getDeleteStatement(table));
    }

    public void createTable(MetadataTable table) {
        executeUpdate(getCreateStatement(table));
    }

    public abstract String createQueryExtras();

    public abstract boolean addComments();

    public abstract String toQueryString(MetadataField field);

    protected DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

}