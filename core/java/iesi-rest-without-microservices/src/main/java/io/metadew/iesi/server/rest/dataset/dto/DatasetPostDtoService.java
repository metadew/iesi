package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.dataset.implementation.IDatasetImplementationPostDtoService;

import java.util.UUID;
import java.util.stream.Collectors;

public class DatasetPostDtoService implements IDatasetPostDtoService {

    private final SecurityGroupConfiguration securityGroupConfiguration;
    private final IDatasetImplementationPostDtoService datasetImplementationPostDtoService;

    public DatasetPostDtoService(SecurityGroupConfiguration securityGroupConfiguration, IDatasetImplementationPostDtoService datasetImplementationPostDtoService) {
        this.securityGroupConfiguration = securityGroupConfiguration;
        this.datasetImplementationPostDtoService = datasetImplementationPostDtoService;
    }


    @Override
    public Dataset convertToEntity(String uuid, DatasetPostDto datasetPostDto) {
        SecurityGroup securityGroup = securityGroupConfiguration.getByName(datasetPostDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find Security Group with name " + datasetPostDto.getSecurityGroupName()));
        return new Dataset(
                new DatasetKey(UUID.fromString(uuid)),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                datasetPostDto.getName(),
                datasetPostDto.getImplementations().stream()
                        .map(datasetImplementationPostDto -> datasetImplementationPostDtoService.convertToEntity(uuid, datasetPostDto.getName(), datasetImplementationPostDto))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Dataset convertToEntity(DatasetPostDto datasetPostDto) {
        return null;
    }
}
