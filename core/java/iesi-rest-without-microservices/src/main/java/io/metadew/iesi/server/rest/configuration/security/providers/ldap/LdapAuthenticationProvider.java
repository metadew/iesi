package io.metadew.iesi.server.rest.configuration.security.providers.ldap;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetails;
import io.metadew.iesi.server.rest.user.ldap.LdapGroup;
import io.metadew.iesi.server.rest.user.ldap.LdapUser;
import io.metadew.iesi.server.rest.user.team.TeamService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapContextSource ldapContextSource;
    private final LdapTemplate ldapTemplate;
    private final LdapAuthentication ldapAuthentication;
    private final LdapGroupMapping ldapGroupMapping;
    private final TeamService teamConfiguration;

    public LdapAuthenticationProvider(
            LdapContextSource ldapContextSource,
            LdapTemplate ldapTemplate,
            LdapAuthentication ldapAuthentication,
            LdapGroupMapping ldapGroupMapping,
            TeamService teamConfiguration
    ) {
        this.ldapContextSource = ldapContextSource;
        this.ldapTemplate = ldapTemplate;
        this.ldapAuthentication = ldapAuthentication;
        this.ldapGroupMapping = ldapGroupMapping;
        this.teamConfiguration = teamConfiguration;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authentication: " + authentication.getPrincipal());
        LdapUser user = authenticate(authentication.getName(), authentication.getCredentials().toString())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", authentication.getName())));

        Set<IESIGrantedAuthority> grantedAuthorities = generateIesiAuthorities(user);

        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                grantedAuthorities
        );
    }

    private Optional<LdapUser> authenticate(String username, String password) {
        String userAuthAttribute = ldapAuthentication.getUsers().getUserAuthenticateAttribute();
        String userBaseDn = ldapAuthentication.getUsers().getUserSearchBaseDn();
        String userSearchAttribute = ldapAuthentication.getUsers().getUserSearchAttribute();

        Filter authFilter = new EqualsFilter(userAuthAttribute, username);
        Filter searchFilter = new EqualsFilter(userSearchAttribute, username);

        boolean auth = ldapTemplate.authenticate(
                LdapUtils.newLdapName(userBaseDn),
                authFilter.encode(), password,
                new IesiLdapAuthenticationErrorCallback()
        );

        if (!auth) {
            return Optional.empty();
        }

        return ldapTemplate.search(
                        userBaseDn,
                        searchFilter.encode(),
                        new IesiLdapContextMappers.UserMapper(ldapContextSource.getBaseLdapPathAsString(), userDn -> {
                            String groupSearchBase = ldapGroupMapping.getGroupSearchBaseDn();
                            String groupSearchAttribute = ldapGroupMapping.getGroupSearchAttribute();
                            String groupMemberAttribute = ldapGroupMapping.getGroupMemberAttribute();

                            String searchFilter1 = String.format("%s=%s", groupMemberAttribute, userDn);

                            return ldapTemplate.search(
                                    groupSearchBase,
                                    searchFilter1,
                                    new IesiLdapContextMappers.GroupMapper(groupSearchAttribute, ldapContextSource.getBaseLdapPathAsString())
                            );
                        }))
                .stream()
                .findFirst();
    }


    private Set<IESIGrantedAuthority> generateIesiAuthorities(LdapUser user) {
        HashSet<IESIGrantedAuthority> iesiGrantedAuthorities = new HashSet<>();
        String iesiTeamName;
        Optional<Set<Privilege>> iesiPrivileges;

        for (LdapGroupMapping.MappingPair mappingPair : ldapGroupMapping.getMappingPairs()) {
            iesiTeamName = mappingPair.getIesiName();

            String finalIesiGroupTeamName = iesiTeamName;
            Team iesiTeam = teamConfiguration.getRawTeam(iesiTeamName)
                    .orElseThrow(() -> new MetadataDoesNotExistException(String.format("The team %s defined in the ldap configuration does not exist", finalIesiGroupTeamName)));
            iesiPrivileges = getPrivilegeFromAdRole(user.getGroups(), mappingPair.getAdName(), iesiTeam);


            if (iesiPrivileges.isPresent()) {
                Set<SecurityGroup> securityGroups = teamConfiguration.getSecurityGroups(iesiTeam.getMetadataKey());
                for (SecurityGroup securityGroup : securityGroups) {
                    for (Privilege privilege : iesiPrivileges.get()) {
                        iesiGrantedAuthorities.add(new IESIGrantedAuthority(
                                securityGroup.getName(),
                                privilege.getPrivilege()
                        ));
                    }
                }
                return iesiGrantedAuthorities;
            }
        }

        return new HashSet<>();
    }

    private Optional<Set<Privilege>> getPrivilegeFromAdRole(List<LdapGroup> groups, String adGroupTeamName, Team iesiTeam) {
        String adGroupName;
        String adRole;

        Set<Role> iesiRoles;
        Set<Privilege> iesiPrivileges;

        for (LdapGroup group : groups) {
            adGroupName = group.getCn();
            if (adGroupName.contains(adGroupTeamName)) {
                iesiRoles = iesiTeam.getRoles();
                adRole = adGroupName.substring(0, adGroupName.lastIndexOf(adGroupTeamName));
                iesiPrivileges = getIesiPrivileges(adRole, iesiRoles)
                        .orElseThrow(() -> new MetadataDoesNotExistException(String.format("The role %s defined in your Active Directory does not exist on the team %s linked to %S", adRole, adGroupTeamName, iesiTeam.getTeamName())));

                return Optional.of(iesiPrivileges);
            }
        }

        return Optional.empty();
    }

    private Optional<Set<Privilege>> getIesiPrivileges(String adRole, Set<Role> iesiRoles) {
        for (Role role : iesiRoles) {
            if (role.getName().equalsIgnoreCase(adRole)) {
                return Optional.of(role.getPrivileges());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
