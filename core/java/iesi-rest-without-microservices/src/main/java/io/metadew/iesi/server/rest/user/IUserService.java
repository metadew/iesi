package io.metadew.iesi.server.rest.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserService {

    public Optional<UserDto> get(String username);

    public Optional<UserDto> get(UUID username);

    public Set<UserDto> getAll();

}
