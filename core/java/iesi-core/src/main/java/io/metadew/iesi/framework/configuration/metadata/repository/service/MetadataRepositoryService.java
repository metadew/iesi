package io.metadew.iesi.framework.configuration.metadata.repository.service;

import io.metadew.iesi.framework.configuration.metadata.repository.MetadataRepositoryDefinition;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.util.List;

public interface MetadataRepositoryService {

    public List<MetadataRepository> convert(MetadataRepositoryDefinition metadataRepositoryDefinition);

}
