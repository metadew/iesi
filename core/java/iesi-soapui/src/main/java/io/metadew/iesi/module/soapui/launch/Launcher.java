package io.metadew.iesi.module.soapui.launch;


import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.metadew.iesi.module.soapui.execution.SoapUiExecution;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class Launcher {

	public static void main(String[] args) {

		//Option oHelp = new Option("help", "print this message");
		Option oProject = new Option("project", true, "define the project file");
		Option oSuite = new Option("suite", true, "define the test suite to execute");
		Option oCase = new Option("case", true, "define the test case to execute");
		Option oOutput = new Option("output", true, "define the output path");

		// create Options object
		Options options = new Options();
		// add options
		//options.addOption(oHelp);
		options.addOption(oProject);
		options.addOption(oSuite);
		options.addOption(oCase);
		options.addOption(oOutput);

		// create the parser
		CommandLineParser parser = new DefaultParser();
		String project = "";
		String testSuite = "";
		String testCase = "";
		String output = "";
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("[command]", options);
				System.exit(0);
			}

			// **************************************************************
			// Project
			//
			if (line.hasOption("project")) {
				project = line.getOptionValue("project");
				System.out.println("Option -project (project) value = " + project);
			} else {
				System.out.println("Option -project (project) missing");
				System.exit(1);
			}
			//
			//****************************************************************


			// **************************************************************
			// Scope
			//
			if (line.hasOption("suite")) {
				testSuite= line.getOptionValue("suite");
				System.out.println("Option -suite (suite) value = " + testSuite);
			}

			if (line.hasOption("case")) {
				testCase= line.getOptionValue("case");
				System.out.println("Option -case (case) value = " + testCase);
			}
			//
			//****************************************************************

			
			// **************************************************************
			// Output
			//
			if (line.hasOption("output")) {
				output = line.getOptionValue("output");
				System.out.println("Option -output (output) value = " + output);
			} else {
				output = new File("").getAbsolutePath();
				System.out.println("Option -output (output) value = (default) " + output);
			}
			//
			//****************************************************************

		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Server mode
		// Calling the launch controller
		System.out.println();
		System.out.println("module.soapui.start");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		SoapUiExecution soapUiExecution = new SoapUiExecution(project, testSuite, testCase, output);
		soapUiExecution.prepare();
		soapUiExecution.execute();
		System.exit(0);
	}

}