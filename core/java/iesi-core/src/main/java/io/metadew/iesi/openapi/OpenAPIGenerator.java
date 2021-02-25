package io.metadew.iesi.openapi;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
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

    public void generate(List<Connection> connections, List<Component> components) {
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
