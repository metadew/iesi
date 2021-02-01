package io.metadew.iesi.server.rest.dataset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IDatasetDtoRepository {

    Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters);

}
