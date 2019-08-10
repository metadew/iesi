package io.metadew.iesi.launch;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.backup.BackupExecution;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.restore.RestoreExecution;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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

    private static boolean actionMatch = false;
    private static FrameworkExecution frameworkExecution;

    @SuppressWarnings({"unchecked", "rawtypes", "unused"})
    public static void main(String[] args) {
        ThreadContext.clearAll();

        Option oHelp = new Option("help", "print this message");
        Option oIni = new Option("ini", true, "define the initialization file");
        Option oType = new Option("type", true, "define the type of metadata repository");
        Option oConfig = new Option("config", true, "define the metadata repository config");
        Option oBackup = new Option("backup", "create a backup of the entire metadata repository");
        Option oRestore = new Option("restore", "restore a backup of the metadata repository");
        Option oPath = new Option("path", true, "path to be used to for backup or restore");
        Option oDrop = new Option("drop", "drop all metadata tables in the metadata repository");
        Option oCreate = new Option("create", "create all metadata tables in the metadata repository");
        Option oClean = new Option("clean", "clean all tables in the metadata repository");
        Option oLoad = new Option("load", "load metadata file from the input folder into the metadata repository");
        Option oDdl = new Option("ddl",
                "generate ddl output instead of execution in the metadata repository, to be combined with options: create, drop");

        String filesHelp = "";
        filesHelp += "Following options are possible:";
        filesHelp += "\n";
        filesHelp += "-(1) a single file name including extension";
        filesHelp += "\n";
        filesHelp += "--Example: Script.json";
        filesHelp += "\n";
        filesHelp += "-(2) list of files separated by commas";
        filesHelp += "\n";
        filesHelp += "--Example: Script1.json,Script2.json";
        filesHelp += "\n";
        filesHelp += "-(3) a regular expression written as function =regex([your expression])";
        filesHelp += "\n";
        filesHelp += "--Example: =regex(.+\\json) > this will load all files";
        filesHelp += "\n";
        Option oFiles = new Option("files", true,
                "filename(s) to load from the input folder into the metadata repository" + "\n" + filesHelp);
        Option oExit = new Option("exit", true, "define if an explicit exit is required");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oIni);
        options.addOption(oType);
        options.addOption(oConfig);
        options.addOption(oBackup);
        options.addOption(oRestore);
        options.addOption(oPath);
        options.addOption(oDrop);
        options.addOption(oCreate);
        options.addOption(oClean);
        options.addOption(oLoad);
        options.addOption(oDdl);
        options.addOption(oFiles);
        options.addOption(oExit);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            // Define the exit behaviour
            boolean exit = true;
            if (line.hasOption("exit")) {
                switch (line.getOptionValue("exit").trim().toLowerCase()) {
                    case "y":
                    case "true":
                        exit = true;
                        break;
                    case "n":
                    case "false":
                        exit = false;
                        break;
                    default:
                        break;
                }
            }

            // Create the framework instance
            FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
            if (line.hasOption("ini")) {
                frameworkInitializationFile.setName(line.getOptionValue("ini"));
                System.out.println("Option -ini (ini) value = " + frameworkInitializationFile.getName());
            }

            FrameworkInstance frameworkInstance = FrameworkInstance.getInstance();
            frameworkInstance.init("owner", frameworkInitializationFile);

            // Create the framework execution
            Context context = new Context();
            context.setName("metadata");
            context.setScope("");
            FrameworkExecution frameworkExecution = FrameworkExecution.getInstance();
            frameworkExecution.init(new FrameworkExecutionContext(context));
            MetadataRepositoryOperation metadataRepositoryOperation = null;
            List<MetadataRepository> metadataRepositories = new ArrayList();

            String type = "";
            if (line.hasOption("type")) {
                type = line.getOptionValue("type");
                System.out.println("Option -type (type) value = " + type);
            } else {
                System.out.println("Option -type (type) missing");
                endLauncher(1, true);
            }


            if (line.hasOption("config")) {
                String config = line.getOptionValue("config");

                ConfigFile configFile = FrameworkControl.getInstance().getConfigFile("keyvalue",
                        FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("conf")
                                + File.separator + config);

                metadataRepositories = new MetadataRepositoryConfiguration(configFile, FrameworkSettingConfiguration.getInstance(), FrameworkCrypto.getInstance())
                        .toMetadataRepositories(FrameworkConfiguration.getInstance());

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
                    case "general":
                    	metadataRepositories.add(MetadataControl.getInstance().getCatalogMetadataRepository());
                        metadataRepositories.add(MetadataControl.getInstance().getConnectivityMetadataRepository());
                        metadataRepositories.add(MetadataControl.getInstance().getControlMetadataRepository());
                        metadataRepositories.add(MetadataControl.getInstance().getDesignMetadataRepository());
                        metadataRepositories.add(MetadataControl.getInstance().getResultMetadataRepository());
                        metadataRepositories.add(MetadataControl.getInstance().getTraceMetadataRepository());
                        break;
                    default:
                        System.out.println("Unknown Option -type (type) = " + type);
                        endLauncher(1, true);
                }
            }
            // Backup
            if (line.hasOption("backup")) {
                for (MetadataRepository metadataRepository : metadataRepositories) {
                    if (actionMatch) {
                        System.out.println();
                    }
                    writeHeaderMessage();
                    System.out.println("Option -backup (backup) selected");
                    actionMatch = true;

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
                    if (actionMatch) {
                        System.out.println();
                    }
                    writeHeaderMessage();
                    System.out.println("Option -restore (restore) selected");
                    System.out.println();
                    actionMatch = true;

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
                    metadataRepositoryOperation = new MetadataRepositoryOperation(metadataRepository);

                    if (actionMatch) {
                        System.out.println();
                    }
                    writeHeaderMessage();
                    System.out.println("Option -drop (drop) selected");
                    System.out.println();
                    actionMatch = true;
                    metadataRepository.dropAllTables();
                    //metadataRepositoryOperation.drop();
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
            for (MetadataRepository metadataRepository : metadataRepositories) {
                metadataRepositoryOperation = new MetadataRepositoryOperation(metadataRepository);
                if (line.hasOption("create")) {
                    if (actionMatch) {
                        System.out.println();
                    }
                    writeHeaderMessage();
                    System.out.println("Option -create (create) selected");
                    actionMatch = true;
                    System.out.println();
                    System.out.println(MessageFormat.format("Creating metadata repository {0}", metadataRepository.getCategory()));
                    metadataRepository.createAllTables();
                    //metadataRepositoryOperation.create(ddl);
                    writeFooterMessage();
                }
            }

            // clean
            if (line.hasOption("clean")) {
                for (MetadataRepository metadataRepository : metadataRepositories) {
                    metadataRepositoryOperation = new MetadataRepositoryOperation(metadataRepository);
                    if (actionMatch) {
                        System.out.println();
                    }
                    writeHeaderMessage();
                    System.out.println("Option -clean (clean) selected");
                    System.out.println();
                    actionMatch = true;
                    metadataRepository.cleanAllTables();
                    //metadataRepositoryOperation.cleanAllTables();
                    writeFooterMessage();
                }

            }

            // load
            if (line.hasOption("load")) {
                if (actionMatch) {
                    System.out.println();
                }
                writeHeaderMessage();
                System.out.println("Option -load (load) selected");
                System.out.println();
                actionMatch = true;
                if (line.hasOption("files")) {
                    String files = "";
                    files = line.getOptionValue("files");
                    metadataRepositoryOperation.loadMetadataRepository(metadataRepositories, files);
                } else {
                    metadataRepositoryOperation.loadMetadataRepository(metadataRepositories);
                }
                writeFooterMessage();
            }

            if (actionMatch) {
                System.out.println();
                System.out.println("metadata.launcher.end");
                endLauncher(0, exit);
            } else {
                System.out.println("No valid arguments have been provided, type -help for help.");
            }

        } catch (ParseException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            endLauncher(1, true);
        }

    }

    private static void endLauncher(int status, boolean exit) {
        FrameworkExecution.getInstance().getFrameworkRuntime().terminate();
        if (exit) {
            System.exit(status);
        }
    }

    private static void writeHeaderMessage() {
        if (!actionMatch) {
            System.out.println("metadata.launcher.start");
            System.out.println();
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    private static void writeFooterMessage() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

}