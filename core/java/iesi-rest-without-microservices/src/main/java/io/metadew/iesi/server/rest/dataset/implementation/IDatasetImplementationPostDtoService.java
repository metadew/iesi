package io.metadew.iesi.server.rest.dataset.implementation;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;

public interface IDatasetImplementationPostDtoService {
    DatasetImplementation convertToEntity (String datasetUUID, String datasetName, DatasetImplementationPostDto datasetImplementationPostDto);
}
