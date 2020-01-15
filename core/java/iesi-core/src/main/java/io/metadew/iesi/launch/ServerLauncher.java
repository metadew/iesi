package io.metadew.iesi.launch;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.runtime.ExecutionRequestListener;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

public class ServerLauncher {

    public static void main(String[] args) throws ParseException {
        ThreadContext.clearAll();
        Options options = new Options()
                .addOption(Option.builder("help")
                        .desc("print this message").build())
                .addOption(Option.builder("ini")
                        .hasArg()
                        .desc("define the initialization file")
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[command]", options);
            System.exit(0);
        }

        if (cmd.hasOption("ini")) {
            FrameworkInstance.getInstance().init(new FrameworkInitializationFile(cmd.getOptionValue("ini")),
                    new FrameworkExecutionContext(new Context("server", "")));
        } else {
            FrameworkInstance.getInstance().init(new FrameworkInitializationFile(),
                    new FrameworkExecutionContext(new Context("server", "")));
        }
        FrameworkInstance frameworkInstance = FrameworkInstance.getInstance();
        ExecutionRequestListener executionRequestListener= new ExecutionRequestListener();
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                executionRequestListener.shutdown();
                frameworkInstance.shutdown();
                mainThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        new Thread(executionRequestListener).start();
    }
}
