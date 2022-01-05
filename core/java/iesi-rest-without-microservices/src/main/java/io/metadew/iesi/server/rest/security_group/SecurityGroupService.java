package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class SecurityGroupService implements ISecurityGroupService {

    private final ISecurityGroupDtoRepository securityGroupDtoRepository;

    private final io.metadew.iesi.metadata.service.security.SecurityGroupService rawSecurityGroupService;

    @Autowired
    public SecurityGroupService(ISecurityGroupDtoRepository securityGroupDtoRepository, io.metadew.iesi.metadata.service.security.SecurityGroupService rawSecurityGroupService) {
        this.securityGroupDtoRepository = securityGroupDtoRepository;
        this.rawSecurityGroupService = rawSecurityGroupService;
    }

    public Optional<SecurityGroupDto> get(String username) {
        return securityGroupDtoRepository.get(username);
    }

    public Optional<SecurityGroupDto> get(UUID id) {
        return securityGroupDtoRepository.get(id);
    }

    public Set<SecurityGroupDto> getAll() {
        return securityGroupDtoRepository.getAll();
    }

    @Override
    public List<SecurityGroup> getAllRawSecurityGroups() {
        return rawSecurityGroupService.getAll();
    }

    @Override
    public boolean exists(SecurityGroupKey securityGroupKey) {
        return rawSecurityGroupService.exists(securityGroupKey);
    }

    @Override
    public void addSecurityGroup(SecurityGroup securityGroup) {
        rawSecurityGroupService.addSecurityGroup(securityGroup);
    }

    @Override
    public Optional<SecurityGroup> getRawSecurityGroup(SecurityGroupKey securityGroupKey) {
        return rawSecurityGroupService.get(securityGroupKey);
    }

    @Override
    public Optional<SecurityGroup> getRawSecurityGroup(String securityGroupname) {
        return rawSecurityGroupService.get(securityGroupname);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void update(SecurityGroup securityGroup) {
        rawSecurityGroupService.update(securityGroup);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(SecurityGroupKey securityGroupKey) {
        rawSecurityGroupService.delete(securityGroupKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        rawSecurityGroupService.addTeam(securityGroupKey, teamKey);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey) {
        rawSecurityGroupService.deleteTeam(securityGroupKey, teamKey);
    }

}
