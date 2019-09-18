package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {

    private static final int DEFAULT_INITIAL_POOL_SIZE = 4;
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final Logger LOGGER = LogManager.getLogger();

    DatabaseConnection databaseConnection;
    private int initialPoolSize;
    private int maximalPoolSize;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();

    public Database(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
        maximalPoolSize = DEFAULT_MAX_POOL_SIZE;
        connectionPool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(databaseConnection.getConnection());
        }
    }

    public Connection getConnection() {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < maximalPoolSize) {
                connectionPool.add(databaseConnection.getConnection());
            } else {
                throw new RuntimeException("Maximum pool size reached, no available connections!");
            }
        }
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        try {
            if (connectionPool.size() > initialPoolSize) {
                connection.close();
            } else {
                connectionPool.add(connection);
            }
            return usedConnections.remove(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            for (int i = 0; i < usedConnections.size(); i++) {
                releaseConnection(usedConnections.get(0));
            }
            for (Connection c : connectionPool) {
                c.close();
            }
            connectionPool.clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getSystemTimestampExpression();

    public abstract String getAllTablesQuery(String pattern);

    public Connection getLiveConnection() {
        return databaseConnection.getConnection();
    }

    public void executeUpdate(String query) {
        Connection connection = getConnection();
        try {
            this.databaseConnection.executeUpdate(query, connection);
            connection.commit();
            releaseConnection(connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
            releaseConnection(connection);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQuery(String query) {
        Connection connection = getConnection();
        try {
            CachedRowSet cachedRowSet = this.databaseConnection.executeQuery(query, connection);
            connection.commit();
            releaseConnection(connection);
            return cachedRowSet;
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
            releaseConnection(connection);
            throw new RuntimeException(e);
        }
    }

    public void executeBatch(List<String> queries) {
        Connection connection = getConnection();
        try {
            databaseConnection.executeBatch(queries, connection);
            connection.commit();
            releaseConnection(connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
            releaseConnection(connection);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQuery(String query, Connection connection) {
        return this.databaseConnection.executeQuery(query, connection);
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit) {
        try {
            Connection connection = getConnection();
            CachedRowSet cachedRowSet = databaseConnection.executeQueryLimitRows(query, limit, connection);
            connection.commit();
            releaseConnection(connection);
            return cachedRowSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit, Connection connection) {
        return this.databaseConnection.executeQueryLimitRows(query, limit, connection);
    }

    public SqlScriptResult executeScript(String filename) {
        Connection connection = getConnection();
        try {
            SqlScriptResult sqlScriptResult = databaseConnection.executeScript(filename, connection);
            connection.commit();
            releaseConnection(connection);
            return sqlScriptResult;
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace);
            releaseConnection(connection);
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(String filename, Connection connection) {
        return this.databaseConnection.executeScript(filename, connection);
    }

    public SqlScriptResult executeScript(InputStream inputStream) {
        try {
            Connection connection = getConnection();
            SqlScriptResult sqlScriptResult = databaseConnection.executeScript(inputStream, connection);
            connection.commit();
            releaseConnection(connection);
            return sqlScriptResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(InputStream inputStream, Connection connection) {
        return this.databaseConnection.executeScript(inputStream, connection);
    }

    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters) {
        try {
            Connection connection = getConnection();
            CachedRowSet cachedRowSet = databaseConnection.executeProcedure(sqlProcedure, sqlParameters, connection);
            connection.commit();
            releaseConnection(connection);
            return cachedRowSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: remove
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        StringBuilder createQuery = new StringBuilder();
        // StringBuilder fieldComments = new StringBuilder();

        String tableName = tableNamePrefix + table.getName();

        createQuery.append("CREATE TABLE ").append(tableName).append("\n(\n");
        int counter = 1;
        for (MetadataField field : table.getFields()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(field.getName());

            int tabNumber = 1;
            if (field.getName().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getName().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(field));
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
        for (MetadataField field : table.getFields()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(field.getName());

            int tabNumber = 1;
            if (field.getName().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getName().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(field));
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

        createQuery.append("\n);\n");
        createQuery.append(createQueryExtras());
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
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

}