package io.metadew.iesi.sqlinsert.hive;

import com.mockrunner.mock.jdbc.MockResultSet;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;
import io.metadew.iesi.sqlinsert.engine.OutputFile;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetInfo {

    public Engine tgt;
    //configuration
    public String schema;
    public String table;
    private FrameworkConfiguration frameworkConfiguration;

    public GetInfo(FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("hive");
        //Get ConfigFile
        this.setFrameworkConfiguration(frameworkConfiguration);
        ConfigFile cfg = new ConfigFile(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.config");
        this.schema = cfg.getProperty("schema");
        this.table = cfg.getProperty("table");
    }

    public void doExec() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("Hive JDBC Driver Not Available");
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
                Statement statement = connection.createStatement();
                String QueryString = "";
                ResultSet rs = null;

                QueryString = this.getInfoSQL();

                try {
                    rs = statement.executeQuery(QueryString);
                    //outputfile
                    OutputFile of = new OutputFile();
                    of.PrintToFile(this.getResultSet(rs), this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.txt");
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

    public ResultSet getResultSet(ResultSet rs) throws Exception {

        // create a mock result set
        MockResultSet mockResultSet = new MockResultSet("myResultSet");

        // Get result set meta data
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        // Get the column names; column indices start from 1
        for (int i = 1; i < cols + 1; i++) {
            mockResultSet.addColumn("table_schema");
            mockResultSet.addColumn("table_name");
            mockResultSet.addColumn("column_name");
            mockResultSet.addColumn("data_type");
            mockResultSet.addColumn("character_maximum_length");
            mockResultSet.addColumn("numeric_precision");
            mockResultSet.addColumn("numeric_scale");
        }


        int rsType = rs.getType();
        List<Object> dataList = new ArrayList<Object>();
        if (rsType != java.sql.ResultSet.TYPE_FORWARD_ONLY) {
            rs.beforeFirst();
        }
        while (rs.next()) {
            //String cid = rs.getString("cid");
            String name = rs.getString("col_name");
            String type = rs.getString("data_type");
            //String notnull = rs.getString("notnull");
            //String dflt_value = rs.getString("dflt_value");
            //String pk = rs.getString("pk");

            dataList.add("");
            dataList.add(this.table);
            dataList.add(name);
            dataList.add(type);
            dataList.add("");
            dataList.add("");
            dataList.add("");

            mockResultSet.addRow(dataList);
            dataList.clear();
        }

        return mockResultSet;
    }


    public String getInfoSQL() {
        String QueryString = "";

        QueryString = "describe " + this.table;

        return QueryString;
    }

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }

}