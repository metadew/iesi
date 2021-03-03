package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAPIGenerator {
    private static OpenAPIGenerator INSTANCE;

    private OpenAPIGenerator() {}


    public synchronized static OpenAPIGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenAPIGenerator();
        }
        return INSTANCE;
    }

    public void generate(List<Connection> connections, List<Component> components, String target, boolean load) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());


        try {
            for (Component component : components) {
                saveComponentInDirectory(writer,target, component, load);
                if (load) ComponentConfiguration.getInstance().insert(component);
            }
            for (Connection connection : connections) {
                saveConnectionInDirectory(writer, target,connection, load);
                if(load) ConnectionConfiguration.getInstance().insert(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveComponentInDirectory(ObjectWriter writer, String target, Component component, boolean load) throws IOException {
        writer.writeValue(
                new File(
                        target + File.separator + "component_" + component.getName() + "_v" +
                                component.getMetadataKey().getVersionNumber() + ".json"), component);

    }
    private void saveConnectionInDirectory(ObjectWriter writer, String target, Connection connection, boolean load) throws IOException {
        writer.writeValue(new File(
                target + File.separator + "Connections.json"
        ), connection);

    }


}
