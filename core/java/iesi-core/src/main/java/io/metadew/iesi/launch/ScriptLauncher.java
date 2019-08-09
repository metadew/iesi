package io.metadew.iesi.launch;

import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.runtime.Executor;
import io.metadew.iesi.runtime.Requestor;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class ScriptLauncher {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		ThreadContext.clearAll();

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

		// create the parser
		CommandLineParser parser = new DefaultParser();
		boolean exit = true;
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
		String userName = "admin";
		String userPassword = "admin";
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

		// Create framework instance
		FrameworkInstance frameworkInstance = FrameworkInstance.getInstance();
		frameworkInstance.init(new FrameworkInitializationFile(initializationFile));

		// Server mode
		String serverMode = "off";
		try {
			serverMode = frameworkInstance.getFrameworkControl().getProperty(frameworkInstance.getFrameworkConfiguration().getSettingConfiguration().getSettingPath("server.mode").get()).toLowerCase();
			System.out.println("Setting framework.server.mode=" + serverMode);
		} catch (Exception e) {
			System.out.println("Setting framework.server.mode=off (setting.notfound)");
		}

		// Calling the launch controller
		System.out.println();
		System.out.println("script.launcher.start");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		List<RequestParameter> requestParameters = new ArrayList();
		requestParameters.add(new RequestParameter("version", "number", Long.toString(scriptVersionNumber)));
		requestParameters.add(new RequestParameter("file", "name", fileName));
		requestParameters.add(new RequestParameter("paramlist", "list", paramList));
		requestParameters.add(new RequestParameter("paramfile", "name", paramFile));
		requestParameters.add(new RequestParameter("actionselect", "list", actionSelect));
		requestParameters.add(new RequestParameter("impersonation", "name", impersonationName));
		requestParameters.add(new RequestParameter("impersonate", "mapping", impersonationCustom));
		requestParameters.add(new RequestParameter("mode", "name", executionMode));
		requestParameters.add(new RequestParameter("settings", "list", settings));
		requestParameters.add(new RequestParameter("exit", "flag", Boolean.toString(exit)));

		String scopeName = "";
		if (executionMode.equalsIgnoreCase("script")) {
			scopeName = scriptName;
		} else if (executionMode.equalsIgnoreCase("file")) {
			scopeName = fileName;
		} else {
			System.out.println("script.exec.mode.invalid");
			System.exit(1);
		}

		// TODO replace local date time in tool across solution
		Request request = new Request("script", LocalDateTime.now().toString(), scriptName, "", 1, "",
				scopeName, environmentName, "admin", userName, userPassword, requestParameters);

		if (serverMode.equalsIgnoreCase("off")) {
			Executor.getInstance().execute(request);
		} else if (serverMode.equalsIgnoreCase("standalone")) {
			Requestor.getInstance().submit(request);
		} else {
			throw new RuntimeException("unknown setting for " + FrameworkSettingConfiguration.getInstance().getSettingPath("server.mode").get());
		}
	}

}