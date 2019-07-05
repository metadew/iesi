package io.metadew.iesi.sqlinsert.netezza;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;
import io.metadew.iesi.sqlinsert.engine.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RunScript {

    public Engine tgt;
    public ConfigFile cfg;
    private FrameworkConfiguration frameworkConfiguration;

    public RunScript(FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("netezza");
        this.setFrameworkConfiguration(frameworkConfiguration);
        this.cfg = new ConfigFile(this.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "exec.config");
    }

    public void doExec() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("Netezza JDBC Driver Not Available");
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
                //engine.ScriptRunner runner = new ScriptRunner(connection, [booleanAutoCommit], [booleanStopOnerror]);
                ScriptRunner runner = new ScriptRunner(connection, false, false);

                InputStreamReader reader = null;

                try {
                    reader = new InputStreamReader(new FileInputStream(cfg.getProperty("file")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    runner.runScript(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                }


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