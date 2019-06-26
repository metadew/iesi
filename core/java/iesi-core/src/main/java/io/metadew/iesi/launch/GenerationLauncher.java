package io.metadew.iesi.launch;

import io.metadew.iesi.data.generation.execution.GenerationExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.metadata.configuration.GenerationConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.Generation;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;

/**
 * The generation launcher is entry point to launch all data generation scripts.
 *
 * @author peter.billen
 */
public class GenerationLauncher {

	public static void main(String[] args) {
		
		Option oHelp = new Option("help", "print this message");
		Option oIni = new Option("ini", true, "define the initialization file");
		Option oGeneration = new Option("generation", true, "define the generation name to execute");
		Option oOutput = new Option("output", true, "define the output name to use for the generation");
		Option oRecords = new Option("records", true, "define the number of records to generate");
		Option oParamList = new Option("paramlist", true, "define a list of parameters to use");
		// Example: -paramlist var=value,var2=value
		Option oParamFile = new Option("paramfile", true, "define a parameter file to use");
		// Example: -paramfile C:/dir/file.conf
		// multiple values are separated by commas: -paramfile C:/dir/file.conf,C:/dir/file.conf
		Option oSettings = new Option("settings", true, "set specific setting values");

		// create Options object
		Options options = new Options();
		// add options
		options.addOption(oHelp);
		options.addOption(oIni);
		options.addOption(oGeneration);
		options.addOption(oOutput);
		options.addOption(oRecords);
		options.addOption(oParamList);
		options.addOption(oParamFile);
		options.addOption(oSettings);

		// create the parser
		CommandLineParser parser = new DefaultParser();
		String initializationFile = "";
		String generationName = null;
		String outputName = null;
		String records = "";
		String paramList = "";
		String paramFile = "";
		String settings = "";
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("[command]", options);
				System.exit(0);
			}

			// Define the initialization file
			if (line.hasOption("ini")) {
				initializationFile = line.getOptionValue("ini");
			}
			System.out.println("Option -ini (ini) value = " + initializationFile);
			
			// Get the Generation Name
			if (line.hasOption("generation")) {
				generationName = line.getOptionValue("generation");
				System.out.println("Option -generation (generation) value = " + generationName);
			} else {
				System.out.println("Option -generation (generation) missing");
				System.exit(1);
			}

			// Get the output
			if (line.hasOption("output")) {
				outputName = line.getOptionValue("output");
				System.out.println("Option -output (output) value = " + outputName);
			} else {
				System.out.println("Option -output (output) missing");
				System.exit(1);
			}
			
			// Get records input
			if (line.hasOption("records"))
				records = line.getOptionValue("records");
			System.out.println("Option -records (records) value = " + records);

			// Get variable configurations
			if (line.hasOption("paramlist"))
				paramList = line.getOptionValue("paramlist");
			System.out.println("Option -paramlist (parameter list) value = " + paramList);
			if (line.hasOption("paramfile"))
				paramFile = line.getOptionValue("paramfile");
			System.out.println("Option -paramfile (parameter file) value = " + paramFile);

			// Get settings input
			if (line.hasOption("settings"))
				settings = line.getOptionValue("settings");
			System.out.println("Option -settings (settings) value = " + settings);
			
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Calling the launch controller
		System.out.println("Invoking the generation execution...");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();

		// Create framework instance
		FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
		frameworkInitializationFile.setName(initializationFile);

		FrameworkInstance frameworkInstance = new FrameworkInstance(frameworkInitializationFile);

		// Create the framework execution
		Context context = new Context();
		context.setName("generation");
		context.setScope(generationName);
		FrameworkExecution frameworkExecution = new FrameworkExecution(frameworkInstance, new FrameworkExecutionContext(context), null);
		
		// Logging
		frameworkExecution.getFrameworkLog().log("option.generation=" + generationName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.output=" + outputName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.records=" + records, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.paramlist=" + paramList, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.paramfile=" + paramFile, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.settings=" + settings, Level.INFO);

		// Set specific settings
		if (!settings.equalsIgnoreCase("")) {
			frameworkExecution.getFrameworkControl().setSettingsList(settings);
		}
		
		// Get the Generation
		GenerationConfiguration generationConfiguration = new GenerationConfiguration(frameworkExecution.getFrameworkInstance());
		Generation generation = generationConfiguration.getGeneration(generationName);
		GenerationExecution eoGeneration = new GenerationExecution(frameworkExecution, generation);
		
		// Get the number of records
		long numberOfRecords = 0;
		if (records.trim().equalsIgnoreCase("")) {
			numberOfRecords = 10;
		} else {
			try {
				numberOfRecords = Long.parseLong(records);
			} catch (Exception e) {
				System.out.println("Invalid number of records entered");
				System.exit(1);
			}
		}
		
		eoGeneration.setNumberOfRecords(numberOfRecords);
				
		if (!paramList.equalsIgnoreCase("")) {
			eoGeneration.setParamList(paramList);
		}
		if (!paramFile.equalsIgnoreCase("")) {
			eoGeneration.setParamFile(paramFile);
		}

		// Execute the generation
		eoGeneration.execute(outputName);
	}
}