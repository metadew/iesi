package io.metadew.iesi.connection.database.connection;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface IDatabaseConnectionHandler {

    public String getDriver(DatabaseConnection databaseConnection);

    public Connection getConnection(DatabaseConnection databaseConnection);

    public String removeIllgegalCharactersForSingleQuery(DatabaseConnection databaseConnection, String input);

    public CachedRowSet executeQuery(DatabaseConnection databaseConnection, String query) throws SQLException;

    public CachedRowSet executeQuery(DatabaseConnection databaseConnection, String query, Connection connection) throws SQLException;

    public CachedRowSet executeQueryLimitRows(DatabaseConnection databaseConnection, String query, int limit) throws SQLException;

    public CachedRowSet executeQueryLimitRows(DatabaseConnection databaseConnection, String query, int limit, Connection connection) throws SQLException;

    public CachedRowSet executeProcedure(DatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters) throws SQLException;

    public CachedRowSet executeProcedure(DatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters, Connection connection) throws SQLException;

    public void executeUpdate(DatabaseConnection databaseConnection, String query) throws SQLException;

    public void executeUpdate(DatabaseConnection databaseConnection, String query, Connection connection) throws SQLException;

    public void executeBatch(DatabaseConnection databaseConnection, List<String> queries) throws SQLException;

    public void executeBatch(DatabaseConnection databaseConnection, List<String> queries, Connection connection) throws SQLException;

    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, String fileName) throws SQLException, IOException;

    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, String fileName, Connection connection) throws IOException, SQLException;

    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, InputStream inputStream) throws SQLException, IOException;

    public SqlScriptResult executeScript(DatabaseConnection databaseConnection, InputStream inputStream, Connection connection) throws IOException, SQLException;

    public HikariConfig configure(DatabaseConnection databaseConnection, HikariConfig hikariConfig);
    @Deprecated
    public PreparedStatement createPreparedStatement(DatabaseConnection databaseConnection, Connection connection, String sqlStatement) throws SQLException;

    public String generateClobInsertValue(DatabaseConnection databaseConnection, String clobString);
}