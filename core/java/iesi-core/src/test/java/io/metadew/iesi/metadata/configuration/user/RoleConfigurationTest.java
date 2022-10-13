package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.user.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, RoleConfiguration.class, RoleListResultSetExtractor.class, UserConfiguration.class, TeamListResultSetExtractor.class,
        SecurityGroupConfiguration.class })
@ActiveProfiles("test")
class RoleConfigurationTest {

    TeamKey teamKey1;
    TeamKey teamKey2;
    RoleKey roleKey1;
    Role role1;
    RoleKey roleKey2;
    Role role2;
    Privilege privilege1;
    Privilege privilege2;
    Privilege privilege3;
    Privilege privilege4;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    RoleConfiguration roleConfiguration;

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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
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
