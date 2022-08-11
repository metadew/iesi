package io.metadew.iesi.openapi;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

@Log4j2
public class OpenAPILauncher {

    private static final String SOURCE = "source";
    private static final String TARGET = "target";
    private static final String LOAD = "load";
    //TODO: DEPENDENCY INJECTION ONCE THIS CLASS BECOME A SPRING APP
    private static final OpenAPIGenerator openApiGenerator = SpringContext.getBean(OpenAPIGenerator.class);

    public static void main(String[] args) throws ParseException {
        ThreadContext.clearAll();
        Options options = new Options()
                .addOption(Option.builder(SOURCE).hasArg().required(true).desc("File that contains openapi documentation").build())
                .addOption(Option.builder(TARGET).hasArg().required(true).desc("Directory to save the configurations").build())
                .addOption(Option.builder(LOAD).required(false).desc("If true, load the configurations in the database").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        TransformResult transformResult = openApiGenerator.transformFromFile(line.getOptionValue(SOURCE));
        openApiGenerator.generate(transformResult, line.getOptionValue(TARGET),line.hasOption(LOAD));
    }
}
