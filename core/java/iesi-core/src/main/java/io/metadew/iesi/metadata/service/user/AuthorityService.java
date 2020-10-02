package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.AuthorityConfiguration;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.PrivilegeKey;

import java.util.List;
import java.util.Optional;

public class AuthorityService {

    private static AuthorityService INSTANCE;

    public synchronized static AuthorityService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthorityService();
        }
        return INSTANCE;
    }

    private AuthorityService() {
    }

    public List<Privilege> getAll() {
        return AuthorityConfiguration.getInstance().getAll();
    }

    public void addAuthority(Privilege privilege) {
        AuthorityConfiguration.getInstance().insert(privilege);
    }

    public Optional<Privilege> get(PrivilegeKey privilegeKey) {
        return AuthorityConfiguration.getInstance().get(privilegeKey);
    }

    public void update(Privilege privilege) {
        AuthorityConfiguration.getInstance().update(privilege);
    }

    public void delete(PrivilegeKey privilegeKey) {
        AuthorityConfiguration.getInstance().delete(privilegeKey);
    }

    public void delete(String authority) {
        AuthorityConfiguration.getInstance().delete(authority);
    }

}
