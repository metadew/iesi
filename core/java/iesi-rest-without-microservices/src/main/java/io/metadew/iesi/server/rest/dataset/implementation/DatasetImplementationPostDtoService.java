package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.DatasetKey;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationKey;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValue;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationKeyValueKey;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabel;
import io.metadew.iesi.datatypes.dataset.implementation.label.DatasetImplementationLabelKey;
import io.metadew.iesi.server.rest.dataset.implementation.inmemory.InMemoryDatasetImplementationPostDto;

import java.util.UUID;
import java.util.stream.Collectors;

public class DatasetImplementationPostDtoService implements IDatasetImplementationPostDtoService {
    @Override
    public DatasetImplementation convertToEntity(String datasetUUID, String datasetName, DatasetImplementationPostDto datasetImplementationPostDto) {
        UUID datasetImplementationUUID = UUID.randomUUID();
        return new InMemoryDatasetImplementation(
                new DatasetImplementationKey(datasetImplementationUUID),
                new DatasetKey(UUID.fromString(datasetUUID)),
                datasetName,
                datasetImplementationPostDto.getLabels().stream()
                        .map(datasetImplementationLabelPostDto -> new DatasetImplementationLabel(
                                new DatasetImplementationLabelKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplementationUUID),
                                datasetImplementationLabelPostDto.getLabel()
                        )).collect(Collectors.toSet()),
                ((InMemoryDatasetImplementationPostDto) datasetImplementationPostDto).getKeyValues().stream()
                        .map(inMemoryDatasetImplementationKeyValuePostDto -> new InMemoryDatasetImplementationKeyValue(
                                new InMemoryDatasetImplementationKeyValueKey(UUID.randomUUID()),
                                new DatasetImplementationKey(datasetImplementationUUID),
                                inMemoryDatasetImplementationKeyValuePostDto.getKey(),
                                inMemoryDatasetImplementationKeyValuePostDto.getValue()
                        )).collect(Collectors.toSet())
        );
    }
}
