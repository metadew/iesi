package io.metadew.iesi.sqlinsert.odbc;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;
import io.metadew.iesi.sqlinsert.engine.OutputFile;

import java.io.File;
import java.sql.*;

public class GetInfo {

    public Engine tgt;
    //configuration
    private FrameworkConfiguration frameworkConfiguration;
    public String schema;
    public String table;
    public String RDBMS;

    public GetInfo(String RDBMS, FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("odbc");
        //Get ConfigFile
        this.setFrameworkConfiguration(frameworkConfiguration);
        ConfigFile cfg = new ConfigFile(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.config");
        this.schema = cfg.getProperty("schema");
        this.table = cfg.getProperty("table");
        this.RDBMS = RDBMS;
    }

    public void doExec() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC-ODBC Driver Not Available");
            e.printStackTrace();
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                    tgt.connectionURL, tgt.user,
                    tgt.password);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                String QueryString = "";
                ResultSet rs = null;

                //Split per supported RDBMS
                if (this.RDBMS.equalsIgnoreCase("MySQL")) {
                    io.metadew.iesi.sqlinsert.mysql.GetInfo getInfo = new io.metadew.iesi.sqlinsert.mysql.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("Netezza")) {
                    io.metadew.iesi.sqlinsert.netezza.GetInfo getInfo = new io.metadew.iesi.sqlinsert.netezza.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("Oracle")) {
                    io.metadew.iesi.sqlinsert.oracle.GetInfo getInfo = new io.metadew.iesi.sqlinsert.oracle.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("PGSQL")) {
                    io.metadew.iesi.sqlinsert.pgsql.GetInfo getInfo = new io.metadew.iesi.sqlinsert.pgsql.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("SQLite")) {
                    io.metadew.iesi.sqlinsert.sqlite.GetInfo getInfo = new io.metadew.iesi.sqlinsert.sqlite.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("SQLServer")) {
                    io.metadew.iesi.sqlinsert.sqlserver.GetInfo getInfo = new io.metadew.iesi.sqlinsert.sqlserver.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else if (this.RDBMS.equalsIgnoreCase("Hive")) {
                    io.metadew.iesi.sqlinsert.hive.GetInfo getInfo = new io.metadew.iesi.sqlinsert.hive.GetInfo(this.getFrameworkConfiguration());
                    QueryString = getInfo.getInfoSQL();
                } else {
                    System.out.println("RDBMS Not Supported");
                    return;
                    //to do: raise error
                }


                try {
                    rs = statement.executeQuery(QueryString);
                    //outputfile
                    OutputFile of = new OutputFile();
                    of.PrintToFile(rs, this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.txt");
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
                //Close the connection
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