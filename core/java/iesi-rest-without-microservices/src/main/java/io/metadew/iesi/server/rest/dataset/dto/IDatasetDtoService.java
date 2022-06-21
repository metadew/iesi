package io.metadew.iesi.server.rest.dataset.dto;


import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.DatasetFilter;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IDatasetDtoService {

    Page<DatasetDto> fetchAll(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters);

    List<DatasetImplementationDto> fetchImplementationsByDatasetUuid(UUID datasetUuid);

    Optional<DatasetImplementationDto> fetchImplementationByUuid(UUID uuid);

    Dataset convertToEntity(DatasetPostDto datasetPostDto);
}
