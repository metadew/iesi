package io.metadew.iesi.launch;

import io.metadew.iesi.util.harvest.DatabaseOffloadExecution;
import org.apache.commons.cli.*;

import java.sql.SQLException;

/**
 * The offload launcher is entry point to launch the data offloading utility.
 *
 * @author peter.billen
 */
public class OffloadLauncher {

    public static void main(String[] args) {

        Option oHelp = new Option("help", "print this message");
        Option oDatabase = new Option("database", "Get data from a database");
        Option oSource = new Option("source", true, "source connection to get data from");
        Option oSourceEnv = new Option("sourceenv", true,
                "define the environment name from where the offload needs to take place");
        Option oTarget = new Option("target", true, "target connection to set data to");
        Option oTargetEnv = new Option("targetenv", true,
                "define the environment name to where the offload needs to take place");
        Option oSchema = new Option("schema", true, "Schema name to use for offload");
        Option oTable = new Option("table", true, "Table name to use for offload");
        Option oColumns = new Option("columns", true, "Column names to offload");
        Option oFilter = new Option("filter", true, "Filter to use to select the appropriate data");
        Option oSQL = new Option("sql", true, "SQL statement to use for offload");
        Option oName = new Option("name", true, "name to be used as table name for storing data");
        Option oClean = new Option("clean", "remove previous data from the target connection");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oDatabase);
        options.addOption(oSource);
        options.addOption(oSourceEnv);
        options.addOption(oTarget);
        options.addOption(oTargetEnv);
        options.addOption(oSchema);
        options.addOption(oTable);
        options.addOption(oColumns);
        options.addOption(oFilter);
        options.addOption(oSQL);
        options.addOption(oName);
        options.addOption(oClean);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        boolean actionMatch = false;
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            // Drop
            if (line.hasOption("database")) {
                System.out.println("Option -database (database) selected");
                actionMatch = true;
                writeHeaderMessage();

                // Get parameters
                String sourceName = "";
                if (line.hasOption("source")) {
                    sourceName = line.getOptionValue("source");
                    System.out.println("Option -source (source) value = " + sourceName);
                } else {
                    System.out.println("Option -source (source) missing");
                    System.exit(1);
                }

                String sourceEnvironmentName = "";
                if (line.hasOption("sourceenv")) {
                    sourceEnvironmentName = line.getOptionValue("sourceenv");
                    System.out.println("Option -source-env (source-env) value = " + sourceEnvironmentName);
                } else {
                    System.out.println("Option -source-env (source-env) missing");
                    System.exit(1);
                }

                String targetName = "";
                if (line.hasOption("target")) {
                    targetName = line.getOptionValue("target");
                    System.out.println("Option -target (target) value = " + targetName);
                } else {
                    System.out.println("Option -target (target) missing");
                    System.exit(1);
                }

                String targetEnvironmentName = "";
                if (line.hasOption("targetenv")) {
                    targetEnvironmentName = line.getOptionValue("targetenv");
                    System.out.println("Option -target-env (target-env) value = " + targetEnvironmentName);
                } else {
                    System.out.println("Option -target-env (target-env) missing");
                    System.exit(1);
                }

                String schemaName = "";
                if (line.hasOption("schema")) {
                    schemaName = line.getOptionValue("schema");
                    System.out.println("Option -schema (schema) value = " + schemaName);
                } else {
                    System.out.println("Option -schema (schema) value = " + schemaName);
                }

                String tableName = "";
                if (line.hasOption("table")) {
                    tableName = line.getOptionValue("table");
                    System.out.println("Option -table (table) value = " + tableName);
                } else {
                    System.out.println("Option -table (table) value = " + tableName);
                }

                String columns = "";
                if (line.hasOption("columns")) {
                    columns = line.getOptionValue("columns");
                    System.out.println("Option -columns (columns) value = " + columns);
                } else {
                    columns = "*";
                    System.out.println("Option -columns (columns) value = " + columns);
                }

                String filter = "";
                if (line.hasOption("filter")) {
                    filter = line.getOptionValue("filter");
                    System.out.println("Option -filter (filter) value = " + filter);
                } else {
                    System.out.println("Option -filter (filter) value = " + filter);
                }

                String sqlStatement = "";
                if (line.hasOption("sql")) {
                    sqlStatement = line.getOptionValue("sql");
                    System.out.println("Option -sql (sql) value = " + sqlStatement);
                } else {
                    System.out.println("Option -sql (sql) value = " + sqlStatement);
                }

                String name = "";
                if (line.hasOption("name")) {
                    name = line.getOptionValue("name");
                    System.out.println("Option -name (name) value = " + name);
                } else {
                    System.out.println("Option -name (name) value = " + name);
                }

                boolean cleanPrevious = false;
                if (line.hasOption("clean")) {
                    cleanPrevious = true;
                }

                // Execute
                DatabaseOffloadExecution databaseOffloadExecution = new DatabaseOffloadExecution();
                databaseOffloadExecution.offloadData(sourceName, sourceEnvironmentName, targetName,
                        targetEnvironmentName, sqlStatement, name, cleanPrevious);

            }

            if (actionMatch) {
                System.exit(0);
            } else {
                System.out.println("No valid parameters have been provided, type -help for help.");
            }

        } catch (ParseException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void writeHeaderMessage() {
        System.out.println("Invoking the harvesting execution...");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();
    }

}