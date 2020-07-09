package io.metadew.iesi.common.configuration.metadata.repository;

import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorHandler;
import io.metadew.iesi.metadata.repository.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MetadataRepositoryService implements IMetadataRepositoryService {


    private static MetadataRepositoryService INSTANCE;

    public synchronized static MetadataRepositoryService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryService();
        }
        return INSTANCE;
    }

    private MetadataRepositoryService() {
    }


    @Override
    public List<MetadataRepository> convert(MetadataRepositoryDefinition metadataRepositoryDefinition) throws Exception {
        // TODO: generate mist of MetadataRepositories, parse categories as list
        List<MetadataRepository> metadataRepositories = new ArrayList<>();
        for (String category : metadataRepositoryDefinition.getCategories()) {
            if (category.equalsIgnoreCase("general")) {
                // Make all repositories
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("design")) {
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("connectivity")) {
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("control")) {
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("trace")) {
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("result")) {
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("execution")) {
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandler.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else {
                throw new RuntimeException(MessageFormat.format("No Metadata repository can be created for {0}", category));
            }
        }
        return metadataRepositories;
    }
}
