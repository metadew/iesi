package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.GroupConfiguration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.Group;
import io.metadew.iesi.metadata.definition.user.GroupKey;
import io.metadew.iesi.metadata.definition.user.User;

import java.util.List;
import java.util.Optional;

public class GroupService {

    private static GroupService INSTANCE;

    public synchronized static GroupService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GroupService();
        }
        return INSTANCE;
    }

    private GroupService() {
    }

    public List<Group> getAll() {
        return GroupConfiguration.getInstance().getAll();
    }

    public void addGroup(Group group) {
        GroupConfiguration.getInstance().insert(group);
    }

    public Optional<Group> get(GroupKey groupKey) {
        return GroupConfiguration.getInstance().get(groupKey);
    }

    public void update(Group group) {
        GroupConfiguration.getInstance().update(group);
    }

    public void delete(Group group) {
        GroupConfiguration.getInstance().delete(group.getMetadataKey());
    }

    public List<Authority> getAuthorities(Group group) {
        return GroupConfiguration.getInstance().getAuthorities(group.getMetadataKey());
    }

    public List<User> getUsers(Group group) {
        return GroupConfiguration.getInstance().getUsers(group.getMetadataKey());
    }

    public void addAuthority(Group group, Authority authority) {
        GroupConfiguration.getInstance().addAuthority(group.getMetadataKey(), authority.getMetadataKey());
    }

    public void removeAuthority(Group group, Authority authority) {
        GroupConfiguration.getInstance().removeAuthority(group.getMetadataKey(), authority.getMetadataKey());
    }

    public void addUser(Group group, User user) {
        GroupConfiguration.getInstance().addUser(group.getMetadataKey(), user.getMetadataKey());
    }

    public void removeUser(Group group, User user) {
        GroupConfiguration.getInstance().removeUser(group.getMetadataKey(), user.getMetadataKey());
    }


}
