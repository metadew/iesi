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

    public T getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection);

    public String keyword();

    public Connection getConnection(T database) throws SQLException;

    public HikariDataSource createConnectionPool(T database, DatabaseConnection databaseConnection);

    public void releaseConnection(T database, Connection connection);

    public void shutdown(T database);

    public boolean isInitializeConnectionPool();

    public String getSystemTimestampExpression(T database);

    public String getAllTablesQuery(T database, String pattern);

    public Connection getLiveConnection(T database);

    public void executeUpdate(T database, String query);

    public CachedRowSet executeQuery(T database, String query);

    public void executeBatch(T database, List<String> queries);

    public CachedRowSet executeQuery(T database, String query, Connection connection);

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit);

    public CachedRowSet executeQueryLimitRows(T database, String query, int limit, Connection connection);

    public SqlScriptResult executeScript(T database, String filename);

    public SqlScriptResult executeScript(T database, String filename, Connection connection);

    public SqlScriptResult executeScript(T database, InputStream inputStream);

    public SqlScriptResult executeScript(T database, InputStream inputStream, Connection connection);

    public CachedRowSet executeProcedure(T database, String sqlProcedure, String sqlParameters);

    // TODO: remove
    public String getCreateStatement(T database, MetadataTable table, String tableNamePrefix);

    public String getDeleteStatement(T database, MetadataTable table);

    public String getDropStatement(T database, MetadataTable table);

    public String getCreateStatement(T database, MetadataTable table);

    Optional<String> getPrimaryKeyConstraints(T database, MetadataTable metadataTable);

    Optional<String> getUniqueConstraints(T database, MetadataTable metadataTable);

    public void dropTable(T database, MetadataTable table);

    public void cleanTable(T database, MetadataTable table);

    public void createTable(T database, MetadataTable table);

    public String createQueryExtras(T database);

    public boolean addComments(T database);

    public String toQueryString(T database, MetadataField field);

    public String fieldNameToQueryString(T database, String fieldName);

    public Class<T> appliesTo();

}