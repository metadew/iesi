//package io.metadew.iesi.launch;
//
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.DefaultParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;
//
//import io.metadew.iesi.data.configuration.DataRepositoryConfiguration;
//import io.metadew.iesi.data.operation.DataRepositoryOperation;
//import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;
//import io.metadew.iesi.metadata.definition.Context;
//import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
//
///**
// * The metadata launcher is entry point to launch all data management
// * operations.
// *
// * @author peter.billen
// *
// */
//public class DataLauncher {
//
//	private static boolean actionMatch = false;
//
//	@SuppressWarnings("unused")
//	public static void main(String[] args) {
//
//		Option oHelp = new Option("help", "print this message");
//		Option oRepository = new Option("repository", true, "define the data repository");
//		Option oInstance = new Option("instance", true, "define the data repository instance");
//		Option oLabels = new Option("labels", true, "define the data repository instance labels");
//		Option oEnvironment = new Option("env", true, "define the environment");
//		Option oDrop = new Option("drop", "drop all metadata tables in the metadata repository");
//		Option oCreate = new Option("create", "create all metadata tables in the metadata repository");
//		Option oClean = new Option("clean", "clean all tables in the metadata repository");
//		Option oLoad = new Option("load", "load metadata file from the input folder into the metadata repository");
//		Option oDdl = new Option("ddl",
//				"generate ddl output instead of execution in the metadata repository, to be combined with options: create, drop");
//
//		String filesHelp = "";
//		filesHelp += "Following options are possible:";
//		filesHelp += "\n";
//		filesHelp += "-(1) a single file name including extension";
//		filesHelp += "\n";
//		filesHelp += "--Example: Script.json";
//		filesHelp += "\n";
//		filesHelp += "-(2) list of files separated by commas";
//		filesHelp += "\n";
//		filesHelp += "--Example: Script1.json,Script2.json";
//		filesHelp += "\n";
//		filesHelp += "-(3) a regular expression written as function =regex([your expression])";
//		filesHelp += "\n";
//		filesHelp += "--Example: =regex(.+\\json) > this will load all files";
//		filesHelp += "\n";
//		Option oFiles = new Option("files", true,
//				"filename(s) to load from the input folder into the metadata repository" + "\n" + filesHelp);
//
//		// create Options object
//		Options options = new Options();
//		// add options
//		options.addOption(oHelp);
//		options.addOption(oRepository);
//		options.addOption(oInstance);
//		options.addOption(oLabels);
//		options.addOption(oEnvironment);
//		options.addOption(oDrop);
//		options.addOption(oCreate);
//		options.addOption(oClean);
//		options.addOption(oLoad);
//		options.addOption(oDdl);
//		options.addOption(oFiles);
//
//		// create the parser
//		CommandLineParser parser = new DefaultParser();
//		try {
//			// parse the command line arguments
//			CommandLine line = parser.parse(options, args);
//
//			if (line.hasOption("help")) {
//				// automatically generate the help statement
//				HelpFormatter formatter = new HelpFormatter();
//				formatter.printHelp("[command]", options);
//				System.exit(0);
//			}
//
//			String repository = "";
//			if (line.hasOption("repository")) {
//				repository = line.getOptionValue("repository");
//				System.out.println("Option -repository (repository) value = " + repository);
//			} else {
//				System.out.println("Option -repository (repository) missing");
//				System.exit(1);
//			}
//
//			String instanceName = "";
//			if (line.hasOption("instance")) {
//				instanceName = line.getOptionValue("instance");
//				System.out.println("Option -instance (instance) value = " + instanceName);
//			} else {
//				System.out.println("Option -instance (instance) missing");
//			}
//
//			String instanceLabels = "";
//			if (line.hasOption("labels")) {
//				instanceLabels = line.getOptionValue("labels");
//				System.out.println("Option -labels (labels) value = " + instanceLabels);
//			} else {
//				System.out.println("Option -labels (labels) missing");
//			}
//
//			String environment = "";
//			if (line.hasOption("env")) {
//				environment = line.getOptionValue("env");
//				System.out.println("Option -env (environment) value = " + environment);
//			} else {
//				System.out.println("Option -env (environment) missing");
//			}
//
//			Context context = new Context();
//			context.setName("data");
//			context.setScope("");
//			FrameworkExecution frameworkExecution = new FrameworkExecution(new FrameworkExecutionContext(context),
//					"owner",null);
//
//			DataRepositoryConfiguration dataRepositoryConfiguration = new DataRepositoryConfiguration(
//					frameworkExecution, repository, instanceName, instanceLabels, environment);
//
//			MetadataRepositoryConfiguration metadataRepositoryConfiguration = new MetadataRepositoryConfiguration(
//					frameworkExecution.getFrameworkConfiguration(), frameworkExecution.getFrameworkControl(),
//					dataRepositoryConfiguration.getConfigFile(), "owner");
//
//			MetadataRepositoryOperation metadataRepositoryOperation = new MetadataRepositoryOperation(frameworkExecution,
//					metadataRepositoryConfiguration);
//
//			DataRepositoryOperation dataRepositoryOperation = new DataRepositoryOperation(frameworkExecution,
//					dataRepositoryConfiguration);
//			System.out.println(dataRepositoryOperation);
//
//			// Drop
//			if (line.hasOption("drop")) {
//				if (actionMatch)
//					System.out.println();
//				writeHeaderMessage();
//				System.out.println("Option -drop (drop) selected");
//				System.out.println();
//				actionMatch = true;
//				// metadataRepositoryOperation.drop();
//				writeFooterMessage();
//			}
//
//			// Create
//			if (line.hasOption("create")) {
//				if (actionMatch)
//					System.out.println();
//				writeHeaderMessage();
//				System.out.println("Option -create (create) selected");
//				System.out.println();
//				actionMatch = true;
//				boolean ddl;
//				if (line.hasOption("ddl")) {
//					ddl = true;
//				} else {
//					ddl = false;
//				}
//				metadataRepositoryOperation.create(ddl);
//
//				writeFooterMessage();
//			}
//
//			// clean
//			if (line.hasOption("clean")) {
//				if (actionMatch)
//					System.out.println();
//				writeHeaderMessage();
//				System.out.println("Option -clean (clean) selected");
//				System.out.println();
//				actionMatch = true;
//				// metadataRepositoryOperation.cleanAllTables();
//				writeFooterMessage();
//
//			}
//
//			// load
//			if (line.hasOption("load")) {
//				if (actionMatch)
//					System.out.println();
//				writeHeaderMessage();
//				System.out.println("Option -load (load) selected");
//				System.out.println();
//				actionMatch = true;
//				if (line.hasOption("files")) {
//					String files = "";
//					files = line.getOptionValue("files");
//					// metadataRepositoryOperation.loadMetadataRepository(metadataRepositoryConfigurationList,
//					// files);
//				} else {
//					// metadataRepositoryOperation.loadMetadataRepository(metadataRepositoryConfigurationList);
//				}
//				writeFooterMessage();
//			}
//
//			if (actionMatch) {
//				System.out.println();
//				System.out.println("metadata.launcher.end");
//				System.exit(0);
//			} else {
//				System.out.println("No valid arguments have been provided, type -help for help.");
//			}
//
//		} catch (
//
//		ParseException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//
//	}
//
//	private static void writeHeaderMessage() {
//		if (!actionMatch) {
//			System.out.println("metadata.launcher.start");
//			System.out.println();
//		}
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//	}
//
//	private static void writeFooterMessage() {
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//	}
//
//}
