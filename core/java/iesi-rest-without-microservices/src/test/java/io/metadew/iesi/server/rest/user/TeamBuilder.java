package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.server.rest.user.role.PrivilegeDto;
import io.metadew.iesi.server.rest.user.role.RoleDto;
import io.metadew.iesi.server.rest.user.team.TeamDto;
import io.metadew.iesi.server.rest.user.team.TeamSecurityGroupDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamBuilder {

    public static Map<String, Object> generateTeam(int teamIndex, int roleCount, int privilegeCount, Set<SecurityGroup> securityGroups) {
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
                                    .teamKey(new TeamKey(uuid))
                                    .name(String.format("role%s", roleIndex))
                                    .privileges(IntStream.range(0, privilegeCount)
                                            .boxed()
                                            .map(privilegeIndex -> {
                                                UUID privilegeUuid = UUID.randomUUID();
                                                info.put(String.format("privilege%d%dUUID", roleIndex, privilegeIndex), privilegeUuid);
                                                Privilege privilege = Privilege.builder()
                                                        .privilegeKey(new PrivilegeKey(privilegeUuid))
                                                        .privilege(String.format("authority%s", privilegeIndex))
                                                        .roleKey(new RoleKey(roleUuid))
                                                        .build();
                                                info.put(String.format("privilege%d%d", roleIndex, privilegeIndex), privilege);
                                                return privilege;
                                            }).collect(Collectors.toSet()))
                                    .users(new HashSet<>())
                                    .build();
                            info.put(String.format("role%d", roleIndex), role);
                            return role;
                        })
                        .collect(Collectors.toSet()))
                .securityGroups(securityGroups)
                .build();
        info.put("team", team);

        TeamDto teamDto = TeamDto.builder()
                .id(uuid)
                .teamName(String.format("team%s", teamIndex))
                .roles(team.getRoles().stream()
                        .map(role -> RoleDto.builder()
                                .id(role.getMetadataKey().getUuid())
                                .name(role.getName())
                                .privileges(role.getPrivileges().stream()
                                        .map(privilege -> PrivilegeDto.builder()
                                                .uuid(privilege.getMetadataKey().getUuid())
                                                .privilege(privilege.getPrivilege())
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .securityGroups(securityGroups.stream()
                        .map(securityGroup -> TeamSecurityGroupDto.builder()
                                .id(securityGroup.getMetadataKey().getUuid())
                                .name(securityGroup.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
        info.put("teamDto", teamDto);

        return info;
    }
}
