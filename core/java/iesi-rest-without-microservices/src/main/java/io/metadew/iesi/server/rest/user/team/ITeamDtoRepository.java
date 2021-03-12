package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.server.rest.user.UserDto;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ITeamDtoRepository {

    public Optional<TeamDto> get(String teamName);

    public Optional<TeamDto> get(UUID uuid);

    public Set<TeamDto> getAll();

}
