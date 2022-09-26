package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2.H2MetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mssql.MssqlMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql.MysqlMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.netezza.NetezzaMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle.OracleMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.postgresql.PostgresqlMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite.SqliteMetadataRepositoryCoordinatorService;
import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetadataRepositoryCoordinatorHandler implements IMetadataRepositoryCoordinatorHandler {

    private Map<Class<? extends MetadataRepositoryCoordinatorDefinition>, IMetadataRepositoryCoordinatorService> metadataRepositoryCoordinatorServiceMap;

    @PostConstruct
    private void postConstruct() {
        metadataRepositoryCoordinatorServiceMap = new HashMap<>();
        metadataRepositoryCoordinatorServiceMap.put(H2MetadataRepositoryCoordinatorService.getInstance().appliesTo(), H2MetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(MssqlMetadataRepositoryCoordinatorService.getInstance().appliesTo(), MssqlMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(NetezzaMetadataRepositoryCoordinatorService.getInstance().appliesTo(), NetezzaMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(OracleMetadataRepositoryCoordinatorService.getInstance().appliesTo(), OracleMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(PostgresqlMetadataRepositoryCoordinatorService.getInstance().appliesTo(), PostgresqlMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(SqliteMetadataRepositoryCoordinatorService.getInstance().appliesTo(), SqliteMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(MysqlMetadataRepositoryCoordinatorService.getInstance().appliesTo(), MysqlMetadataRepositoryCoordinatorService.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public RepositoryCoordinator convert(MetadataRepositoryCoordinatorDefinition metadataRepositoryCoordinatorDefinition) {
        return metadataRepositoryCoordinatorServiceMap.get(metadataRepositoryCoordinatorDefinition.getClass()).convert(metadataRepositoryCoordinatorDefinition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DatabaseConnection getDatabaseConnection(MetadataRepositoryCoordinatorDefinition metadataRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        return metadataRepositoryCoordinatorServiceMap.get(metadataRepositoryCoordinatorDefinition.getClass()).getDatabaseConnection(metadataRepositoryCoordinatorDefinition, metadataRepositoryCoordinatorProfileDefinition);
    }
}
