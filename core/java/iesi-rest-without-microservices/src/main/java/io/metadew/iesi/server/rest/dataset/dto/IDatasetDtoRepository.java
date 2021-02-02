package io.metadew.iesi.server.rest.dataset.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDatasetDtoRepository {

    Page<DatasetDto> fetchAll(Pageable pageable);
    Page<DatasetNoImplDto> fetchAllOnlyUuid(Pageable pageable);

}
