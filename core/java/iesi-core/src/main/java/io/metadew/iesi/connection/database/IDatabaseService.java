package io.metadew.iesi.connection.database;

import com.zaxxer.hikari.HikariDataSource;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IDatabaseService<T extends Database> {

    T getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection);

    String keyword();

    Connection getConnection(T database) throws SQLException;

    HikariDataSource createConnectionPool(T database, DatabaseConnection databaseConnection);

    void releaseConnection(T database, Connection connection);

    void shutdown(T database);

    boolean isInitializeConnectionPool();

    String getSystemTimestampExpression(T database);

    String getAllTablesQuery(T database, String pattern);

    Connection getLiveConnection(T database);

    void executeUpdate(T database, String query);

    CachedRowSet executeQuery(T database, String query);

    void executeBatch(T database, List<String> queries);

    CachedRowSet executeQuery(T database, String query, Connection connection);

    CachedRowSet executeQueryLimitRows(T database, String query, int limit);

    CachedRowSet executeQueryLimitRows(T database, String query, int limit, Connection connection);

    SqlScriptResult executeScript(T database, String filename);

    SqlScriptResult executeScript(T database, String filename, Connection connection);

    SqlScriptResult executeScript(T database, InputStream inputStream);

    SqlScriptResult executeScript(T database, InputStream inputStream, Connection connection);

    CachedRowSet executeProcedure(T database, String sqlProcedure, String sqlParameters);

    // TODO: remove
    String getCreateStatement(T database, MetadataTable table, String tableNamePrefix);

    String getDeleteStatement(T database, MetadataTable table);

    String getDropStatement(T database, MetadataTable table);

    String getCreateStatement(T database, MetadataTable table);

    Optional<String> getPrimaryKeyConstraints(T database, MetadataTable metadataTable);

    void dropTable(T database, MetadataTable table);

    void cleanTable(T database, MetadataTable table);

    void createTable(T database, MetadataTable table);

    String createQueryExtras(T database);

    boolean addComments(T database);

    String toQueryString(T database, MetadataField field);

    String fieldNameToQueryString(T database, String fieldName);

    Class<T> appliesTo();

}
