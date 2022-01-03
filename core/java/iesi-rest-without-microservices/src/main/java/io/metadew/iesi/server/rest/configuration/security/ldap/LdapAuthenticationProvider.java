package io.metadew.iesi.server.rest.configuration.security.ldap;

import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private LdapContextSource ldapContextSource;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Filter filter = new EqualsFilter("cn", "cagetat");
        System.out.println("TEST");
        LdapQuery query = LdapQueryBuilder.query().where("cn").is("cagetat");
        System.out.println("LDAP NAME : " + ldapContextSource.getBaseLdapName());
        ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.encode(), "hErebih0Nlph99");
        System.out.println("TEST");
        /*
        if (authenticate) {
            Set<IESIGrantedAuthority> authorities = generateIesiAuthorities();
            IesiUserDetails iesiUserDetails = generateIesiDetails();
            return new UsernamePasswordAuthenticationToken(
                    iesiUserDetails,
                    authorities
            );
        }
        */
        return null;
       /* AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("cn", authentication.getName()));
        filter.and(new EqualsFilter("ou", authentication.getName()));
        filter.and(new HardcodedFilter(partitionSuffix));
        boolean authenticate = ldapTemplate.authenticate(LdapUtils.emptyLdapName(),
                filter.encode(),
                authentication.getCredentials().toString());
        if (authenticate) {
            Set<IESIGrantedAuthority> authorities = generateIesiAuthorities();
            IesiUserDetails iesiUserDetails = generateIesiDetails();
            return new UsernamePasswordAuthenticationToken(
                    iesiUserDetails,
                    authorities
            );
        }
        return null;*/
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private Set<IESIGrantedAuthority> generateIesiAuthorities() {
        return null;
    }

    private IesiUserDetails generateIesiDetails() {
        return null;
    }
}
