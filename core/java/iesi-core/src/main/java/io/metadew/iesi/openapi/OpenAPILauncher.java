package io.metadew.iesi.openapi;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
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
                .addOption(Option.builder("source").hasArg().desc("File that contains openapi documentation").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (!line.hasOption("source")) {
            System.out.println("Please indicate the openapi file");
            System.exit(0);
        }

        Configuration.getInstance();
        FrameworkCrypto.getInstance();
        OpenAPI openAPI = init(line.getOptionValue("source"));

        List<Connection> connections = ConnectionParser.getInstance().parse(openAPI);
        List<Component> components = ComponentParser.getInstance().parse(openAPI);
        ;

        if (false) {
            connections.forEach(connection -> {
                try {
                    ConnectionConfiguration.getInstance().insert(connection);
                } catch (MetadataAlreadyExistsException e) {
                    ConnectionConfiguration.getInstance().update(connection);
                }
            });
            components.forEach(component -> {
                try {
                    ComponentConfiguration.getInstance().insert(component);
                } catch (MetadataAlreadyExistsException e) {
                    ComponentConfiguration.getInstance().update(component);
                }
            });
        }

        OpenAPIGenerator.getInstance().generate(connections, components);

    }


    private static OpenAPI init(String docLocation) {
        File docFile = new File(docLocation);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);

        if (result.getMessages() != null)
            result.getMessages().forEach(System.err::println); // validation errors and warnings

        return result.getOpenAPI();
    }
}
