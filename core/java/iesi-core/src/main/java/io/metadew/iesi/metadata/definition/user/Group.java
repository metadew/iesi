package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Group extends Metadata<GroupKey> {

    private String groupName;

    @Builder
    public Group(GroupKey groupKey, String groupName) {
        super(groupKey);
        this.groupName = groupName;
    }

    //TODO: Lazy loading of authorities and groups. When moving to Spring add as lazy loading
    public List<User> getUsers() {
        return new ArrayList<>();
    }

    public List<User> getAuthorities() {
        return new ArrayList<>();
    }

}