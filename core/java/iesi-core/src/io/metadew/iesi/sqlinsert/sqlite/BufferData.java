package io.metadew.iesi.sqlinsert.sqlite;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;

import java.io.File;
import java.sql.*;

public class BufferData {

    public Engine tgt;
    // configuration
    private FrameworkConfiguration frameworkConfiguration;
    public String sql;
    public String localdb;
    public String dataset;
    public boolean dropPrev;

    public BufferData(FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("sqlite");
        // Get ConfigFile
        this.setFrameworkConfiguration(frameworkConfiguration);
        ConfigFile cfg = new ConfigFile(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "buffer.config");
        this.sql = cfg.getProperty("sql");
        this.localdb = cfg.getProperty("localdb");
        this.dataset = cfg.getProperty("dataset");
        String sTemp = cfg.getProperty("dropprev");
        if (sTemp.equalsIgnoreCase("no")) {
            this.dropPrev = false;
        } else {
            this.dropPrev = true;
        }
    }

    public void getBuffer() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver Not Available");
            e.printStackTrace();
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(tgt.connectionURL, tgt.user, tgt.password);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = null;
                String QueryString = "";

                QueryString = this.sql;

                try {
                    rs = statement.executeQuery(QueryString);
                    this.setBuffer(rs);
                    rs.close();
                } catch (Exception e) {
                    System.out.println(QueryString);
                    System.out.println("Query Actions Failed");
                    e.printStackTrace();
                }

                statement.close();

            } catch (SQLException e) {
                System.out.println("database Actions Failed");
                e.printStackTrace();
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Connection CLose Failed");
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("Connection Lost");
        }

    }

    public void setBuffer(ResultSet rs) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver Not Available");
            e.printStackTrace();
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.localdb, "", "");
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            try {
                getSQL sqlTools = new getSQL();

                Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                String QueryString = "";

                try {
                    // Get result set meta data
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int cols = rsmd.getColumnCount();

                    if (this.dropPrev) {
                        QueryString = sqlTools.getDropStmt(this.dataset, true);
                        statement.executeUpdate(QueryString);
                    }

                    // create the dataset table if needed
                    QueryString = sqlTools.getCreateStmt(rsmd, this.dataset, true);
                    statement.executeUpdate(QueryString);

                    String temp = "";
                    String sql = sqlTools.getInsertPstmt(rsmd, this.dataset);
                    PreparedStatement pstmt = connection.prepareStatement(sql);

                    int rsType = rs.getType();
                    if (rsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
                        rs.beforeFirst();
                    }

                    while (rs.next()) {
                        for (int i = 1; i < cols + 1; i++) {
                            temp = rs.getString(i);
                            pstmt.setString(i, temp);
                        }
                        pstmt.executeUpdate();
                    }
                } catch (Exception e) {
                    System.out.println(QueryString);
                    System.out.println("Query Actions Failed");
                    e.printStackTrace();
                }

                statement.close();

            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Connection CLose Failed");
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("Connection Lost");
        }

    }

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }
}