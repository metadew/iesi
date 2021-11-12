package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;

public interface IDatasetPostDtoService {
    Dataset convertToEntity (String uuid, DatasetPostDto datasetPostDto);
}
