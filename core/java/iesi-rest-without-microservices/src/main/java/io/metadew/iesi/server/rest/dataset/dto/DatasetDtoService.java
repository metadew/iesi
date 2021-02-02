package io.metadew.iesi.server.rest.dataset.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DatasetDtoService implements IDatasetDtoService {

    final IDatasetDtoRepository datasetDtoRepository;

    @Autowired
    public DatasetDtoService(IDatasetDtoRepository datasetDtoRepository) {
        this.datasetDtoRepository = datasetDtoRepository;
    }

    public Page<DatasetDto> fetchAll(Pageable pageable) {
        return datasetDtoRepository.fetchAll(pageable);
    }

    public Page<DatasetNoImplDto> fetchAllOnlyUuid(Pageable pageable) {
        return datasetDtoRepository.fetchAllOnlyUuid(pageable);
    }

}
