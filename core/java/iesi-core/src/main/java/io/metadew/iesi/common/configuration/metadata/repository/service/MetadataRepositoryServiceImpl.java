package io.metadew.iesi.common.configuration.metadata.repository.service;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.service.MetadataRepositoryCoordinatorHandlerImpl;
import io.metadew.iesi.metadata.repository.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MetadataRepositoryServiceImpl implements MetadataRepositoryService {


    private static MetadataRepositoryServiceImpl INSTANCE;

    public synchronized static MetadataRepositoryServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryServiceImpl();
        }
        return INSTANCE;
    }

    private MetadataRepositoryServiceImpl() {
    }


    @Override
    public List<MetadataRepository> convert(MetadataRepositoryDefinition metadataRepositoryDefinition) {
        // TODO: generate mist of MetadataRepositories, parse categories as list
        List<MetadataRepository> metadataRepositories = new ArrayList<>();
        for (String category : metadataRepositoryDefinition.getCategories()) {
            if (category.equalsIgnoreCase("general")) {
                // Make all repositories
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("design")) {
                metadataRepositories.add(new DesignMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("connectivity")) {
                metadataRepositories.add(new ConnectivityMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("control")) {
                metadataRepositories.add(new ControlMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("trace")) {
                metadataRepositories.add(new TraceMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("result")) {
                metadataRepositories.add(new ResultMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else if (category.equalsIgnoreCase("execution")) {
                metadataRepositories.add(new ExecutionServerMetadataRepository(
                        metadataRepositoryDefinition.getName(),
                        metadataRepositoryDefinition.getInstance(),
                        MetadataRepositoryCoordinatorHandlerImpl.getInstance().convert(metadataRepositoryDefinition.getCoordinator())));
            } else {
                throw new RuntimeException(MessageFormat.format("No Metadata repository can be created for {0}", category));
            }
        }
        return metadataRepositories;
    }
}
