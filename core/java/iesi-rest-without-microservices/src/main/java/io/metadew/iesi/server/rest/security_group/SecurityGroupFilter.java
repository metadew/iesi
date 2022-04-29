package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.server.rest.dataset.Filter;

public class SecurityGroupFilter extends Filter {
    public SecurityGroupFilter(SecurityGroupFilterOption securityGroupFilterOption, String value, boolean exactMatch) {
        super(securityGroupFilterOption, value, exactMatch);
    }
}
