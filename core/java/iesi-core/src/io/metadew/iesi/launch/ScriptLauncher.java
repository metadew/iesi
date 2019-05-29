package io.metadew.iesi.launch;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.launch.operation.ScriptLaunchOperation;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.runtime.Requestor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class ScriptLauncher {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		Option oHelp = new Option("help", "print this message");
		Option oIni = new Option("ini", true, "define the initialization file");
		Option oScript = new Option("script", true, "define the script name to execute");
		Option oVersion = new Option("version", true, "define the version of the script to execute");
		Option oFile = new Option("file", true, "define the configuration file to execute");
		Option oEnv = new Option("env", true, "define the environment name where the execution needs to take place");
		Option oParamList = new Option("paramlist", true, "define a list of parameters to use");
		// Example: -paramlist var1=value1,var2=value
		Option oParamFile = new Option("paramfile", true, "define a parameter file to use");
		// Example: -paramfile C:/dir/file.conf
		// multiple values are separated by commas: -paramfile
		// C:/dir/file.conf,C:/dir/file.conf
		Option oActionSelect = new Option("actions", true, "select actions to execute or not");
		// Example -actions type=number,mode=include,scope=2-3,6
		Option oSettings = new Option("settings", true, "set specific setting values");
		Option oImpersonation = new Option("impersonation", true, "define impersonation name to use");
		Option oImpersonate = new Option("impersonate", true, "define custom impersonations to use");
		Option oExit = new Option("exit", true, "define if an explicit exit is required");
		Option oUser = new Option("user", true, "define the user to log in with");
		Option oPassword = new Option("password", true, "define the password to log in with");
		Option oServer = new Option("server", "flag to indicate to submit a request on the execution server");

		// create Options object
		Options options = new Options();
		// add options
		options.addOption(oHelp);
		options.addOption(oIni);
		options.addOption(oScript);
		options.addOption(oVersion);
		options.addOption(oFile);
		options.addOption(oEnv);
		options.addOption(oParamList);
		options.addOption(oParamFile);
		options.addOption(oActionSelect);
		options.addOption(oSettings);
		options.addOption(oImpersonation);
		options.addOption(oImpersonate);
		options.addOption(oExit);
		options.addOption(oUser);
		options.addOption(oPassword);
		options.addOption(oServer);

		// create the parser
		CommandLineParser parser = new DefaultParser();
		boolean exit = true;
		boolean server = false;
		String initializationFile = "";
		String environmentName = "";
		String executionMode = "";
		String scriptName = "";
		long scriptVersionNumber = -1;
		String fileName = "";
		String paramList = "";
		String paramFile = "";
		String actionSelect = "";
		String settings = "";
		String impersonationName = "";
		String impersonationCustom = "";
		String userName = "";
		String userPassword = "";
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("[command]", options);
				System.exit(0);
			}

			// Define the execution route: server or commandline
			if (line.hasOption("server")) {
				server = true;
			} else {
				server = false;
			}

			// Define the exit behaviour
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

			// Define the initialization file
			if (line.hasOption("ini")) {
				initializationFile = line.getOptionValue("ini");
			}
			System.out.println("Option -ini (ini) value = " + initializationFile);

			// Get the script
			// Script is leading, Json option is trailing
			if (line.hasOption("script")) {
				executionMode = "script";
				scriptName = line.getOptionValue("script");
				System.out.println("Option -script (script) value = " + scriptName);

				if (line.hasOption("version")) {
					String scriptVersionInput = line.getOptionValue("version");
					try {
						scriptVersionNumber = Long.parseLong(scriptVersionInput);
					} catch (Exception e) {
						System.out.println("Option -version (version) is not in the correct format");
						System.exit(1);
					}
					System.out.println("Option -version (version) value = " + scriptVersionNumber);
				} else {
					System.out.println("Option -version (version) value = ");
				}

			} else {
				// Json option
				if (line.hasOption("file")) {
					executionMode = "file";
					fileName = line.getOptionValue("file");
					System.out.println("Option -file (file) value = " + fileName);
				} else {
					System.out.println("Option -script (script) or -file (file) missing");
					System.exit(1);
				}
			}

			// Get the environment
			if (line.hasOption("env")) {
				environmentName = line.getOptionValue("env");
				System.out.println("Option -env (environment) value = " + environmentName);
			} else {
				System.out.println("Option -env (environment) missing");
				System.exit(1);
			}

			// Get variable configurations
			if (line.hasOption("paramlist")) {
				paramList = line.getOptionValue("paramlist");
			}
			System.out.println("Option -paramlist (parameter list) value = " + paramList);
			if (line.hasOption("paramfile")) {
				paramFile = line.getOptionValue("paramfile");
			}
			System.out.println("Option -paramfile (parameter file) value = " + paramFile);

			// Get action select settings
			if (line.hasOption("actions")) {
				actionSelect = line.getOptionValue("actions");
			}
			System.out.println("Option -actions (actions) value = " + actionSelect);

			// Get settings input
			if (line.hasOption("settings")) {
				settings = line.getOptionValue("settings");
			}
			System.out.println("Option -settings (settings) value = " + settings);

			// Get impersonation input
			if (line.hasOption("impersonation")) {
				impersonationName = line.getOptionValue("impersonation");
			}
			System.out.println("Option -impersonation (impersonation) value = " + impersonationName);

			// Get impersonation input
			if (line.hasOption("impersonate")) {
				impersonationCustom = line.getOptionValue("impersonate");
			}
			System.out.println("Option -impersonate (impersonate) value = " + impersonationCustom);

			// Get the user name
			if (line.hasOption("user")) {
				userName = line.getOptionValue("user");
			}
			System.out.println("Option -user (user) value = " + userName);

			// Get the user password
			if (line.hasOption("password")) {
				userPassword = line.getOptionValue("password");
				System.out.println("Option -password (password) value = " + "*****");
			} else {
				System.out.println("Option -password (password) value = " + "");
			}

		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Calling the launch controller
		System.out.println();
		System.out.println("script.launcher.start");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		// Create framework instance
		FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
		frameworkInitializationFile.setName(initializationFile);

		FrameworkInstance frameworkInstance = new FrameworkInstance(frameworkInitializationFile);

		List<RequestParameter> requestParameterList = new ArrayList();
		RequestParameter requestParameter = null;
		requestParameter = new RequestParameter("version", "number", Long.toString(scriptVersionNumber));
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("file", "name", fileName);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("paramlist", "list", paramList);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("paramfile", "name", paramFile);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("actionselect", "list", actionSelect);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("impersonation", "name", impersonationName);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("impersonate", "mapping", impersonationCustom);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("mode", "name", executionMode);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("settings", "list", settings);
		requestParameterList.add(requestParameter);
		requestParameter = new RequestParameter("exit", "flag", Boolean.toString(exit));
		requestParameterList.add(requestParameter);

		String scopeName = "";
		if (executionMode.equalsIgnoreCase("script")) {
			scopeName = scriptName;
		} else if (executionMode.equalsIgnoreCase("file")) {
			scopeName = fileName;
		} else {
			System.out.println("script.exec.mode.invalid");
			System.exit(1);
		}

		Request request = new Request("script", Long.toString(System.currentTimeMillis()), scriptName, "", 1, "",
				scopeName, environmentName, "", userName, userPassword, requestParameterList);

		if (server) {
			Requestor.getInstance(frameworkInstance).submit(request);
		} else {
			// TODO update to use executor
			ScriptLaunchOperation.execute(frameworkInstance, request);
		}
	}

}