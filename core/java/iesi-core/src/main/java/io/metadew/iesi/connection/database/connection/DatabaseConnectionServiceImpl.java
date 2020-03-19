package io.metadew.iesi.connection.database.connection;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.database.ScriptRunner;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.*;
import java.sql.*;
import java.util.List;

@Log4j2
public abstract class DatabaseConnectionServiceImpl<T extends DatabaseConnection> implements DatabaseConnectionService<T> {

    public Connection getConnection(T databaseConnection) {
        try {
            Class.forName(getDriver(databaseConnection));
            Connection connection = DriverManager.getConnection(databaseConnection.getConnectionURL(), databaseConnection.getUserName(), databaseConnection.getUserPassword());
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            log.info("exception=" + e);
            log.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }

    public String removeIllgegalCharactersForSingleQuery(T databaseConnection, String input) {
        input = input.trim();
        if (input.endsWith(";")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }

    public CachedRowSet executeQuery(T databaseConnection, String query) throws SQLException {
        Connection connection = getConnection(databaseConnection);
        CachedRowSet cachedRowSet = executeQuery(databaseConnection, query, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeQuery(T databaseConnection, String query, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(databaseConnection, query);
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        log.info(databaseConnection.getConnectionURL() + ":" + query);
        ResultSet resultSet = statement.executeQuery(query);
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(resultSet);
        resultSet.close();
        statement.close();
        return crs;
    }

    public CachedRowSet executeQueryLimitRows(T databaseConnection, String query, int limit) throws SQLException {
        Connection connection = getConnection(databaseConnection);
        CachedRowSet cachedRowSet = executeQueryLimitRows(databaseConnection, query, limit, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeQueryLimitRows(T databaseConnection, String query, int limit, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(databaseConnection, query);
        // query = prepareQuery(query);

        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        statement.setMaxRows(limit);

        log.info(databaseConnection.getConnectionURL() + ":" + query);
        ResultSet rs = statement.executeQuery(query);
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs);
        rs.close();

        statement.close();

        return crs;
    }

    public CachedRowSet executeProcedure(T databaseConnection, String sqlProcedure, String sqlParameters) throws SQLException {
        Connection connection = getConnection(databaseConnection);
        CachedRowSet cachedRowSet = executeProcedure(databaseConnection, sqlProcedure, sqlParameters, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeProcedure(T databaseConnection, String sqlProcedure, String sqlParameters, Connection connection) throws SQLException {
        StringBuilder sqlProcedureStatement = new StringBuilder();
        sqlProcedureStatement.append("EXEC ");
        sqlProcedureStatement.append(sqlProcedure);

        String sqlParameterValues = "";
        if (!sqlParameters.isEmpty()) {
            String[] parameters = sqlParameters.split(",");
            for (int i = 0; i < parameters.length; i++) {
                String parameter = parameters[i];
                int delim = parameter.indexOf("=");
                if (delim > 0) {
                    if (i == 0) {
                        sqlProcedureStatement.append(" ");
                    } else {
                        sqlProcedureStatement.append(",");
                    }

                    String key = parameter.substring(0, delim);
                    String value = parameter.substring(delim + 1);
                    if (sqlParameterValues.isEmpty()) {
                        sqlParameterValues = sqlParameterValues + value;
                    } else {
                        sqlParameterValues = sqlParameterValues + "," + value;
                    }

                    sqlProcedureStatement.append(key);
                    sqlProcedureStatement.append("=?");
                } else {
                    throw new RuntimeException("sql.procedure.parameter.error");
                }
            }
        }

        CachedRowSet crs = null;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlProcedureStatement.toString());
        preparedStatement.setEscapeProcessing(true);
        preparedStatement.setQueryTimeout(600);

        if (!sqlParameterValues.isEmpty()) {
            String[] parameterValues = sqlParameterValues.split(",");
            for (int i = 0; i < parameterValues.length; i++) {
                preparedStatement.setString(i + 1, parameterValues[i]);
            }
        }

        ResultSet rs = preparedStatement.executeQuery();
        crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs);
        rs.close();

        preparedStatement.close();


        return crs;
    }

    public void executeUpdate(T databaseConnection, String query) throws SQLException {
        Connection connection = getConnection(databaseConnection);
        executeUpdate(databaseConnection, query, connection);
        connection.close();
    }

    public void executeUpdate(T databaseConnection, String query, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(databaseConnection, query);
        // query = prepareQuery(query);
        log.info(databaseConnection.getConnectionURL() + ":" + query);

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    public void executeBatch(T databaseConnection, List<String> queries) throws SQLException {
        Connection connection = getConnection(databaseConnection);
        executeBatch(databaseConnection, queries, connection);
        connection.close();
    }

    public void executeBatch(T databaseConnection, List<String> queries, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        for (String query : queries) {
            query = this.removeIllgegalCharactersForSingleQuery(databaseConnection, query);
            // query = prepareQuery(query);
            log.info(databaseConnection.getConnectionURL() + ":" + query);
            statement.addBatch(query);
        }
        statement.executeBatch();
        statement.close();
    }

    public SqlScriptResult executeScript(T databaseConnection, String fileName) throws SQLException, IOException {
        Connection connection = getConnection(databaseConnection);
        SqlScriptResult sqlScriptResult = executeScript(databaseConnection, fileName, connection);
        connection.close();
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(T databaseConnection, String fileName, Connection connection) throws IOException, SQLException {
        SqlScriptResult dcSQLScriptResult;
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName));
        dcSQLScriptResult = scriptRunner.runScript(reader);
        return dcSQLScriptResult;
    }

    public SqlScriptResult executeScript(T databaseConnection, InputStream inputStream) throws SQLException, IOException {
        Connection connection = getConnection(databaseConnection);
        SqlScriptResult sqlScriptResult = executeScript(databaseConnection, inputStream, connection);
        connection.close();
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(T databaseConnection, InputStream inputStream, Connection connection) throws IOException, SQLException {
        SqlScriptResult sqlScriptResult;
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
        InputStreamReader reader;
        reader = new InputStreamReader(inputStream);
        sqlScriptResult = scriptRunner.runScript(reader);
        return sqlScriptResult;
    }

    @Deprecated
    public PreparedStatement createPreparedStatement(T databaseConnection, Connection connection, String sqlStatement) throws SQLException {
        return connection.prepareStatement(sqlStatement);
    }

}