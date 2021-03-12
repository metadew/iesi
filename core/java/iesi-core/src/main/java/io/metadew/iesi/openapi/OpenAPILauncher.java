package io.metadew.iesi.openapi;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.util.List;

@Log4j2
public class OpenAPILauncher {

    private static final String SOURCE = "source";
    private static final String TARGET = "target";
    private static final String LOAD = "load";


    public static void main(String[] args) throws ParseException {
        ThreadContext.clearAll();
        Options options = new Options()
                .addOption(Option.builder(SOURCE).hasArg().required(true).desc("File that contains openapi documentation").build())
                .addOption(Option.builder(TARGET).hasArg().required(true).desc("Directory to save the configurations").build())
                .addOption(Option.builder(LOAD).required(false).desc("If true, load the configurations in the database").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);


        Configuration.getInstance();
        FrameworkCrypto.getInstance();

        OpenAPI openAPI = init(line.getOptionValue(SOURCE));


        List<Connection> connections = ConnectionParser.getInstance().parse(openAPI);
        List<Component> components = ComponentParser.getInstance().parse(openAPI);


        OpenAPIGenerator.getInstance().generate(connections, components, line.getOptionValue(TARGET), line.hasOption(LOAD));
    }


    private static OpenAPI init(String docLocation) {
        File docFile = new File(docLocation);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);

        if (result.getMessages() != null)
            result.getMessages().forEach(log::warn); // validation errors and warnings

        return result.getOpenAPI();
    }
}
