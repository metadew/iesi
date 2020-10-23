package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
@Profile("security")
@DependsOn("metadataRepositoryConfiguration")
public class IesiUserDetailsManager implements UserDetailsManager {

    private TeamConfiguration teamConfiguration;

    //TODO: move to Spring, extend JDBCUserDetailsManager. Override getXSql() methods to adhere to custom data model
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTeamConfiguration(TeamConfiguration teamConfiguration) {
        this.teamConfiguration = teamConfiguration;
    }

    @Override
    public IesiUserDetails loadUserByUsername(String name) {
        Set<IESIGrantedAuthority> iesiGrantedAuthorities = new HashSet<>();
        User user = userService
                .get(name)
                .orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));

        for (Role role : userService.getRoles(user.getMetadataKey())) {
            Set<SecurityGroup> securityGroups = teamConfiguration.getSecurityGroups(role.getTeamKey());
            for (SecurityGroup securityGroup : securityGroups) {
                for (Privilege privilege : role.getPrivileges()) {
                    iesiGrantedAuthorities.add(new IESIGrantedAuthority(securityGroup, privilege));
                }
            }

        }
        return new IesiUserDetails(user, iesiGrantedAuthorities);
    }

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException("A user cannot be created in this way");
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("A user cannot be created in this way");
    }

    @Override
    public void deleteUser(String username) {
        userService.delete(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new AccessDeniedException("old password cannot be the same as new password");

        }

        String username = currentUser.getName();
        User user = userService.get(username)
                .orElseThrow(() -> new RuntimeException("Could not find user with name " + username));
        if (!user.getPassword().equals(oldPassword)) {
            throw new AccessDeniedException("old password does not match existing password");
        }

        user.setPassword(newPassword);
        userService.update(user);

        SecurityContextHolder.getContext().setAuthentication(
                createNewAuthentication(currentUser, newPassword));
    }

    private Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userService.exists(username);
    }

}
