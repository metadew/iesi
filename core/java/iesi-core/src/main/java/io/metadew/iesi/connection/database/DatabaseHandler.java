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

public interface DatabaseHandler {

    public Database getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection);

    public Connection getConnection(Database database) throws SQLException;

    public void releaseConnection(Database database, Connection connection);

    public void shutdown(Database database);

    public String getSystemTimestampExpression(Database database);

    public String getAllTablesQuery(Database database, String pattern);

    public Connection getLiveConnection(Database database);

    public void executeUpdate(Database database, String query);

    public CachedRowSet executeQuery(Database database, String query);

    public void executeBatch(Database database, List<String> queries);

    public CachedRowSet executeQuery(Database database, String query, Connection connection);

    public CachedRowSet executeQueryLimitRows(Database database, String query, int limit);

    public CachedRowSet executeQueryLimitRows(Database database, String query, int limit, Connection connection);

    public SqlScriptResult executeScript(Database database, String filename);

    public SqlScriptResult executeScript(Database database, String filename, Connection connection);

    public SqlScriptResult executeScript(Database database, InputStream inputStream);

    public SqlScriptResult executeScript(Database database, InputStream inputStream, Connection connection);

    public CachedRowSet executeProcedure(Database database, String sqlProcedure, String sqlParameters);

    public boolean isInitializeConnectionPool(Database database);

    public HikariDataSource createConnectionPool(Database database, DatabaseConnection databaseConnection);

    public String getCreateStatement(Database database, MetadataTable table, String tableNamePrefix);

    public String getDeleteStatement(Database database, MetadataTable table);

    public String getDropStatement(Database database, MetadataTable table);

    public String getCreateStatement(Database database, MetadataTable table);

    Optional<String> getPrimaryKeyConstraints(Database database, MetadataTable metadataTable);

    public void dropTable(Database database, MetadataTable table);

    public void cleanTable(Database database, MetadataTable table);

    public void createTable(Database database, MetadataTable table);

    public String createQueryExtras(Database database);

    public boolean addComments(Database database);

    public String toQueryString(Database database, MetadataField field);

}