package io.metadew.iesi.launch;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
@Component
@Lazy
@Log4j2
public class ExecutionLauncher {

    private final Configuration configuration;
    private final ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration;
    private final FrameworkInstance frameworkInstance;
    private final ScriptExecutorService scriptExecutorService;

    public ExecutionLauncher(Configuration configuration,
                             ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration,
                             FrameworkInstance frameworkInstance,
                             ScriptExecutorService scriptExecutorService
    ) {
        this.configuration = configuration;
        this.scriptExecutionRequestConfiguration = scriptExecutionRequestConfiguration;
        this.frameworkInstance = frameworkInstance;
        this.scriptExecutorService = scriptExecutorService;
    }


    public void execute(String[] args) throws ParseException {
        ThreadContext.clearAll();

        Options options = new Options().addOption(Option.builder("help").desc("print this message").build()).addOption(Option.builder("scriptExecutionRequestKey").hasArg().desc("identified of the script exection request to execute").build()).addOption(Option.builder("debugMode").hasArg().desc("Define if logs should be enabled for the execution").build());

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

        if (line.hasOption("debugMode") && line.getOptionValue("debugMode").equalsIgnoreCase("Y")) {
            String log4j2File = (configuration.getProperty("iesi.home").orElse("")) + "/lib/log4j2.xml";
            if (new File(log4j2File).exists()) {
                Configurator.initialize(null, log4j2File);
            } else {
                log.warn(String.format("The file %s does not exist", log4j2File));
            }
        }

        ScriptExecutionRequest scriptExecutionRequest;
        if (line.hasOption("scriptExecutionRequestKey")) {
            log.info("Option -scriptExecutionRequestKey (scriptExecutionRequestKey) value = " + line.getOptionValue("scriptExecutionRequestKey"));
            ScriptExecutionRequestKey scriptExecutionRequestKey = new ScriptExecutionRequestKey(line.getOptionValue("scriptExecutionRequestKey"));
            Optional<ScriptExecutionRequest> optionalScriptExecutionRequest = scriptExecutionRequestConfiguration.get(scriptExecutionRequestKey);
            if (optionalScriptExecutionRequest.isPresent()) {
                scriptExecutionRequest = optionalScriptExecutionRequest.get();
            } else {
                log.info("Cannot find scriptExecutionRequestKey %s%n", scriptExecutionRequestKey.getId());
                System.exit(1);
                return;
            }
        } else {
            log.info("Option -scriptExecutionRequestKey (scriptExecutionRequestKey) missing");
            System.exit(1);
            return;
        }
        scriptExecutorService.execute(scriptExecutionRequest);

        frameworkInstance.shutdown();
        System.exit(0);
    }


}