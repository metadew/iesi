package io.metadew.iesi.connection.database.connection;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.database.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Connection object for databases. This is extended depending on the database
 * type.
 *
 * @author peter.billen
 */
public abstract class DatabaseConnection {

    private static Logger LOGGER = LogManager.getLogger();

    private String type;
    private String connectionURL;
    private String userName;
    private String userPassword;

    public DatabaseConnection(String type, String connectionURL, String userName, String userPassword) {
        this.type = type;
        this.connectionURL = connectionURL;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public abstract String getDriver();

//    public String prepareQuery(String query) {
//        return query;
//    }

    public Connection getConnection() {
        try {
            Class.forName(getDriver());
            Connection connection = DriverManager.getConnection(connectionURL, userName, userPassword);
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.info("exception=" + e);
            LOGGER.debug("exception.stacktrace=" + stackTrace.toString());
            throw new RuntimeException(e);
        }
    }

    // Illegal character manipulation
    private String removeIllgegalCharactersForSingleQuery(String input) {
        input = input.trim();
        if (input.endsWith(";")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }

    public CachedRowSet executeQuery(String query) throws SQLException {
        Connection connection = getConnection();
        CachedRowSet cachedRowSet = executeQuery(query, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeQuery(String query, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        LOGGER.info(connectionURL + ":" + query);
        ResultSet resultSet = statement.executeQuery(query);
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(resultSet);
        resultSet.close();
        statement.close();

        return crs;
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit) throws SQLException {
        Connection connection = getConnection();
        CachedRowSet cachedRowSet = executeQueryLimitRows(query, limit, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);
        // query = prepareQuery(query);

        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        statement.setMaxRows(limit);

        LOGGER.info(connectionURL + ":" + query);
        ResultSet rs = statement.executeQuery(query);
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs);
        rs.close();

        statement.close();

        return crs;

    }

    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters) throws SQLException {
        Connection connection = getConnection();
        CachedRowSet cachedRowSet = executeProcedure(sqlProcedure, sqlParameters, connection);
        connection.close();
        return cachedRowSet;
    }

    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters, Connection connection) throws SQLException {
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

    public void executeUpdate(String query) throws SQLException {
            Connection connection = getConnection();
            executeUpdate(query, connection);
            connection.close();
    }

    public void executeUpdate(String query, Connection connection) throws SQLException {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);
        // query = prepareQuery(query);
        LOGGER.info(connectionURL + ":" + query);

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    public void executeBatch(List<String> queries) throws SQLException {
        Connection connection = getConnection();
        executeBatch(queries, connection);
        connection.close();
    }

    public void executeBatch(List<String> queries, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        for (String query : queries) {
            query = this.removeIllgegalCharactersForSingleQuery(query);
            // query = prepareQuery(query);
            LOGGER.info(connectionURL + ":" + query);
            statement.addBatch(query);
        }
        statement.executeBatch();
        statement.close();
    }

    public SqlScriptResult executeScript(String fileName) throws SQLException, IOException {
        Connection connection = getConnection();
        SqlScriptResult sqlScriptResult = executeScript(fileName, connection);
        connection.close();
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(String fileName, Connection connection) throws IOException, SQLException {
        SqlScriptResult dcSQLScriptResult;

        //
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

        InputStreamReader reader;

        reader = new InputStreamReader(new FileInputStream(fileName));

        dcSQLScriptResult = scriptRunner.runScript(reader);

        return dcSQLScriptResult;
    }

    public SqlScriptResult executeScript(InputStream inputStream) throws SQLException, IOException {
        Connection connection = getConnection();
        SqlScriptResult sqlScriptResult = executeScript(inputStream, connection);
        connection.close();
        return sqlScriptResult;
    }

    public SqlScriptResult executeScript(InputStream inputStream, Connection connection) throws IOException, SQLException {
        SqlScriptResult sqlScriptResult;
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
        InputStreamReader reader;
        reader = new InputStreamReader(inputStream);
        sqlScriptResult = scriptRunner.runScript(reader);
        return sqlScriptResult;
    }

    @Deprecated
    public PreparedStatement createPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        return connection.prepareStatement(sqlStatement);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConnectionURL() {
        return connectionURL;
    }
}