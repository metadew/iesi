package io.metadew.iesi.server.rest.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserDtoService {

    public Optional<UserDto> get(String username);

    public Optional<UserDto> get(UUID uuid);

    public Set<UserDto> getAll();

}
