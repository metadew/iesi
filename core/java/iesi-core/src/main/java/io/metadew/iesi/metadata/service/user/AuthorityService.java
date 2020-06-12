package io.metadew.iesi.metadata.service.user;

import io.metadew.iesi.metadata.configuration.user.AuthorityConfiguration;
import io.metadew.iesi.metadata.definition.user.Authority;
import io.metadew.iesi.metadata.definition.user.AuthorityKey;

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

    public List<Authority> getAll() {
        return AuthorityConfiguration.getInstance().getAll();
    }

    public void addAuthority(Authority authority) {
        AuthorityConfiguration.getInstance().insert(authority);
    }

    public Optional<Authority> get(AuthorityKey authorityKey) {
        return AuthorityConfiguration.getInstance().get(authorityKey);
    }

    public void update(Authority authority) {
        AuthorityConfiguration.getInstance().update(authority);
    }

    public void delete(AuthorityKey authorityKey) {
        AuthorityConfiguration.getInstance().delete(authorityKey);
    }

    public void delete(String authority) {
        AuthorityConfiguration.getInstance().delete(authority);
    }

}
