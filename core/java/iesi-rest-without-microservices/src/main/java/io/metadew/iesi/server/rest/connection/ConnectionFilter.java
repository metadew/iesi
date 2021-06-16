package io.metadew.iesi.server.rest.connection;

import io.metadew.iesi.server.rest.dataset.Filter;

public class ConnectionFilter extends Filter {
    public ConnectionFilter(ConnectionFilterOption scriptFilterOption, String value, boolean exactMatch) {
        super(scriptFilterOption, value, exactMatch);
    }
}
