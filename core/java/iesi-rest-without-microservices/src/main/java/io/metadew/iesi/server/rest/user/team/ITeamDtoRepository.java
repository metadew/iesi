package io.metadew.iesi.server.rest.user.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITeamDtoRepository {

    Optional<TeamDto> get(String teamName);

    Optional<TeamDto> get(UUID uuid);

    Page<TeamDto> getAll(Pageable pageable, List<TeamFilter> teamFilters);
}
