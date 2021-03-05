package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.server.rest.dataset.Filter;

public class UserFilter extends Filter {

    public UserFilter(UserFilterOption filterOption, String value, boolean exactMatch) {
        super(filterOption, value, exactMatch);
    }
}
