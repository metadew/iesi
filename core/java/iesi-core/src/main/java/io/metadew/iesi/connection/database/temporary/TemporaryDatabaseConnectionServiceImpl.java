package io.metadew.iesi.connection.database.temporary;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Log4j2
public class TemporaryDatabaseConnectionServiceImpl implements IDatabaseConnectionService<TemporaryDatabaseConnection> {

    private static TemporaryDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static TemporaryDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemporaryDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private TemporaryDatabaseConnectionServiceImpl() {
    }

    @Override
    public String getDriver(TemporaryDatabaseConnection databaseConnection) {
        return null;
    }

    @Override
    public Connection getConnection(TemporaryDatabaseConnection databaseConnection) {
        return null;
    }

    @Override
    public String removeIllegalCharactersForSingleQuery(TemporaryDatabaseConnection databaseConnection, String input) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQuery(TemporaryDatabaseConnection databaseConnection, String query) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQuery(TemporaryDatabaseConnection databaseConnection, String query, Connection connection) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQueryLimitRows(TemporaryDatabaseConnection databaseConnection, String query, int limit) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeQueryLimitRows(TemporaryDatabaseConnection databaseConnection, String query, int limit, Connection connection) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeProcedure(TemporaryDatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public CachedRowSet executeProcedure(TemporaryDatabaseConnection databaseConnection, String sqlProcedure, String sqlParameters, Connection connection) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeUpdate(TemporaryDatabaseConnection databaseConnection, String query) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeUpdate(TemporaryDatabaseConnection databaseConnection, String query, Connection connection) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeBatch(TemporaryDatabaseConnection databaseConnection, List<String> queries) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public void executeBatch(TemporaryDatabaseConnection databaseConnection, List<String> queries, Connection connection) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabaseConnection databaseConnection, String fileName) throws SQLException, IOException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabaseConnection databaseConnection, String fileName, Connection connection) throws IOException, SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabaseConnection databaseConnection, InputStream inputStream) throws SQLException, IOException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public SqlScriptResult executeScript(TemporaryDatabaseConnection databaseConnection, InputStream inputStream, Connection connection) throws IOException, SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public HikariConfig configure(TemporaryDatabaseConnection databaseConnection, HikariConfig hikariConfig) {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public PreparedStatement createPreparedStatement(TemporaryDatabaseConnection databaseConnection, Connection connection, String sqlStatement) throws SQLException {
        throw new RuntimeException("Cannot execute queries for a temporary database");
    }

    @Override
    public Class<TemporaryDatabaseConnection> appliesTo() {
        return TemporaryDatabaseConnection.class;
    }

    @Override
    public String refactorLimitAndOffset(TemporaryDatabaseConnection databaseConnection, String query) {
        return query;
    }
}