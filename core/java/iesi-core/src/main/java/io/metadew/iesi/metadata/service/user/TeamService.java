package io.metadew.iesi.metadata.service.user;


import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamService {

    private final TeamConfiguration teamConfiguration;
    private final RoleConfiguration roleConfiguration;

    public TeamService(TeamConfiguration teamConfiguration, RoleConfiguration roleConfiguration) {
        this.teamConfiguration = teamConfiguration;
        this.roleConfiguration = roleConfiguration;
    }

    public List<Team> getAll() {
        return teamConfiguration.getAll();
    }

    public boolean exists(TeamKey teamKey) {
        return teamConfiguration.exists(teamKey);
    }

    public boolean exists(String teamname) {
        return teamConfiguration.exists(teamname);
    }

    public void addTeam(Team team) {
        teamConfiguration.insert(team);
    }

    public Optional<Team> get(TeamKey teamKey) {
        return teamConfiguration.get(teamKey);
    }

    public Optional<Team> get(String teamname) {
        return teamConfiguration.getByName(teamname);
    }

    public void update(Team team) {
        teamConfiguration.update(team);
    }

    public void delete(TeamKey teamKey) {
        teamConfiguration.delete(teamKey);
    }

    public void delete(String teamname) {
        teamConfiguration.delete(teamname);
    }

    public Set<SecurityGroup> getSecurityGroups(TeamKey teamKey) {
        return teamConfiguration.getSecurityGroups(teamKey);
    }

    public void addRole(TeamKey teamKey, Role role) {
        roleConfiguration.insert(role);
    }

    public void deleteRole(TeamKey teamKey, RoleKey roleKey) {
        roleConfiguration.delete(roleKey);
    }

    public Set<User> getUsers(TeamKey teamKey) {
        return teamConfiguration.getUsers(teamKey);
    }

    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        teamConfiguration.addSecurityGroup(teamKey, securityGroupKey);
    }

    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        teamConfiguration.removeSecurityGroup(teamKey, securityGroupKey);
    }

}
