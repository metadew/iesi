package io.metadew.iesi.server.rest.user;

import java.util.HashSet;
import java.util.Set;

public class UserFiltersBuilder {

    private String username;

    public UserFiltersBuilder username(String username) {
        this.username = username;
        return this;
    }

    public Set<UserFilter> build() {
        Set<UserFilter> userFilters = new HashSet<>();
        if (username != null) {
            userFilters.add(new UserFilter(UserFilterOption.USERNAME, username, false));
        }
        return userFilters;
    }

}
