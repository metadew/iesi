package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.user.Role;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserBuilder {

    private static Map<String, Object> generateUser(String username, Set<Role> roles) {
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
        return info;
    }
}
