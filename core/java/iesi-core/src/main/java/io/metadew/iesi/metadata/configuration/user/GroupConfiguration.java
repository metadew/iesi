package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.*;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class GroupConfiguration extends Configuration<Team, TeamKey> {

    private static GroupConfiguration INSTANCE;

    public synchronized static GroupConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroupConfiguration();
        }
        return INSTANCE;
    }

    private GroupConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
    }

    @Override
    public Optional<Team> get(TeamKey metadataKey) {
        try {
            String queryScript = "select ID, GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
                    " WHERE ID=" + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapGroup(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Team> getAll() {
        List<Team> teams = new ArrayList<>();
        try {
            String queryScript = "select ID, GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                teams.add(mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    @Override
    public void delete(TeamKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }


    public void delete(String groupName) {
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
                " WHERE GROUP_NAME = " + SQLTools.GetStringForSQL(groupName) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    @Override
    public void insert(Team metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
                " (ID, GROUP_NAME) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getTeamName()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }


    public boolean exists(String groupName) {
        return get(groupName).isPresent();
    }


    @Override
    public void update(Team metadata) {
        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Groups") +
                " SET GROUP_NAME = " + SQLTools.GetStringForSQL(metadata.getTeamName()) +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(updateStatement);
    }

    public List<User> getUsers(TeamKey teamKey) {
        List<User> users = new ArrayList<>();
        try {
            String queryScript = "select users.ID, users.USERNAME, users.PASSWORD, users.ENABLED, users.EXPIRED, users.CREDENTIALS_EXPIRED, users.LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                    "on group_members.USER_ID=users.ID WHERE " +
                    "GROUP_ID =" + SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                users.add(UserConfiguration.getInstance().mapUser(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<User> getUsers(String groupName) {
        List<User> users = new ArrayList<>();
        try {
            String queryScript = "select users.ID, users.USERNAME, users.PASSWORD, users.ENABLED, users.EXPIRED, users.CREDENTIALS_EXPIRED, users.LOCKED " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
                    "on group_members.GROUP_ID=groups.ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                    "on group_members.USER_ID=users.ID WHERE " +
                    "GROUP_NAME =" + SQLTools.GetStringForSQL(groupName) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                users.add(UserConfiguration.getInstance().mapUser(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<Privilege> getAuthorities(TeamKey teamKey) {
        List<Privilege> authorities = new ArrayList<>();
        try {
            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on group_authorities.AUTHORITY_ID=authorities.ID WHERE " +
                    "GROUP_ID =" + SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    public List<Privilege> getAuthorities(String groupName) {
        List<Privilege> authorities = new ArrayList<>();
        try {
            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
                    "on groups.ID=group_authorities.GROUP_ID " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                    "on group_authorities.AUTHORITY_ID=authorities.ID WHERE " +
                    "GROUP_NAME =" + SQLTools.GetStringForSQL(groupName) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    public void addUser(TeamKey teamKey, UserKey userKey) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
                " (USER_ID, GROUP_ID) VALUES (" +
                SQLTools.GetStringForSQL(userKey.getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void addUser(String groupName, String username) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
                " (GROUP_ID, USER_ID) SELECT groups.ID, users.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                " where groups.GROUP_NAME=" + SQLTools.GetStringForSQL(groupName) + " and " +
                "users.USERNAME=" + SQLTools.GetStringForSQL(username) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeUser(TeamKey teamKey, UserKey userKey) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
                " WHERE USER_ID=" + SQLTools.GetStringForSQL(userKey.getUuid().toString()) + " and " +
                "GROUP_ID= " + SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeUser(String groupName, String username) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
                " WHERE (GROUP_ID, USER_ID) IN ( " +
                "SELECT groups.ID, users.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
                " where groups.GROUP_NAME=" + SQLTools.GetStringForSQL(groupName) + " and " +
                "users.USERNAME=" + SQLTools.GetStringForSQL(username) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void addAuthority(TeamKey teamKey, PrivilegeKey privilegeKey) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
                " (GROUP_ID, AUTHORITY_ID) VALUES (" +
                SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(privilegeKey.getUuid().toString()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void addAuthority(String groupName, String authority) {
        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
                " (GROUP_ID, AUTHORITY_ID) SELECT groups.ID, authorities.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                " where groups.GROUP_NAME=" + SQLTools.GetStringForSQL(groupName) + " and " +
                "authorities.AUTHORITY=" + SQLTools.GetStringForSQL(authority) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeAuthority(String groupName, String authority) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
                " WHERE (GROUP_ID, AUTHORITY_ID) IN ( " +
                "SELECT groups.ID, authorities.ID FROM " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
                " where groups.GROUP_NAME=" + SQLTools.GetStringForSQL(groupName) + " and " +
                "authorities.AUTHORITY=" + SQLTools.GetStringForSQL(authority) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public void removeAuthority(TeamKey teamKey, PrivilegeKey privilegeKey) {
        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
                " WHERE AUTHORITY_ID=" + SQLTools.GetStringForSQL(privilegeKey.getUuid().toString()) + " and " +
                "GROUP_ID= " + SQLTools.GetStringForSQL(teamKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    public Optional<Team> get(String name) {
        try {
            String queryScript = "select ID, GROUP_NAME " +
                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
                    " WHERE GROUP_NAME=" + SQLTools.GetStringForSQL(name) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            if (cachedRowSet.next()) {
                return Optional.of(mapGroup(cachedRowSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Team mapGroup(CachedRowSet cachedRowSet) throws SQLException {
        return new Team(new TeamKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("GROUP_NAME"), roles, userKeys, roles);
    }

}
