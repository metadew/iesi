package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Privilege;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.metadata.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Component
@Profile("security")
@DependsOn("metadataRepositoryConfiguration")
public class CustomUserDetailsManager implements UserDetailsManager {

    private TeamConfiguration teamConfiguration;

    //@Autowired
    //public void setGroupService(GroupService groupService) {

    //    this.groupService = groupService;
    //}

    //TODO: move to Spring, extend JDBCUserDetailsManager. Override getXSql() methods to adhere to custom data model
    private UserService userService;
    // private GroupService groupService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTeamConfiguration(TeamConfiguration teamConfiguration) {
        this.teamConfiguration = teamConfiguration;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
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
        return new CustomUserDetails(user, iesiGrantedAuthorities);
    }

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException("A user cannot be created in this way");
//        userService.addUser(convert(user));
//        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
//            userService.addRole(user.getUsername(), grantedAuthority.getAuthority());
//        }
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("A user cannot be created in this way");
        //userService.update(convert(user));
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

    private Authentication createNewAuthentication(Authentication currentAuth,
                                                   String newPassword) {
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

//    @Override
//    public List<String> findAllGroups() {
//        return groupService.getAll().stream()
//                .map(Group::getGroupName)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<String> findUsersInGroup(String groupName) {
//        return groupService.getUsers(groupName).stream()
//                .map(User::getUsername)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void createGroup(String groupName, List<GrantedAuthority> authorities) {
//        groupService.addGroup(new Group(new GroupKey(UUID.randomUUID()), groupName));
//        authorities.forEach(
//                grantedAuthority -> groupService.addAuthority(groupName, grantedAuthority.getAuthority())
//        );
//    }
//
//    @Override
//    public void deleteGroup(String groupName) {
//        groupService.delete(groupName);
//    }
//
//    @Override
//    public void renameGroup(String oldName, String newName) {
//        Group group = groupService.get(oldName).orElseThrow(() -> new RuntimeException("Could not find group " + oldName));
//        group.setGroupName(newName);
//        groupService.update(group);
//    }
//
//    @Override
//    public void addUserToGroup(String username, String group) {
//        groupService.addUser(group, username);
//    }
//
//    @Override
//    public void removeUserFromGroup(String username, String groupName) {
//        groupService.removeUser(groupName, username);
//    }
//
//    @Override
//    public List<GrantedAuthority> findGroupAuthorities(String groupName) {
//        return groupService.getAuthorities(groupName).stream()
//                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void addGroupAuthority(String groupName, GrantedAuthority authority) {
//        groupService.addAuthority(groupName, authority.getAuthority());
//    }
//
//    @Override
//    public void removeGroupAuthority(String groupName, GrantedAuthority authority) {
//        groupService.removeAuthority(groupName, authority.getAuthority());
//    }
}
