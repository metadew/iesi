package io.metadew.iesi.server.rest.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserDtoService {

    Optional<UserDto> get(String username);

    Optional<UserDto> get(UUID uuid);

    Set<UserDto> getAll();

}
