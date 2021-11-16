package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IConnectionDtoService {
    Page<ConnectionDto> getAll(Authentication authentication, Pageable pageable, List<ConnectionFilter> componentFilters);

    Optional<ConnectionDto> getByName(Authentication authentication, String name);

    List<Connection> convertToEntity(ConnectionDto connectionDto);
}
