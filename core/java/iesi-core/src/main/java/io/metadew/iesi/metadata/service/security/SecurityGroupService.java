package io.metadew.iesi.metadata.service.security;


import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service()
@Log4j2
public class SecurityGroupService {

    private final SecurityGroupConfiguration securityGroupConfiguration;

    public SecurityGroupService(SecurityGroupConfiguration securityGroupConfiguration) {
        this.securityGroupConfiguration = securityGroupConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        if (!securityGroupConfiguration.getByName("PUBLIC").isPresent()) {
            log.info("Creating PUBLIC security group for IESI");
            SecurityGroup publicSecurityGroup = SecurityGroup.builder()
                    .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                    .name("PUBLIC")
                    .teamKeys(new HashSet<>())
                    .securedObjects(new HashSet<>())
                    .build();
            addSecurityGroup(publicSecurityGroup);
        }
    }

    public List<SecurityGroup> getAll() {
        return securityGroupConfiguration.getAll();
    }

    public boolean exists(SecurityGroupKey securityGroupKey) {
        return securityGroupConfiguration.exists(securityGroupKey);
    }


    public void addSecurityGroup(SecurityGroup securityGroup) {
        securityGroupConfiguration.insert(securityGroup);
    }

    public Optional<SecurityGroup> get(SecurityGroupKey securityGroupKey) {
        return securityGroupConfiguration.get(securityGroupKey);
    }

    public Optional<SecurityGroup> get(String securityGroupname) {
        return securityGroupConfiguration.getByName(securityGroupname);
    }

    public void update(SecurityGroup securityGroup) {
        securityGroupConfiguration.update(securityGroup);
    }

    public void delete(SecurityGroupKey securityGroupKey) {
        securityGroupConfiguration.delete(securityGroupKey);
    }

    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        securityGroupConfiguration.addTeam(securityGroupKey, teamKey);
    }

    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        securityGroupConfiguration.deleteTeam(securityGroupKey, teamKey);
    }

}
