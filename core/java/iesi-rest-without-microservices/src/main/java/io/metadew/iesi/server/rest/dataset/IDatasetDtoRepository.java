package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.server.rest.dataset.dto.DatasetNoImplDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IDatasetDtoRepository {

    Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters);

    Page<DatasetNoImplDto> fetchAllOnlyUuid(Pageable pageable, Set<DatasetFilter> datasetFilters);
}
