package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConnectionDtoService {
    Page<ConnectionDto> getAll(Pageable pageable, List<ConnectionFilter> componentFilters);
}
