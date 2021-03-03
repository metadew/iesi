package io.metadew.iesi.openapi;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.logging.log4j.ThreadContext;
import org.apache.commons.cli.*;
import java.io.File;
import java.util.List;

public class OpenAPILauncher {


    public static void main(String[] args) throws ParseException {
        ThreadContext.clearAll();
        Options options = new Options()
                .addOption(Option.builder("source").hasArg().desc("File that contains openapi documentation").build())
                .addOption(Option.builder("target").hasArg().desc("Directory to save the configurations").build())
                .addOption(Option.builder("load").desc("If true, load the configurations in the database").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);


        if (!line.hasOption("source")) {
            System.out.println("Please indicate the openapi file");
            System.exit(0);
        }
        if (!line.hasOption("target")) {
            System.out.println("Please indicate the target directory file");
            System.exit(0);
        }


        Configuration.getInstance();
        FrameworkCrypto.getInstance();
        OpenAPI openAPI = init(line.getOptionValue("source"));

        List<Connection> connections = ConnectionParser.getInstance().parse(openAPI);
        List<Component> components = ComponentParser.getInstance().parse(openAPI);;



        OpenAPIGenerator.getInstance().generate(connections, components, line.getOptionValue("target"), line.hasOption("load"));

    }


    private static OpenAPI init(String docLocation) {
        System.out.println(docLocation);
        File docFile = new File(docLocation);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);

        if (result.getMessages() != null) result.getMessages().forEach(System.err::println); // validation errors and warnings

        return result.getOpenAPI();
    }
}
