package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.metadata.MetadataTableService;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MetadataRepository {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String tablePrefix;
    @Getter
    private final List<MetadataTable> metadataTables;
    @Getter
    private final RepositoryCoordinator repositoryCoordinator;

    public MetadataRepository(String instanceName,
                              RepositoryCoordinator repositoryCoordinator) {
        this.tablePrefix = "iesi".toUpperCase() + "_" + (instanceName != null ? instanceName + "_" : "");
        this.repositoryCoordinator = repositoryCoordinator;
        this.metadataTables = SpringContext.getBean(MetadataTablesConfiguration.class).getMetadataTables().stream()
                .filter(metadataTable -> metadataTable.getCategory().equalsIgnoreCase(getCategory()))
                .peek(metadataTable -> metadataTable.setName(tablePrefix + metadataTable.getName()))
                .collect(Collectors.toList());
    }

    public abstract String getCategory();

    private void dropTable(MetadataTable metadataTable) {
        repositoryCoordinator.dropTable(metadataTable);
    }

    public void dropAllTables() {
        metadataTables.forEach(this::dropTable);
    }

    private void cleanTable(MetadataTable metadataTable) {
        repositoryCoordinator.cleanTable(metadataTable);
    }

    public void cleanAllTables() {
        metadataTables.forEach(this::cleanTable);
    }

    // TODO: remove because security danger: query can target objects outside of
    public CachedRowSet executeQuery(String query, String logonType) {
        return repositoryCoordinator.executeQuery(query, logonType);
    }

    // TODO: remove because security danger: query can target objects outside of
    public void executeUpdate(String query) {
        repositoryCoordinator.executeUpdate(query);
    }

    public void executeBatch(List<String> queries) {
        repositoryCoordinator.executeBatch(queries);
    }

    private void createTable(MetadataTable metadataTable) {
        this.repositoryCoordinator.createTable(metadataTable);
    }

    public void createAllTables() {
        metadataTables.forEach(this::createTable);
    }

    public String generateDDL() {
        return metadataTables.stream()
                .map(repositoryCoordinator::getDropStatement)
                .collect(Collectors.joining("\n\n")) + "\n\n" +
                metadataTables.stream()
                        .map(repositoryCoordinator::getCreateStatement)
                        .collect(Collectors.joining("\n\n"));
    }


    public String getTableNameByLabel(String label) {
        return SpringContext.getBean(MetadataTableService.class).getByLabel(label).getName();
    }

    public abstract void save(DataObject dataObject) throws MetadataRepositorySaveException;

    public void shutdown() {
        LOGGER.debug("shutting down metadata repository " + getCategory());
        repositoryCoordinator.shutdown();
    }
}
