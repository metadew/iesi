package io.metadew.iesi.server.rest.connection.dto;


import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.connection.ConnectionFilter;
import io.metadew.iesi.server.rest.connection.ConnectionFilterOption;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class ConnectionDtoRepositoryTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    private ConnectionConfiguration connectionConfiguration;
    @Autowired
    private EnvironmentConfiguration environmentConfiguration;
    @Autowired
    private ConnectionDtoResourceAssembler connectionDtoResourceAssembler;
    @Autowired
    private ConnectionDtoRepository connectionDtoRepository;
    @Autowired
    private SecurityGroupConfiguration securityGroupConfiguration;

    @AfterEach
    void cleanup() {
        metadataRepositoryConfiguration.clearAllTables();
    }

    @Test
    void getAllNoResultsTest() {
        assertThat(connectionDtoRepository.getAll(null, Pageable.unpaged(), new ArrayList<>())).isEmpty();
    }

    @Test
    void getAllTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);
        assertThat(connectionDtoRepository.getAll(null, Pageable.unpaged(), new ArrayList<>())).containsOnly(
                connection1Dto,
                connection2Dto
        );
    }

    @Test
    void getAllPaginatedAllInclusiveTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection3 = createConnection("connection3", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        insert(connection3);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);

        Pageable page1 = PageRequest.of(0, 1);
        Pageable page2 = PageRequest.of(1, 1);
        assertThat(connectionDtoRepository.getAll(null, page1, new ArrayList<>())).containsOnly(
                connection1Dto
        );
        assertThat(connectionDtoRepository.getAll(null, page2, new ArrayList<>())).containsOnly(
                connection2Dto
        );
    }

    @Test
    void getAllPaginatedSortedAscTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection3 = createConnection("connection3", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection4 = createConnection("connection4", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection5 = createConnection("connection5", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        insert(connection3);
        insert(connection4);
        insert(connection5);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);
        ConnectionDto connection3Dto = createConnectionDto(connection3);
        ConnectionDto connection4Dto = createConnectionDto(connection4);
        ConnectionDto connection5Dto = createConnectionDto(connection5);

        PageRequest pageable1 = PageRequest.of(0, 2);
        PageRequest pageable2 = PageRequest.of(1, 2);
        PageRequest pageable3 = PageRequest.of(2, 2);

        Page<ConnectionDto> page1 = connectionDtoRepository.getAll(null, pageable1, new ArrayList<>());
        Page<ConnectionDto> page2 = connectionDtoRepository.getAll(null, pageable2, new ArrayList<>());
        Page<ConnectionDto> page3 = connectionDtoRepository.getAll(null, pageable3, new ArrayList<>());

        assertThat(page1).containsExactly(
                connection1Dto,
                connection2Dto
        );

        assertThat(page2).containsExactly(
                connection3Dto,
                connection4Dto
        );
        assertThat(page3).containsExactly(
                connection5Dto
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
    }

    @Test
    void getAllPaginatedSortedDescTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection3 = createConnection("connection3", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection4 = createConnection("connection4", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection5 = createConnection("connection5", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        insert(connection3);
        insert(connection4);
        insert(connection5);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);
        ConnectionDto connection3Dto = createConnectionDto(connection3);
        ConnectionDto connection4Dto = createConnectionDto(connection4);
        ConnectionDto connection5Dto = createConnectionDto(connection5);

        PageRequest pageable1 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name"));
        PageRequest pageable2 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "name"));
        PageRequest pageable3 = PageRequest.of(2, 2, Sort.by(Sort.Direction.DESC, "name"));

        Page<ConnectionDto> page1 = connectionDtoRepository.getAll(null, pageable1, new ArrayList<>());
        Page<ConnectionDto> page2 = connectionDtoRepository.getAll(null, pageable2, new ArrayList<>());
        Page<ConnectionDto> page3 = connectionDtoRepository.getAll(null, pageable3, new ArrayList<>());
        assertThat(page1).containsExactly(
                connection5Dto,
                connection4Dto
        );
        assertThat(page2).containsExactly(
                connection3Dto,
                connection2Dto
        );
        assertThat(page3).containsExactly(
                connection1Dto
        );
        assertThat(page1.getNumberOfElements()).isEqualTo(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
    }

    @Test
    void getAllFilteredOnNameTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);
        assertThat(connectionDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ConnectionFilter(ConnectionFilterOption.NAME, "ion", false)
                ).collect(Collectors.toList())
        )).containsOnly(connection2Dto, connection1Dto);
        assertThat(connectionDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ConnectionFilter(ConnectionFilterOption.NAME, "ion1", false)
                ).collect(Collectors.toList())
        )).containsExactly(connection1Dto);
        assertThat(connectionDtoRepository.getAll(null, Pageable.unpaged(),
                Stream.of(
                        new ConnectionFilter(ConnectionFilterOption.NAME, "ion2", false)
                ).collect(Collectors.toList())
        )).containsExactly(connection2Dto);
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "CONNECTIONS_READ@GROUPA"
    })
    void getAllSecurityGroupA() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        ConnectionDto connectionDtoA = createConnectionDto(connectionA);

        Page<ConnectionDto> connectionDtoPage = connectionDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );
        assertThat(connectionDtoPage).hasSize(1).containsOnly(connectionDtoA);
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "CONNECTIONS_READ@GROUPB"
    })
    void getAllSecurityGroupB() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        ConnectionDto connectionDtoB = createConnectionDto(connectionB);

        Page<ConnectionDto> connectionDtoPage = connectionDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );
        assertThat(connectionDtoPage).hasSize(1).containsOnly(connectionDtoB);
    }

    @Test
    @WithIesiUser(username = "userBA", authorities = {
            "CONNECTIONS_READ@GROUPB",
            "CONNECTIONS_READ@GROUPA"
    })
    void getAllSecurityGroupBA() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String connectionBameC = "connectionC";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";
        String securityGroupCName = "GROUPC";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");
        SecurityGroup securityGroupC = createSecurityGroup(securityGroupCName, connectionBameC, "ennv0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        securityGroupConfiguration.insert(securityGroupC);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        List<Connection> connectionC = createConnection(connectionBameC, Stream.of("env0").collect(Collectors.toList()), securityGroupCName);
        insert(connectionA);
        insert(connectionB);
        insert(connectionC);

        ConnectionDto connectionDtoA = createConnectionDto(connectionA);
        ConnectionDto connectionDtoB = createConnectionDto(connectionB);

        Page<ConnectionDto> connectionDtoPage = connectionDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );
        assertThat(connectionDtoPage).hasSize(2).containsOnly(connectionDtoA, connectionDtoB);
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "CONNECTIONS_READ@PUBLIC",
    })
    void getAllNoMatchedSecurityGroups() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        Page<ConnectionDto> connectionDtoPage = connectionDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );
        assertThat(connectionDtoPage).isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getAllNoSecurityGroups() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        Page<ConnectionDto> connectionDtoPage = connectionDtoRepository.getAll(
                SecurityContextHolder.getContext().getAuthentication(),
                Pageable.unpaged(),
                new ArrayList<>()
        );
        assertThat(connectionDtoPage).isEmpty();
    }

    @Test
    void getByNameTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);
        ConnectionDto connection1Dto = createConnectionDto(connection1);
        ConnectionDto connection2Dto = createConnectionDto(connection2);

        assertThat(connectionDtoRepository.getByName(null, "connection1"))
                .isEqualTo(Optional.of(connection1Dto));
        assertThat(connectionDtoRepository.getByName(null, "connection2"))
                .isEqualTo(Optional.of(connection2Dto));
    }

    @Test
    void getByNameNoResultsTest() {
        List<Connection> connection1 = createConnection("connection1", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        List<Connection> connection2 = createConnection("connection2", Stream.of("env1", "env2").collect(Collectors.toList()), "PUBLIC");
        environmentConfiguration.insert(createEnvironment("env1"));
        environmentConfiguration.insert(createEnvironment("env2"));
        insert(connection1);
        insert(connection2);

        assertThat(connectionDtoRepository.getByName(null, "connection3"))
                .isEmpty();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "CONNECTIONS_READ@GROUPA"
    })
    void getByNameSecurityGroupA() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        ConnectionDto connectionDtoA = createConnectionDto(connectionA);

        Optional<ConnectionDto> fetchedConnectionDtoA = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionA"
        );
        Optional<ConnectionDto> fetchedConnectionDtoB = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionB"
        );
        assertThat(fetchedConnectionDtoA).isPresent();
        assertThat(fetchedConnectionDtoA.get()).isEqualTo(connectionDtoA);
        assertThat(fetchedConnectionDtoB).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userB", authorities = {
            "CONNECTIONS_READ@GROUPB"
    })
    void getByNameSecurityGroupB() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        ConnectionDto connectionDtoB = createConnectionDto(connectionB);
        Optional<ConnectionDto> fetchedConnectionDtoA = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionA"
        );
        Optional<ConnectionDto> fetchedConnectionDtoB = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionB"
        );

        assertThat(fetchedConnectionDtoB).isPresent();
        assertThat(fetchedConnectionDtoB.get()).isEqualTo(connectionDtoB);
        assertThat(fetchedConnectionDtoA).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userBA", authorities = {
            "CONNECTIONS_READ@GROUPB",
            "CONNECTIONS_READ@GROUPA"
    })
    void getByNameSecurityGroupBA() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String connectionBameC = "connectionC";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";
        String securityGroupCName = "GROUPC";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");
        SecurityGroup securityGroupC = createSecurityGroup(securityGroupCName, connectionBameC, "ennv0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        securityGroupConfiguration.insert(securityGroupC);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        List<Connection> connectionC = createConnection(connectionBameC, Stream.of("env0").collect(Collectors.toList()), securityGroupCName);
        insert(connectionA);
        insert(connectionB);
        insert(connectionC);

        ConnectionDto connectionDtoA = createConnectionDto(connectionA);
        ConnectionDto connectionDtoB = createConnectionDto(connectionB);

        Optional<ConnectionDto> fetchedConnectionDtoA = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionA"
        );
        Optional<ConnectionDto> fetchedConnectionDtoB = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionB"
        );
        Optional<ConnectionDto> fetchedConnectionDtoC = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionC"
        );

        assertThat(fetchedConnectionDtoA).isPresent();
        assertThat(fetchedConnectionDtoB).isPresent();
        assertThat(fetchedConnectionDtoA.get()).isEqualTo(connectionDtoA);
        assertThat(fetchedConnectionDtoB.get()).isEqualTo(connectionDtoB);
        assertThat(fetchedConnectionDtoC).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {
            "CONNECTIONS_READ@PUBLIC",
    })
    void getByNameNoMatchedSecurityGroups() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        Optional<ConnectionDto> fetchedConnectionDtoA = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionA"
        );
        Optional<ConnectionDto> fetchedConnectionDtoB = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionB"
        );

        assertThat(fetchedConnectionDtoA).isNotPresent();
        assertThat(fetchedConnectionDtoB).isNotPresent();
    }

    @Test
    @WithIesiUser(username = "userA", authorities = {})
    void getByNameNoSecurityGroups() {
        String connectionNameA = "connectionA";
        String connectionNameB = "connectionB";
        String securityGroupAName = "GROUPA";
        String securityGroupBName = "GROUPB";

        SecurityGroup securityGroupA = createSecurityGroup(securityGroupAName, connectionNameA, "env0");
        SecurityGroup securityGroupB = createSecurityGroup(securityGroupBName, connectionNameB, "env0");


        securityGroupConfiguration.insert(securityGroupA);
        securityGroupConfiguration.insert(securityGroupB);
        environmentConfiguration.insert(createEnvironment("env0"));

        List<Connection> connectionA = createConnection(connectionNameA, Stream.of("env0").collect(Collectors.toList()), securityGroupAName);
        List<Connection> connectionB = createConnection(connectionNameB, Stream.of("env0").collect(Collectors.toList()), securityGroupBName);
        insert(connectionA);
        insert(connectionB);

        Optional<ConnectionDto> fetchedConnectionDtoA = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionA"
        );
        Optional<ConnectionDto> fetchedConnectionDtoB = connectionDtoRepository.getByName(
                SecurityContextHolder.getContext().getAuthentication(),
                "connectionB"
        );

        assertThat(fetchedConnectionDtoA).isNotPresent();
        assertThat(fetchedConnectionDtoB).isNotPresent();
    }

    List<Connection> createConnection(String name, List<String> environments, String securityGroupName) {
        return environments.stream().map(environment -> {
            ConnectionKey connectionKey = new ConnectionKey(name, environment);
            return Connection.builder()
                    .connectionKey(connectionKey)
                    .type("http")
                    .description("connection description")
                    .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                    .securityGroupName(securityGroupName)
                    .parameters(
                            Stream.of(
                                            new ConnectionParameter(
                                                    new ConnectionParameterKey(connectionKey, "host"),
                                                    "test.test.com"
                                            ),
                                            new ConnectionParameter(
                                                    new ConnectionParameterKey(connectionKey, "port"),
                                                    "80"
                                            ),
                                            new ConnectionParameter(
                                                    new ConnectionParameterKey(connectionKey, "baseUrl"),
                                                    "/api"
                                            ),
                                            new ConnectionParameter(
                                                    new ConnectionParameterKey(connectionKey, "tls"),
                                                    "N"
                                            )
                                    )
                                    .collect(Collectors.toList())
                    )
                    .build();
        }).collect(Collectors.toList());
    }

    ConnectionDto createConnectionDto(List<Connection> connection) {
        return ConnectionDto.builder()
                .name(connection.get(0).getMetadataKey().getName())
                .securityGroupName(connection.get(0).getSecurityGroupName())
                .type(connection.get(0).getType())
                .description(connection.get(0).getDescription())
                .environments(
                        connection.stream().map(item -> new ConnectionEnvironmentDto(
                                item.getMetadataKey().getEnvironmentKey().getName(),
                                item.getParameters().stream().map(connectionParameter -> new ConnectionParameterDto(
                                        connectionParameter.getMetadataKey().getParameterName(),
                                        connectionParameter.getValue()
                                )).collect(Collectors.toSet())
                        )).collect(Collectors.toSet())
                )
                .build();
    }

    Environment createEnvironment(String name) {
        return new Environment(
                name,
                "",
                new ArrayList<>()
        );
    }

    SecurityGroup createSecurityGroup(String name, String connectionName, String env) {
        return SecurityGroup.builder()
                .name(name)
                .metadataKey(new SecurityGroupKey(UUID.randomUUID()))
                .securedObjects(Stream.of(
                        new ConnectionKey(connectionName, env)
                ).collect(Collectors.toSet()))
                .teamKeys(new HashSet<>())
                .build();
    }

    void insert(List<Connection> connections) {
        for (Connection connection : connections) {
            connectionConfiguration.insert(connection);
        }
    }
}
