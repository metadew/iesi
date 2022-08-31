package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamBuilder {

    private static Map<String, Object> generateUser(int teamIndex, int roleCount, TeamKey teamKey, int privilegeCount, Set<SecurityGroup> securityGroups) {
        Map<String, Object> info = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        info.put("teamUUID", uuid);

        Team team = Team.builder()
                .teamKey(new TeamKey(uuid))
                .teamName(String.format("team%s", teamIndex))
                .roles(IntStream.range(0, roleCount)
                        .boxed()
                        .map(roleIndex -> {
                            UUID roleUuid = UUID.randomUUID();
                            info.put(String.format("role%dUUID", roleIndex), roleUuid);
                            Role role = Role.builder()
                                    .metadataKey(new RoleKey(roleUuid))
                                    .teamKey(teamKey)
                                    .name(String.format("role%s", roleIndex))
                                    .privileges(IntStream.range(0, privilegeCount)
                                            .boxed()
                                            .map(privilegeIndex -> {
                                                UUID privilegeUuid = UUID.randomUUID();
                                                info.put(String.format("privilege%d%dUUID", roleIndex, privilegeIndex), privilegeUuid);
                                                Privilege privilege =  Privilege.builder()
                                                        .privilegeKey(PrivilegeKey.builder()
                                                                .uuid(privilegeUuid)
                                                                .build())
                                                        .privilege(String.format("authority%s", privilegeIndex))
                                                        .build();
                                                info.put(String.format("privilege%d%d", roleIndex, privilegeIndex), privilege);
                                                return privilege;
                                            }).collect(Collectors.toSet()))
                                    .build();
                            info.put(String.format("role%d", roleIndex), role);
                            return role;
                        })
                        .collect(Collectors.toSet()))
                .securityGroups(securityGroups)
                .build();
        info.put("team", team);

        return info;
    }
}
