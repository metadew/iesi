package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class DataMetadataRepository extends MetadataRepository {

    public DataMetadataRepository(String instance, RepositoryCoordinator repositoryCoordinator) {
        super(instance, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "data";
    }

    @Override
    public void save(DataObject dataObject) throws MetadataRepositorySaveException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("dataset")) {
            Dataset inMemoryDatasetImplementation = (Dataset) objectMapper.convertValue(dataObject, Metadata.class);
            save(inMemoryDatasetImplementation);
        } else {
            log.trace(MessageFormat.format("Data repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }

    public void save(Dataset dataset) {
        log.info(MessageFormat.format("Saving dataset {0} into data repository", dataset.getName()));
        if (dataset.getSecurityGroupKey() == null) {
            log.warn("{0} not linked to a security group, linking it to the public security group");
            SecurityGroup publicSecurityGroup = SpringContext.getBean(SecurityGroupService.class).get("PUBLIC")
                    .orElseThrow(() -> new RuntimeException("Could not find security group with name PUBLIC"));
            dataset.setSecurityGroupKey(publicSecurityGroup.getMetadataKey());
            dataset.setSecurityGroupName(publicSecurityGroup.getName());
        }
        if (!SpringContext.getBean(DatasetConfiguration.class).exists(dataset.getMetadataKey())) {
            SpringContext.getBean(DatasetConfiguration.class).insert(dataset);
        } else {
            log.info(MessageFormat.format("dataset {0} already exists in data repository. Updating to new definition", dataset.getName()));
            SpringContext.getBean(DatasetConfiguration.class).update(dataset);
        }
    }

}
