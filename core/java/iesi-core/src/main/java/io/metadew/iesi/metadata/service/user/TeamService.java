package io.metadew.iesi.metadata.service.user;


import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TeamService {

    private static TeamService instance;

    public static synchronized TeamService getInstance() {
        if (instance == null) {
            instance = new TeamService();
        }
        return instance;
    }

    private TeamService() {
    }

    public List<Team> getAll() {
        return TeamConfiguration.getInstance().getAll();
    }

    public boolean exists(TeamKey teamKey) {
        return TeamConfiguration.getInstance().exists(teamKey);
    }

    public boolean exists(String teamname) {
        return TeamConfiguration.getInstance().exists(teamname);
    }

    public void addTeam(Team team) {
        TeamConfiguration.getInstance().insert(team);
    }

    public Optional<Team> get(TeamKey teamKey) {
        return TeamConfiguration.getInstance().get(teamKey);
    }

    public Optional<Team> get(String teamname) {
        return TeamConfiguration.getInstance().getByName(teamname);
    }

    public void update(Team team) {
        TeamConfiguration.getInstance().update(team);
    }

    public void delete(TeamKey teamKey) {
        TeamConfiguration.getInstance().delete(teamKey);
    }

    public void delete(String teamname) {
        TeamConfiguration.getInstance().delete(teamname);
    }

    public Set<SecurityGroup> getSecurityGroups(TeamKey teamKey) {
        return TeamConfiguration.getInstance().getSecurityGroups(teamKey);
    }

    public void addRole(TeamKey teamKey, Role role) {
        RoleConfiguration.getInstance().insert(role);
    }

    public void deleteRole(TeamKey teamKey, RoleKey roleKey) {
        RoleConfiguration.getInstance().delete(roleKey);
    }

    public Set<User> getUsers(TeamKey teamKey) {
        return TeamConfiguration.getInstance().getUsers(teamKey);
    }

    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        TeamConfiguration.getInstance().addSecurityGroup(teamKey, securityGroupKey);
    }

    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        TeamConfiguration.getInstance().removeSecurityGroup(teamKey, securityGroupKey);
    }

}
