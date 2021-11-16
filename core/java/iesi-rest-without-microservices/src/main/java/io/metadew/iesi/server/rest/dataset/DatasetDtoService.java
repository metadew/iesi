package io.metadew.iesi.server.rest.dataset;

import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@ConditionalOnWebApplication
public class DatasetDtoService implements IDatasetDtoService {

    final IDatasetDtoRepository datasetDtoRepository;

    @Autowired
    public DatasetDtoService(IDatasetDtoRepository datasetDtoRepository) {
        this.datasetDtoRepository = datasetDtoRepository;
    }

    public Page<DatasetDto> fetchAll(Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return datasetDtoRepository.fetchAll(pageable, datasetFilters);
    }

    public List<DatasetImplementationDto> fetchImplementationsByDatasetUuid(UUID datasetUuid){
        return datasetDtoRepository.fetchImplementationsByDatasetUuid(datasetUuid);
    }

    @Override
    public Optional<DatasetImplementationDto> fetchImplementationByUuid(UUID uuid) {
        return datasetDtoRepository.fetchImplementationByUuid(uuid);
    }

}
