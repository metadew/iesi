package io.metadew.iesi.server.rest.cli;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.server.rest.client.IMasterClient;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestPostDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestPostDto;
import io.metadew.iesi.server.rest.user.AuthenticationRequest;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConditionalOnNotWebApplication
@Log4j2
public class ConsoleApplication implements CommandLineRunner {

    private final ConfigurableApplicationContext context;
    private final IMasterClient masterClient;
    private final FrameworkCrypto frameworkCrypto;
    private final String defaultIesiUser;
    private final String defaultIesiPassword;

    @Autowired
    public ConsoleApplication(ConfigurableApplicationContext context, IMasterClient masterClient, FrameworkCrypto frameworkCrypto, Configuration iesiProperties) {
        this.context = context;
        this.masterClient = masterClient;
        this.frameworkCrypto = frameworkCrypto;
        this.defaultIesiUser = (String) iesiProperties.getMandatoryProperty("iesi.master.credentials.user");
        this.defaultIesiPassword = (String) iesiProperties.getMandatoryProperty("iesi.master.credentials.password");

    }

    @Override
    public void run(String... args) throws ParseException, InterruptedException {
        Options options = new Options()
                .addOption(Option.builder("launch").desc("launch table generator(s)").build())
                .addOption(Option.builder("script").hasArg().desc("define the script name to execute").build())
                .addOption(Option.builder("version").hasArg().desc("define the version of the script to execute").build())
                .addOption(Option.builder("env").hasArg().desc("define the environment name where the execution needs to take place").build())
                .addOption(Option.builder("paramlist").hasArgs().desc("define a list of parameters to use").build())
                .addOption(Option.builder("impersonation").hasArgs().desc("define impersonation name to use").build())
                .addOption(Option.builder("user").hasArg().desc("define the user to log in with").build())
                .addOption(Option.builder("labels").hasArgs().desc("define the user to log in with").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("launch")) {
            ThreadContext.clearAll();
            ThreadContext.put("type", "request");

            // Get the environment
            String user;
            String password;
            if (cmd.hasOption("user")) {
                user = cmd.getOptionValue("user");
                java.io.Console console = System.console();
                password = new String(console.readPassword("password:"));
            } else {
                log.info("Option -user (user) missing. Using default user");
                user = defaultIesiUser;
                password = frameworkCrypto.decryptIfNeeded(defaultIesiPassword);
            }

            AuthenticationResponse authenticationResponse = masterClient.login(new AuthenticationRequest(
                    user,
                    password
            )).block();

            ExecutionRequestPostDto executionRequestPostDto = new ExecutionRequestPostDto();
            ScriptExecutionRequestPostDto scriptExecutionRequestPostDto = new ScriptExecutionRequestPostDto();

            // parse the command cmd arguments

            // Get the script
            // Script is leading, Json option is trailing
            if (cmd.hasOption("script")) {
                log.info("Option -script (script) value = " + cmd.getOptionValue("script"));
                scriptExecutionRequestPostDto.setScriptName(cmd.getOptionValue("script"));

                if (cmd.hasOption("version")) {
                    log.info("Option -version (version) value = " + cmd.getOptionValue("version"));
                    scriptExecutionRequestPostDto.setScriptVersion(Long.parseLong(cmd.getOptionValue("version")));
                } else {
                    log.info("Option -version (version) value = latest");
                }
            } else {
                log.info("Option -script (script) missing");
                context.close();
                System.exit(1);
                return;
            }

            // Get the environment
            if (cmd.hasOption("env")) {
                log.info("Option -env (environment) value = " + cmd.getOptionValue("env"));
                scriptExecutionRequestPostDto.setEnvironment(cmd.getOptionValue("env"));
            } else {
                log.info("Option -env (environment) missing");
                context.close();
                System.exit(1);
                return;
            }

            // Get variable configurations
            if (cmd.hasOption("paramlist")) {
                log.info("Option -paramlist (parameter list) value = " + Arrays.toString(cmd.getOptionValues("paramlist")));
                for (String parameter : cmd.getOptionValues("paramlist")) {
                    scriptExecutionRequestPostDto.getParameters().add(new ScriptExecutionRequestParameterDto(
                            parameter.split("=")[0],
                            parameter.split("=")[1]));
                }
            }

            // Get impersonation input
            if (cmd.hasOption("impersonations")) {
                log.info("Option -impersonations (impersonations) value = " + Arrays.toString(cmd.getOptionValues("impersonations")));
                for (String impersonation : cmd.getOptionValues("impersonation")) {
                    scriptExecutionRequestPostDto.getImpersonations().add(new ScriptExecutionRequestImpersonationDto(
                            impersonation));
                }
            }

            // Get the labels
            if (cmd.hasOption("labels")) {
                log.info("Option -labels (labels) value = " + Arrays.toString(cmd.getOptionValues("labels")));
                for (String label : cmd.getOptionValues("labels")) {
                    executionRequestPostDto.getExecutionRequestLabels().add(new ExecutionRequestLabelDto(
                            label.split("=")[0],
                            label.split("=")[1]));
                }
            }

            executionRequestPostDto.setName("scriptLauncher");
            executionRequestPostDto.setScope("execution_request");
            executionRequestPostDto.setContext("on_demand");
            executionRequestPostDto.getScriptExecutionRequests().add(scriptExecutionRequestPostDto);
            ExecutionRequestDto executionRequestDto = masterClient.createExecutionRequest(
                    executionRequestPostDto,
                    authenticationResponse.getAccessToken()
            ).block();
            log.info("created execution request: " + executionRequestDto.getExecutionRequestId());
            context.close();
            System.exit(0);
        }
    }
}