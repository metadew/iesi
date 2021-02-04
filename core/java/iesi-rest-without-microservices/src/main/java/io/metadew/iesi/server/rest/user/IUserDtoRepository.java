package io.metadew.iesi.server.rest.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserDtoRepository {

    Optional<UserDto> get(String username);

    Optional<UserDto> get(UUID username);

    Page<UserDto> getAll(Pageable pageable, Set<UserFilter> userFilters);

}
