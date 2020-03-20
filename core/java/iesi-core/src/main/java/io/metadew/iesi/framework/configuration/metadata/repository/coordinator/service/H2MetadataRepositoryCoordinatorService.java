package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.H2RepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class H2MetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<H2RepositoryCoordinatorDefinition, H2DatabaseConnection> {

    private static H2MetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static H2MetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2MetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private H2MetadataRepositoryCoordinatorService() {}

    @Override
    public RepositoryCoordinator convert(H2RepositoryCoordinatorDefinition h2RepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (h2RepositoryCoordinatorDefinition.getOwner() != null) {
            H2DatabaseConnection h2DatabaseConnection = getDatabaseConnection(h2RepositoryCoordinatorDefinition,
                    h2RepositoryCoordinatorDefinition.getOwner());
            H2Database h2Database = new H2Database(h2DatabaseConnection);
            h2RepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("owner", h2Database);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
        }
        if (h2RepositoryCoordinatorDefinition.getWriter() != null) {
            H2DatabaseConnection h2DatabaseConnection = getDatabaseConnection(h2RepositoryCoordinatorDefinition,
                    h2RepositoryCoordinatorDefinition.getWriter());
            H2Database h2Database = new H2Database(h2DatabaseConnection);
            h2RepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
        }
        if (h2RepositoryCoordinatorDefinition.getReader() != null) {
            H2DatabaseConnection h2DatabaseConnection = getDatabaseConnection(h2RepositoryCoordinatorDefinition,
                    h2RepositoryCoordinatorDefinition.getWriter());
            H2Database h2Database = new H2Database(h2DatabaseConnection);
            h2RepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("reader", h2Database);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public Class<H2RepositoryCoordinatorDefinition> appliesTo() {
        return H2RepositoryCoordinatorDefinition.class;
    }

    public H2DatabaseConnection getDatabaseConnection(H2RepositoryCoordinatorDefinition h2RepositoryCoordinatorDefinition,
                                                         RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (h2RepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new H2DatabaseConnection(
                    h2RepositoryCoordinatorDefinition.getConnection().get(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
        }

        H2DatabaseConnection databaseConnection;
        switch (h2RepositoryCoordinatorDefinition.getMode()) {
            case "embedded":
                databaseConnection = new H2EmbeddedDatabaseConnection(h2RepositoryCoordinatorDefinition.getFile(),
                        repositoryCoordinatorProfileDefinition.getUser(),
                        FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            case "server":
                databaseConnection = new H2ServerDatabaseConnection(h2RepositoryCoordinatorDefinition.getHost(),
                        h2RepositoryCoordinatorDefinition.getPort(),
                        h2RepositoryCoordinatorDefinition.getFile(),
                        repositoryCoordinatorProfileDefinition.getUser(),
                        FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            case "memory":
                databaseConnection = new H2MemoryDatabaseConnection(h2RepositoryCoordinatorDefinition.getDatabaseName(),
                        repositoryCoordinatorProfileDefinition.getUser(),
                        FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            default:
                throw new RuntimeException();
        }
        return databaseConnection;
    }

}
