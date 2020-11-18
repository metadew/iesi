package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Profile("security")
@Tag(name = "security", description = "Everything about securities")
@RequestMapping("/security-groups")
@CrossOrigin
@Log4j2
public class SecurityGroupController {

    private final SecurityGroupService securityGroupService;
    private final ISecurityGroupDtoService securityGroupDtoService;

    public SecurityGroupController(SecurityGroupService securityGroupService, ISecurityGroupDtoService securityGroupDtoService) {
        this.securityGroupService = securityGroupService;
        this.securityGroupDtoService = securityGroupDtoService;
    }

    @PostMapping("")
    public ResponseEntity<SecurityGroupDto> create(@RequestBody SecurityGroupPostDto securityGroupPostDto) {
        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(securityGroupPostDto.getId()))
                .name(securityGroupPostDto.getName())
                .teamKeys(new HashSet<>())
                .securedObjects(new HashSet<>())
                .build();
        securityGroupService.addSecurityGroup(securityGroup);
        return ResponseEntity.of(securityGroupDtoService.get(securityGroup.getMetadataKey().getUuid()));
    }

    @PostMapping("/{uuid}/teams")
    public ResponseEntity<SecurityGroupDto> addTeam(@PathVariable UUID uuid, @RequestBody SecurityGroupTeamPutDto securityGroupTeamPutDto) {
        if (securityGroupService.exists(new SecurityGroupKey(uuid))) {
            securityGroupService.addTeam(new SecurityGroupKey(uuid), new TeamKey(securityGroupTeamPutDto.getId()));
            return ResponseEntity.of(securityGroupDtoService.get(uuid));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{security-group-uuid}/teams/{team-uuid}")
    public ResponseEntity<SecurityGroupDto> deleteTeam(@PathVariable("security-group-uuid") UUID securityGroupUuid, @PathVariable("team-uuid") UUID teamUuid) {
        if (securityGroupService.exists(new SecurityGroupKey(securityGroupUuid))) {
            securityGroupService.deleteTeam(new SecurityGroupKey(securityGroupUuid), new TeamKey(teamUuid));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SecurityGroupDto> fetch(@PathVariable UUID uuid) {
        return ResponseEntity
                .of(securityGroupDtoService.get(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<SecurityGroupDto> update(@PathVariable UUID uuid, @RequestBody SecurityGroupPutDto securityGroupPutDto) {
        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(securityGroupPutDto.getId()))
                .name(securityGroupPutDto.getName())
                .securedObjects(new HashSet<>())
                .teamKeys(securityGroupPutDto.getTeams().stream()
                        .map(securityGroupTeamPutDto -> new TeamKey(securityGroupPutDto.getId()))
                        .collect(Collectors.toSet()))
                .build();
        securityGroupService.update(securityGroup);
        return ResponseEntity
                .of(securityGroupDtoService.get(uuid));
    }

    @GetMapping("")
    public Set<SecurityGroupDto> fetchAll() {
        return securityGroupDtoService.getAll();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Object> deleteById(@PathVariable UUID uuid) {
        securityGroupService.delete(new SecurityGroupKey(uuid));
        return ResponseEntity.noContent().build();
    }

}
