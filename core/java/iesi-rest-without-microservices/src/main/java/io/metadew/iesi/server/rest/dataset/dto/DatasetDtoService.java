package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.server.rest.dataset.DatasetFilter;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import io.metadew.iesi.server.rest.dataset.implementation.database.DatabaseDatasetImplementationPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
public class DatasetDtoService implements IDatasetDtoService {

    final IDatasetDtoRepository datasetDtoRepository;

    @Autowired
    public DatasetDtoService(IDatasetDtoRepository datasetDtoRepository) {
        this.datasetDtoRepository = datasetDtoRepository;
    }

    public Page<DatasetDto> fetchAll(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return datasetDtoRepository.fetchAll(authentication, pageable, datasetFilters);
    }

    public List<DatasetImplementationDto> fetchImplementationsByDatasetUuid(UUID datasetUuid) {
        return datasetDtoRepository.fetchImplementationsByDatasetUuid(datasetUuid);
    }

    @Override
    public Optional<DatasetImplementationDto> fetchImplementationByUuid(UUID uuid) {
        return datasetDtoRepository.fetchImplementationByUuid(uuid);
    }

    @Override
    public Dataset convertToEntity(DatasetPostDto datasetPostDto) {
        String datasetName = datasetPostDto.getName();
        UUID datasetUuid = UUID.randomUUID();

        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(datasetPostDto.getSecurityGroupName())
                .orElseThrow(() -> new RuntimeException("Could not find security group with name + " + datasetPostDto.getSecurityGroupName()));

        return new Dataset(
                new DatasetKey(datasetUuid),
                securityGroup.getMetadataKey(),
                securityGroup.getName(),
                datasetPostDto.getName(),
                datasetPostDto.getImplementations().stream()
                        .map(datasetImplementationDto -> {
                            UUID datasetImplementationUuid = UUID.randomUUID();
                            return new DatabaseDatasetImplementation(
                                    new DatasetImplementationKey(datasetImplementationUuid),
                                    new DatasetKey(datasetUuid),
                                    datasetName,
                                    datasetImplementationDto.getLabels().stream()
                                            .map(datasetImplementationLabelDto -> new DatasetImplementationLabel(
                                                    new DatasetImplementationLabelKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    datasetImplementationLabelDto.getLabel()))
                                            .collect(Collectors.toSet()),
                                    ((DatabaseDatasetImplementationPostDto) datasetImplementationDto).getKeyValues().stream()
                                            .map(inMemoryDatasetImplementationKeyValuePostDto -> new DatabaseDatasetImplementationKeyValue(
                                                    new DatabaseDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                                    new DatasetImplementationKey(datasetImplementationUuid),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getKey(),
                                                    inMemoryDatasetImplementationKeyValuePostDto.getValue()
                                            ))
                                            .collect(Collectors.toSet())
                            );
                        })
                        .collect(Collectors.toSet())
        );
    }
}
