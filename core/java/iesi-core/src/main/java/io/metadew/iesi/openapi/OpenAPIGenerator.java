package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class OpenAPIGenerator {
    private static OpenAPIGenerator instance;

    private OpenAPIGenerator() {}



    public synchronized static OpenAPIGenerator getInstance() {
        if (instance == null) {
            instance = new OpenAPIGenerator();
        }
        return instance;
    }

    public void generate(List<Connection> connections, List<Component> components, String target, boolean load) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());


        try {
            for (Component component : components) {
                saveComponentInDirectory(writer,target, component, load);
                if (load) saveComponent(component);
            }
            for (Connection connection : connections) {
                saveConnectionInDirectory(writer, target,connection, load);
                if(load) saveConnection(connection);
            }
        } catch (IOException e) {
            log.warn("The target directory doesn't exist, the process is aborted");
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
