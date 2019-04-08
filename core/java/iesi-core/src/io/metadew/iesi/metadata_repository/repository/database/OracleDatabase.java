package io.metadew.iesi.metadata_repository.repository.database;

import io.metadew.iesi.framework.execution.FrameworkLog;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.repository.database.connection.OracleDatabaseConnection;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;
import java.util.Optional;

public class OracleDatabase extends Database {

    String schema;

    public OracleDatabase(OracleDatabaseConnection oracleDatabaseConnection, String schema) {
        super(oracleDatabaseConnection);
        this.schema = schema;
    }

    @Override
    public String getSystemTimestampExpression() {
        return "systimestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select OWNER, TABLE_NAME from ALL_TABLES where owner = '"
                + schema + "' and TABLE_NAME like '"
                + pattern +
                "%' order by TABLE_NAME ASC";
    }

    @Override
    public String getCreateStatement(MetadataTable table, String tableNamePrefix) {
        String sql = "";
        String fieldComments = "";
        String tempTableName = tableNamePrefix + table.getName();

        sql += "CREATE TABLE " + schema + "."
                + tempTableName;
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
                sql += "VARCHAR2";
                sql += " (";
                sql += field.getLength();
                sql += " CHAR";
                sql += ")";
            } else if (field.getType().equals("flag")) {
                sql += "CHAR";
                sql += " (";
                sql += field.getLength();
                sql += " CHAR";
                sql += ")";
            } else if (field.getType().equals("number")) {
                sql += "NUMBER";
            } else if (field.getType().equals("timestamp")) {
                sql += "TIMESTAMP (6)";
            }

            // Default DtTimestamp
            if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
                sql += " DEFAULT systimestamp";
            }

            // Nullable
            if (field.getNullable().trim().equalsIgnoreCase("n")) {
                sql += " NOT NULL";
            }

            // Add comments
            if (!field.getDescription().trim().equals("")) {
                fieldComments += "\n";
                fieldComments += "COMMENT ON COLUMN ";
                fieldComments += getSchema().orElse("") + "."
                        + tempTableName + "." + field.getName();
                fieldComments += " IS ";
                fieldComments += "'";
                fieldComments += field.getDescription();
                fieldComments += "'";
                fieldComments += ";";
            }

            counter++;
        }
        sql += "\n";
        sql += ")";
        sql += "\n";

        sql += "LOGGING";
        sql += "\n";
        sql += "NOCOMPRESS";
        sql += "\n";
        sql += "NOCACHE";
        sql += "\n";
        sql += "NOPARALLEL";
        sql += "\n";
        sql += "MONITORING;";
        sql += "\n";

        sql += fieldComments;
        sql += "\n";
        sql += "\n";

        return sql;
    }

    @Override
    public void cleanTable(String tableName, FrameworkLog frameworkLog) {
        frameworkLog.log(MessageFormat.format("metadata.clean.table={0}", getSchema().map(schema -> schema + "." + tableName).orElse(tableName)), Level.INFO);
        String query = getSchema().map(schema -> "delete from " + schema + "." + tableName).orElse("delete from " + tableName);
        databaseConnection.executeQuery(query);
    }

    @Override
    public void dropTable(String tableName, FrameworkLog frameworkLog) {
        frameworkLog.log(MessageFormat.format("metadata.drop.table={0}", getSchema().map(schema -> schema + "." + tableName).orElse(tableName)), Level.INFO);
        String query = getSchema().map(schema -> "drop table " + schema + "." + tableName).orElse("drop table " + tableName);
        databaseConnection.executeQuery(query);
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
