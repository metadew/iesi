package io.metadew.iesi.connection.database.connection;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseConnectionService<T extends DatabaseConnection> {

    public String getDriver(T databaseConnection);

    public Connection getConnection(T databaseConnection);

    public String removeIllgegalCharactersForSingleQuery(T databaseConnection, String input);

    public CachedRowSet executeQuery(T databaseConnection, String query) throws SQLException;

    public CachedRowSet executeQuery(T databaseConnection, String query, Connection connection) throws SQLException;

    public CachedRowSet executeQueryLimitRows(T databaseConnection, String query, int limit) throws SQLException;

    public CachedRowSet executeQueryLimitRows(T databaseConnection, String query, int limit, Connection connection) throws SQLException;

    public CachedRowSet executeProcedure(T databaseConnection, String sqlProcedure, String sqlParameters) throws SQLException;

    public CachedRowSet executeProcedure(T databaseConnection, String sqlProcedure, String sqlParameters, Connection connection) throws SQLException;

    public void executeUpdate(T databaseConnection, String query) throws SQLException;

    public void executeUpdate(T databaseConnection, String query, Connection connection) throws SQLException;

    public void executeBatch(T databaseConnection, List<String> queries) throws SQLException;

    public void executeBatch(T databaseConnection, List<String> queries, Connection connection) throws SQLException;

    public SqlScriptResult executeScript(T databaseConnection, String fileName) throws SQLException, IOException;

    public SqlScriptResult executeScript(T databaseConnection, String fileName, Connection connection) throws IOException, SQLException;

    public SqlScriptResult executeScript(T databaseConnection, InputStream inputStream) throws SQLException, IOException;

    public SqlScriptResult executeScript(T databaseConnection, InputStream inputStream, Connection connection) throws IOException, SQLException;

    @Deprecated
    public PreparedStatement createPreparedStatement(T databaseConnection, Connection connection, String sqlStatement) throws SQLException;

    public Class<T> appliesTo();
}