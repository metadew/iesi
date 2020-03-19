package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
@Getter
public abstract class Database {

    private static final int DEFAULT_INITIAL_POOL_SIZE = 4;
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final Logger LOGGER = LogManager.getLogger();

    private DatabaseConnection databaseConnection;
    private int initialPoolSize;
    private int maximalPoolSize;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private final Object connectionPoolLock = new Object();

    public Database(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
        maximalPoolSize = DEFAULT_MAX_POOL_SIZE;
        connectionPool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(DatabaseConnectionHandlerImpl.getInstance().getConnection(databaseConnection));
        }
    }

    public Database(DatabaseConnection databaseConnection, int initialPoolSize, int maximalPoolSize) {
        this.databaseConnection = databaseConnection;
        this.initialPoolSize = initialPoolSize;
        this.maximalPoolSize = maximalPoolSize;
        connectionPool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(DatabaseConnectionHandlerImpl.getInstance().getConnection(databaseConnection));
        }
    }

//    public Connection getConnection() {
//        synchronized (this.connectionPoolLock) {
//            if (connectionPool.isEmpty()) {
//                if (usedConnections.size() < maximalPoolSize) {
//                    connectionPool.add(DatabaseConnectionHandlerImpl.getInstance().getConnection(databaseConnection));
//                } else {
//                    throw new RuntimeException("Maximum pool size reached, no available connections!");
//                }
//            }
//            Connection connection = connectionPool.remove(connectionPool.size() - 1);
//            usedConnections.add(connection);
//            return connection;
//        }
//    }
//
//    public boolean releaseConnection(Connection connection) {
//        synchronized (this.connectionPoolLock) {
//            try {
//                if (connectionPool.size() > initialPoolSize) {
//                    connection.close();
//                } else {
//                    connectionPool.add(connection);
//                }
//                return usedConnections.remove(connection);
//            } catch (SQLException e) {
//                StringWriter stackTrace = new StringWriter();
//                e.printStackTrace(new PrintWriter(stackTrace));
//                LOGGER.info("exception=" + e);
//                LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//                LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void shutdown() {
//        try {
//            for (int i = 0; i < usedConnections.size(); i++) {
//                releaseConnection(usedConnections.get(0));
//            }
//            for (Connection c : connectionPool) {
//                c.close();
//            }
//            connectionPool.clear();
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public abstract String getSystemTimestampExpression();
//
//    public abstract String getAllTablesQuery(String pattern);
//
//    public Connection getLiveConnection() {
//        return DatabaseConnectionHandlerImpl.getInstance().getConnection(databaseConnection);
//    }
//
//    public void executeUpdate(String query) {
//        Connection connection = getConnection();
//        try {
//            DatabaseConnectionHandlerImpl.getInstance().executeUpdate(databaseConnection, query, connection);
//            connection.commit();
//            releaseConnection(connection);
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("sql.exception=" + e);
//            LOGGER.debug("sql.exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            LOGGER.debug("sql.exception.query=" + query);
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public CachedRowSet executeQuery(String query) {
//        Connection connection = getConnection();
//        try {
//            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeQuery(databaseConnection, query, connection);
//            connection.commit();
//            releaseConnection(connection);
//            return cachedRowSet;
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            LOGGER.debug("sql.exception.query=" + query);
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void executeBatch(List<String> queries) {
//        Connection connection = getConnection();
//        try {
//            DatabaseConnectionHandlerImpl.getInstance().executeBatch(databaseConnection, queries, connection);
//            connection.commit();
//            releaseConnection(connection);
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("exception.sql=" + databaseConnection.getConnectionURL());
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public CachedRowSet executeQuery(String query, Connection connection) {
//        try {
//            return DatabaseConnectionHandlerImpl.getInstance().executeQuery(databaseConnection, query, connection);
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            LOGGER.debug("sql.exception.query=" + query);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public CachedRowSet executeQueryLimitRows(String query, int limit) {
//        Connection connection = getConnection();
//        try {
//            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeQueryLimitRows(databaseConnection, query, limit, connection);
//            connection.commit();
//            releaseConnection(connection);
//            return cachedRowSet;
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            LOGGER.debug("sql.exception.query=" + query);
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public CachedRowSet executeQueryLimitRows(String query, int limit, Connection connection) {
//        try {
//            return DatabaseConnectionHandlerImpl.getInstance().executeQueryLimitRows(databaseConnection, query, limit, connection);
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            LOGGER.debug("sql.exception.query=" + query);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public SqlScriptResult executeScript(String filename) {
//        Connection connection = getConnection();
//        try {
//            SqlScriptResult sqlScriptResult = DatabaseConnectionHandlerImpl.getInstance().executeScript(databaseConnection, filename, connection);
//            connection.commit();
//            releaseConnection(connection);
//            return sqlScriptResult;
//        } catch (SQLException | IOException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public SqlScriptResult executeScript(String filename, Connection connection) {
//        try {
//            return DatabaseConnectionHandlerImpl.getInstance().executeScript(databaseConnection, filename, connection);
//        } catch (SQLException | IOException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public SqlScriptResult executeScript(InputStream inputStream) {
//        Connection connection = getConnection();
//        try {
//            SqlScriptResult sqlScriptResult = DatabaseConnectionHandlerImpl.getInstance().executeScript(databaseConnection, inputStream, connection);
//            connection.commit();
//            releaseConnection(connection);
//            return sqlScriptResult;
//        } catch (SQLException | IOException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public SqlScriptResult executeScript(InputStream inputStream, Connection connection) {
//        try {
//            return DatabaseConnectionHandlerImpl.getInstance().executeScript(databaseConnection, inputStream, connection);
//        } catch (SQLException | IOException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters) {
//        Connection connection = getConnection();
//        try {
//            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeProcedure(databaseConnection, sqlProcedure, sqlParameters, connection);
//            connection.commit();
//            releaseConnection(connection);
//            return cachedRowSet;
//        } catch (SQLException e) {
//            StringWriter stackTrace = new StringWriter();
//            e.printStackTrace(new PrintWriter(stackTrace));
//            LOGGER.info("exception=" + e);
//            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
//            LOGGER.debug("sql.exception.db=" + databaseConnection.getConnectionURL());
//            releaseConnection(connection);
//            throw new RuntimeException(e);
//        }
//    }
//
//    // TODO: remove
//    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
//        StringBuilder createQuery = new StringBuilder();
//        // StringBuilder fieldComments = new StringBuilder();
//        String tableName = tableNamePrefix + table.getName();
//
//        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
//        int counter = 1;
//        for (MetadataField field : table.getFields()) {
//            if (counter > 1) {
//                createQuery.append(",\n");
//            }
//            createQuery.append("\t").append(field.getName());
//
//            int tabNumber = 1;
//            if (field.getName().length() >= 8) {
//                tabNumber = (int) (4 - Math.ceil((double) field.getName().length() / 8));
//            } else {
//                tabNumber = 4;
//            }
//
//            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//                createQuery.append("\t");
//            }
//
//            createQuery.append(toQueryString(field));
//            /*
//             * TODO create comment syntax inside subclasses returning stringbuilder rather
//             * than just a boolean
//             *
//             * if (addComments() && field.getDescription().isPresent()) {
//             * fieldComments.append("\nCOMMENT ON COLUMN ").append(tableName).append(".").
//             * append(field.getScriptName())
//             * .append(" IS '").append(field.getDescription().get()).append("';"); }
//             */
//            counter++;
//        }
//
//        createQuery.append("\n)\n").append(createQueryExtras()).append(";");
//        // createQuery.append(fieldComments).append("\n\n");
//
//        return createQuery.toString();
//    }
//
//    public String getDeleteStatement(MetadataTable table) {
//        return "delete from " + table.getName() + ";";
//    }
//
//    public String getDropStatement(MetadataTable table) {
//        return "drop table " + table.getName() + ";";
//    }
//
//    public String getCreateStatement(MetadataTable table) {
//        StringBuilder createQuery = new StringBuilder();
//        // StringBuilder fieldComments = new StringBuilder();
//        String tableName = table.getName();
//        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
//        int counter = 1;
//        for (MetadataField field : table.getFields()) {
//            if (counter > 1) {
//                createQuery.append(",\n");
//            }
//            createQuery.append("\t").append(field.getName());
//
//            int tabNumber = 1;
//            if (field.getName().length() >= 8) {
//                tabNumber = (int) (4 - Math.ceil((double) field.getName().length() / 8));
//            } else {
//                tabNumber = 4;
//            }
//
//            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
//                createQuery.append("\t");
//            }
//
//            createQuery.append(toQueryString(field));
//            /*
//             * TODO create comment syntax inside subclasses returning stringbuilder rather
//             * than just a boolean
//             *
//             * if (addComments() && field.getDescription().isPresent()) {
//             * fieldComments.append("\nCOMMENT ON COLUMN ").append(tableName).append(".").
//             * append(field.getScriptName())
//             * .append(" IS '").append(field.getDescription().get()).append("';"); }
//             */
//            counter++;
//        }
//        getPrimaryKeyConstraints(table).ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));
//
//        createQuery.append("\n);\n");
//        createQuery.append(createQueryExtras());
//        // createQuery.append(fieldComments).append("\n\n");
//
//        return createQuery.toString();
//    }
//
//    Optional<String> getPrimaryKeyConstraints(MetadataTable metadataTable) {
//        List<MetadataField> primaryKeyMetadataFields = metadataTable.getFields().stream()
//                .filter(MetadataField::isPrimaryKey)
//                .collect(Collectors.toList());
//        if (primaryKeyMetadataFields.isEmpty()) {
//            return Optional.empty();
//        } else {
//            return Optional.of("CONSTRAINT pk_" + metadataTable.getName() + " PRIMARY KEY (" + primaryKeyMetadataFields.stream().map(MetadataField::getName).collect(Collectors.joining(", ")) + ")");
//        }
//    }
//
//    public void dropTable(MetadataTable table) {
//        executeUpdate(getDropStatement(table));
//    }
//
//    public void cleanTable(MetadataTable table) {
//        executeUpdate(getDeleteStatement(table));
//    }
//
//    public void createTable(MetadataTable table) {
//        executeUpdate(getCreateStatement(table));
//    }
//
//    public abstract String createQueryExtras();
//
//    public abstract boolean addComments();
//
//    public abstract String toQueryString(MetadataField field);
//
//    protected DatabaseConnection getDatabaseConnection() {
//        return databaseConnection;
//    }

}