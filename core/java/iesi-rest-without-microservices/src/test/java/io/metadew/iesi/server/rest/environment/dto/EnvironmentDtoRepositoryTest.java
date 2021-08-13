package io.metadew.iesi.server.rest.environment.dto;


import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class EnvironmentDtoRepositoryTest {

    @Autowired
    private EnvironmentDtoRepository environmentDtoRepository;

    @Autowired
    private EnvironmentConfiguration environmentConfiguration;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private EnvironmentDtoResourceAssembler environmentDtoResourceAssembler;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllNoResultsTest() {
        assertThat(environmentDtoRepository.getAll(Pageable.unpaged())).isEmpty();
    }

    @Test
    void getAllTest() {
        Environment environment1 = createEnvironment("iesi-test");
        Environment environment2 = createEnvironment("iesi-dev");
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        EnvironmentDto environmentDto1 = environmentDtoResourceAssembler.toModel(environment1);
        EnvironmentDto environmentDto2 = environmentDtoResourceAssembler.toModel(environment2);
        assertThat(environmentDtoRepository.getAll(Pageable.unpaged())).containsOnly(
                environmentDto1,
                environmentDto2
        );
    }

    @Test
    void getAllPaginatedAllInclusiveTest() {
        Environment environment1 = createEnvironment("iesi-dev");
        Environment environment2 = createEnvironment("iesi-sit");
        Environment environment3 = createEnvironment("iesi-test");
        Environment environment4 = createEnvironment("iesi-uat");
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        environmentConfiguration.insert(environment3);
        environmentConfiguration.insert(environment4);
        EnvironmentDto environmentDto1 = environmentDtoResourceAssembler.toModel(environment1);
        EnvironmentDto environmentDto2 = environmentDtoResourceAssembler.toModel(environment2);
        EnvironmentDto environmentDto3 = environmentDtoResourceAssembler.toModel(environment3);
        EnvironmentDto environmentDto4 = environmentDtoResourceAssembler.toModel(environment4);
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);
        Pageable page3 = PageRequest.of(0, 4);
        assertThat(environmentDtoRepository.getAll(page1)).containsOnly(
                environmentDto1,
                environmentDto2
        );
        assertThat(environmentDtoRepository.getAll(page2)).containsOnly(
                environmentDto3,
                environmentDto4
        );
        assertThat(environmentDtoRepository.getAll(page3)).containsOnly(
                environmentDto1,
                environmentDto2,
                environmentDto3,
                environmentDto4
        );
    }

    @Test
    void getAllPaginatedSortedAscTest() {
        Environment environment1 = createEnvironment("iesi-dev");
        Environment environment2 = createEnvironment("iesi-sit");
        Environment environment3 = createEnvironment("iesi-test");
        Environment environment4 = createEnvironment("iesi-uat");
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        environmentConfiguration.insert(environment3);
        environmentConfiguration.insert(environment4);
        EnvironmentDto environmentDto1 = environmentDtoResourceAssembler.toModel(environment1);
        EnvironmentDto environmentDto2 = environmentDtoResourceAssembler.toModel(environment2);
        EnvironmentDto environmentDto3 = environmentDtoResourceAssembler.toModel(environment3);
        EnvironmentDto environmentDto4 = environmentDtoResourceAssembler.toModel(environment4);

        PageRequest pageable1 = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        PageRequest pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "name"));

        Page<EnvironmentDto> page1 = environmentDtoRepository.getAll(pageable1);
        Page<EnvironmentDto> page2 = environmentDtoRepository.getAll(pageable2);

        assertThat(page1).containsExactly(
                environmentDto1,
                environmentDto2
        );
        assertThat(page2).containsExactly(
                environmentDto3,
                environmentDto4
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(4);
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    @Test
    void getAllPaginatedSortedDscTest() {
        Environment environment1 = createEnvironment("iesi-dev");
        Environment environment2 = createEnvironment("iesi-sit");
        Environment environment3 = createEnvironment("iesi-test");
        Environment environment4 = createEnvironment("iesi-uat");
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        environmentConfiguration.insert(environment3);
        environmentConfiguration.insert(environment4);
        EnvironmentDto environmentDto1 = environmentDtoResourceAssembler.toModel(environment1);
        EnvironmentDto environmentDto2 = environmentDtoResourceAssembler.toModel(environment2);
        EnvironmentDto environmentDto3 = environmentDtoResourceAssembler.toModel(environment3);
        EnvironmentDto environmentDto4 = environmentDtoResourceAssembler.toModel(environment4);

        PageRequest pageable1 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name"));
        PageRequest pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "name"));

        Page<EnvironmentDto> page1 = environmentDtoRepository.getAll(pageable1);
        Page<EnvironmentDto> page2 = environmentDtoRepository.getAll(pageable2);

        assertThat(page1).containsExactly(
                environmentDto4,
                environmentDto3
        );
        assertThat(page2).containsExactly(
                environmentDto2,
                environmentDto1
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(4);
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    Environment createEnvironment(String name) {
        return Environment.builder()
                .name(name)
                .description("iesi-environment")
                .parameters(Stream.of(
                        environmentParameter(name, "param", "value")
                ).collect(Collectors.toList()))
                .build();
    }

    EnvironmentParameter environmentParameter(String environmentName, String environmentParameterName, String value) {
        return new EnvironmentParameter(environmentName, environmentParameterName, value);
    }

    @Test
    void getAllListTest() {
        Environment environment1 = createEnvironment("iesi-test");
        Environment environment2 = createEnvironment("iesi-dev");
        environmentConfiguration.insert(environment1);
        environmentConfiguration.insert(environment2);
        EnvironmentDto environmentDto1 = environmentDtoResourceAssembler.toModel(environment1);
        EnvironmentDto environmentDto2 = environmentDtoResourceAssembler.toModel(environment2);
        assertThat(environmentDtoRepository.getAll()).containsOnly(
                environmentDto1,
                environmentDto2
        );
    }


}
