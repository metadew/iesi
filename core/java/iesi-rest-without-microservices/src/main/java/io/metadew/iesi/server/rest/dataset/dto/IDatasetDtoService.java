package io.metadew.iesi.server.rest.dataset.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDatasetDtoService {

    Page<DatasetDto> fetchAll(Pageable pageable);

}
