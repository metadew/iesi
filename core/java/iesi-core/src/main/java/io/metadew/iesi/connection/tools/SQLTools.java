package io.metadew.iesi.connection.tools;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandler;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

public final class SQLTools {

    private SQLTools() {
    }

    public static final DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Insert statement tools
    public static String getStringForSQL(String input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + getCleanString(input) + "'";
        }
    }


    public static String getStringFromSQLClob(Clob clob) {
        if (clob == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = clob.getCharacterStream();
            char[] cbuf = new char[2048];
            if (reader == null) {
                return null;
            }
            BufferedReader br = new BufferedReader(reader);
            int line;
            while (-1 != (line = br.read(cbuf, 0, cbuf.length))) {
                sb.append(cbuf, 0, line);
            }
            br.close();
        } catch (SQLException | IOException e) {
            // handle this exception
        }
        return sb.toString();
    }

    public static String getStringForSQLClob(String clobString, Database database) {
        try {
            Connection connection = DatabaseHandler.getInstance().getConnection(database);
            String rawClobString;
            try {
                Clob clob = connection.createClob();
                clob.setString(1, clobString);
                rawClobString = getStringFromSQLClob(clob);
            } catch (SQLException e) {
                rawClobString = clobString;
            }
            connection.close();
            return DatabaseConnectionHandler.getInstance()
                    .generateClobInsertValue(database.getDatabaseConnection(), getCleanString(rawClobString));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Clob getClobForSQL(String clobString, Database database) {
        try {
            Connection connection = database.getConnectionPool().getConnection();
            Clob clob = connection.createClob();
            clob.setString(1, clobString);
            connection.close();
            return clob;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Insert statement tools
    public static String getStringForSQL(UUID input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + getCleanString(input.toString()) + "'";
        }
    }

    public static String getStringForSQLTable(String input) {
        if (input == null) {
            return "\"null\"";
        } else {
            return "\"" + getCleanString(input) + "\"";
        }
    }

    public static String getStringForSQL(boolean input) {
        return "'" + (input ? "Y" : "N") + "'";
    }

    public static String getStringForSQL(List<String> list) {
        return list == null || list.isEmpty() ? "null" : "'" + String.join(",", list) + "'";
    }

    public static String getStringForSQL(Map<String, String> map) {
        return map == null || map.isEmpty() ? "null" : "'" + map.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(",")) + "'";
    }

    public static String getStringForSQL(Long _long) {
        return _long == null ? "null" : _long.toString();
    }

    public static String getStringForSQL(Double _double) {
        return _double == null ? "null" : _double.toString();
    }

    public static String getStringForSQL(Timestamp input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + getCleanString(input.toString()) + "'";
        }
    }

    public static String getStringForSQL(LocalDateTime input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + getCleanString(input.format(defaultDateTimeFormatter)) + "'";
        }
    }

    public static String getStringForSQL(Integer input) {
        if (input == null) {
            return "null";
        }
        return Integer.toString(input);
    }

    public static String getStringForSQL(long input) {
        return Long.toString(input);
    }

    private static String getCleanString(String input) {
        if (input == null) return null;
        return input.replace("'", "''");
    }


    // Identifier tools
    public static String getNextIdStatement(String tableName, String idFieldName) {
        String result = "";
        result += "select coalesce(max(" + idFieldName + ")+1,1) as \"" + idFieldName + "\" from " + tableName;
        return result;
    }

    public static String getLookupIdStatement(String tableName, String idFieldName, String lookupFieldName,
                                              String lookupFieldValue) {
        String result = "";
        result += "select " + idFieldName + " from " + tableName + " where " + lookupFieldName + " = '"
                + lookupFieldValue + "'";
        return result;
    }

    public static String getLookupIdStatement(String tableName, String idFieldName, String lookupWhereClause) {
        String result = "";
        if (lookupWhereClause == null) lookupWhereClause = "";
        if (lookupWhereClause.equalsIgnoreCase("")) {
            result += "select " + idFieldName + " from " + tableName;
        } else {
            result += "select " + idFieldName + " from " + tableName + " " + lookupWhereClause;
        }
        return result;
    }

    // Resultset tools
    public static int getRowCount(ResultSet rs) {
        int rowCount = -1;
        try {
            rs.last();
            rowCount = rs.getRow();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return rowCount;
    }

    // File tools
    public static String getFirstSQLStmt(String filePath, String fileName) {
        return getFirstSQLStmt(filePath + File.separator + fileName);
    }

    public static String getFirstSQLStmt(String absoluteFilePath) {
        String result = "";
        try (
                BufferedReader br = new BufferedReader(new FileReader(absoluteFilePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(";") < 0) {
                    if (!result.equalsIgnoreCase(""))
                        result = result + " ";
                    result = result + line;
                } else if (line.indexOf(";") == 0) {
                    break;
                } else if (line.indexOf(";") > 0) {
                    if (!result.equalsIgnoreCase(""))
                        result = result + " ";
                    result = result + line.substring(0, line.indexOf(";"));
                    break;
                }

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    //
    public static String getCreateStmt(ResultSetMetaData rsmd, String target, Boolean ifExists) throws SQLException {
        int cols = rsmd.getColumnCount();
        String createIfExists = "";
        if (ifExists) {
            //sqlite
            createIfExists = "Create table if not exists ";
        } else {
            //sqlite
            createIfExists = "Create table ";
        }
        StringBuilder sb = new StringBuilder(1024);
        if (cols > 0) {
            if (target != null && !target.isEmpty()) {
                sb.append(createIfExists).append(target).append(" ( ");
            } else {
                sb.append(createIfExists).append(rsmd.getTableName(1)).append(" ( ");
            }
        }
        for (int i = 1; i <= cols; i++) {
            if (i > 1) sb.append(", ");
            String columnName = rsmd.getColumnLabel(i);
            String columnType = rsmd.getColumnTypeName(i);

            sb.append(columnName).append(" ").append(columnType);

            int precision = rsmd.getPrecision(i);
            if (precision != 0) {
                sb.append("( ").append(precision).append(" )");
            }
        } // for columns
        sb.append(" ) ");

        return sb.toString();
    }

    public static String getInsertPstmt(ResultSetMetaData rsmd, String target) throws SQLException {

        int cols = rsmd.getColumnCount();

        String sql = "";
        if (target != null && !target.isEmpty()) {
            sql = "insert into " + target + " (";
        } else {
            sql = "insert into " + rsmd.getTableName(1) + " (";
        }

        // Get the column names; column indices start from 1
        for (int i = 1; i < cols + 1; i++) {
            sql = sql + rsmd.getColumnName(i);
            if (i != cols) {
                sql = sql + ",";
            }
        }
        sql = sql + ") values (";
        for (int i = 1; i < cols + 1; i++) {
            sql = sql + "?";
            if (i != cols) {
                sql = sql + ",";
            }
        }
        sql = sql + ")";

        return sql;
    }

    public static String getDropStmt(String target, boolean ifExists) {
        String sql = "";
        if (ifExists) {
            sql = "drop table if exists " + target;
        } else {
            sql = "drop table " + target;
        }
        return sql;
    }

    public static LocalDateTime getLocalDatetimeFromSql(String localDateTime) {
        return localDateTime == null ? null : LocalDateTime.parse(localDateTime, new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd[ ]['T']HH:mm:ss")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 9, true)
                .toFormatter());
    }

    public static List<String> getListFromSql(String list) {
        return list == null ? new ArrayList<>() : Arrays.stream(list.split(",")).collect(Collectors.toList());
    }

    public static boolean getBooleanFromSql(String bool) {
        return bool.equalsIgnoreCase("y");
    }

    public static Map<String, String> getMapFromSql(String map) {
        return map == null ? new HashMap<>() : Arrays.stream(map.split(",")).collect(Collectors.toMap(s -> s.split(":")[0], s -> {
            if (s.split(":").length == 2) {
                return s.split(":")[1];
            } else {
                return "";
            }
        }));
    }


    public static String getStringFromSQLClob(CachedRowSet cachedRowSet, String clobColumnName) {
        try {
            // try to convert to Clob
            Clob clob = cachedRowSet.getClob(clobColumnName);
            return SQLTools.getStringFromSQLClob(clob);
        } catch (SQLException e1) {
            // If database does not allow clob: java.sql.SQLException: Data Type Mismatch then retry with string value
            try {
                return cachedRowSet.getString(clobColumnName);
            } catch (SQLException e2) {
                throw new RuntimeException(e1);
            }
        }
    }
}
