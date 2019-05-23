package io.metadew.iesi.connection.tools.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.tools.SQLTools;

import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public final class SQLDataTransfer {

    // Insert statement tools
    public static void transferData(CachedRowSet crs, Database targetDatabase, String name, boolean cleanPrevious) throws SQLException {

        String QueryString = "";
        java.sql.Connection liveTargetDatabaseConnection = null;
        try {
            // Get result set meta data
            ResultSetMetaData rsmd = crs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Determine name
            if (name == null || name.isEmpty()) {
                name = rsmd.getTableName(1);
            }

            // Cleaning
            if (cleanPrevious) {
                QueryString = SQLTools.getDropStmt(name, true);
                targetDatabase.executeUpdate(QueryString);
            }

            // create the dataset table if needed
            QueryString = SQLTools.getCreateStmt(rsmd, name, true);
            targetDatabase.executeUpdate(QueryString);

            String temp = "";
            String sql = SQLTools.getInsertPstmt(rsmd, name);
            liveTargetDatabaseConnection = targetDatabase.getLiveConnection();
            PreparedStatement preparedStatement = liveTargetDatabaseConnection.prepareStatement(sql);

            int crsType = crs.getType();
            if (crsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
                crs.beforeFirst();
            }

            while (crs.next()) {
                for (int i = 1; i < cols + 1; i++) {
                    temp = crs.getString(i);
                    preparedStatement.setString(i, temp);
                }
                preparedStatement.executeUpdate();
            }

            crs.close();
        } catch (Exception e) {
            throw new RuntimeException("sql.data.transfer.failed");
        } finally {
            liveTargetDatabaseConnection.close();
        }


    }


}
