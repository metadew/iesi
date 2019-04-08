package io.metadew.iesi.metadata_repository.repository.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;

import javax.sql.rowset.CachedRowSet;

public class SqliteDatabase extends Database{


    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "datetime(CURRENT_TIMESTAMP, 'localtime')";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return "select tbl_name 'TABLE_NAME', '' 'OWNER' from sqlite_master where tbl_name like '"
                + pattern
                + "%' order by tbl_name asc";
    }


    @Override
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        String sql = "";
        String tempTableName = tableNamePrefix + table.getName();

        sql += "CREATE TABLE " + tempTableName;
        sql += "\n";
        sql += "(";
        sql += "\n";

        int counter = 1;
        for (MetadataField field : table.getFields()) {
            if (counter > 1) {
                sql += ",";
                sql += "\n";
            }

            sql += "\t";
            sql += field.getName();

            int tabNumber = 1;
            if (field.getName().length() >= 8) {
                tabNumber = (int) (4 - Math.ceil(field.getName().length() / 8));
            } else {
                tabNumber = 4;
            }

            for (int tabCount = 1; tabCount <= tabNumber; tabCount++) {
                sql += "\t";
            }

            // Data Types
            if (field.getType().equals("string")) {
                sql += "TEXT";
            } else if (field.getType().equals("flag")) {
                sql += "TEXT";
            } else if (field.getType().equals("number")) {
                sql += "NUMERIC";
            } else if (field.getType().equals("timestamp")) {
                sql += "TEXT";
            }

            // Nullable
            if (field.getNullable().trim().equalsIgnoreCase("n")) {
                sql += " NOT NULL";
            }

            // Default DtTimestamp
            if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
                //sql += " DEFAULT CURRENT_TIMESTAMP";
                sql += " DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))";
            }

            counter++;
        }
        sql += "\n";
        sql += ");";
        sql += "\n";

        return sql;
    }

}
