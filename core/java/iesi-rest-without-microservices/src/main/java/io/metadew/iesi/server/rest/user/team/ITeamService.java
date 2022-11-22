package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ITeamService {

    Optional<TeamDto> get(String teamName);

    Optional<TeamDto> get(UUID uuid);

    Page<TeamDto> getAll(Pageable pageable, List<TeamFilter> teamFilters);

    List<Team> getAllRawTeams();

    boolean exists(TeamKey teamKey);

    boolean exists(String teamname);

    void addTeam(Team team);

    Optional<Team> getRawTeam(TeamKey teamKey);

    Optional<Team> getRawTeam(String teamname);

    void update(Team team);

    void delete(TeamKey teamKey);

    void delete(String teamname);

    Set<SecurityGroup> getSecurityGroups(TeamKey teamKey);

    void addRole(TeamKey teamKey, Role role);

    void deleteRole(TeamKey teamKey, RoleKey roleKey);

    Set<User> getUsers(TeamKey teamKey);

    void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey);

    void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey);

    void removeUserFromRole(TeamKey teamKey, RoleKey roleKey, UserKey userKey);

    void addUserToRole(TeamKey teamKey, RoleKey roleKey, UserKey userKey);

    Team convertToEntity(TeamPostDto teamDto);



}
