package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OpenAPIGenerator {
    private static OpenAPIGenerator instance;

    private OpenAPIGenerator() {}


    public static synchronized OpenAPIGenerator getInstance() {
        if (instance  == null) {
            instance = new OpenAPIGenerator();
        }
        return instance;
    }

    public void generate(List<Connection> connections, List<Component> components) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            for (Component component : components) {
                writer.writeValue(
                        new File(".." + File.separator + "metadata" + File.separator + "in" + File.separator + "new" +
                                File.separator +  "component_" + component.getName() + "_v" +
                                component.getMetadataKey().getVersionNumber() + ".json")
                        , component);
                ComponentConfiguration.getInstance().insert(component);
            }
            for (Connection connection : connections) {
                writer.writeValue(
                        new File(".." + File.separator + "metadata" + File.separator + "in" + File.separator + "new" +
                                File.separator +  "Connections.json"),
                        connection);
                ConnectionConfiguration.getInstance().insert(connection);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
