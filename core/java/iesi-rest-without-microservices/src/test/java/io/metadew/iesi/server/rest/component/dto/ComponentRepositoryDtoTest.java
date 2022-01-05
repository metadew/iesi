package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.component.ComponentFilter;
import io.metadew.iesi.server.rest.component.ComponentFilterOption;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ComponentRepositoryDtoTest {

    @Autowired
    private ComponentDtoRepository componentDtoRepository;
    @Autowired
    private ComponentConfiguration componentConfiguration;
    @Autowired
    private SecurityGroupConfiguration securityGroupConfiguration;
    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    private ComponentDtoResourceAssembler componentDtoResourceAssembler;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllNoResultsTest() {
        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(), new ArrayList<>()))
                .isEmpty();
    }

    @Test
    void getAllTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);
        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(), new ArrayList<>())).containsOnly(
                component1Dto,
                component2Dto
        );
    }

    @Test
    void getAllPaginatedAllInclusiveTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        Component component3 = createComponent("component3", "3", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);
        ComponentDto component3Dto = componentDtoResourceAssembler.toModel(component3);

        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);
        assertThat(componentDtoRepository.getAll(null, page1, new ArrayList<>())).containsOnly(
                component1Dto,
                component2Dto
        );
        assertThat(componentDtoRepository.getAll(null, page2, new ArrayList<>())).containsOnly(
                component3Dto
        );
    }

    @Test
    void getAllPaginatedSortedAscTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        Component component3 = createComponent("component3", "3", "PUBLIC");
        Component component4 = createComponent("component4", "4", "PUBLIC");
        Component component5 = createComponent("component5", "5", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);
        componentConfiguration.insert(component4);
        componentConfiguration.insert(component5);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);
        ComponentDto component3Dto = componentDtoResourceAssembler.toModel(component3);
        ComponentDto component4Dto = componentDtoResourceAssembler.toModel(component4);
        ComponentDto component5Dto = componentDtoResourceAssembler.toModel(component5);

        PageRequest pageable1 = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        PageRequest pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "name"));
        PageRequest pageable3 = PageRequest.of(2, 2, Sort.by(Sort.Direction.ASC, "name"));

        Page<ComponentDto> page1 = componentDtoRepository.getAll(null, pageable1, new ArrayList<>());
        Page<ComponentDto> page2 = componentDtoRepository.getAll(null, pageable2, new ArrayList<>());
        Page<ComponentDto> page3 = componentDtoRepository.getAll(null, pageable3, new ArrayList<>());
        assertThat(page1).containsExactly(
                component1Dto,
                component2Dto
        );
        assertThat(page2).containsExactly(
                component3Dto,
                component4Dto
        );
        assertThat(page3).containsExactly(
                component5Dto
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);

    }

    @Test
    void getAllPaginatedSortedDescTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        Component component3 = createComponent("component3", "3", "PUBLIC");
        Component component4 = createComponent("component4", "4", "PUBLIC");
        Component component5 = createComponent("component5", "5", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);
        componentConfiguration.insert(component4);
        componentConfiguration.insert(component5);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);
        ComponentDto component3Dto = componentDtoResourceAssembler.toModel(component3);
        ComponentDto component4Dto = componentDtoResourceAssembler.toModel(component4);
        ComponentDto component5Dto = componentDtoResourceAssembler.toModel(component5);

        PageRequest pageable1 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name"));
        PageRequest pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "name"));
        PageRequest pageable3 = PageRequest.of(2, 2, Sort.by(Sort.Direction.DESC, "name"));

        Page<ComponentDto> page1 = componentDtoRepository.getAll(null, pageable1, new ArrayList<>());
        Page<ComponentDto> page2 = componentDtoRepository.getAll(null, pageable2, new ArrayList<>());
        Page<ComponentDto> page3 = componentDtoRepository.getAll(null, pageable3, new ArrayList<>());
        assertThat(page1).containsExactly(
                component5Dto,
                component4Dto
        );
        assertThat(page2).containsExactly(
                component3Dto,
                component2Dto
        );
        assertThat(page3).containsExactly(
                component1Dto
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
    }

    @Test
    void getAllFilteredOnNameTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);

        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ComponentFilter(ComponentFilterOption.NAME, "nent", false)
                ).collect(Collectors.toList())
        )).containsOnly(component2Dto, component1Dto);
        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ComponentFilter(ComponentFilterOption.NAME, "nent1", false)
                ).collect(Collectors.toList())
        )).containsExactly(component1Dto);
        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ComponentFilter(ComponentFilterOption.NAME, "nent2", false)
                ).collect(Collectors.toList())
        )).containsExactly(component2Dto);
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "COMPONENTS_READ@GROUPA"
    })
    void getAllSecurityGroupA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoA = createComponentDto(componentA);

        Page<ComponentDto> componentDtoPage = componentDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );

        assertThat(componentDtoPage).hasSize(1).containsOnly(componentDtoA);
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPB"
    })
    void getAllSecurityGroupB() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoB = createComponentDto(componentB);

        Page<ComponentDto> componentDtoPage = componentDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );

        assertThat(componentDtoPage).hasSize(1).containsOnly(componentDtoB);
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPA",
            "COMPONENTS_READ@GROUPB"
    })
    void getAllSecurityGroupBA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String componentNameC = "componentC";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";
        String securityGroupCName = "GROUPC";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);
        SecurityGroup securityGroupC = createSecurityGroup(securityGroupCName, componentNameC);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        securityGroupConfiguration.insert(securityGroupC);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);
        Component componentC = createComponent(componentNameC, componentNameC, securityGroupCName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);
        componentConfiguration.insert(componentC);

        ComponentDto componentDtoA = createComponentDto(componentA);
        ComponentDto componentDtoB = createComponentDto(componentB);

        Page<ComponentDto> componentDtoPage = componentDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );

        assertThat(componentDtoPage).hasSize(2).containsOnly(componentDtoA, componentDtoB);
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "COMPONENTS_READ@PUBLIC"
    })
    void getAllNoMatchedSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Page<ComponentDto> componentDtoPage = componentDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );

        assertThat(componentDtoPage).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getAllNoSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Page<ComponentDto> componentDtoPage = componentDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );

        assertThat(componentDtoPage).isEmpty();
    }

    @Test
    void getAllByVersionTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);

        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ComponentFilter(ComponentFilterOption.VERSION, "0", false)
                ).collect(Collectors.toList())
        )).isEmpty();
        assertThat(componentDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ComponentFilter(ComponentFilterOption.VERSION, "1", false)
                ).collect(Collectors.toList())
        )).containsOnly(component1Dto, component2Dto);
    }

    @Test
    void getByNameTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        ComponentDto component1Dto = componentDtoResourceAssembler.toModel(component1);
        ComponentDto component2Dto = componentDtoResourceAssembler.toModel(component2);

        assertThat(componentDtoRepository.getByName(null, Pageable.unpaged(), "component1"))
                .containsOnly(component1Dto);
        assertThat(componentDtoRepository.getByName(null, Pageable.unpaged(), "component2"))
                .containsOnly(component2Dto);
    }

    @Test
    void getByNameNoResultsTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);

        assertThat(componentDtoRepository.getByName(null, Pageable.unpaged(), "component3"))
                .isEmpty();
    }

    @Test
    void getByNameAndVersionNoResultsTest() {
        Component component1 = createComponent("component1", "1", "PUBLIC");
        Component component2 = createComponent("component2", "2", "PUBLIC");
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);

        assertThat(componentDtoRepository.getByNameAndVersion(null, "component3", 1L))
                .isEmpty();
        assertThat(componentDtoRepository.getByNameAndVersion(null, "component1", 2L))
                .isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "COMPONENTS_READ@GROUPA"
    })
    void getByNameSecurityGroupA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoA = createComponentDto(componentA);
        Page<ComponentDto> componentDtoPageA = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameA
        );
        Page<ComponentDto> componentDtoPageB = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameB
        );

        assertThat(componentDtoPageA).isNotEmpty();
        assertThat(componentDtoPageA.stream().findFirst()).isPresent().contains(componentDtoA);
        assertThat(componentDtoPageB).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPB"
    })
    void getByNameSecurityGroupB() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoB = createComponentDto(componentB);
        Page<ComponentDto> componentDtoPageA = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameA
        );
        Page<ComponentDto> componentDtoPageB = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameB
        );

        assertThat(componentDtoPageB).isNotEmpty();
        assertThat(componentDtoPageB.stream().findFirst()).isPresent().contains(componentDtoB);
        assertThat(componentDtoPageA).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPA",
            "COMPONENTS_READ@GROUPB"
    })
    void getByNameSecurityGroupBA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String componentNameC = "componentC";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";
        String securityGroupCName = "GROUPC";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);
        SecurityGroup securityGroupC = createSecurityGroup(securityGroupCName, componentNameC);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        securityGroupConfiguration.insert(securityGroupC);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);
        Component componentC = createComponent(componentNameC, componentNameC, securityGroupCName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);
        componentConfiguration.insert(componentC);

        ComponentDto componentDtoA = createComponentDto(componentA);
        ComponentDto componentDtoB = createComponentDto(componentB);

        Page<ComponentDto> componentDtoPageA = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameA
        );
        Page<ComponentDto> componentDtoPageB = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameB
        );
        Page<ComponentDto> componentDtoPageC = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameC
        );


        assertThat(componentDtoPageA).isNotEmpty();
        assertThat(componentDtoPageA.stream().findFirst()).isPresent().contains(componentDtoA);
        assertThat(componentDtoPageB).isNotEmpty();
        assertThat(componentDtoPageB.stream().findFirst()).isPresent().contains(componentDtoB);
        assertThat(componentDtoPageC).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "COMPONENTS_READ@PUBLIC"
    })
    void getByNameNoMatchedSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Page<ComponentDto> componentDtoPageA = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameA
        );
        Page<ComponentDto> componentDtoPageB = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameB
        );

        assertThat(componentDtoPageA).isEmpty();
        assertThat(componentDtoPageB).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getByNameNoSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Page<ComponentDto> componentDtoPageA = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameA
        );
        Page<ComponentDto> componentDtoPageB = componentDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                componentNameB
        );

        assertThat(componentDtoPageA).isEmpty();
        assertThat(componentDtoPageB).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "COMPONENTS_READ@GROUPA"
    })
    void getByNameAndVersionSecurityGroupA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoA = createComponentDto(componentA);

        Optional<ComponentDto> fetchedComponentDtoA = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameA,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoB = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameB,
                1L
        );

        assertThat(fetchedComponentDtoA).isPresent().contains(componentDtoA);
        assertThat(fetchedComponentDtoB).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPB"
    })
    void getByNameAndVersionSecurityGroupB() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        ComponentDto componentDtoB = createComponentDto(componentB);

        Optional<ComponentDto> fetchedComponentDtoA = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameA,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoB = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameB,
                1L
        );

        assertThat(fetchedComponentDtoB).isPresent().contains(componentDtoB);
        assertThat(fetchedComponentDtoA).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@GROUPA",
            "COMPONENTS_READ@GROUPB"
    })
    void getByNameAndVersionSecurityGroupBA() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String componentNameC = "componentC";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";
        String securityGroupCName = "GROUPC";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);
        SecurityGroup securityGroupC = createSecurityGroup(securityGroupCName, componentNameC);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        securityGroupConfiguration.insert(securityGroupC);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);
        Component componentC = createComponent(componentNameC, componentNameC, securityGroupCName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);
        componentConfiguration.insert(componentC);

        ComponentDto componentDtoA = createComponentDto(componentA);
        ComponentDto componentDtoB = createComponentDto(componentB);

        Optional<ComponentDto> fetchedComponentDtoA = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameA,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoB = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameB,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoC = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameC,
                1L
        );

        assertThat(fetchedComponentDtoA).isPresent().contains(componentDtoA);
        assertThat(fetchedComponentDtoB).isPresent().contains(componentDtoB);
        assertThat(fetchedComponentDtoC).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "COMPONENTS_READ@PUBLIC"
    })
    void getByNameAndVersionNoMatchedSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Optional<ComponentDto> fetchedComponentDtoA = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameA,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoB = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameB,
                1L
        );

        assertThat(fetchedComponentDtoB).isNotPresent();
        assertThat(fetchedComponentDtoA).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {})
    void getByNameAndVersionNoSecurityGroups() {
        String componentNameA = "componentA";
        String componentNameB = "componentB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, componentNameA);
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, componentNameB);

        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);

        Component componentA = createComponent(componentNameA, componentNameA, securityGroupAName);
        Component componentB = createComponent(componentNameB, componentNameB, securityGroupBName);

        componentConfiguration.insert(componentA);
        componentConfiguration.insert(componentB);

        Optional<ComponentDto> fetchedComponentDtoA = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameA,
                1L
        );
        Optional<ComponentDto> fetchedComponentDtoB = componentDtoRepository.getByNameAndVersion(
                SecurityContextHolder.getContext().getAuthentication(),
                componentNameB,
                1L
        );

        assertThat(fetchedComponentDtoB).isNotPresent();
        assertThat(fetchedComponentDtoA).isNotPresent();
    }

    Component createComponent(String name, String id, String securityGroupName) {
        ComponentKey componentKey = new ComponentKey(id, 1L);
        return Component.builder()
                .componentKey(componentKey)
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName(securityGroupName)
                .type("http.request")
                .name(name)
                .description("component description")
                .version(componentVersion(componentKey))
                .parameters(
                        Stream.of(
                                componentParameter(componentKey, "connection", "connection"),
                                componentParameter(componentKey, "endpoint", "/users"),
                                componentParameter(componentKey, "type", "GET")
                        ).collect(Collectors.toList())
                )
                .attributes(new ArrayList<>())
                .build();
    }

    ComponentDto createComponentDto(Component component) {
        return new ComponentDto(
                component.getType(),
                component.getSecurityGroupName(),
                component.getMetadataKey().getId(),
                component.getDescription(),
                new ComponentVersionDto(component.getVersion().getMetadataKey().getComponentKey().getVersionNumber(), component.getVersion().getDescription()),
                component.getParameters().stream().map(componentParameter -> new ComponentParameterDto(componentParameter.getMetadataKey().getParameterName(), componentParameter.getValue())).collect(Collectors.toSet()),
                component.getAttributes().stream().map(componentAttribute -> new ComponentAttributeDto(
                        componentAttribute.getMetadataKey().getEnvironmentKey().getName(),
                        componentAttribute.getMetadataKey().getComponentAttributeName(), componentAttribute.getValue())).collect(Collectors.toSet())

        );
    }

    ComponentVersion componentVersion(ComponentKey componentKey) {
        return new ComponentVersion(
                new ComponentVersionKey(componentKey),
                "Component description"
        );
    }

    ComponentParameter componentParameter(ComponentKey componentKey, String name, String value) {
        return new ComponentParameter(
                new ComponentParameterKey(
                        componentKey,
                        name
                ),
                value
        );
    }

    SecurityGroup createSecurityGroup(String name, String componentName) {
        return SecurityGroup.builder()
                .name(name)
                .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                .securedObjects(Stream.of(
                        new ComponentKey(componentName, 1)
                ).collect(Collectors.toSet()))
                .teamKeys(new HashSet<>())
                .build();
    }

}
