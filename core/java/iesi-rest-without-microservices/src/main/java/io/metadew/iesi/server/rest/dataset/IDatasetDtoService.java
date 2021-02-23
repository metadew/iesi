package io.metadew.iesi.server.rest.dataset;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IDatasetDtoService {

    Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters);

    List<DatasetWImplementationDto> getDataImplementations(UUID uuid);


}
