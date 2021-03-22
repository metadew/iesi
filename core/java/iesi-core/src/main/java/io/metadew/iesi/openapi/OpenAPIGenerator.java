package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
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

@Log4j2
public class OpenAPIGenerator {
    private static OpenAPIGenerator instance;

    private OpenAPIGenerator() {
    }


    public static synchronized OpenAPIGenerator getInstance() {
        if (instance == null) {
            instance = new OpenAPIGenerator();
        }
        return instance;
    }

    public TransformResult transformFromFile(String path) {
        File docFile = new File(path);
        SwaggerParseResult result = new OpenAPIParser().readLocation(String.valueOf(docFile), null, null);
        checkForMessages(result);

        return new TransformResult(
                ConnectionParser.getInstance().parse(result.getOpenAPI()),
                ComponentParser.getInstance().parse(result.getOpenAPI())
        );
    }

    public TransformResult transformFromJsonContent(String doc) {
        SwaggerParseResult result = new OpenAPIParser().readContents(doc, null, null);
        checkForMessages(result);

        return new TransformResult(
                ConnectionParser.getInstance().parse(result.getOpenAPI()),
                ComponentParser.getInstance().parse(result.getOpenAPI())
        );
    }


    private void checkForMessages(SwaggerParseResult result) {
        if (result.getMessages() != null) {
            result.getMessages().forEach(log::warn);
        }
    }

    public void generate(TransformResult transformResult, String target, boolean load) {
        Configuration.getInstance();
        FrameworkCrypto.getInstance();
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
            ComponentConfiguration.getInstance().insert(component);
        } catch (MetadataAlreadyExistsException e) {
            ComponentConfiguration.getInstance().update(component);
        }
    }

    private void saveConnection(Connection connection) {
        try {
            ConnectionConfiguration.getInstance().insert(connection);
        } catch (MetadataAlreadyExistsException e) {
            ConnectionConfiguration.getInstance().update(connection);
        }
    }

}
