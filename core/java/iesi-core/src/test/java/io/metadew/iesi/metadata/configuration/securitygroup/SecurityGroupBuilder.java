package io.metadew.iesi.metadata.configuration.securitygroup;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.Team;

import java.util.*;
import java.util.stream.Collectors;

public class SecurityGroupBuilder {

    private static Map<String, Object> generateSecurityGroup(int securityGroupIndex, Set<Team> teams) {
        Map<String, Object> info = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        info.put("securityGroupUUID", uuid);


        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(uuid))
                .name(String.format("securityGroup%d", securityGroupIndex))
                .teamKeys(teams.stream().map(Team::getMetadataKey).collect(Collectors.toSet()))
                .securedObjects(new HashSet<>())
                .build();
        info.put("securityGroup", securityGroup);

        return info;
    }
}
