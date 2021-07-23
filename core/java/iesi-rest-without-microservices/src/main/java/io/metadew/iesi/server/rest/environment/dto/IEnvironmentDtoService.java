package io.metadew.iesi.server.rest.environment.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEnvironmentDtoService {
    Page<EnvironmentDto> getAll(Pageable pageable);
}
