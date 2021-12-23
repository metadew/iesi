package io.metadew.iesi.server.rest.user.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITeamDtoRepository {

    public Optional<TeamDto> get(String teamName);

    public Optional<TeamDto> get(UUID uuid);

    public Page<TeamDto> getAll(Pageable pageable, List<TeamFilter> teamFilters);

}
