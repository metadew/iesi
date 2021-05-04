package io.metadew.iesi.server.rest.connection.dto;

import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionDtoService implements IConnectionDtoService {

    private final ConnectionDtoRepository connectionDtoRepository;

    @Autowired
    public ConnectionDtoService(ConnectionDtoRepository connectionDtoRepository) {
        this.connectionDtoRepository = connectionDtoRepository;
    }

    @Override
    public Page<ConnectionDto> getAll(Pageable pageable, List<ConnectionFilter> componentFilters) {
        return connectionDtoRepository.getAll(pageable, componentFilters);
    }
}
