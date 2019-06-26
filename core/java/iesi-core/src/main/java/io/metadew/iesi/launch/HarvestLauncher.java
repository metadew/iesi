package io.metadew.iesi.launch;

import io.metadew.iesi.util.harvest.DatabaseHarvestExecution;
import org.apache.commons.cli.*;

/**
 * The harvest launcher is entry point to launch the data harvesting utility.
 *
 * @author peter.billen
 */
public class HarvestLauncher {

    public static void main(String[] args) {

        Option oHelp = new Option("help", "print this message");
        Option oDatabase = new Option("database", "Get data from a database");
        Option oSource = new Option("source", true, "source connection to get data from");
        Option oEnv = new Option("env", true, "define the environment name where the harvesting needs to take place");
        Option oSchema = new Option("schema", true, "Schema name to use for harvesting");
        Option oTable = new Option("table", true, "Table name to use for harvesting");
        Option oColumns = new Option("columns", true, "Column names to harvest");
        Option oFilter = new Option("filter", true, "Filter to use to select the appropriate data");
        Option oSQL = new Option("sql", true, "SQL statement to use for harvesting");
        Option oFileName = new Option("file", true, "filename to be used for storing data");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oDatabase);
        options.addOption(oEnv);
        options.addOption(oSource);
        options.addOption(oSchema);
        options.addOption(oTable);
        options.addOption(oColumns);
        options.addOption(oFilter);
        options.addOption(oSQL);
        options.addOption(oFileName);

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

                String environmentName = "";
                if (line.hasOption("env")) {
                    environmentName = line.getOptionValue("env");
                    System.out.println("Option -env (env) value = " + environmentName);
                } else {
                    System.out.println("Option -env (env) missing");
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
                    System.out.println("Option -table (table) missing");
                    System.exit(1);
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

                String fileName = "";
                if (line.hasOption("file")) {
                    fileName = line.getOptionValue("file");
                    System.out.println("Option -file (file) value = " + fileName);
                } else {
                    System.out.println("Option -file (file) missing");
                    System.exit(1);
                }

                // Execute
                DatabaseHarvestExecution databaseHarvestExecution = new DatabaseHarvestExecution();
                databaseHarvestExecution.getInsertStatements(sourceName, environmentName, schemaName, tableName, columns, filter, fileName);

            }

            if (actionMatch) {
                System.exit(0);
            } else {
                System.out.println("No valid parameters have been provided, type -help for help.");
            }

        } catch (ParseException e) {
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