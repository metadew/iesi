package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IConnectionDtoRepository {
    Page<ConnectionDto> getAll(Authentication authentication, Pageable pageable, List<ConnectionFilter> connectionFilters);

    Optional<ConnectionDto> getByName(Authentication authentication, String name);
}
