package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public abstract class DatabaseServiceImpl<T extends Database> implements DatabaseService<T> {

    public Connection getConnection(T database) {
        synchronized (database.getConnectionPoolLock()) {
            if (database.getConnectionPool().isEmpty()) {
                if (database.getUsedConnections().size() < database.getMaximalPoolSize()) {
                    database.getConnectionPool().add(DatabaseConnectionHandlerImpl.getInstance().getConnection(database.getDatabaseConnection()));
                } else {
                    throw new RuntimeException("Maximum pool size reached, no available connections!");
                }
            }
            Connection connection = database.getConnectionPool().remove(database.getConnectionPool().size() - 1);
            database.getUsedConnections().add(connection);
            return connection;
        }
    }

    public boolean releaseConnection(T database, Connection connection) {
        synchronized (database.getConnectionPoolLock()) {
            try {
                if (database.getConnectionPool().size() > database.getInitialPoolSize()) {
                    connection.close();
                } else {
                    database.getConnectionPool().add(connection);
                }
                return database.getUsedConnections().remove(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown(T database) {
        try {
            for (int i = 0; i < database.getUsedConnections().size(); i++) {
                releaseConnection(database, database.getUsedConnections().get(0));
            }
            for (Connection c : database.getConnectionPool()) {
                c.close();
            }
            database.getConnectionPool().clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getLiveConnection(T database) {
        return DatabaseConnectionHandlerImpl.getInstance().getConnection(database.getDatabaseConnection());
    }

    public void executeUpdate(T database, String query) {
        Connection connection = getConnection(database);
        try {
            DatabaseConnectionHandlerImpl.getInstance().executeUpdate(database.getDatabaseConnection(), query, connection);
            connection.commit();
            releaseConnection(database, connection);
        } catch (SQLException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQuery(T database, String query) {
        Connection connection = getConnection(database);
        try {
            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeQuery(database.getDatabaseConnection(), query, connection);
            connection.commit();
            releaseConnection(database, connection);
            return cachedRowSet;
        } catch (SQLException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public void executeBatch(T database, List<String> queries) {
        Connection connection = getConnection(database);
        try {
            DatabaseConnectionHandlerImpl.getInstance().executeBatch(database.getDatabaseConnection(), queries, connection);
            connection.commit();
            releaseConnection(database, connection);
        } catch (SQLException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQuery(T database, String query, Connection connection) {
        try {
            return DatabaseConnectionHandlerImpl.getInstance().executeQuery(database.getDatabaseConnection(), query, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit) {
        Connection connection = getConnection(database);
        try {
            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeQueryLimitRows(database.getDatabaseConnection(), query, limit, connection);
            connection.commit();
            releaseConnection(database, connection);
            return cachedRowSet;
        } catch (SQLException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit, Connection connection) {
        try {
            return DatabaseConnectionHandlerImpl.getInstance().executeQueryLimitRows(database.getDatabaseConnection(), query, limit, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, String filename) {
        Connection connection = getConnection(database);
        try {
            SqlScriptResult sqlScriptResult = DatabaseConnectionHandlerImpl.getInstance().executeScript(database.getDatabaseConnection(), filename, connection);
            connection.commit();
            releaseConnection(database, connection);
            return sqlScriptResult;
        } catch (SQLException | IOException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, String filename, Connection connection) {
        try {
            return DatabaseConnectionHandlerImpl.getInstance().executeScript(database.getDatabaseConnection(), filename, connection);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, InputStream inputStream) {
        Connection connection = getConnection(database);
        try {
            SqlScriptResult sqlScriptResult = DatabaseConnectionHandlerImpl.getInstance().executeScript(database.getDatabaseConnection(), inputStream, connection);
            connection.commit();
            releaseConnection(database, connection);
            return sqlScriptResult;
        } catch (SQLException | IOException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    public SqlScriptResult executeScript(T database, InputStream inputStream, Connection connection) {
        try {
            return DatabaseConnectionHandlerImpl.getInstance().executeScript(database.getDatabaseConnection(), inputStream, connection);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CachedRowSet executeProcedure(T database, String sqlProcedure, String sqlParameters) {
        Connection connection = getConnection(database);
        try {
            CachedRowSet cachedRowSet = DatabaseConnectionHandlerImpl.getInstance().executeProcedure(database.getDatabaseConnection(), sqlProcedure, sqlParameters, connection);
            connection.commit();
            releaseConnection(database, connection);
            return cachedRowSet;
        } catch (SQLException e) {
            releaseConnection(database, connection);
            throw new RuntimeException(e);
        }
    }

    // TODO: remove
    public String getCreateStatement(T database, MetadataTable table, String tableNamePrefix) {
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

            createQuery.append(toQueryString(database, field));
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

            createQuery.append(toQueryString(database, field));
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
        getPrimaryKeyConstraints(database, table).ifPresent(primaryKeysConstraint -> createQuery.append(",\n").append(primaryKeysConstraint));

        createQuery.append("\n);\n");
        createQuery.append(createQueryExtras(database));
        // createQuery.append(fieldComments).append("\n\n");

        return createQuery.toString();
    }

    public Optional<String> getPrimaryKeyConstraints(T database, MetadataTable metadataTable) {
        List<MetadataField> primaryKeyMetadataFields = metadataTable.getFields().stream()
                .filter(MetadataField::isPrimaryKey)
                .collect(Collectors.toList());
        if (primaryKeyMetadataFields.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of("CONSTRAINT pk_" + metadataTable.getName() + " PRIMARY KEY (" + primaryKeyMetadataFields.stream().map(MetadataField::getName).collect(Collectors.joining(", ")) + ")");
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

}