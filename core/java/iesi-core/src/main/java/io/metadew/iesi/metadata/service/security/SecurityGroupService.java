package io.metadew.iesi.metadata.service.security;


import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public class SecurityGroupService {

    private static SecurityGroupService instance;

    public static synchronized SecurityGroupService getInstance() {
        if (instance == null) {
            instance = new SecurityGroupService();
        }
        return instance;
    }

    private SecurityGroupService() {
        if (!SecurityGroupConfiguration.getInstance().getByName("PUBLIC").isPresent()) {
            log.info("Creating PUBLIC security group for IESI");
            SecurityGroup publicSecurityGroup = SecurityGroup.builder()
                    .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                    .name("PUBLIC")
                    .teams(new HashSet<>())
                    .securedObjects(new HashSet<>())
                    .build();
            addSecurityGroup(publicSecurityGroup);
        }
    }

    public List<SecurityGroup> getAll() {
        return SecurityGroupConfiguration.getInstance().getAll();
    }

    public boolean exists(SecurityGroupKey securityGroupKey) {
        return SecurityGroupConfiguration.getInstance().exists(securityGroupKey);
    }


    public void addSecurityGroup(SecurityGroup securityGroup) {
        SecurityGroupConfiguration.getInstance().insert(securityGroup);
    }

    public Optional<SecurityGroup> get(SecurityGroupKey securityGroupKey) {
        return SecurityGroupConfiguration.getInstance().get(securityGroupKey);
    }

    public Optional<SecurityGroup> get(String securityGroupname) {
        return SecurityGroupConfiguration.getInstance().getByName(securityGroupname);
    }

    public void update(SecurityGroup securityGroup) {
        SecurityGroupConfiguration.getInstance().update(securityGroup);
    }

    public void delete(SecurityGroupKey securityGroupKey) {
        SecurityGroupConfiguration.getInstance().delete(securityGroupKey);
    }

    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        SecurityGroupConfiguration.getInstance().addTeam(securityGroupKey, teamKey);
    }

    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        SecurityGroupConfiguration.getInstance().deleteTeam(securityGroupKey, teamKey);
    }

}
