package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
@org.springframework.stereotype.Component
public class OpenAPIGenerator {

    private final ComponentConfiguration componentConfiguration;
    private final ConnectionConfiguration connectionConfiguration;
    private final ComponentParser componentParser;
    private final ConnectionParser connectionParser;

    public OpenAPIGenerator(ComponentConfiguration componentConfiguration,
                            ConnectionConfiguration connectionConfiguration,
                            ComponentParser componentParser,
                            ConnectionParser connectionParser) {
        this.componentConfiguration = componentConfiguration;
        this.connectionConfiguration = connectionConfiguration;
        this.componentParser = componentParser;
        this.connectionParser = connectionParser;
    }

    public TransformResult transformFromFile(String path) throws SwaggerParserException {
        File docFile = new File(path);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);
        checkForMessages(result.getMessages());

        return new TransformResult(
                connectionParser.parse(result.getOpenAPI()),
                componentParser.parse(result.getOpenAPI()),
                result.getOpenAPI().getInfo().getTitle(),
                result.getOpenAPI().getInfo().getVersion()
        );
    }

    public TransformResult transformFromJsonContent(String doc) throws SwaggerParserException {
        SwaggerParseResult result = new OpenAPIParser().readContents(doc, null, null);
        checkForMessages(result.getMessages());

        return new TransformResult(
                connectionParser.parse(result.getOpenAPI()),
                componentParser.parse(result.getOpenAPI()),
                result.getOpenAPI().getInfo().getTitle(),
                result.getOpenAPI().getInfo().getVersion()
        );
    }


    private void checkForMessages(List<String> messages) {
        if (!messages.isEmpty()) {
            throw new SwaggerParserException(messages);
        }
    }

    public void generate(TransformResult transformResult, String target, boolean load) {
        try {
            for (Component component : transformResult.getComponents()) {
                saveComponentInDirectory(target, component);
                if (load) {
                    saveComponent(component);
                }
            }
            for (Connection connection : transformResult.getConnections()) {
                saveConnectionInDirectory(target, connection);
                if (load) {
                    saveConnection(connection);
                }
            }
        } catch (IOException e) {
            log.warn(e);
        }
    }

    private void saveComponentInDirectory(String target, Component component) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(
                new File(
                        target + File.separator + "component_" + component.getName() + "_v" +
                                component.getMetadataKey().getVersionNumber() + ".json"), component);
    }

    private void saveConnectionInDirectory(String target, Connection connection) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        String connectionName = connection.getMetadataKey().getName();
        String environmentName = connection.getMetadataKey().getEnvironmentKey().getName();
        writer.writeValue(new File(
                target + File.separator + connectionName + "_" + environmentName + ".json"
        ), connection);
    }

    private void saveComponent(Component component) {
        try {
            componentConfiguration.insert(component);
        } catch (MetadataAlreadyExistsException e) {
            componentConfiguration.update(component);
        }
    }

    private void saveConnection(Connection connection) {
        try {
            connectionConfiguration.insert(connection);
        } catch (MetadataAlreadyExistsException e) {
            connectionConfiguration.update(connection);
        }
    }

}
