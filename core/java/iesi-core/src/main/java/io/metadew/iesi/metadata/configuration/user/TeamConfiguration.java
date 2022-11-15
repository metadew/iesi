package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupListResultSetExtractor;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class TeamConfiguration extends Configuration<Team, TeamKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final RoleConfiguration roleConfiguration;
    private final TeamListResultSetExtractor teamListResultSetExtractor;

    private String fetchIdByNameQuery() {
        return "select teams.ID as team_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " WHERE TEAM_NAME={0};";
    }

    private String fetchSingleQuery() {
        return "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "user_roles.user_id as user_role_user_id, " +
                "security_group_teams.security_group_id as security_group_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " ON teams.ID = roles.TEAM_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON roles.ID = privileges.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON roles.ID = user_roles.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON teams.ID = security_group_teams.TEAM_ID " +
                " WHERE teams.ID={0};";
    }


    private String fetchByNameQuery() {
        return "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "user_roles.user_id as user_role_user_id, " +
                "security_group_teams.security_group_id as security_group_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " ON teams.ID = roles.TEAM_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON roles.ID = privileges.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON roles.ID = user_roles.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON teams.ID = security_group_teams.TEAM_ID " +
                " WHERE teams.TEAM_NAME={0};";
    }

    private String fetchAllQuery() {
        return "select teams.ID as team_id, teams.TEAM_NAME as team_name, " +
                "roles.id as role_id, roles.team_id as role_team_id, roles.role_name as role_role_name, " +
                "privileges.id as privilege_id, privileges.role_id as privilege_role_id, privileges.privilege as privilege_privilege, " +
                "user_roles.user_id as user_role_user_id, " +
                "security_group_teams.security_group_id as security_group_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() + " teams" +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " ON teams.ID = roles.TEAM_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Privileges").getName() + " privileges " +
                " ON roles.ID = privileges.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON roles.ID = user_roles.ROLE_ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON teams.ID = security_group_teams.TEAM_ID;";
    }

    private String fetchUsersByTeamIdQuery() {
        return "select users.ID as user_id, users.USERNAME as user_username, users.PASSWORD as user_password, " +
                "users.ENABLED as user_enabled, users.EXPIRED as user_expired, users.CREDENTIALS_EXPIRED as user_credentials_expired, users.LOCKED as user_locked, user_roles.ROLE_ID as role_id" +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Roles").getName() + " roles " +
                " INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("UserRoles").getName() + " user_roles " +
                " ON user_roles.ROLE_ID = roles.ID " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Users").getName() + " users" +
                " ON users.ID = user_roles.USER_ID " +
                " WHERE roles.TEAM_ID={0};";
    }

    private String fetchSecurityGroupsByTeamIdQuery() {
        return "select security_groups.id as security_groups_id, security_groups.name as security_groups_name, " +
                "security_group_teams.team_id as security_group_teams_team_id " +
                " FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroups").getName() + " security_groups " +
                " LEFT OUTER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() + " security_group_teams " +
                " ON security_groups.ID = security_group_teams.SECURITY_GROUP_ID " +
                " WHERE security_group_teams.TEAM_ID={0};";
    }

    private String deleteSingleQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() +
                " WHERE ID={0};";
    }

    private String deleteSecurityGroupTeamsByTeamIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " WHERE TEAM_ID={0};";
    }

    private String deleteTeamRolesByTeamIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " WHERE TEAM_ID={0};";
    }

    private String deleteSecurityGroupTeamsBySecurityGroupIdAndTeamIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " WHERE TEAM_ID={0} AND SECURITY_GROUP_ID={1};";
    }

    private String insertQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() +
                " (ID, TEAM_NAME) VALUES ({0}, {1});";
    }

    private String insertSecurityGroupTeamsQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("SecurityGroupTeams").getName() +
                " (SECURITY_GROUP_ID, TEAM_ID) VALUES ({0}, {1});";
    }

    private String updateQuery() {
        return "UPDATE " + metadataTablesConfiguration.getMetadataTableNameByLabel("Teams").getName() +
                " SET TEAM_NAME = {0} " +
                " WHERE ID = {1};";
    }

    public TeamConfiguration(
            MetadataRepositoryConfiguration metadataRepositoryConfiguration,
            MetadataTablesConfiguration metadataTablesConfiguration,
            RoleConfiguration roleConfiguration,
            TeamListResultSetExtractor teamListResultSetExtractor
    ) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.roleConfiguration = roleConfiguration;
        this.teamListResultSetExtractor = teamListResultSetExtractor;
    }

    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getControlMetadataRepository());
    }

    @Override
    public Optional<Team> get(TeamKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid())),
                    "reader");
            return teamListResultSetExtractor.extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Team> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery(), SQLTools.getStringForSQL(name)),
                    "reader");
            return teamListResultSetExtractor.extractData(cachedRowSet).stream()
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String username) {
        return getByName(username).isPresent();
    }

    @Override
    public List<Team> getAll() {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery(), "reader");
            return teamListResultSetExtractor.extractData(cachedRowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(TeamKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey));
        String deleteStatement = MessageFormat.format(deleteSingleQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteStatement);
        String deleteSecurityGroupMembershipsStatement = MessageFormat.format(deleteSecurityGroupTeamsByTeamIdQuery(), SQLTools.getStringForSQL(metadataKey.getUuid()));
        getMetadataRepository().executeUpdate(deleteSecurityGroupMembershipsStatement);
        roleConfiguration.deleteByTeamKey(metadataKey);
    }

    public void delete(String teamName) {
        log.trace(MessageFormat.format("Deleting {0}.", teamName));
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(MessageFormat.format(fetchIdByNameQuery(), SQLTools.getStringForSQL(teamName)), "reader");
        try {
            if (cachedRowSet.next()) {
                TeamKey teamKey = new TeamKey(UUID.fromString(cachedRowSet.getString("team_id")));
                delete(teamKey);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Team metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata));
        String insertStatement =
                MessageFormat.format(insertQuery(),
                        SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()),
                        SQLTools.getStringForSQL(metadata.getTeamName()));
        getMetadataRepository().executeUpdate(insertStatement);
        for (SecurityGroup securityGroup : metadata.getSecurityGroups()) {
            String insertSecurityGroupStatement =
                    MessageFormat.format(insertSecurityGroupTeamsQuery(),
                            SQLTools.getStringForSQL(securityGroup.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
            getMetadataRepository().executeUpdate(insertSecurityGroupStatement);
        }

        for (Role role : metadata.getRoles()) {
            roleConfiguration.insert(role);
        }

    }

    @Override
    public void update(Team metadata) {
        getMetadataRepository().executeUpdate(MessageFormat.format(updateQuery(),
                SQLTools.getStringForSQL(metadata.getTeamName()),
                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString())));

        String deleteSecurityGroupMembershipStatement = MessageFormat.format(deleteSecurityGroupTeamsByTeamIdQuery(), SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
        getMetadataRepository().executeUpdate(deleteSecurityGroupMembershipStatement);
        for (SecurityGroup securityGroup : metadata.getSecurityGroups()) {
            String insertSecurityGroupStatement =
                    MessageFormat.format(insertSecurityGroupTeamsQuery(),
                            SQLTools.getStringForSQL(securityGroup.getMetadataKey().getUuid()),
                            SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid()));
            getMetadataRepository().executeUpdate(insertSecurityGroupStatement);
        }

        Set<RoleKey> oldRoleKeyss = roleConfiguration.getByTeamId(metadata.getMetadataKey()).stream()
                .map(Metadata::getMetadataKey)
                .collect(Collectors.toSet());
        oldRoleKeyss.removeAll(metadata.getRoles().stream()
                .map(Metadata::getMetadataKey)
                .collect(Collectors.toSet()));
        for (RoleKey roleKey : oldRoleKeyss) {
            roleConfiguration.delete(roleKey);
        }

        for (Role role : metadata.getRoles()) {
            roleConfiguration.update(role);
        }
    }

    public Set<SecurityGroup> getSecurityGroups(TeamKey teamKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSecurityGroupsByTeamIdQuery(), SQLTools.getStringForSQL(teamKey.getUuid())),
                    "reader");
            return new HashSet<>(new SecurityGroupListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<User> getUsers(TeamKey teamKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchUsersByTeamIdQuery(), SQLTools.getStringForSQL(teamKey.getUuid())),
                    "reader");
            return new HashSet<>(new UserListResultSetExtractor().extractData(cachedRowSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                insertSecurityGroupTeamsQuery(),
                SQLTools.getStringForSQL(securityGroupKey.getUuid()),
                SQLTools.getStringForSQL(teamKey.getUuid())
        ));
    }

    public void removeSecurityGroup(TeamKey teamKey, SecurityGroupKey securityGroupKey) {
        getMetadataRepository().executeUpdate(MessageFormat.format(
                deleteSecurityGroupTeamsBySecurityGroupIdAndTeamIdQuery(),
                SQLTools.getStringForSQL(teamKey.getUuid()),
                SQLTools.getStringForSQL(securityGroupKey.getUuid())
        ));
    }

}
