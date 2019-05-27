package io.metadew.iesi.sqlinsert.mysql;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;
import io.metadew.iesi.sqlinsert.engine.OutputFile;

import java.io.File;
import java.sql.*;

public class GetData {

    public Engine tgt;
    // configuration
    public String sql;
    private FrameworkConfiguration frameworkConfiguration;

    public GetData(FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("mysql");
        // Get ConfigFile
        this.setFrameworkConfiguration(frameworkConfiguration);
        ConfigFile cfg = new ConfigFile(
                this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "data.config");
        this.sql = cfg.getProperty("sql");
    }

    public void doExec() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver Not Available");
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
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                String QueryString = "";
                ResultSet rs = null;

                QueryString = this.sql;

                try {
                    rs = statement.executeQuery(QueryString);
                    // outputfile
                    OutputFile of = new OutputFile();
                    of.PrintToFile(rs, this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "data.txt");
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

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }

}