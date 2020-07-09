package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class ControlMetadataRepository extends MetadataRepository {

    public ControlMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "control";
    }

    @Override
    public void save(DataObject dataObject) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        //if (dataObject.getType().equalsIgnoreCase("user")) {
        //    User user = objectMapper.convertValue(dataObject, User.class);
        //} else if (dataObject.getType().equalsIgnoreCase("usergroup")) {
        //    //TODO
        //} else if (dataObject.getType().equalsIgnoreCase("userrole")) {
        //    // TODO
        //} else if (dataObject.getType().equalsIgnoreCase("spaceuser")) {
        //    // TODO
        //} else {
        //    LOGGER.trace(MessageFormat.format("Control repository is not responsible for loading saving {0}", dataObject.getType()));
        //}
    }
}
