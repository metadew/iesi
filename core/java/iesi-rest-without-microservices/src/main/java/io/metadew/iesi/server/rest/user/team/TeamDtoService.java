package io.metadew.iesi.server.rest.user.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@ConditionalOnWebApplication
public class TeamDtoService implements ITeamDtoService {

    private final ITeamDtoRepository teamDtoRepository;

    @Autowired
    public TeamDtoService(ITeamDtoRepository teamDtoRepository) {
        this.teamDtoRepository = teamDtoRepository;
    }

    public Optional<TeamDto> get(String teamName) {
        return teamDtoRepository.get(teamName);
    }

    public Optional<TeamDto> get(UUID uuid) {
        return teamDtoRepository.get(uuid);
    }

    public Set<TeamDto> getAll() {
        return teamDtoRepository.getAll();
    }

}
