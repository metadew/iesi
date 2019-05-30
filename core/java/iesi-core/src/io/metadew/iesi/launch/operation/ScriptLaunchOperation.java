package io.metadew.iesi.launch.operation;

import java.io.File;
import java.util.Optional;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkExecutionSettings;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.guard.configuration.UserAccessConfiguration;
import io.metadew.iesi.guard.definition.UserAccess;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.JsonInputOperation;
import io.metadew.iesi.script.operation.YamlInputOperation;

public final class ScriptLaunchOperation {

	public static void execute(FrameworkInstance frameworkInstance, Request request) {
		String actionSelect = "";
		String environmentName = request.getContext();
		String executionMode = "";
		boolean exit = true;
		String fileName = "";
		String impersonationName = "";
		String impersonationCustom = "";
		String paramFile = "";
		String paramList = "";
		long scriptVersionNumber = -1;
		String settings = "";
		String userName = request.getUser();
		String userPassword = request.getPassword();

		for (RequestParameter requestParameter : request.getParameters()) {
			switch (requestParameter.getType().toLowerCase()) {
			case "actionselect":
				actionSelect = requestParameter.getValue();
				break;
			case "exit":
				exit = Boolean.valueOf(requestParameter.getValue());
				break;
			case "file":
				fileName = requestParameter.getValue();
				break;
			case "impersonate":
				impersonationCustom = requestParameter.getValue();
				break;
			case "impersonation":
				impersonationName = requestParameter.getValue();
				break;
			case "mode":
				executionMode = requestParameter.getValue();
				break;
			case "paramfile":
				paramFile = requestParameter.getValue();
				break;
			case "paramlist":
				paramList = requestParameter.getValue();
				break;
			case "scriptversionnumber":
				scriptVersionNumber = Long.parseLong(requestParameter.getValue());
				break;
			case "settings":
				settings = requestParameter.getValue();
				break;
			default:
				break;
			}
		}

		String scriptName = "";
		if (executionMode.equalsIgnoreCase("script")) {
			scriptName = request.getScope();
		} else if (executionMode.equalsIgnoreCase("file")) {
			scriptName= FileTools.getFileName(new File(request.getScope()),false);
		} else {
			System.out.println("script.exec.mode.invalid");
			System.exit(1);
		}
		
		// Create framework execution
		FrameworkExecutionSettings frameworkExecutionSettings = new FrameworkExecutionSettings(settings);
		Context context = new Context();
		context.setName("script");
		context.setScope(scriptName);

		FrameworkExecution frameworkExecution = new FrameworkExecution(frameworkInstance,
				new FrameworkExecutionContext(context), frameworkExecutionSettings,
				frameworkInstance.getFrameworkInitializationFile());

		// Logging
		frameworkExecution.getFrameworkLog().log("option.script=" + scriptName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.version=" + scriptVersionNumber, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.file=" + fileName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.env=" + environmentName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.paramlist=" + paramList, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.paramfile=" + paramFile, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.actionselect=" + actionSelect, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.settings=" + settings, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.impersonation=" + impersonationName, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.impersonate=" + impersonationCustom, Level.INFO);
		frameworkExecution.getFrameworkLog().log("option.user=" + userName, Level.INFO);
		if (userPassword.isEmpty()) {
			frameworkExecution.getFrameworkLog().log("option.password=" + "", Level.INFO);
		} else {
			frameworkExecution.getFrameworkLog().log("option.password=" + "*****", Level.INFO);
		}

		// User authentication
		// TODO: move outside server logic
		if (frameworkExecution.getFrameworkControl().getProperty(frameworkExecution.getFrameworkConfiguration()
				.getSettingConfiguration().getSettingPath("guard.authenticate").get()).equalsIgnoreCase("y")) {

			if (userName.isEmpty()) {
				throw new RuntimeException("guard.user.name.missing");
			}

			if (userPassword.isEmpty()) {
				throw new RuntimeException("guard.user.password.missing");
			}

			UserAccessConfiguration userAccessConfiguration = new UserAccessConfiguration(frameworkExecution);
			UserAccess userAccess = userAccessConfiguration.doUserLogin(userName, userPassword);

			if (userAccess.isException()) {
				frameworkExecution.getFrameworkLog().log("guard.user.exception=" + userAccess.getExceptionMessage(),
						Level.INFO);
				frameworkExecution.getFrameworkLog().log("guard.user.denied", Level.INFO);
				throw new RuntimeException("guard.user.denied");
			}

		}
		// TODO link user access into framework

		// Get the Script
		ScriptConfiguration scriptConfiguration = null;
		Optional<Script> script = Optional.empty();
		if (executionMode.equalsIgnoreCase("script")) {
			scriptConfiguration = new ScriptConfiguration(frameworkExecution.getFrameworkInstance());
			if (scriptVersionNumber == -1) {
				script = scriptConfiguration.getScript(scriptName);
			} else {
				script = scriptConfiguration.getScript(scriptName, scriptVersionNumber);
			}
			if (!script.isPresent()) {
				System.out.println("No script found for execution");
				System.exit(1);
			}

		} else if (executionMode.equalsIgnoreCase("file")) {
			File file = new File(fileName);
			if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
				JsonInputOperation jsonInputOperation = new JsonInputOperation(frameworkExecution, fileName);
				script = jsonInputOperation.getScript();
			} else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
				YamlInputOperation yamlInputOperation = new YamlInputOperation(frameworkExecution, fileName);
				script = yamlInputOperation.getScript();
			}
			if (!script.isPresent()) {
				System.out.println("No script found for execution");
				System.exit(1);
			}
		} else {
			System.out.println("script.exec.mode.invalid");
			System.exit(1);
		}

		ScriptExecution scriptExecution = new ScriptExecution(frameworkExecution, script.get());
		scriptExecution.initializeAsRootScript(environmentName);
		scriptExecution.setActionSelectOperation(new ActionSelectOperation(actionSelect));
		scriptExecution.setImpersonations(impersonationName, impersonationCustom);
		scriptExecution.setExitOnCompletion(exit);

		if (!paramList.equals("")) {
			scriptExecution.setParamList(paramList);
		}
		if (!paramFile.equals("")) {
			scriptExecution.setParamFile(paramFile);
		}

		// Execute the Script
		scriptExecution.execute();
	}

}
