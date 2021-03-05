package io.metadew.iesi.launch;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.FrameworkRuntime;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MetadataLauncher {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ParseException, SQLException, IOException {
        ThreadContext.clearAll();

        Options options = new Options().addOption(new Option("help", "print this message"))
                .addOption(Option.builder("type").hasArg().desc("define the type of metadata repository").required().build())
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

        Configuration.getInstance();
        FrameworkCrypto.getInstance();
        // FrameworkInstance.getInstance().init(frameworkInitializationFile, new FrameworkExecutionContext(new Context("metadata", "")));

        MetadataRepositoryOperation metadataRepositoryOperation = new MetadataRepositoryOperation();
        List<MetadataRepository> metadataRepositories = new ArrayList<>();

        System.out.println("Option -type (type) value = " + line.getOptionValue("type"));
        String type = line.getOptionValue("type");

        switch (type) {
            case "connectivity":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository());
                break;
            case "control":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
                break;
            case "design":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
                break;
            case "result":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getResultMetadataRepository());
                break;
            case "trace":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
                break;
            case "execution":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());
                break;
            case "data":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getDataMetadataRepository());
                break;
            case "general":
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getResultMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getTraceMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getExecutionServerMetadataRepository());
                metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getDataMetadataRepository());
                break;
            default:
                System.out.println("Unknown Option -type (type) = " + type);
                endLauncher(1, true);
        }

        // Drop
        if (line.hasOption("drop")) {
            writeHeaderMessage();
            for (MetadataRepository metadataRepository : metadataRepositories) {
                System.out.println("Option -drop (drop) selected");
                System.out.println();
                metadataRepository.dropAllTables();
            }
            writeFooterMessage();
        }

        // DDL
        if (line.hasOption("ddl")) {
            writeHeaderMessage();
            for (MetadataRepository metadataRepository : metadataRepositories) {
                Files.deleteIfExists(FrameworkConfiguration.getInstance()
                        .getMandatoryFrameworkFolder("metadata.out.ddl")
                        .getAbsolutePath()
                        .resolve("ddl_" + metadataRepository.getCategory() + ".sql"));
                Files.createFile(FrameworkConfiguration.getInstance()
                        .getMandatoryFrameworkFolder("metadata.out.ddl")
                        .getAbsolutePath()
                        .resolve("ddl_" + metadataRepository.getCategory() + ".sql"));

                Files.write(FrameworkConfiguration.getInstance()
                                .getMandatoryFrameworkFolder("metadata.out.ddl")
                                .getAbsolutePath()
                                .resolve("ddl_" + metadataRepository.getCategory() + ".sql"),
                        metadataRepository.generateDDL().getBytes(StandardCharsets.UTF_8));
            }
            writeFooterMessage();
        }

        // Create
        if (line.hasOption("create")) {
            writeHeaderMessage();
            for (MetadataRepository metadataRepository : metadataRepositories) {
                System.out.println("Option -create (create) selected");
                System.out.println();
                System.out.println(MessageFormat.format("Creating metadata repository {0}", metadataRepository.getCategory()));
                metadataRepository.createAllTables();
            }
            writeFooterMessage();
        }

        // clean
        if (line.hasOption("clean")) {
            writeHeaderMessage();
            for (MetadataRepository metadataRepository : metadataRepositories) {
                System.out.println("Option -clean (clean) selected");
                System.out.println();
                metadataRepository.cleanAllTables();
            }
            writeFooterMessage();
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

        FrameworkInstance.getInstance().shutdown();
        System.out.println();
        System.out.println("metadata.launcher.end");
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