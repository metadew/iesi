package io.metadew.iesi.common.configuration.metadata.repository;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorHandler;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.metadata.repository.*;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MetadataRepositoryService implements IMetadataRepositoryService {


    private static MetadataRepositoryService INSTANCE;
    private final MetadataRepositoryCoordinatorHandler metadataRepositoryCoordinatorHandler = SpringContext.getBean(MetadataRepositoryCoordinatorHandler.class);

    public synchronized static MetadataRepositoryService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryService();
        }
        return INSTANCE;
    }

    private MetadataRepositoryService() {
    }


    @Override
    public List<MetadataRepository> convert(MetadataRepositoryDefinition metadataRepositoryDefinition) {
        // TODO: generate mist of MetadataRepositories, parse categories as list
        List<MetadataRepository> metadataRepositories = new ArrayList<>();
        for (String category : metadataRepositoryDefinition.getCategories()) {
            if (category.equalsIgnoreCase("general")) {
                RepositoryCoordinator repositoryCoordinator = metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator());
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
                metadataRepositories.add(new DataMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        repositoryCoordinator));
            } else if (category.equalsIgnoreCase("design")) {
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("data")) {
                metadataRepositories.add(new DataMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("connectivity")) {
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("control")) {
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("trace")) {
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("result")) {
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("execution")) {
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getInstance(),
                        metadataRepositoryCoordinatorHandler.convert(metadataRepositoryDefinition.getCoordinator())));
            } else {
                throw new RuntimeException(MessageFormat.format("No Metadata repository can be created for {0}", category));
            }
        }
        return metadataRepositories;
    }
}
