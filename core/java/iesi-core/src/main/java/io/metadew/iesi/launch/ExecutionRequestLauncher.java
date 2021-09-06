package io.metadew.iesi.launch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.http.request.*;
import io.metadew.iesi.connection.http.response.HttpResponse;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilder;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
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
import org.apache.commons.cli.*;
import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class ExecutionRequestLauncher {

    public static void main(String[] args) throws ScriptExecutionRequestBuilderException, ExecutionRequestBuilderException, ParseException, IOException, URISyntaxException, HttpRequestBuilderException, NoSuchAlgorithmException, KeyManagementException {
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
        FrameworkCrypto.getInstance();
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
                        new ScriptExecutionRequestParameterKey(UUID.randomUUID().toString()),
                        scriptExecutionRequestKey,
                        parameter.split("=")[0],
                        parameter.split("=")[1]));
            }
        }

        // Get impersonation input
        if (line.hasOption("impersonations")) {
            System.out.println("Option -impersonations (impersonations) value = " + Arrays.toString(line.getOptionValues("impersonations")));
            for (String impersonation : line.getOptionValues("impersonation")) {
                scriptExecutionRequestBuilder.impersonations(new ScriptExecutionRequestImpersonation(
                        new ScriptExecutionRequestImpersonationKey(UUID.randomUUID().toString()),
                        scriptExecutionRequestKey,
                        new ImpersonationKey(impersonation)));
            }
        }

        // Get the labels
        if (line.hasOption("labels")) {
            System.out.println("Option -labels (labels) value = " + Arrays.toString(line.getOptionValues("labels")));
            for (String label : line.getOptionValues("labels")) {
                executionRequestBuilder.executionRequestLabel(new ExecutionRequestLabel(
                        new ExecutionRequestLabelKey(UUID.randomUUID().toString()),
                        new ExecutionRequestKey(executionRequestId),
                        label.split("=")[0],
                        label.split("=")[1]));
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

        // TODO: listener of the REST server will pick this up
        String accessToken = loginIntoMaster();
        ExecutionRequestConfiguration.getInstance().insert(executionRequest);

//        if (serverMode.equalsIgnoreCase("off")) {
//            executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.SUBMITTED);
//            ExecutionRequestConfiguration.getInstance().update(executionRequest);
//            ExecutionRequestExecutorService.getInstance().execute(executionRequest);
//        } else if (serverMode.equalsIgnoreCase("standalone")) {
        System.out.println("RequestID=" + executionRequest.getMetadataKey().getId());
        System.out.println("ScriptExecutionRequestID=" + executionRequest.getScriptExecutionRequests().get(0).getMetadataKey().getId());
//        } else {
//            throw new RuntimeException("unknown setting for " + Configuration.getInstance().getMandatoryProperty("iesi.server.mode"));
//        }

        FrameworkInstance.getInstance().shutdown();
        System.exit(0);
    }

    private static String loginIntoMaster() throws IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException, HttpRequestBuilderException {
        String iesiEndpoint = (String) Configuration.getInstance().getMandatoryProperty("iesi.master.endpoint");
        String iesiUser = (String) Configuration.getInstance().getMandatoryProperty("iesi.master.credentials.user");
        String iesiUserPassword = (String) Configuration.getInstance().getMandatoryProperty("iesi.master.credentials.user");
        HttpRequest loginHttpRequest = new HttpRequestBuilder()
                .jsonBody(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", iesiUser, iesiUserPassword))
                .type("post")
                .header("content-type", "application/json")
                .uri(String.format("%s/api/users/logon", iesiEndpoint))
                .build();
        HttpResponse httpResponse = HttpRequestService.getInstance().send(loginHttpRequest);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            System.out.println("unable to authenticate to master");
            System.exit(1);
        }
        if (!httpResponse.getEntityContent().isPresent()) {
            System.out.println("unable to interpret login response");
            System.exit(1);
        }
        byte[] loginResponseBytes = httpResponse.getEntityContent().get();
        Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                .map(contentType -> Optional.ofNullable(contentType.getCharset())
                        .orElse(Consts.UTF_8))
                .orElse(Consts.UTF_8);
        String loginResponse = new String(loginResponseBytes, charset);
        JsonNode loginJsonNode = new ObjectMapper().readTree(loginResponse);
        if (loginJsonNode.hasNonNull("accessToken")) {
            System.out.println("unable to obtain access token from login response");
            System.exit(1);
        }
        return loginJsonNode.get("accessToken").asText();
    }

    private static String sendExecutionRequest(ExecutionRequest executionRequest, String accessToken) throws IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException, HttpRequestBuilderException {
        String iesiEndpoint = (String) Configuration.getInstance().getMandatoryProperty("iesi.master.endpoint");
        HttpRequest loginHttpRequest = new HttpRequestBuilder()
                .jsonBody(new ObjectMapper().writeValueAsString(executionRequest))
                .type("post")
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", accessToken))
                .uri(String.format("%s/api/execution-requests", iesiEndpoint))
                .build();
        HttpResponse httpResponse = HttpRequestService.getInstance().send(loginHttpRequest);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            System.out.println("unable to create execution request at master");
            System.exit(1);
        }
        if (!httpResponse.getEntityContent().isPresent()) {
            System.out.println("unable to interpret execution request response");
            System.exit(1);
        }
        byte[] executionRequestResponseBytes = httpResponse.getEntityContent().get();
        Charset charset = Optional.ofNullable(ContentType.get(httpResponse.getHttpEntity()))
                .map(contentType -> Optional.ofNullable(contentType.getCharset())
                        .orElse(Consts.UTF_8))
                .orElse(Consts.UTF_8);
        String executionRequestResponse = new String(executionRequestResponseBytes, charset);
        JsonNode executionRequestJsonNode = new ObjectMapper().readTree(executionRequestResponse);
        if (executionRequestJsonNode.hasNonNull("id")) {
            System.out.println("unable to obtain id from execution request response");
            System.exit(1);
        }
        return executionRequestJsonNode.get("is").asText();
    }

}