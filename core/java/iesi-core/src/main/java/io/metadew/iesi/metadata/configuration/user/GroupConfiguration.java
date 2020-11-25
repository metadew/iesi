//package io.metadew.iesi.metadata.configuration.user;
//
//import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
//import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
//import io.metadew.iesi.connection.tools.SQLTools;
//import io.metadew.iesi.metadata.configuration.Configuration;
//import io.metadew.iesi.metadata.definition.user.*;
//import lombok.extern.log4j.Log4j2;
//
//import javax.sql.rowset.CachedRowSet;
//import java.sql.SQLException;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Log4j2
//public class GroupConfiguration extends Configuration<Group, GroupKey> {
//
//    private static GroupConfiguration INSTANCE;
//
//    public synchronized static GroupConfiguration getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new GroupConfiguration();
//        }
//        return INSTANCE;
//    }
//
//    private GroupConfiguration() {
//        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getControlMetadataRepository());
//    }
//
//    @Override
//    public Optional<Group> get(GroupKey metadataKey) {
//        try {
//            String queryScript = "select ID, GROUP_NAME " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
//                    " WHERE ID=" + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            if (cachedRowSet.next()) {
//                return Optional.of(mapGroup(cachedRowSet));
//            } else {
//                return Optional.empty();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public List<Group> getAll() {
//        List<Group> groups = new ArrayList<>();
//        try {
//            String queryScript = "select ID, GROUP_NAME " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            while (cachedRowSet.next()) {
//                groups.add(mapGroup(cachedRowSet));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return groups;
//    }
//
//    @Override
//    public void delete(GroupKey metadataKey) {
//        log.trace(MessageFormat.format("Deleting {0}.", metadataKey.toString()));
//        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
//                " WHERE ID = " + SQLTools.getStringForSQL(metadataKey.getUuid().toString()) + ";";
//        getMetadataRepository().executeUpdate(deleteStatement);
//    }
//
//
//    public void delete(String groupName) {
//        String deleteStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
//                " WHERE GROUP_NAME = " + SQLTools.getStringForSQL(groupName) + ";";
//        getMetadataRepository().executeUpdate(deleteStatement);
//    }
//
//    @Override
//    public void insert(Group metadata) {
//        log.trace(MessageFormat.format("Inserting {0}.", metadata.toString()));
//        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
//                " (ID, GROUP_NAME) VALUES (" +
//                SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ", " +
//                SQLTools.getStringForSQL(metadata.getGroupName()) + ");";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//
//    public boolean exists(String groupName) {
//        return get(groupName).isPresent();
//    }
//
//
//    @Override
//    public void update(Group metadata) {
//        String updateStatement = "UPDATE " + getMetadataRepository().getTableNameByLabel("Groups") +
//                " SET GROUP_NAME = " + SQLTools.getStringForSQL(metadata.getGroupName()) +
//                " WHERE ID = " + SQLTools.getStringForSQL(metadata.getMetadataKey().getUuid().toString()) + ";";
//        getMetadataRepository().executeUpdate(updateStatement);
//    }
//
//    public List<User> getUsers(GroupKey groupKey) {
//        List<User> users = new ArrayList<>();
//        try {
//            String queryScript = "select users.ID, users.USERNAME, users.PASSWORD, users.ENABLED, users.EXPIRED, users.CREDENTIALS_EXPIRED, users.LOCKED " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
//                    "on group_members.USER_ID=users.ID WHERE " +
//                    "GROUP_ID =" + SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            while (cachedRowSet.next()) {
//                users.add(UserConfiguration.getInstance().mapUser(cachedRowSet));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return users;
//    }
//
//    public List<User> getUsers(String groupName) {
//        List<User> users = new ArrayList<>();
//        try {
//            String queryScript = "select users.ID, users.USERNAME, users.PASSWORD, users.ENABLED, users.EXPIRED, users.CREDENTIALS_EXPIRED, users.LOCKED " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() + " group_members " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
//                    "on group_members.GROUP_ID=groups.ID " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
//                    "on group_members.USER_ID=users.ID WHERE " +
//                    "GROUP_NAME =" + SQLTools.getStringForSQL(groupName) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            while (cachedRowSet.next()) {
//                users.add(UserConfiguration.getInstance().mapUser(cachedRowSet));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return users;
//    }
//
//    public List<Authority> getAuthorities(GroupKey groupKey) {
//        List<Authority> authorities = new ArrayList<>();
//        try {
//            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
//                    "on group_authorities.AUTHORITY_ID=authorities.ID WHERE " +
//                    "GROUP_ID =" + SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            while (cachedRowSet.next()) {
//                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return authorities;
//    }
//
//    public List<Authority> getAuthorities(String groupName) {
//        List<Authority> authorities = new ArrayList<>();
//        try {
//            String queryScript = "select authorities.ID, authorities.AUTHORITY " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() + " group_authorities " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups " +
//                    "on groups.ID=group_authorities.GROUP_ID " +
//                    "inner join " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
//                    "on group_authorities.AUTHORITY_ID=authorities.ID WHERE " +
//                    "GROUP_NAME =" + SQLTools.getStringForSQL(groupName) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            while (cachedRowSet.next()) {
//                authorities.add(AuthorityConfiguration.getInstance().mapAuthority(cachedRowSet));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return authorities;
//    }
//
//    public void addUser(GroupKey groupKey, UserKey userKey) {
//        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
//                " (USER_ID, GROUP_ID) VALUES (" +
//                SQLTools.getStringForSQL(userKey.getUuid().toString()) + ", " +
//                SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ");";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void addUser(String groupName, String username) {
//        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
//                " (GROUP_ID, USER_ID) SELECT groups.ID, users.ID FROM " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
//                " where groups.GROUP_NAME=" + SQLTools.getStringForSQL(groupName) + " and " +
//                "users.USERNAME=" + SQLTools.getStringForSQL(username) + ";";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void removeUser(GroupKey groupKey, UserKey userKey) {
//        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
//                " WHERE USER_ID=" + SQLTools.getStringForSQL(userKey.getUuid().toString()) + " and " +
//                "GROUP_ID= " + SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ";";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void removeUser(String groupName, String username) {
//        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupMembers").getName() +
//                " WHERE (GROUP_ID, USER_ID) IN ( " +
//                "SELECT groups.ID, users.ID FROM " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Users").getName() + " users " +
//                " where groups.GROUP_NAME=" + SQLTools.getStringForSQL(groupName) + " and " +
//                "users.USERNAME=" + SQLTools.getStringForSQL(username) + ");";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void addAuthority(GroupKey groupKey, AuthorityKey authorityKey) {
//        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
//                " (GROUP_ID, AUTHORITY_ID) VALUES (" +
//                SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ", " +
//                SQLTools.getStringForSQL(authorityKey.getUuid().toString()) + ");";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void addAuthority(String groupName, String authority) {
//        String insertStatement = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
//                " (GROUP_ID, AUTHORITY_ID) SELECT groups.ID, authorities.ID FROM " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
//                " where groups.GROUP_NAME=" + SQLTools.getStringForSQL(groupName) + " and " +
//                "authorities.AUTHORITY=" + SQLTools.getStringForSQL(authority) + ";";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void removeAuthority(String groupName, String authority) {
//        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
//                " WHERE (GROUP_ID, AUTHORITY_ID) IN ( " +
//                "SELECT groups.ID, authorities.ID FROM " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() + " groups, " +
//                MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Authorities").getName() + " authorities " +
//                " where groups.GROUP_NAME=" + SQLTools.getStringForSQL(groupName) + " and " +
//                "authorities.AUTHORITY=" + SQLTools.getStringForSQL(authority) + ");";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public void removeAuthority(GroupKey groupKey, AuthorityKey authorityKey) {
//        String insertStatement = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("GroupAuthorities").getName() +
//                " WHERE AUTHORITY_ID=" + SQLTools.getStringForSQL(authorityKey.getUuid().toString()) + " and " +
//                "GROUP_ID= " + SQLTools.getStringForSQL(groupKey.getUuid().toString()) + ";";
//        getMetadataRepository().executeUpdate(insertStatement);
//    }
//
//    public Optional<Group> get(String name) {
//        try {
//            String queryScript = "select ID, GROUP_NAME " +
//                    "from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Groups").getName() +
//                    " WHERE GROUP_NAME=" + SQLTools.getStringForSQL(name) + ";";
//            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(queryScript, "reader");
//            if (cachedRowSet.next()) {
//                return Optional.of(mapGroup(cachedRowSet));
//            } else {
//                return Optional.empty();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Group mapGroup(CachedRowSet cachedRowSet) throws SQLException {
//        return new Group(new GroupKey(UUID.fromString(cachedRowSet.getString("ID"))),
//                cachedRowSet.getString("GROUP_NAME"));
//    }
//
//}
