package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.logging.log4j.ThreadContext;
import org.apache.commons.cli.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAPILauncher {

    public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

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
        List<Component> components = ComponentParser.getInstance().parse(openAPI);;



        generate(connections, components);

    }


    private static OpenAPI init(String docLocation) {
        File docFile = new File(docLocation);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);

        if (result.getMessages() != null) result.getMessages().forEach(System.err::println); // validation errors and warnings

        return result.getOpenAPI();
    }


    private static void generate(List<Connection> connections, List<Component> components) {
        MetadataRepositoryOperation metadataRepositoryOperation = new MetadataRepositoryOperation();
        List<MetadataRepository> metadataRepositories = new ArrayList<>();
        metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
        metadataRepositories.add(MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository());

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            for (Component component : components) {
                writer.writeValue(
                        new File(".." + File.separator + "metadata" + File.separator + "in" + File.separator + "new" +
                                File.separator +  "component_" + component.getName() + "_v" +
                                component.getMetadataKey().getVersionNumber() + ".json")
                        , component);
            }
            for (Connection connection : connections) {
                writer.writeValue(
                        new File(".." + File.separator + "metadata" + File.separator + "in" + File.separator + "new" +
                                File.separator +  "Connections.json"),
                        connection);
            }

            metadataRepositoryOperation.loadMetadataRepository(metadataRepositories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
