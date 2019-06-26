package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.LedgerConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Ledger;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class LedgerMetadataRepository extends MetadataRepository {

    public LedgerMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return null;
    }

    @Override
    public String getObjectDefinitionFileName() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getCategoryPrefix() {
        return null;
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        // TODO: based on MetadataRepository object decide to insert or not insert the objects
        // TODO: insert should be handled on database level as insert can differ from database type/dialect? JDBC Dialect/Spring
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("ledger")) {
            Ledger ledger = objectMapper.convertValue(dataObject.getData(), Ledger.class);
            LedgerConfiguration ledgerConfiguration = new LedgerConfiguration(ledger, frameworkExecution.getFrameworkInstance());
            executeUpdate(ledgerConfiguration.getInsertStatement());
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("Ledger repository is not responsible for loading saving {0}", dataObject.getType()), Level.TRACE);
        }
    }
}
