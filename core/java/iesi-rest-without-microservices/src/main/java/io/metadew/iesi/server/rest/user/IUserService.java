package io.metadew.iesi.server.rest.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserService {

    Optional<UserDto> get(String username);

    Optional<UserDto> get(UUID username);

    Set<UserDto> getAll();

}
