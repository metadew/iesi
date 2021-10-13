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

interface IDatabaseHandler {

    Database getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection);

    Connection getConnection(Database database) throws SQLException;

    void releaseConnection(Database database, Connection connection);

    void shutdown(Database database);

    String getSystemTimestampExpression(Database database);

    String getAllTablesQuery(Database database, String pattern);

    Connection getLiveConnection(Database database);

    void executeUpdate(Database database, String query);

    CachedRowSet executeQuery(Database database, String query);

    void executeBatch(Database database, List<String> queries);

    CachedRowSet executeQuery(Database database, String query, Connection connection);

    CachedRowSet executeQueryLimitRows(Database database, String query, int limit);

    CachedRowSet executeQueryLimitRows(Database database, String query, int limit, Connection connection);

    SqlScriptResult executeScript(Database database, String filename);

    SqlScriptResult executeScript(Database database, String filename, Connection connection);

    SqlScriptResult executeScript(Database database, InputStream inputStream);

    SqlScriptResult executeScript(Database database, InputStream inputStream, Connection connection);

    CachedRowSet executeProcedure(Database database, String sqlProcedure, String sqlParameters);

    boolean isInitializeConnectionPool(Database database);

    HikariDataSource createConnectionPool(Database database, DatabaseConnection databaseConnection);

    String getCreateStatement(Database database, MetadataTable table, String tableNamePrefix);

    String getDeleteStatement(Database database, MetadataTable table);

    String getDropStatement(Database database, MetadataTable table);

    String getCreateStatement(Database database, MetadataTable table);

    Optional<String> getPrimaryKeyConstraints(Database database, MetadataTable metadataTable);

    void dropTable(Database database, MetadataTable table);

    void cleanTable(Database database, MetadataTable table);

    void createTable(Database database, MetadataTable table);

    String createQueryExtras(Database database);

    boolean addComments(Database database);

    String toQueryString(Database database, MetadataField field);

}