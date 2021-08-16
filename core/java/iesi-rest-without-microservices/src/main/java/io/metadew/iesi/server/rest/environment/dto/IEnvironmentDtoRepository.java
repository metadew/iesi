package io.metadew.iesi.server.rest.environment.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEnvironmentDtoRepository {

    Page<EnvironmentDto> getAll(Pageable pageable);
}
