package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.server.rest.dataset.DatasetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDatasetDtoRepository {

    Page<DatasetDto> fetchAll(Pageable pageable);

}
