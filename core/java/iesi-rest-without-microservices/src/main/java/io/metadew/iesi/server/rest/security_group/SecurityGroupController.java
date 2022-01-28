package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/security-groups")
@CrossOrigin
@Log4j2
@ConditionalOnWebApplication
public class SecurityGroupController {

    public static final String PUBLIC_GROUP_NAME = "PUBLIC";

    private final ISecurityGroupService securityGroupService;
    private final ISecurityGroupPutDtoService securityGroupPutDtoService;
    private final PagedResourcesAssembler<SecurityGroupDto> securityGroupDtoPagedResourceAssembler;
    private final SecurityGroupDtoResourceAssembler securityGroupDtoResourceAssembler;

    public SecurityGroupController(
            ISecurityGroupService securityGroupService,
            ISecurityGroupPutDtoService securityGroupPutDtoService,
            PagedResourcesAssembler securityGroupDtoPagedResourceAssembler,
            SecurityGroupDtoResourceAssembler securityGroupDtoResourceAssembler
    ) {
        this.securityGroupService = securityGroupService;
        this.securityGroupPutDtoService = securityGroupPutDtoService;
        this.securityGroupDtoPagedResourceAssembler = securityGroupDtoPagedResourceAssembler;
        this.securityGroupDtoResourceAssembler = securityGroupDtoResourceAssembler;
    }

    @PostConstruct
    void checkPublicSecurityGroup() {
        if (!securityGroupService.get(PUBLIC_GROUP_NAME).isPresent()) {
            log.warn(String.format("Creating %s security group.", PUBLIC_GROUP_NAME));
            SecurityGroup publicSecurityGroup = SecurityGroup.builder()
                    .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                    .name(PUBLIC_GROUP_NAME)
                    .teams(new HashSet<>())
                    .securedObjects(new HashSet<>())
                    .build();
            securityGroupService.addSecurityGroup(publicSecurityGroup);
        }
    }

    @PostMapping("")
    @PreAuthorize("hasPrivilege('GROUPS_WRITE')")
    public ResponseEntity<SecurityGroupDto> create(@RequestBody SecurityGroupPostDto securityGroupPostDto) {
        SecurityGroup securityGroup = SecurityGroup.builder()
                .metadataKey(new SecurityGroupKey(securityGroupPostDto.getId()))
                .name(securityGroupPostDto.getName())
                .teams(new HashSet<>())
                .securedObjects(new HashSet<>())
                .build();
        securityGroupService.addSecurityGroup(securityGroup);
        return ResponseEntity.of(securityGroupService.get(securityGroup.getMetadataKey().getUuid()));
    }

    @PostMapping("/{uuid}/teams")
    @PreAuthorize("hasPrivilege('GROUPS_WRITE')")
    public ResponseEntity<SecurityGroupDto> addTeam(@PathVariable UUID uuid, @RequestBody SecurityGroupTeamPutDto securityGroupTeamPutDto) {
        if (securityGroupService.exists(new SecurityGroupKey(uuid))) {
            securityGroupService.addTeam(new SecurityGroupKey(uuid), new TeamKey(securityGroupTeamPutDto.getId()));
            return ResponseEntity.of(securityGroupService.get(uuid));
        } else {
            throw new MetadataDoesNotExistException(new SecurityGroupKey(uuid));
        }
    }

    @DeleteMapping("/{security-group-uuid}/teams/{team-uuid}")
    @PreAuthorize("hasPrivilege('GROUPS_WRITE')")
    public ResponseEntity<SecurityGroupDto> deleteTeam(@PathVariable("security-group-uuid") UUID securityGroupUuid, @PathVariable("team-uuid") UUID teamUuid) {
        if (securityGroupService.exists(new SecurityGroupKey(securityGroupUuid))) {
            securityGroupService.deleteTeam(new SecurityGroupKey(securityGroupUuid), new TeamKey(teamUuid));
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new MetadataDoesNotExistException(new SecurityGroupKey(securityGroupUuid));
        }
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasPrivilege('GROUPS_READ')")
    public ResponseEntity<SecurityGroupDto> get(@PathVariable String name) {
        return ResponseEntity
                .of(securityGroupService.get(name));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('GROUPS_WRITE')")
    public ResponseEntity<SecurityGroupDto> update(@PathVariable UUID uuid, @RequestBody SecurityGroupPutDto securityGroupPutDto) {
        SecurityGroup securityGroup = securityGroupPutDtoService.convertToEntity(securityGroupPutDto);
        securityGroupService.update(securityGroup);
        return ResponseEntity
                .of(securityGroupService.get(uuid));
    }

    @GetMapping("")
    @PreAuthorize("hasPrivilege('GROUPS_READ')")
    public PagedModel<SecurityGroupDto> getAll(Pageable pageable, @RequestParam(required = false, name = "name") String name) {
        List<SecurityGroupFilter> securityGroupFilters = extractSecurityGroupFilterOptions(name);
        Page<SecurityGroupDto> securityGroupDtoPage = securityGroupService.getAll(pageable, securityGroupFilters);

        if (securityGroupDtoPage.hasContent()) {
            return securityGroupDtoPagedResourceAssembler.toModel(securityGroupDtoPage, securityGroupDtoResourceAssembler::toModel);
        }

        return (PagedModel<SecurityGroupDto>) securityGroupDtoPagedResourceAssembler.toEmptyModel(securityGroupDtoPage, SecurityGroupDto.class);
    }

    private List<SecurityGroupFilter> extractSecurityGroupFilterOptions(String name) {
        List<SecurityGroupFilter> securityGroupFilters = new ArrayList<>();
        if (name != null) {
            securityGroupFilters.add(new SecurityGroupFilter(SecurityGroupFilterOption.NAME, name, false));
        }
        return securityGroupFilters;
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasPrivilege('GROUPS_WRITE')")
    public ResponseEntity<Object> deleteById(@PathVariable UUID uuid) {
        securityGroupService.delete(new SecurityGroupKey(uuid));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
