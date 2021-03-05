package io.metadew.iesi.server.rest.security_group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class SecurityGroupDtoService implements ISecurityGroupDtoService {

    private final ISecurityGroupDtoRepository securityGroupDtoRepository;

    @Autowired
    public SecurityGroupDtoService(ISecurityGroupDtoRepository securityGroupDtoRepository) {
        this.securityGroupDtoRepository = securityGroupDtoRepository;
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

}
