package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleTeamDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserBuilder {

    public static Map<String, Object> generateUser(String username, Set<Role> roles, String teamName) {
        Map<String, Object> info = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        info.put("userUUID", uuid);
        User user = User.builder()
                .userKey(UserKey.builder()
                        .uuid(uuid)
                        .build())
                .username(username)
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .password("password")
                .roleKeys(roles.stream()
                        .map(Metadata::getMetadataKey)
                        .collect(Collectors.toSet())
                )
                .build();
        info.put("user", user);

        UserDto userDto = UserDto.builder()
                .id(uuid)
                .username(username)
                .enabled(true)
                .expired(false)
                .credentialsExpired(false)
                .locked(false)
                .roles(roles.stream()
                        .map(role -> UserRoleDto.builder()
                                .id(role.getMetadataKey().getUuid())
                                .name(role.getName())
                                .team(RoleTeamDto.builder()
                                        .id(role.getTeamKey().getUuid())
                                        .name(teamName)
                                        .build())
                                .privileges(role.getPrivileges().stream()
                                        .map(privilege -> PrivilegeDto.builder()
                                                .uuid(privilege.getMetadataKey().getUuid())
                                                .privilege(privilege.getPrivilege())
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet())

                )
                .build();
        info.put("userDto", userDto);
        return info;
    }

}
