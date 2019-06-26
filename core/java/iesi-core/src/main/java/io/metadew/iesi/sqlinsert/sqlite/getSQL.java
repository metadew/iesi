package io.metadew.iesi.sqlinsert.sqlite;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class getSQL {

    public getSQL() {

    }

    public String getCreateStmt(ResultSetMetaData rsmd, String target, Boolean ifExists) throws SQLException {
        int cols = rsmd.getColumnCount();
        String CreateIfExists = "";
        if (ifExists) {
            //sqlite
            CreateIfExists = "Create table if not exists ";
        } else {
            //sqlite
            CreateIfExists = "Create table ";
        }
        StringBuilder sb = new StringBuilder(1024);
        if (cols > 0) {
            if (target != null && !target.isEmpty()) {
                sb.append(CreateIfExists).append(target).append(" ( ");
            } else {
                sb.append(CreateIfExists).append(rsmd.getTableName(1)).append(" ( ");
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

    public String getInsertPstmt(ResultSetMetaData rsmd, String target) throws SQLException {

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

    public String getDropStmt(String target, boolean ifExists) {
        String sql = "";
        if (ifExists) {
            sql = "drop table if exists " + target;
        } else {
            sql = "drop table " + target;
        }
        return sql;
    }

}
