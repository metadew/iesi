package io.metadew.iesi.connection.database.connection;

import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.database.ScriptRunner;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.*;
import java.sql.*;

/**
 * Connection object for databases. This is extended depending on the database
 * type.
 *
 * @author peter.billen
 */
public abstract class DatabaseConnection {

    @SuppressWarnings("unused")
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

    public Connection getConnection() throws SQLException {
//		System.out.println("Creating connection " + connectionURL + " " + userName + " " + userPassword);
        return DriverManager.getConnection(connectionURL, userName, userPassword);
    }

    // Illegal character manipulation
    private String removeIllgegalCharactersForSingleQuery(String input) {
        input = input.trim();
        if (input.endsWith(";")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }

    // database interactions
    public CachedRowSet executeQuery(String query) {

        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);
        CachedRowSet crs = null;

        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
            throw new RuntimeException(e.getMessage());
        }

        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);

                try {
                    ResultSet rs = statement.executeQuery(query);
                    crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    rs.close();
                } catch (Exception e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Query Actions Failed");
                    System.out.println(e.getMessage());
                    System.out.println(query);
                    throw new RuntimeException(e.getMessage());
                }

                statement.close();

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database actions Failed");
                System.out.println(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
        }

        return crs;
    }

    public CachedRowSet executeQueryLimitRows(String query, int limit) {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);

        CachedRowSet crs = null;

        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
            throw new RuntimeException(e.getMessage());
        }

        Connection connection;

        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                statement.setMaxRows(limit);

                try {
                    ResultSet rs = statement.executeQuery(query);
                    crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    rs.close();
                } catch (Exception e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Query Actions Failed");
                    throw new RuntimeException(e.getMessage());
                }

                statement.close();

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database actions Failed");
                System.out.println(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
        }

        return crs;
    }

    public CachedRowSet executeProcedure(String sqlProcedure, String sqlParameters) {

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

        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
            throw new RuntimeException(e.getMessage());
        }

        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlProcedureStatement.toString());
                preparedStatement.setEscapeProcessing(true);
                preparedStatement.setQueryTimeout(600);

                if (!sqlParameterValues.isEmpty()) {
                    String[] parameterValues = sqlParameterValues.split(",");
                    for (int i = 0; i < parameterValues.length; i++) {
                        preparedStatement.setString(i + 1, parameterValues[i]);
                    }
                }

                try {
                    ResultSet rs = preparedStatement.executeQuery();
                    crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    rs.close();
                } catch (Exception e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Query Actions Failed");
                    System.out.println(e.getMessage());
                    System.out.println(sqlProcedure.toString());
                    throw new RuntimeException(e.getMessage());
                }

                preparedStatement.close();

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database actions Failed");
                System.out.println(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
        }

        return crs;
    }

    public void executeUpdate(String query) {
        // Remove illegal characters at the end
        query = this.removeIllgegalCharactersForSingleQuery(query);

        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
            throw new RuntimeException(e.getMessage());
        }

        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                statement.close();
            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database Actions Failed");
                System.out.println(e.getMessage());
                System.out.println(query);
                throw new RuntimeException(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
        }
    }

    @SuppressWarnings("finally")
    public SqlScriptResult executeScript(String fileName) {
        SqlScriptResult dcSQLScriptResult = null;
        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
            throw new RuntimeException(e.getMessage());
        }

        Connection connection;

        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                //
                ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

                InputStreamReader reader;

                try {
                    reader = new InputStreamReader(new FileInputStream(fileName));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    dcSQLScriptResult = scriptRunner.runScript(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database Actions Failed");
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                    return dcSQLScriptResult;
                } catch (SQLException e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
            throw new RuntimeException("Connection lost");
        }
    }

    @SuppressWarnings("finally")
    public SqlScriptResult executeScript(InputStream inputStream) {
        SqlScriptResult dcSQLScriptResult = null;
        try {
            Class.forName(this.getDriver());
        } catch (ClassNotFoundException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("JDBC Driver Not Available");
        }

        Connection connection;

        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }

        if (connection != null) {
            try {
                //
                ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

                InputStreamReader reader;

                try {
                    reader = new InputStreamReader(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }

                try {
                    dcSQLScriptResult = scriptRunner.runScript(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database Actions Failed");
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                    return dcSQLScriptResult;
                } catch (Exception e) {
                    StringWriter StackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(StackTrace));
                    System.out.println("Connection Close Failed");
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            System.out.println("Connection lost");
            throw new RuntimeException("Connection lost");
        }
    }

    public PreparedStatement createLivePreparedStatement(Connection connection, String sqlStatement) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    public Connection createLiveConnection() {
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            System.out.println("Connection Failed");
            throw new RuntimeException(e.getMessage());
        }
        return connection;
    }

    public void closeLiveConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("Connection Close Failed");
                throw new RuntimeException(e.getMessage());
            }

        } else {
            System.out.println("Connection lost");
        }
    }


}
