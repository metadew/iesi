package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TeamService implements ITeamService {

    private final ITeamDtoRepository teamDtoRepository;

    private final io.metadew.iesi.metadata.service.user.TeamService rawTeamService;

    @Autowired
    public TeamService(ITeamDtoRepository teamDtoRepository,
                       io.metadew.iesi.metadata.service.user.TeamService rawTeamService) {
        this.teamDtoRepository = teamDtoRepository;
        this.rawTeamService = rawTeamService;
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

    @Override
    public List<Team> getAllRawTeams() {
        return rawTeamService.getAll();
    }

    @Override
    public boolean exists(TeamKey teamKey) {
        return rawTeamService.exists(teamKey);
    }

    @Override
    public boolean exists(String teamname) {
        return rawTeamService.exists(teamname);
    }

    @Override
    public void addTeam(Team team) {
        rawTeamService.addTeam(team);
    }

    @Override
    public Optional<Team> getRawTeam(TeamKey teamKey) {
        return rawTeamService.get(teamKey);
    }

    @Override
    public Optional<Team> getRawTeam(String teamname) {
        return rawTeamService.get(teamname);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void update(Team team) {
        rawTeamService.update(team);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(TeamKey teamKey) {
        rawTeamService.delete(teamKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(String teamname) {
        rawTeamService.delete(teamname);
    }

    @Override
    public Set<SecurityGroup> getSecurityGroups(TeamKey teamKey) {
        return rawTeamService.getSecurityGroups(teamKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void addRole(TeamKey teamKey, Role role) {
        rawTeamService.addRole(teamKey, role);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteRole(TeamKey teamKey, RoleKey roleKey) {
        rawTeamService.deleteRole(teamKey, roleKey);
    }

    @Override
    public Set<User> getUsers(TeamKey teamKey) {
        return rawTeamService.getUsers(teamKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        rawTeamService.addSecurityGroup(teamKey, securityGroupKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        rawTeamService.removeSecurityGroup(teamKey, securityGroupKey);
    }

}
