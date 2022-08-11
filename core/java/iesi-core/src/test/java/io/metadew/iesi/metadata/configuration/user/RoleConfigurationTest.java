package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = { Configuration.class, SpringContext.class, MetadataRepositoryConfiguration.class, RoleConfiguration.class })
class RoleConfigurationTest {

    private TeamKey teamKey1;
    private TeamKey teamKey2;

    private RoleKey roleKey1;
    private Role role1;
    private RoleKey roleKey2;
    private Role role2;

    private Privilege privilege1;
    private Privilege privilege2;
    private Privilege privilege3;
    private Privilege privilege4;

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private RoleConfiguration roleConfiguration;

    @BeforeAll
    static void prepare() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        metadataRepositoryConfiguration
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void setup() {
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());
        roleKey1 = new RoleKey(UUID.randomUUID());
        roleKey2 = new RoleKey(UUID.randomUUID());

        privilege1 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority1")
                .roleKey(roleKey1)
                .build();
        privilege2 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority2")
                .roleKey(roleKey1)
                .build();
        privilege3 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .privilege("authority3")
                .roleKey(roleKey2)
                .build();
        privilege4 = Privilege.builder()
                .privilegeKey(PrivilegeKey.builder()
                        .uuid(UUID.randomUUID())
                        .build())
                .roleKey(roleKey2)
                .privilege("authority4")
                .build();
        role1 = Role.builder()
                .metadataKey(roleKey1)
                .teamKey(teamKey1)
                .name("role1")
                .users(new HashSet<>())
                .privileges(Stream.of(privilege1, privilege2).collect(Collectors.toSet()))
                .build();
        role2 = Role.builder()
                .metadataKey(roleKey2)
                .teamKey(teamKey2)
                .name("role2")
                .users(new HashSet<>())
                .privileges(Stream.of(privilege3, privilege4).collect(Collectors.toSet()))
                .build();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(roleConfiguration.exists(roleKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.exists(roleKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(roleConfiguration.get(roleKey1)).isEmpty();
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.get(roleKey2)).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userInsertTest() {
        assertThat(roleConfiguration.exists(roleKey1))
                .isFalse();
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.exists(roleKey1))
                .isTrue();
        assertThat(roleConfiguration.get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        roleConfiguration.insert(role1);
        assertThatThrownBy(() -> roleConfiguration.insert(role1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(roleConfiguration.exists(roleKey1))
                .isFalse();
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.exists(roleKey1))
                .isTrue();
        assertThat(roleConfiguration.get(roleKey1))
                .isPresent()
                .hasValue(role1);
        roleConfiguration.insert(role2);
        assertThat(roleConfiguration.exists(roleKey2))
                .isTrue();
        assertThat(roleConfiguration.exists(roleKey1))
                .isTrue();
        assertThat(roleConfiguration.get(roleKey2))
                .isPresent()
                .hasValue(role2);
        assertThat(roleConfiguration.get(roleKey1))
                .isPresent()
                .hasValue(role1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        roleConfiguration.delete(role1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
        roleConfiguration.insert(role1);
        assertThat(roleConfiguration.exists(roleKey1))
                .isTrue();
        roleConfiguration.delete(role1.getMetadataKey());

        assertThat(roleConfiguration.exists(roleKey1))
                .isFalse();
    }


    @Test
    void userUpdateTest() {
        roleConfiguration.insert(role1);
        Optional<Role> user = roleConfiguration.get(roleKey1);
        assertThat(user)
                .isPresent()
                .hasValue(role1);
        assertThat(user.get().getName())
                .isEqualTo("role1");

        role1.setName("role3");
        roleConfiguration.update(role1);

        assertThat(roleConfiguration.get(roleKey1).get().getName())
                .isEqualTo("role3");
    }

    @Test
    void userUpdateMultipleTest() {
        roleConfiguration.insert(role1);
        roleConfiguration.insert(role2);
        Optional<Role> fetchedrole1 = roleConfiguration.get(roleKey1);
        assertThat(fetchedrole1)
                .isPresent()
                .hasValue(role1);
        assertThat(fetchedrole1.get().getName())
                .isEqualTo("role1");
        Optional<Role> fetchedrole2 = roleConfiguration.get(roleKey2);
        assertThat(fetchedrole2)
                .isPresent()
                .hasValue(role2);
        assertThat(fetchedrole2.get().getName())
                .isEqualTo("role2");

        role1.setName("role3");
        roleConfiguration.update(role1);

        assertThat(roleConfiguration.get(roleKey1).get().getName())
                .isEqualTo("role3");
        assertThat(roleConfiguration.get(roleKey2).get().getName())
                .isEqualTo("role2");
    }
}
