/*
 * Creation Date: 19-JUL-2013
 * Update Date: 19-JUL-2013
 * Version: 1
 */

package io.metadew.iesi.sqlinsert;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Entry point for launching the sqlinsert functionality.
 *
 * @author peter.billen
 */
public class SQLInsert {

    public static void main(String[] args) throws InterruptedException, IOException {

        //Get ConfigFile
        FrameworkConfiguration frameworkConfiguration = new FrameworkConfiguration();
        ConfigFile cfg = new ConfigFile(frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "service.config");
        ConfigFile eng = new ConfigFile(frameworkConfiguration.getFolderConfiguration().getFolderAbsolutePath("modules.sqlinsert.run") + File.separator + "engine.config");
        String Type = eng.getProperty("type");
        String RDBMS = eng.getProperty("rdbms");
        String Service = cfg.getProperty("service");
        String Background = cfg.getProperty("background");

        JButton closeButton = new JButton("Close Window");

        if (!"yes".equals(Background)) {
            //Create the log window
            JFrame frame = new JFrame("SQL Insert");
            frame.setPreferredSize(new Dimension(1000, 600));
            //frame.setLocationRelativeTo(null);
            frame.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            //frame.setLayout(new FlowLayout());

            JTextArea ta = new JTextArea();
            TextAreaOutputStream taos = new TextAreaOutputStream(ta, 60);
            PrintStream ps = new PrintStream(taos);
            System.setOut(ps);
            System.setErr(ps);

            c.gridwidth = 1000;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.ipady = 500;
            c.weightx = 1;
            c.weighty = 1;
            c.gridx = 0;
            c.gridy = 0;
            frame.add(new JScrollPane(ta), c);

            closeButton.setPreferredSize(new Dimension(100, 100));
            closeButton.setEnabled(false);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            c.gridwidth = 1000;
            c.ipady = 100;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.weighty = 1;
            c.gridx = 0;
            c.gridy = 1;
            frame.add(closeButton, c);

            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);

        }

        //JDBC Connections
        if (Type.equals("JDBC")) {
            if (Service.equals("info")) {
                if (RDBMS.equals("MySQL")) {
                    io.metadew.iesi.sqlinsert.mysql.GetInfo getInfo = new io.metadew.iesi.sqlinsert.mysql.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("Netezza")) {
                    io.metadew.iesi.sqlinsert.netezza.GetInfo getInfo = new io.metadew.iesi.sqlinsert.netezza.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("Oracle")) {
                    io.metadew.iesi.sqlinsert.oracle.GetInfo getInfo = new io.metadew.iesi.sqlinsert.oracle.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("SQLite")) {
                    io.metadew.iesi.sqlinsert.sqlite.GetInfo getInfo = new io.metadew.iesi.sqlinsert.sqlite.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("SQLServer")) {
                    io.metadew.iesi.sqlinsert.sqlserver.GetInfo getInfo = new io.metadew.iesi.sqlinsert.sqlserver.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("PGSQL")) {
                    io.metadew.iesi.sqlinsert.pgsql.GetInfo getInfo = new io.metadew.iesi.sqlinsert.pgsql.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else if (RDBMS.equals("Hive")) {
                    io.metadew.iesi.sqlinsert.hive.GetInfo getInfo = new io.metadew.iesi.sqlinsert.hive.GetInfo(frameworkConfiguration);
                    getInfo.doExec();
                    System.out.println("Info Service Action Completed");
                } else {
                    System.out.println("RDBMS Not Supported");
                    System.exit(1);
                }
            } else if (Service.equals("exec")) {
                if (RDBMS.equals("MySQL")) {
                    io.metadew.iesi.sqlinsert.mysql.RunScript runScript = new io.metadew.iesi.sqlinsert.mysql.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else if (RDBMS.equals("Netezza")) {
                    io.metadew.iesi.sqlinsert.netezza.RunScript runScript = new io.metadew.iesi.sqlinsert.netezza.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else if (RDBMS.equals("Oracle")) {
                    io.metadew.iesi.sqlinsert.oracle.RunScript runScript = new io.metadew.iesi.sqlinsert.oracle.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else if (RDBMS.equals("SQLite")) {
                    io.metadew.iesi.sqlinsert.sqlite.RunScript runScript = new io.metadew.iesi.sqlinsert.sqlite.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else if (RDBMS.equals("SQLServer")) {
                    io.metadew.iesi.sqlinsert.sqlserver.RunScript runScript = new io.metadew.iesi.sqlinsert.sqlserver.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else if (RDBMS.equals("PGSQL")) {
                    io.metadew.iesi.sqlinsert.pgsql.RunScript runScript = new io.metadew.iesi.sqlinsert.pgsql.RunScript(frameworkConfiguration);
                    runScript.doExec();
                } else {
                    System.out.println("RDBMS Not Supported");
                    System.exit(1);
                }
            } else if (Service.equals("data")) {
                if (RDBMS.equals("MySQL")) {
                    io.metadew.iesi.sqlinsert.mysql.GetData getData = new io.metadew.iesi.sqlinsert.mysql.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("Netezza")) {
                    io.metadew.iesi.sqlinsert.netezza.GetData getData = new io.metadew.iesi.sqlinsert.netezza.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("Oracle")) {
                    io.metadew.iesi.sqlinsert.oracle.GetData getData = new io.metadew.iesi.sqlinsert.oracle.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("SQLite")) {
                    io.metadew.iesi.sqlinsert.sqlite.GetData getData = new io.metadew.iesi.sqlinsert.sqlite.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("SQLServer")) {
                    io.metadew.iesi.sqlinsert.sqlserver.GetData getData = new io.metadew.iesi.sqlinsert.sqlserver.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("PGSQL")) {
                    io.metadew.iesi.sqlinsert.pgsql.GetData getData = new io.metadew.iesi.sqlinsert.pgsql.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else if (RDBMS.equals("Hive")) {
                    io.metadew.iesi.sqlinsert.hive.GetData getData = new io.metadew.iesi.sqlinsert.hive.GetData(frameworkConfiguration);
                    getData.doExec();
                    System.out.println("Data Service Action Completed");
                } else {
                    System.out.println("RDBMS Not Supported");
                    System.exit(1);
                }
            } else if (Service.equals("buffer")) {
                if (RDBMS.equals("MySQL")) {
                    io.metadew.iesi.sqlinsert.mysql.BufferData buf = new io.metadew.iesi.sqlinsert.mysql.BufferData(frameworkConfiguration);
                    buf.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("Netezza")) {
                    io.metadew.iesi.sqlinsert.netezza.BufferData getData = new io.metadew.iesi.sqlinsert.netezza.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("Oracle")) {
                    io.metadew.iesi.sqlinsert.oracle.BufferData getData = new io.metadew.iesi.sqlinsert.oracle.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("SQLite")) {
                    io.metadew.iesi.sqlinsert.sqlite.BufferData getData = new io.metadew.iesi.sqlinsert.sqlite.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("SQLServer")) {
                    io.metadew.iesi.sqlinsert.sqlserver.BufferData getData = new io.metadew.iesi.sqlinsert.sqlserver.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("PGSQL")) {
                    io.metadew.iesi.sqlinsert.pgsql.BufferData getData = new io.metadew.iesi.sqlinsert.pgsql.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else if (RDBMS.equals("Hive")) {
                    io.metadew.iesi.sqlinsert.hive.BufferData getData = new io.metadew.iesi.sqlinsert.hive.BufferData(frameworkConfiguration);
                    getData.getBuffer();
                    System.out.println("Buffer Action Completed");
                } else {
                    System.out.println("RDBMS Not Supported");
                    System.exit(1);
                }
            } else {
                System.out.println("Service Not Supported");
                System.exit(1);
            }
        } else if (Type.equals("ODBC")) {
            if (Service.equals("info")) {
                io.metadew.iesi.sqlinsert.odbc.GetInfo getInfo = new io.metadew.iesi.sqlinsert.odbc.GetInfo(RDBMS, frameworkConfiguration);
                getInfo.doExec();
                System.out.println("Info Service Action Completed");
            } else if (Service.equals("exec")) {
                io.metadew.iesi.sqlinsert.odbc.RunScript runScript = new io.metadew.iesi.sqlinsert.odbc.RunScript(frameworkConfiguration);
                runScript.doExec();
            } else if (Service.equals("data")) {
                io.metadew.iesi.sqlinsert.odbc.GetData getData = new io.metadew.iesi.sqlinsert.odbc.GetData(frameworkConfiguration);
                getData.doExec();
                System.out.println("Data Service Action Completed");
            } else if (Service.equals("buffer")) {
                System.out.println("Service Not Supported");
                System.exit(1);
            } else {
                System.out.println("Service Not Supported");
                System.exit(1);
            }
        } else {
            System.out.println("Type Not Supported");
            System.exit(1);
        }

        if (!"yes".equals(Background)) {
            closeButton.setEnabled(true);
        }

    }

}