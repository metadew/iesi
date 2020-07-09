package io.metadew.iesi.common.configuration.metadata.repository;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryDefinition;
import io.metadew.iesi.metadata.repository.MetadataRepository;

import java.util.List;

public interface IMetadataRepositoryService {

    public List<MetadataRepository> convert(MetadataRepositoryDefinition metadataRepositoryDefinition) throws Exception;

}
