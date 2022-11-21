package io.metadew.iesi.launch;

import io.metadew.iesi.openapi.OpenAPIGenerator;
import io.metadew.iesi.openapi.TransformResult;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Log4j2
public class OpenAPILauncher {

    private static final String SOURCE = "source";
    private static final String TARGET = "target";
    private static final String LOAD = "load";

    private final OpenAPIGenerator openApiGenerator;

    public OpenAPILauncher(OpenAPIGenerator openApiGenerator) {
        this.openApiGenerator = openApiGenerator;
    }

    public void execute(String[] args) throws ParseException {
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
