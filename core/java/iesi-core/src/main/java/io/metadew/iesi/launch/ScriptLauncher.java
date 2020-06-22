package io.metadew.iesi.launch;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.*;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.runtime.ExecutionRequestExecutorService;
import org.apache.commons.cli.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.util.*;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class ScriptLauncher {

    public static void main(String[] args) throws ScriptExecutionRequestBuilderException, ExecutionRequestBuilderException, ParseException, IOException {
        ThreadContext.clearAll();

        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("script").hasArg().desc("define the script name to execute").build())
                .addOption(Option.builder("version").hasArg().desc("define the version of the script to execute").build())
                .addOption(Option.builder("file").hasArg().desc("define the configuration file to execute").build())
                .addOption(Option.builder("env").hasArg().desc("define the environment name where the execution needs to take place").build())
                .addOption(Option.builder("paramlist").hasArgs().desc("define a list of parameters to use").build())
                .addOption(Option.builder("impersonation").hasArgs().desc("define impersonation name to use").build())
                .addOption(Option.builder("exit").hasArg().desc("define if an explicit exit is required").build())
                .addOption(Option.builder("password").hasArg().desc("define the password to log in with").build())
                .addOption(Option.builder("user").hasArg().desc("define the user to log in with").build())
                .addOption(Option.builder("labels").hasArgs().desc("define the user to log in with").build());

        // create the parser
        CommandLineParser parser = new DefaultParser();

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[command]", options);
            System.exit(0);
        }

        Configuration.getInstance();
        MetadataConfiguration.getInstance();
        ExecutionRequestBuilder executionRequestBuilder = new ExecutionRequestBuilder();
        String executionRequestId = UUID.randomUUID().toString();
        executionRequestBuilder.id(executionRequestId);
        ScriptExecutionRequestBuilder scriptExecutionRequestBuilder = new ScriptExecutionRequestBuilder();
        ScriptExecutionRequestKey scriptExecutionRequestKey = new ScriptExecutionRequestKey(UUID.randomUUID().toString());
        scriptExecutionRequestBuilder.scriptExecutionRequestKey(scriptExecutionRequestKey);

        // parse the command line arguments

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
                    scriptExecutionRequestBuilder.exit(true);
                    break;
                case "n":
                case "false":
                    scriptExecutionRequestBuilder.exit(false);
                    break;
                default:
                    break;
            }
        }

        // Get the script
        // Script is leading, Json option is trailing
        if (line.hasOption("script")) {
            System.out.println("Option -script (script) value = " + line.getOptionValue("script"));
            scriptExecutionRequestBuilder.mode("script");
            scriptExecutionRequestBuilder.scriptName(line.getOptionValue("script"));

            if (line.hasOption("version")) {
                System.out.println("Option -version (version) value = " + line.getOptionValue("version"));
                scriptExecutionRequestBuilder.scriptVersion(Long.parseLong(line.getOptionValue("version")));
            } else {
                System.out.println("Option -version (version) value = latest");
            }

        } else if (line.hasOption("file")) {
            System.out.println("Option -file (file) value = " + line.getOptionValue("file"));
            scriptExecutionRequestBuilder.mode("file");
            scriptExecutionRequestBuilder.fileName(line.getOptionValue("file"));
        } else {
            System.out.println("Option -script (script) or -file (file) missing");
            System.exit(1);
        }

        // Get the environment
        if (line.hasOption("env")) {
            System.out.println("Option -env (environment) value = " + line.getOptionValue("env"));
            scriptExecutionRequestBuilder.environment(line.getOptionValue("env"));
        } else {
            System.out.println("Option -env (environment) missing");
            System.exit(1);
        }

        // Get variable configurations
        if (line.hasOption("paramlist")) {
            System.out.println("Option -paramlist (parameter list) value = " + Arrays.toString(line.getOptionValues("paramlist")));
            for (String parameter : line.getOptionValues("paramlist")) {
                scriptExecutionRequestBuilder.parameter(new ScriptExecutionRequestParameter(
                        new ScriptExecutionRequestParameterKey(DigestUtils.sha256Hex(scriptExecutionRequestKey.getId() + parameter.split("=")[0])),
                        scriptExecutionRequestKey,
                        parameter.split("=")[0], parameter.split("=")[1]));
            }
        }

        // Get impersonation input
        if (line.hasOption("impersonations")) {
            System.out.println("Option -impersonations (impersonations) value = " + Arrays.toString(line.getOptionValues("impersonations")));
            for (String impersonation : line.getOptionValues("impersonation")) {
                scriptExecutionRequestBuilder.impersonations(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(DigestUtils.sha256Hex(scriptExecutionRequestKey.getId()+impersonation)),
                        scriptExecutionRequestKey,
                        new ImpersonationKey(impersonation)));
            }
        }

        // Get the user name
        if (line.hasOption("user")) {
            System.out.println("Option -user (user) value = " + line.getOptionValue("user"));
            executionRequestBuilder.user(line.getOptionValue("user"));
        }

        // Get the user password
        if (line.hasOption("password")) {
            System.out.println("Option -password (password) value = " + "*****");
            executionRequestBuilder.password(line.getOptionValue("password"));
        }
        // Get the labels
        if (line.hasOption("labels")) {
            System.out.println("Option -labels (labels) value = " + Arrays.toString(line.getOptionValues("labels")));
            for (String label : line.getOptionValues("labels")) {
                executionRequestBuilder.executionRequestLabel(new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey(DigestUtils.sha256Hex(executionRequestId+label.split("=")[0])),
                        new ExecutionRequestKey(executionRequestId),
                        label.split("=")[0], label.split("=")[1]));
            }
        }

        // Server mode
        String serverMode = "off";
        try {
            serverMode = Configuration.getInstance().getProperty("iesi.server.mode")
                    .map(settingPath -> (String) settingPath)
                    .orElse("off")
                    .toLowerCase();
            System.out.println("Setting framework.server.mode=" + serverMode);
        } catch (Exception e) {
            System.out.println("Setting framework.server.mode=off (setting.notfound)");
        }

        executionRequestBuilder.name("scriptLauncher");
        executionRequestBuilder.scope("execution_request");
        executionRequestBuilder.context("on_demand");
        ExecutionRequest executionRequest = executionRequestBuilder.build();
        scriptExecutionRequestBuilder.executionRequestKey(executionRequest.getMetadataKey());
        executionRequest.setScriptExecutionRequests(Collections.singletonList(scriptExecutionRequestBuilder.build()));

        // Calling the launch controller
        System.out.println();
        System.out.println("script.launcher.start");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        ExecutionRequestConfiguration.getInstance().insert(executionRequest);

        if (serverMode.equalsIgnoreCase("off")) {
            executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
            ExecutionRequestConfiguration.getInstance().update(executionRequest);
            ExecutionRequestExecutorService.getInstance().execute(executionRequest);
        } else if (serverMode.equalsIgnoreCase("standalone")) {
            System.out.println("RequestID=" + executionRequest.getMetadataKey().getId());
        } else {
            throw new RuntimeException("unknown setting for " + Configuration.getInstance().getMandatoryProperty("iesi.server.mode"));
        }

        FrameworkInstance.getInstance().shutdown();
    }

//    // TODO: move to service, see fwk execute script
//    private static Map<String, String> parseParameterFiles(String files) {
//        Map<String, String> parameters = new HashMap<>();
//        String[] parts = files.split(",");
//        for (String paremeterFile : parts) {
//            parameters.putAll(parseParameterFile(paremeterFile));
//        }
//        return parameters;
//    }
//
//    private static Map<String, String> parseParameterFile(String file) {
//        Map<String, String> parameters = new HashMap<>();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(file));
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                int delim = line.indexOf("=");
//                if (delim > 0) {
//                    parameters.put(line.substring(0, delim), line.substring(delim + 1));
//                }
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return parameters;
//    }
//
//    private static Map<String, String> parseParameterRepresentation(String parametersRepresentation) {
//        Map<String, String> parameters = new HashMap<>();
//        for (String parameterCombination : parametersRepresentation.split(",")) {
//            String[] parameter = parameterCombination.split("=");
//            if (parameter.length == 2) {
//                parameters.put(parameter[0], parameter[1]);
//            }
//        }
//        return parameters;
//    }

}