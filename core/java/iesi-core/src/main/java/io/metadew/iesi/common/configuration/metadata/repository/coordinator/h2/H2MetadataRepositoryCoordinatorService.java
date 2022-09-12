package io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.h2.*;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class H2MetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<H2MetadataRepositoryCoordinatorDefinition, H2DatabaseConnection> {

    private static H2MetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static H2MetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2MetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private H2MetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(H2MetadataRepositoryCoordinatorDefinition h2RepositoryCoordinatorDefinition) {
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
                    h2RepositoryCoordinatorDefinition.getReader());
            H2Database h2Database = new H2Database(h2DatabaseConnection);
            h2RepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("reader", h2Database);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public Class<H2MetadataRepositoryCoordinatorDefinition> appliesTo() {
        return H2MetadataRepositoryCoordinatorDefinition.class;
    }

    public H2DatabaseConnection getDatabaseConnection(H2MetadataRepositoryCoordinatorDefinition h2RepositoryCoordinatorDefinition,
                                                      MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        H2DatabaseConnection databaseConnection;
        if (h2RepositoryCoordinatorDefinition.getConnection().isPresent()) {
            databaseConnection = new H2DatabaseConnection(
                    h2RepositoryCoordinatorDefinition.getConnection().get(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    h2RepositoryCoordinatorDefinition.getInitSql());
            h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
            return databaseConnection;
        }

        switch (h2RepositoryCoordinatorDefinition.getMode()) {
            case "embedded":
                databaseConnection = new H2EmbeddedDatabaseConnection(h2RepositoryCoordinatorDefinition.getFile(),
                        metadataRepositoryCoordinatorProfileDefinition.getUser(),
                        SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                        h2RepositoryCoordinatorDefinition.getInitSql());
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            case "server":
                databaseConnection = new H2ServerDatabaseConnection(h2RepositoryCoordinatorDefinition.getHost(),
                        h2RepositoryCoordinatorDefinition.getPort(),
                        h2RepositoryCoordinatorDefinition.getFile(),
                        metadataRepositoryCoordinatorProfileDefinition.getUser(),
                        SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                        h2RepositoryCoordinatorDefinition.getInitSql());
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            case "memory":
                databaseConnection = new H2MemoryDatabaseConnection(h2RepositoryCoordinatorDefinition.getDatabaseName(),
                        metadataRepositoryCoordinatorProfileDefinition.getUser(),
                        SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                        h2RepositoryCoordinatorDefinition.getInitSql());
                h2RepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                break;
            default:
                throw new RuntimeException();
        }
        return databaseConnection;
    }

}
