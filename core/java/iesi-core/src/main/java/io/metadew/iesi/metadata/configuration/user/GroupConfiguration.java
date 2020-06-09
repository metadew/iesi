package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.Group;
import io.metadew.iesi.metadata.definition.user.GroupKey;
import io.metadew.iesi.metadata.definition.user.User;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class GroupConfiguration extends Configuration<Group, GroupKey> {

    private static GroupConfiguration INSTANCE;

    public synchronized static GroupConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroupConfiguration();
        }
        return INSTANCE;
    }

    private GroupConfiguration() {
    }

    @Override
    public Optional<Group> get(GroupKey metadataKey) {
        try {
            String queryScript = "select ID, GROUP_NAME from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups") +
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
    public List<Group> getAll() {
        List<Group> groups = new ArrayList<>();
        try {
            String queryScript = "select ID, GROUP_NAME from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                groups.add(mapGroup(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }

    @Override
    public void delete(GroupKey metadataKey) {
        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups") +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadataKey.getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    @Override
    public void insert(Group metadata) {
        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
        String insertStatement = "INSERT INTO FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups") +
                " (ID, GROUP_NAME) VALUES (" +
                SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
                SQLTools.GetStringForSQL(metadata.getGroupName()) + ");";
        getMetadataRepository().executeUpdate(insertStatement);
    }

    @Override
    public void update(Group metadata) {
        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Groups") +
                " SET GROUP_NAME = " + SQLTools.GetStringForSQL(metadata.getGroupName()) +
                " WHERE ID = " + SQLTools.GetStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ";";
        getMetadataRepository().executeUpdate(updateStatement);
    }

    public List<User> getUsers(GroupKey groupKey) {
        List<User> users = new ArrayList<>();
        try {
            String queryScript = "select users.ID, users.USERNAME, users.PASSWORD, users.ENABLED from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers") + " group_members " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users") + " users " +
                    "on group_members.USER_ID=users.ID WHERE " +
                    "GROUP_ID =" + SQLTools.GetStringForSQL(groupKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                users.add(UserConfiguration.getInstance().mapUser(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<Authority> getAuthorities(GroupKey groupKey) {
        List<Authority> authorities = new ArrayList<>();
        try {
            String queryScript = "select authorities.ID, authorities.AUTHORITY from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities") + " group_authorities " +
                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities") + " authorities " +
                    "on group_authorities.AUTHORITY_ID=authorities.ID WHERE " +
                    "GROUP_ID =" + SQLTools.GetStringForSQL(groupKey.getUuid().toString()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
            while (cachedRowSet.next()) {
                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }


    public Group mapGroup(CachedRowSet cachedRowSet) throws SQLException {
        return new Group(new GroupKey(UUID.fromString(cachedRowSet.getString("ID"))),
                cachedRowSet.getString("GROUP_NAME"));
    }

}
