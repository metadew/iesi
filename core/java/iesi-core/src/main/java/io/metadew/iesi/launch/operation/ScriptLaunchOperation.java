package io.metadew.iesi.launch.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.definition.FrameworkRunIdentifier;
import io.metadew.iesi.framework.execution.*;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.guard.configuration.UserAccessConfiguration;
import io.metadew.iesi.guard.definition.UserAccess;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.script.operation.ActionSelectOperation;
import io.metadew.iesi.script.operation.JsonInputOperation;
import io.metadew.iesi.script.operation.YamlInputOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

public final class ScriptLaunchOperation {


	private static final Logger LOGGER = LogManager.getLogger();

	public static void execute(FrameworkInstance frameworkInstance, Request request, FrameworkRunIdentifier frameworkRunIdentifier) throws ScriptExecutionBuildException {
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
			case "version":
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
		Context context = new Context("script", scriptName);

		FrameworkExecution frameworkExecution = new FrameworkExecution(new FrameworkExecutionContext(context), frameworkExecutionSettings, frameworkRunIdentifier);

		// Logging
		LOGGER.info(new IESIMessage("option.script=" + scriptName));
		LOGGER.info(new IESIMessage("option.version=" + scriptVersionNumber));
		LOGGER.info(new IESIMessage("option.file=" + fileName));
		LOGGER.info(new IESIMessage("option.env=" + environmentName));
		LOGGER.info(new IESIMessage("option.paramlist=" + paramList));
		LOGGER.info(new IESIMessage("option.paramfile=" + paramFile));
		LOGGER.info(new IESIMessage("option.actionselect=" + actionSelect));
		LOGGER.info(new IESIMessage("option.settings=" + settings));
		LOGGER.info(new IESIMessage("option.impersonation=" + impersonationName));
		LOGGER.info(new IESIMessage("option.impersonate=" + impersonationCustom));
		LOGGER.info(new IESIMessage("option.user=" + userName));
		LOGGER.info(new IESIMessage("option.password=" + (userPassword.isEmpty() ? "" : "******")));

		// User authentication
		// TODO: move outside server logic
		if (FrameworkControl.getInstance().getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("guard.authenticate").get()).equalsIgnoreCase("y")) {

			if (userName.isEmpty()) {
				throw new RuntimeException("guard.user.name.missing");
			}

			if (userPassword.isEmpty()) {
				throw new RuntimeException("guard.user.password.missing");
			}

			UserAccessConfiguration userAccessConfiguration = new UserAccessConfiguration(frameworkExecution);
			UserAccess userAccess = userAccessConfiguration.doUserLogin(userName, userPassword);

			if (userAccess.isException()) {
				LOGGER.info(new IESIMessage("guard.user.exception=" + userAccess.getExceptionMessage()));
				LOGGER.info(new IESIMessage("guard.user.denied"));
				throw new RuntimeException("guard.user.denied");
			}

		}
		// TODO link user access into framework

		// Get the Script
		Optional<Script> script = Optional.empty();
		if (executionMode.equalsIgnoreCase("script")) {
			ScriptConfiguration scriptConfiguration = new ScriptConfiguration(FrameworkInstance.getInstance());
			if (scriptVersionNumber == -1) {
				script = scriptConfiguration.get(scriptName);
			} else {
				script = scriptConfiguration.get(scriptName, scriptVersionNumber);
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

		ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
				.frameworkExecution(frameworkExecution)
				.script(script.get())
				.exitOnCompletion(false)
				.paramList(paramList)
				.paramFile(paramFile)
				.actionSelectOperation(new ActionSelectOperation(actionSelect))
				.environment(environmentName)
				.build();

		scriptExecution.setImpersonations(impersonationName, impersonationCustom);
		// always set to false - deprecated (needs to be set by the executor instead)

		// Execute the Script
		scriptExecution.execute();
	}

}
