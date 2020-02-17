package io.metadew.iesi.launch;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.backup.BackupExecution;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.restore.RestoreExecution;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The metadata launcher is entry point to launch all configuration management
 * operations.
 *
 * @author peter.billen
 */
public class MetadataLauncher {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException, SQLException {
        ThreadContext.clearAll();

        Options options = new Options().addOption(new Option("help", "print this message"))
                .addOption(Option.builder("ini").hasArg().desc("define the initialization file").build())
                .addOption(Option.builder("type").hasArg().desc("define the type of metadata repository").required().build())
                .addOption(Option.builder("config").hasArg().desc("define the metadata repository config").build())
                .addOption(Option.builder("backup").desc("create a backup of the entire metadata repository").build())
                .addOption(Option.builder("restore").desc("restore a backup of the metadata repository").build())
                .addOption(Option.builder("path").hasArg().desc("path to be used to for backup or restore").build())
                .addOption(Option.builder("drop").desc("drop all metadata tables in the metadata repository").build())
                .addOption(Option.builder("create").desc("create all metadata tables in the metadata repository").build())
                .addOption(Option.builder("clean").desc("clean all tables in the metadata repository").build())
                .addOption(Option.builder("load").desc("load metadata file from the input folder into the metadata repository").build())
                .addOption(Option.builder("ddl").desc("generate ddl output instead of execution in the metadata repository, to be combined with options: create, drop").build())
                .addOption(Option.builder("files").hasArg().desc(
                        "filename(s) to load from the input folder into the metadata repository\n" +
                                "Following options are possible:\n" +
                                "-(1) a single file name including extension\n" +
                                "--Example: Script.json\n" +
                                "-(2) list of files separated by commas \n" +
                                "--Example: Script1.json,Script2.json\n" +
                                "-(3) a regular expression written as function =regex([your expression])\n" +
                                "--Example: =regex(.+\\json) > this will load all files").build())
                .addOption(Option.builder("exit").hasArg().desc("define if an explicit exit is required").build());


        // create the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[command]", options);
            System.exit(0);
        }

        // Define the exit behaviour
        boolean exit = !line.hasOption("exit") || line.getOptionValue("exit").equalsIgnoreCase("y") || line.getOptionValue("exit").equalsIgnoreCase("true");

        // Create the framework instance
        FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
        if (line.hasOption("ini")) {
            System.out.println("Option -ini (ini) value = " + line.getOptionValue("ini"));
            frameworkInitializationFile.setName(line.getOptionValue("ini"));
        }

        FrameworkInstance.getInstance().init(frameworkInitializationFile, new FrameworkExecutionContext(new Context("metadata", "")));

        MetadataRepositoryOperation metadataRepositoryOperation = new MetadataRepositoryOperation();
        List<MetadataRepository> metadataRepositories = new ArrayList<>();

        System.out.println("Option -type (type) value = " + line.getOptionValue("type"));
        String type = line.getOptionValue("type");


        if (line.hasOption("config")) {
            String config = line.getOptionValue("config");

            //ConfigFile configFile = FrameworkControl.getInstance().getConfigFile("keyvalue",
            //        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("conf") + File.separator + config);

            //metadataRepositories = new MetadataRepositoryConfiguration(configFile).toMetadataRepositories();

            // metadataRepositories.addAll(metadataRepositories);

        } else {
            switch (type) {
                case "catalog":
                    metadataRepositories.add(MetadataControl.getInstance().getCatalogMetadataRepository());
                    break;
                case "connectivity":
                    metadataRepositories.add(MetadataControl.getInstance().getConnectivityMetadataRepository());
                    break;
                case "control":
                    metadataRepositories.add(MetadataControl.getInstance().getControlMetadataRepository());
                    break;
                case "design":
                    metadataRepositories.add(MetadataControl.getInstance().getDesignMetadataRepository());
                    break;
                case "result":
                    metadataRepositories.add(MetadataControl.getInstance().getResultMetadataRepository());
                    break;
                case "trace":
                    metadataRepositories.add(MetadataControl.getInstance().getTraceMetadataRepository());
                    break;
                case "execution_server":
                    metadataRepositories.add(MetadataControl.getInstance().getExecutionServerMetadataRepository());
                    break;
                case "general":
                    metadataRepositories.add(MetadataControl.getInstance().getCatalogMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getConnectivityMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getControlMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getDesignMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getResultMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getTraceMetadataRepository());
                    metadataRepositories.add(MetadataControl.getInstance().getExecutionServerMetadataRepository());
                    break;
                default:
                    System.out.println("Unknown Option -type (type) = " + type);
                    endLauncher(1, true);
            }
        }
        // Backup
        if (line.hasOption("backup")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {
                writeHeaderMessage();
                System.out.println("Option -backup (backup) selected");

                // Get path value
                String path = "";
                if (line.hasOption("path")) {
                    path = line.getOptionValue("path");
                    System.out.println("Option -path (path) value = " + path);
                } else {
                    System.out.println("Option -path (path) not provided");
                    writeFooterMessage();
                    endLauncher(1, true);
                }

                // Execute
                BackupExecution backupExecution = new BackupExecution();
                backupExecution.execute(path);
                writeFooterMessage();
                endLauncher(0, true);
            }
        }

        // Restore
        if (line.hasOption("restore")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {
                writeHeaderMessage();
                System.out.println("Option -restore (restore) selected");
                System.out.println();

                // Get path value
                String path = "";
                if (line.hasOption("path")) {
                    path = line.getOptionValue("path");
                    System.out.println("Option -path (path) value = " + path);
                } else {
                    System.out.println("Option -path (path) missing");
                    endLauncher(1, true);
                }

                // Execute
                RestoreExecution restoreExecution = new RestoreExecution();
                restoreExecution.execute(path);
                writeFooterMessage();
                endLauncher(0, true);
            }
        }

        // Drop
        if (line.hasOption("drop")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {

                writeHeaderMessage();
                System.out.println("Option -drop (drop) selected");
                System.out.println();
                metadataRepository.dropAllTables();
                writeFooterMessage();
            }
        }

        // DDL
        if (line.hasOption("ddl")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {
                System.out.println(metadataRepository.generateDDL());
            }
        }

        // Create
        if (line.hasOption("create")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {
                writeHeaderMessage();
                System.out.println("Option -create (create) selected");
                System.out.println();
                System.out.println(MessageFormat.format("Creating metadata repository {0}", metadataRepository.getCategory()));
                metadataRepository.createAllTables();
                writeFooterMessage();
            }
        }

        // clean
        if (line.hasOption("clean")) {
            for (MetadataRepository metadataRepository : metadataRepositories) {
                writeHeaderMessage();
                System.out.println("Option -clean (clean) selected");
                System.out.println();
                metadataRepository.cleanAllTables();
                writeFooterMessage();
            }

        }

        // load
        if (line.hasOption("load")) {
            writeHeaderMessage();
            System.out.println("Option -load (load) selected");
            System.out.println();
            if (line.hasOption("files")) {
                String files = "";
                files = line.getOptionValue("files");
                metadataRepositoryOperation.loadMetadataRepository(metadataRepositories, files);
            } else {
                metadataRepositoryOperation.loadMetadataRepository(metadataRepositories);
            }
            writeFooterMessage();
        }

        System.out.println();
        System.out.println("metadata.launcher.end");
        FrameworkInstance.getInstance().shutdown();
        endLauncher(0, exit);
    }

    private static void endLauncher(int status, boolean exit) {
        FrameworkRuntime.getInstance().terminate();
        if (exit) {
            System.exit(status);
        }
    }

    private static void writeHeaderMessage() {
        System.out.println("metadata.launcher.start");
        System.out.println();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    private static void writeFooterMessage() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

}