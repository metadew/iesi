package io.metadew.iesi.launch;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.runtime.ExecutionRequestListener;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        ThreadContext.clearAll();
        Options options = new Options()
                .addOption(Option.builder("help")
                        .desc("print this message").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[command]", options);
            System.exit(0);
        }

        Configuration.getInstance();
        MetadataConfiguration.getInstance();

        FrameworkInstance frameworkInstance = FrameworkInstance.getInstance();
        ExecutionRequestListener executionRequestListener= new ExecutionRequestListener();
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                executionRequestListener.shutdown();
                frameworkInstance.shutdown();
                mainThread.join(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        new Thread(executionRequestListener).start();
    }
}
