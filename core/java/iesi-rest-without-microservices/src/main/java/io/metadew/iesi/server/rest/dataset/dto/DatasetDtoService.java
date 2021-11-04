package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.Dataset;
import io.metadew.iesi.server.rest.dataset.DatasetFilter;
import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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

    public Page<DatasetDto> fetchAll(Authentication authentication, Pageable pageable, Set<DatasetFilter> datasetFilters) {
        return datasetDtoRepository.fetchAll(authentication, pageable, datasetFilters);
    }

    public List<DatasetImplementationDto> fetchImplementationsByDatasetUuid(Authentication authentication, UUID datasetUuid){
        return datasetDtoRepository.fetchImplementationsByDatasetUuid(authentication, datasetUuid);
    }

    @Override
    public Optional<DatasetImplementationDto> fetchImplementationByUuid(Authentication authentication, UUID uuid) {
        return datasetDtoRepository.fetchImplementationByUuid(authentication, uuid);
    }

    @Override
    public Dataset convertToEntity(DatasetDto datasetDto) {
        return null;
    }

}
