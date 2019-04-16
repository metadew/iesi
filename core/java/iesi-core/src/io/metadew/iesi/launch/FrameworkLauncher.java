package io.metadew.iesi.launch;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.metadew.iesi.framework.configuration.FrameworkSettings;

/**
 * The framework launcher is entry point to get framework information.
 *
 * @author peter.billen
 */
public class FrameworkLauncher {

	public static void main(String[] args) {

		Option oHelp = new Option("help", "print this message");
		Option oVersion = new Option("version", "print the version of the framework");

		// create Options object
		Options options = new Options();
		// add options
		options.addOption(oHelp);
		options.addOption(oVersion);

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

			if (line.hasOption("version")) {
				System.out.println(FrameworkSettings.VERSION.value());
				System.exit(0);
			}
			
			System.out.println("run with option -help for more information");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}