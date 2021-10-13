package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.server.rest.user.IUserService;
import io.metadew.iesi.server.rest.user.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
@DependsOn("metadataRepositoryConfiguration")
@Log4j2
@ConditionalOnWebApplication
public class IesiUserDetailsManager implements UserDetailsManager {

    private final IUserService userService;

    public IesiUserDetailsManager(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public IesiUserDetails loadUserByUsername(String name) {
        User rawUser = userService
                .getRawUser(name)
                .orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));

        return new IesiUserDetails(rawUser, getGrantedAuthorities(name));
    }

    public Set<IESIGrantedAuthority> getGrantedAuthorities(String username) {
        UserDto user = userService
                .get(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
        return user.getRoles()
                .stream()
                .map(userRoleDto -> userRoleDto.getTeam().getSecurityGroups().stream()
                        .map(teamSecurityGroupDto -> userRoleDto.getPrivileges().stream()
                                .map(privilegeDto -> new IESIGrantedAuthority(teamSecurityGroupDto.getName(), privilegeDto.getPrivilege()))
                                .collect(Collectors.toSet())
                        )
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
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
                    "Can't change password as no Authentication object found in context for current user.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new AccessDeniedException("old password cannot be the same as new password");

        }

        String username = currentUser.getName();
        User user = userService.getRawUser(username)
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
