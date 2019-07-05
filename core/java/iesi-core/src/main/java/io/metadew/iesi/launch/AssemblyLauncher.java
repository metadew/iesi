package io.metadew.iesi.launch;

import io.metadew.iesi.assembly.execution.AssemblyExecution;
import org.apache.commons.cli.*;

/**
 * The assembly launcher is entry point to launch the assembly of the framework.
 * It is used only to prepare the packaging, not in a production mode.
 *
 * @author peter.billen
 */
public class AssemblyLauncher {

    public static void main(String[] args) {

        Option oHelp = new Option("help", "print this message");
        Option oRepository = new Option("repository", true, "set repository location");
        Option oDevelopment = new Option("development", true, "set development location");
        Option oSandbox = new Option("sandbox", true, "set sandbox location");
        Option oInstance = new Option("instance", true, "provide target instance");
        Option oVersion = new Option("version", true, "provide target version");
        Option oConfiguration = new Option("configuration", true, "provide target configuration");
        Option oTestAssembly = new Option("test", "test assembly flag");
        Option oDistribution = new Option("distribution", "distribution flag");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oSandbox);
        options.addOption(oRepository);
        options.addOption(oDevelopment);
        options.addOption(oInstance);
        options.addOption(oVersion);
        options.addOption(oConfiguration);
        options.addOption(oTestAssembly);
        options.addOption(oDistribution);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        String repository = "";
        String development = "";
        String sandbox = "";
        String instance = "";
        String version = "";
        String configuration = "";
        boolean testAssembly = false;
        boolean applyConfiguration = false;
        boolean distribution = false;
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

            if (line.hasOption("repository")) {
                actionMatch = true;
                writeHeaderMessage();
                repository = line.getOptionValue("repository");
                System.out.println("Option -repository (repository) value = " + repository);
            } else {
                System.out.println("Option -repository (repository) missing");
                System.exit(1);
            }

            // Development
            if (line.hasOption("development")) {
                development = line.getOptionValue("development");
                System.out.println("Option -development (development) value = " + development);
            } else {
                System.out.println("Option -development (development) missing");
                System.exit(1);
            }

            // Sandbox
            if (line.hasOption("sandbox")) {
                sandbox = line.getOptionValue("sandbox");
                System.out.println("Option -sandbox (sandbox) value = " + sandbox);
            } else {
                System.out.println("Option -sandbox (sandbox) missing");
                System.exit(1);
            }

            // Instance
            if (line.hasOption("instance")) {
                instance = line.getOptionValue("instance");
                System.out.println("Option -instance (instance) value = " + instance);
            } else {
                System.out.println("Option -instance (instance) missing");
                System.exit(1);
            }

            // Version
            if (line.hasOption("version")) {
                version = line.getOptionValue("version");
                System.out.println("Option -version (version) value = " + version);
            } else {
                System.out.println("Option -version (version) missing");
                System.exit(1);
            }

            // Configuration
            if (line.hasOption("configuration")) {
                configuration = line.getOptionValue("configuration");
                applyConfiguration = true;
                System.out.println("Option -configuration (configuration) value = " + configuration);
            } else {
                System.out.println("Option -configuration (configuration) missing");
                System.exit(1);
            }

            // test assembly
            if (line.hasOption("test")) {
                testAssembly = true;
                System.out.println("Option -test (test assembly) value = " + testAssembly);
            } else {
                testAssembly = false;
                System.out.println("Option -test (test assembly) value = " + testAssembly);
            }

            // Distribution
            if (line.hasOption("distribution")) {
                distribution = true;
                System.out.println("Option -distribution (distribution) value = " + distribution);
            } else {
                distribution = false;
                System.out.println("Option -distribution (distribution) value = " + distribution);
            }

            AssemblyExecution assemblyExecution = new AssemblyExecution(repository, development, sandbox, instance,
                    version, configuration, applyConfiguration, testAssembly, distribution);
            assemblyExecution.execute();

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (actionMatch) {
            System.exit(0);
        } else {
            System.out.println("No valid parameters have been provided, type -help for help.");
        }

    }

    private static void writeHeaderMessage() {
        System.out.println("Invoking the assembly execution...");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();
    }

}