package io.metadew.iesi.launch.operation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ScriptLaunchOperation {


	private static final Logger LOGGER = LogManager.getLogger();

//	public static void execute(Request request) throws ScriptExecutionBuildException {
//		String actionSelect = "";
//		String environmentName = request.getContext();
//		String executionMode = "";
//		boolean exit = true;
//		String fileName = "";
//		String impersonationName = "";
//		String impersonationCustom = "";
//		String paramFile = "";
//		String paramList = "";
//		long scriptVersionNumber = -1;
//		String settings = "";
//		String userName = request.getUser();
//		String userPassword = request.getPassword();
//
//		for (RequestParameter requestParameter : request.getParameters()) {
//			switch (requestParameter.getType().toLowerCase()) {
//			case "actionselect":
//				actionSelect = requestParameter.getValue();
//				break;
//			case "exit":
//				exit = Boolean.valueOf(requestParameter.getValue());
//				break;
//			case "file":
//				fileName = requestParameter.getValue();
//				break;
//			case "impersonate":
//				impersonationCustom = requestParameter.getValue();
//				break;
//			case "impersonation":
//				impersonationName = requestParameter.getValue();
//				break;
//			case "mode":
//				executionMode = requestParameter.getValue();
//				break;
//			case "paramfile":
//				paramFile = requestParameter.getValue();
//				break;
//			case "paramlist":
//				paramList = requestParameter.getValue();
//				break;
//			case "version":
//				scriptVersionNumber = Long.parseLong(requestParameter.getValue());
//				break;
//			case "settings":
//				settings = requestParameter.getValue();
//				break;
//			default:
//				break;
//			}
//		}
//
//		String scriptName = "";
//		if (executionMode.equalsIgnoreCase("script")) {
//			scriptName = request.getScope();
//		} else if (executionMode.equalsIgnoreCase("file")) {
//			scriptName= FileTools.getFileName(new File(request.getScope()),false);
//		} else {
//			System.out.println("script.exec.mode.invalid");
//			System.exit(1);
//		}
//
//		// Create framework execution
//
//		// Logging
//		LOGGER.info(new IESIMessage("option.script=" + scriptName));
//		LOGGER.info(new IESIMessage("option.version=" + scriptVersionNumber));
//		LOGGER.info(new IESIMessage("option.file=" + fileName));
//		LOGGER.info(new IESIMessage("option.env=" + environmentName));
//		LOGGER.info(new IESIMessage("option.paramlist=" + paramList));
//		LOGGER.info(new IESIMessage("option.paramfile=" + paramFile));
//		LOGGER.info(new IESIMessage("option.actionselect=" + actionSelect));
//		LOGGER.info(new IESIMessage("option.settings=" + settings));
//		LOGGER.info(new IESIMessage("option.impersonation=" + impersonationName));
//		LOGGER.info(new IESIMessage("option.impersonate=" + impersonationCustom));
//		LOGGER.info(new IESIMessage("option.user=" + userName));
//		LOGGER.info(new IESIMessage("option.password=" + (userPassword.isEmpty() ? "" : "******")));
//
//		// User authentication
//		// TODO: move outside server logic
//		if (FrameworkControl.getInstance().getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("guard.authenticate").get()).equalsIgnoreCase("y")) {
//
//			if (userName.isEmpty()) {
//				throw new RuntimeException("guard.user.name.missing");
//			}
//
//			if (userPassword.isEmpty()) {
//				throw new RuntimeException("guard.user.password.missing");
//			}
//
//			UserAccessConfiguration userAccessConfiguration = new UserAccessConfiguration();
//			UserAccess userAccess = userAccessConfiguration.doUserLogin(userName, userPassword);
//
//			if (userAccess.isException()) {
//				LOGGER.info(new IESIMessage("guard.user.exception=" + userAccess.getExceptionMessage()));
//				LOGGER.info(new IESIMessage("guard.user.denied"));
//				throw new RuntimeException("guard.user.denied");
//			}
//
//		}
//		// TODO link user access into framework
//
//		// Get the Script
//		Optional<Script> script = Optional.empty();
//		if (executionMode.equalsIgnoreCase("script")) {
//			ScriptConfiguration scriptConfiguration = new ScriptConfiguration();
//			if (scriptVersionNumber == -1) {
//				script = scriptConfiguration.get(scriptName);
//			} else {
//				script = scriptConfiguration.get(scriptName, scriptVersionNumber);
//			}
//			if (!script.isPresent()) {
//				System.out.println("No script found for execution");
//				System.exit(1);
//			}
//
//		} else if (executionMode.equalsIgnoreCase("file")) {
//			File file = new File(fileName);
//			if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
//				JsonInputOperation jsonInputOperation = new JsonInputOperation(fileName);
//				script = jsonInputOperation.getScript();
//			} else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
//				YamlInputOperation yamlInputOperation = new YamlInputOperation(fileName);
//				script = yamlInputOperation.getScript();
//			}
//			if (!script.isPresent()) {
//				System.out.println("No script found for execution");
//				System.exit(1);
//			}
//		} else {
//			System.out.println("script.exec.mode.invalid");
//			System.exit(1);
//		}
//
//		ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
//				.script(script.get())
//				.exitOnCompletion(false)
//				.paramList(paramList)
//				.paramFile(paramFile)
//				.actionSelectOperation(new ActionSelectOperation(actionSelect))
//				.environment(environmentName)
//				.build();
//
//		scriptExecution.setImpersonations(impersonationName, impersonationCustom);
//		// always set to false - deprecated (needs to be set by the executor instead)
//
//		// Execute the Script
//		scriptExecution.execute();
//	}

}
