package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandler;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

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

@Log4j2
public abstract class DatabaseService<T extends Database> implements IDatabaseService<T> {

    public Connection getConnection(T database) throws SQLException {
        if (database.getConnectionPool() == null) {
            database.setConnectionPool(DatabaseHandler.getInstance().createConnectionPool(database, database.getDatabaseConnection()));
        }
        return database.getConnectionPool().getConnection();
    }

    public void releaseConnection(T database, Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                log.info("sql.exception=" + e);
                log.debug("sql.exception.stacktrace=" + stackTrace.toString());
                throw new RuntimeException(e);
            }
        }
    }

    public HikariDataSource createConnectionPool(T database, DatabaseConnection databaseConnection) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(UUID.randomUUID().toString());
        hikariConfig.setMaximumPoolSize(database.getMaximalPoolSize());
        hikariConfig.setMinimumIdle(database.getInitialPoolSize());
        hikariConfig.setAutoCommit(false);
        DatabaseConnectionHandler.getInstance().configure(databaseConnection, hikariConfig);
        return new HikariDataSource(hikariConfig);
    }

    public boolean isInitializeConnectionPool() {
        return true;
    }


    public void shutdown(T database) {
        if (database.getConnectionPool() != null && !database.getConnectionPool().isClosed()) {
            database.getConnectionPool().close();
        }
    }

    public Connection getLiveConnection(T database) {
        return DatabaseConnectionHandler.getInstance().getConnection(database.getDatabaseConnection());
    }

    public void executeUpdate(T database, String query) {
        Connection connection = null;
        try {
            connection = getConnection(database);
            DatabaseConnectionHandler.getInstance().executeUpdate(database.getDatabaseConnection(), query, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("sql.exception=" + e);
            log.debug("sql.exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            log.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
    }

    public CachedRowSet executeQuery(T database, String query) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection(database);
            cachedRowSet = DatabaseConnectionHandler.getInstance().executeQuery(database.getDatabaseConnection(), query, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            log.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
        return cachedRowSet;
    }

    public void executeBatch(T database, List<String> queries) {
        Connection connection = null;
        try {
            connection = getConnection(database);
            DatabaseConnectionHandler.getInstance().executeBatch(database.getDatabaseConnection(), queries, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("exception.sql=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
    }

    public CachedRowSet executeQuery(T database, String query, Connection connection) {
        try {
            return DatabaseConnectionHandler.getInstance().executeQuery(database.getDatabaseConnection(), query, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            log.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection(database);
            cachedRowSet = DatabaseConnectionHandler.getInstance().executeQueryLimitRows(database.getDatabaseConnection(), query, limit, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            log.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
        return cachedRowSet;
    }

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit, Connection connection) {
        try {
            return DatabaseConnectionHandler.getInstance().executeQueryLimitRows(database.getDatabaseConnection(), query, limit, connection);
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            log.debug("sql.exception.query=" + query);
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, String filename) {
        Connection connection = null;
        SqlScriptResult sqlScriptResult;
        try {
            connection = getConnection(database);
            sqlScriptResult = DatabaseConnectionHandler.getInstance().executeScript(database.getDatabaseConnection(), filename, connection);
            connection.commit();
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(T database, String filename, Connection connection) {
        try {
            return DatabaseConnectionHandler.getInstance().executeScript(database.getDatabaseConnection(), filename, connection);
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, InputStream inputStream) {
        Connection connection = null;
        SqlScriptResult sqlScriptResult;
        try {
            connection = getConnection(database);
            sqlScriptResult = DatabaseConnectionHandler.getInstance().executeScript(database.getDatabaseConnection(), inputStream, connection);
            connection.commit();
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(T database, InputStream inputStream, Connection connection) {
        try {
            return DatabaseConnectionHandler.getInstance().executeScript(database.getDatabaseConnection(), inputStream, connection);
        } catch (SQLException | IOException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeProcedure(T database, String sqlProcedure, String sqlParameters) {
        Connection connection = null;
        CachedRowSet cachedRowSet;
        try {
            connection = getConnection(database);
            cachedRowSet = DatabaseConnectionHandler.getInstance().executeProcedure(database.getDatabaseConnection(), sqlProcedure, sqlParameters, connection);
            connection.commit();
        } catch (SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            log.debug("sql.exception.db=" + database.getDatabaseConnection().getConnectionURL());
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                releaseConnection(database, connection);
            }
        }
        return cachedRowSet;
    }

    // TODO: remove
    public String getCreateStatement(T database, MetadataTable table, String tableNamePrefix) {
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

            createQuery.append(toQueryString(database, field.getValue()));
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

        createQuery.append("\n)\n").append(createQueryExtras(database)).append(";");
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public String getDeleteStatement(T database, MetadataTable table) {
        return "delete from " + table.getName() + ";";
    }

    public String getDropStatement(T database, MetadataTable table) {
        return "drop table " + table.getName() + ";";
    }

    public String getCreateStatement(T database, MetadataTable table) {
        StringBuilder createQuery = new StringBuilder();
        // StringBuilder fieldComments = new StringBuilder();
        createQuery.append("CREATE TABLE ").append(table.getName()).append("\n(\n");
        int counter = 1;
        for (Map.Entry<String, MetadataField> field : table.getFields().entrySet()) {
            if (counter > 1) {
                createQuery.append(",\n");
            }
            createQuery.append("\t").append(fieldNameToQueryString(database, field.getKey()));

            int tabNumber = 1;
            if (field.getKey().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil((double) field.getKey().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                createQuery.append("\t");
            }

            createQuery.append(toQueryString(database, field.getValue()));
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
        getPrimaryKeyConstraints(database, table)
                .ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));
        getUniqueConstraints(database, table)
                .ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));

        createQuery.append("\n);\n");
        createQuery.append(createQueryExtras(database));
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public Optional<String> getPrimaryKeyConstraints(T database, MetadataTable metadataTable) {
        Map<String, MetadataField> primaryKeyMetadataFields = metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getValue().isPrimaryKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT pk_" + metadataTable.getName() + " PRIMARY KEY (" + String.join(", ", primaryKeyMetadataFields.keySet().stream()
                    .map(s -> fieldNameToQueryString(database, s))
                    .collect(Collectors.toSet())) + ")");
        }
    }

    public Optional<String> getUniqueConstraints(T database, MetadataTable metadataTable) {
        Map<String, MetadataField> primaryKeyMetadataFields = metadataTable.getFields().entrySet().stream()
                .filter(entry -> entry.getValue().isUnique())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT uc_" + metadataTable.getName() + " UNIQUE  (" + String.join(", ", primaryKeyMetadataFields.keySet().stream()
                    .map(s -> fieldNameToQueryString(database, s)).collect(Collectors.toSet())) + ")");
        }
    }

    public void dropTable(T database, MetadataTable table) {
        executeUpdate(database, getDropStatement(database, table));
    }

    public void cleanTable(T database, MetadataTable table) {
        executeUpdate(database, getDeleteStatement(database, table));
    }

    public void createTable(T database, MetadataTable table) {
        executeUpdate(database, getCreateStatement(database, table));
    }


    public String fieldNameToQueryString(T database, String fieldName) {
        return fieldName;
    }

}