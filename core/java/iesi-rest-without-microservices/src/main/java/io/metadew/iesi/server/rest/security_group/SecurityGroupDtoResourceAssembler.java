package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SecurityGroupDtoResourceAssembler extends RepresentationModelAssemblerSupport<SecurityGroup, SecurityGroupDto> {

    private final TeamConfiguration teamConfiguration;

    public SecurityGroupDtoResourceAssembler(TeamConfiguration teamConfiguration) {
        super(SecurityGroupController.class, SecurityGroupDto.class);
        this.teamConfiguration = teamConfiguration;
    }

    @Override
    public SecurityGroupDto toModel(SecurityGroup entity) {
        SecurityGroupDto securityGroupDto = convertToDto(entity);
        Link selfLink = linkTo(methodOn(SecurityGroupController.class).get(securityGroupDto.getName()))
                .withRel("security group:" + securityGroupDto.getId());
        securityGroupDto.add(selfLink);
        return securityGroupDto;
    }

    private SecurityGroupDto convertToDto(SecurityGroup securityGroup) {
        return new SecurityGroupDto(
                securityGroup.getMetadataKey().getUuid(),
                securityGroup.getName(),
                securityGroup.getTeamKeys().stream().map(this::convertToDto).collect(Collectors.toSet()),
                new HashSet<>());
    }

    private SecurityGroupTeamDto convertToDto(TeamKey teamKey) {
        Team team = teamConfiguration.get(teamKey)
                .orElseThrow(() -> new MetadataDoesNotExistException(teamKey));
        return new SecurityGroupTeamDto(
                team.getMetadataKey().getUuid(),
                team.getTeamName()
        );
    }

    public SecurityGroupDto toModel(SecurityGroupDto securityGroupDto) {
        return securityGroupDto;
    }
}
