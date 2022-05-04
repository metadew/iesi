package io.metadew.iesi.server.rest.configuration.security.providers;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.server.rest.configuration.ldap.LdapAuthentication;
import io.metadew.iesi.server.rest.configuration.ldap.LdapGroupMapping;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetails;
import io.metadew.iesi.server.rest.security_group.SecurityGroupService;
import io.metadew.iesi.server.rest.user.ldap.LdapGroup;
import io.metadew.iesi.server.rest.user.ldap.LdapUser;
import io.metadew.iesi.server.rest.user.team.TeamService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LdapContextSource ldapContextSource;

    @Autowired
    private LdapTemplate ldapTemplate;

    private final LdapAuthentication ldapAuthentication;
    private final LdapGroupMapping ldapGroupMapping;
    private final SecurityGroupService securityGroupConfiguration;
    private final TeamService teamConfiguration;
    private final RoleService roleConfiguration;

    public LdapAuthenticationProvider(
            LdapAuthentication ldapAuthentication,
            LdapGroupMapping ldapGroupMapping,
            SecurityGroupService securityGroupConfiguration,
            TeamService teamConfiguration,
            RoleService roleConfiguration
    ) {
        this.ldapAuthentication = ldapAuthentication;
        this.ldapGroupMapping = ldapGroupMapping;
        this.securityGroupConfiguration = securityGroupConfiguration;
        this.teamConfiguration = teamConfiguration;
        this.roleConfiguration = roleConfiguration;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LdapUser user = authenticate(authentication.getName(), authentication.getCredentials().toString())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", authentication.getName())));

        user.setGroups(getUserGroups(user.getDn()));

        log.info("USER AUTHENTICATED: " + user);

        Set<IESIGrantedAuthority> grantedAuthorities = generateIesiAuthorities(user);

        return new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                grantedAuthorities
        );
    }

    private List<LdapGroup> getUserGroups(String userDn) {
        String groupSearchBase = ldapGroupMapping.getGroupSearchBaseDn();
        String groupSearchAttribute = ldapGroupMapping.getGroupSearchAttribute();
        String groupMemberAttribute = ldapGroupMapping.getGroupMemberAttribute();

        String searchFilter = String.format("%s=%s", groupMemberAttribute, userDn);

        return ldapTemplate.search(
                groupSearchBase,
                searchFilter,
                new IesiLdapContextMappers.GroupMapper(groupSearchAttribute, ldapContextSource.getBaseLdapPathAsString())
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
                        new IesiLdapContextMappers.UserMapper(ldapContextSource.getBaseLdapPathAsString()))
                .stream()
                .findFirst();
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
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
            }
        }
        return iesiGrantedAuthorities;
    }

    private Optional<Set<Privilege>> getPrivilegeFromAdRole(List<LdapGroup> groups, String adGroupTeamName, Team iesiTeam) {
        String adGroupAbsoluteName;
        String adRole;

        List<Role> iesiRoles;
        Set<Privilege> iesiPrivileges;

        for (LdapGroup group : groups) {
            adGroupAbsoluteName = group.getCn();
            if (adGroupAbsoluteName.contains(adGroupTeamName)) {
                iesiRoles = roleConfiguration.getByTeamId(iesiTeam.getMetadataKey());
                adRole = adGroupAbsoluteName.substring(0, adGroupAbsoluteName.lastIndexOf(adGroupTeamName));
                iesiPrivileges = getIesiPrivileges(adRole, iesiRoles)
                        .orElseThrow(() -> new MetadataDoesNotExistException(String.format("The role %s defined in your Active Directory does not exist on the team %s linked to %S", adRole, adGroupTeamName, iesiTeam.getTeamName())));

                return Optional.of(iesiPrivileges);
            }
        }

        return Optional.empty();
    }

    private Optional<Set<Privilege>> getIesiPrivileges(String adRole, List<Role> iesiRoles) {
        for (Role role : iesiRoles) {
            if (role.getName().equalsIgnoreCase(adRole)) {
                return Optional.of(role.getPrivileges());
            }
        }
        return Optional.empty();
    }

    private IesiUserDetails generateIesiDetails() {
        return null;
    }
}
