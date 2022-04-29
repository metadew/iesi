package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.server.rest.dataset.Filter;

public class TeamFilter extends Filter {
    public TeamFilter(TeamFilterOption teamFilterOption, String value, boolean exactMatch) {
        super(teamFilterOption, value, exactMatch);
    }
}
