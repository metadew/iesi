package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;

public interface ISecurityGroupPutDtoService {
    SecurityGroup convertToEntity(SecurityGroupPutDto securityGroupPutDto);
}
