package io.metadew.iesi.launch;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.util.Optional;

/**
 * The execution launcher is entry point to launch all automation scripts.
 *
 * @author peter.billen
 */
public class ExecutionLauncher {

    public static void main(String[] args) throws ParseException, IOException {
        ThreadContext.clearAll();

        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("scriptExecutionRequestKey").hasArg().desc("identified of the script exection request to execute").build())
                .addOption(Option.builder("debugMode").hasArg().desc("Define if logs should be enabled for the execution").build());

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
            Configurator.initialize(null, Configuration.getInstance().getProperty("iesi.home").get() + "/lib/log4j2.xml");
        }

        ScriptExecutionRequest scriptExecutionRequest;
        if (line.hasOption("scriptExecutionRequestKey")) {
            System.out.println("Option -scriptExecutionRequestKey (scriptExecutionRequestKey) value = " + line.getOptionValue("scriptExecutionRequestKey"));
            ScriptExecutionRequestKey scriptExecutionRequestKey = new ScriptExecutionRequestKey(line.getOptionValue("scriptExecutionRequestKey"));
            Optional<ScriptExecutionRequest> optionalScriptExecutionRequest = ScriptExecutionRequestConfiguration.getInstance().get(scriptExecutionRequestKey);
            if (optionalScriptExecutionRequest.isPresent()) {
                scriptExecutionRequest = optionalScriptExecutionRequest.get();
            } else {
                System.out.printf("Cannot find scriptExecutionRequestKey %s%n", scriptExecutionRequestKey.getId());
                System.exit(1);
                return;
            }
        } else {
            System.out.println("Option -scriptExecutionRequestKey (scriptExecutionRequestKey) missing");
            System.exit(1);
            return;
        }
        ScriptExecutorService.getInstance().execute(scriptExecutionRequest);

        FrameworkInstance.getInstance().shutdown();
        System.exit(0);
    }


}