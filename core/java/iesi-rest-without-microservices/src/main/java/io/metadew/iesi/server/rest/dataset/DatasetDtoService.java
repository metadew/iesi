package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.server.rest.dataset.dto.DatasetNoImplDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DatasetDtoService implements IDatasetDtoService {

    final IDatasetDtoRepository datasetDtoRepository;

    @Autowired
    public DatasetDtoService(IDatasetDtoRepository datasetDtoRepository) {
        this.datasetDtoRepository = datasetDtoRepository;
    }

    public Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return datasetDtoRepository.fetchAll(pageable, datasetFilters);
    }

    public Page<DatasetNoImplDto> fetchAllOnlyUuid(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return datasetDtoRepository.fetchAllOnlyUuid(pageable, datasetFilters);
    }

}
