package io.metadew.iesi.metadata.configuration.securitygroup;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = { SecurityGroupConfiguration.class } )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Log4j2
class SecurityGroupConfigurationTest {

    private SecurityGroupKey securityGroupKey1;
    private SecurityGroup securityGroup1;
    private SecurityGroupKey securityGroupKey2;
    private SecurityGroup securityGroup2;


    private TeamKey teamKey1;
    private TeamKey teamKey2;

    @Autowired
    private SecurityGroupConfiguration securityGroupConfiguration;

    @BeforeEach
    void setup() {
        securityGroupKey1 = new SecurityGroupKey(UUID.randomUUID());
        securityGroupKey2 = new SecurityGroupKey(UUID.randomUUID());
        teamKey1 = new TeamKey(UUID.randomUUID());
        teamKey2 = new TeamKey(UUID.randomUUID());

        securityGroup1 = SecurityGroup.builder()
                .metadataKey(securityGroupKey1)
                .name("group1")
                .teamKeys(Stream.of(teamKey1).collect(Collectors.toSet()))
                .securedObjects(new HashSet<>())
                .build();
        securityGroup2 = SecurityGroup.builder()
                .metadataKey(securityGroupKey2)
                .name("group2")
                .teamKeys(Stream.of(teamKey1, teamKey2).collect(Collectors.toSet()))
                .securedObjects(new HashSet<>())
                .build();
    }

    @Test
    void userDoesNotExistsTest() {
        assertThat(securityGroupConfiguration.exists(securityGroupKey1)).isFalse();
    }

    @Test
    void userExistsTest() {
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.exists(securityGroupKey1)).isTrue();
    }

    @Test
    void userGetDoesNotExistsTest() {
        assertThat(securityGroupConfiguration.get(securityGroupKey2)).isEmpty();
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.get(securityGroupKey2)).isEmpty();
    }

    @Test
    void userGetByNameDoesNotExistsTest() {
        assertThat(securityGroupConfiguration.getByName(securityGroup1.getName())).isEmpty();
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.getByName(securityGroup2.getName())).isEmpty();
    }

    @Test
    void userGetExistsTest() {
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userGetByNameExistsTest() {
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.getByName(securityGroup1.getName()))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userInsertTest() {
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isFalse();
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isTrue();
        assertThat(securityGroupConfiguration.get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userInsertAlreadyExistingTest() {
        securityGroupConfiguration.insert(securityGroup1);
        assertThatThrownBy(() -> securityGroupConfiguration.insert(securityGroup1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void userInsertMultipleUsersTest() {
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isFalse();
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isTrue();
        assertThat(securityGroupConfiguration.get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
        securityGroupConfiguration.insert(securityGroup2);
        assertThat(securityGroupConfiguration.exists(securityGroupKey2))
                .isTrue();
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isTrue();
        assertThat(securityGroupConfiguration.get(securityGroupKey2))
                .isPresent()
                .hasValue(securityGroup2);
        assertThat(securityGroupConfiguration.get(securityGroupKey1))
                .isPresent()
                .hasValue(securityGroup1);
    }

    @Test
    void userDeleteDoesNotExistTest() {
        securityGroupConfiguration.delete(securityGroup1.getMetadataKey());
    }

    @Test
    void userDeleteTest() {
        securityGroupConfiguration.insert(securityGroup1);
        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isTrue();
        securityGroupConfiguration.delete(securityGroup1.getMetadataKey());

        assertThat(securityGroupConfiguration.exists(securityGroupKey1))
                .isFalse();
    }

    @Test
    void userUpdateTest() {
        securityGroupConfiguration.insert(securityGroup1);
        Optional<SecurityGroup> user = securityGroupConfiguration.get(securityGroupKey1);
        assertThat(user)
                .isPresent()
                .hasValue(securityGroup1);
        assertThat(user.get().getName())
                .isEqualTo("group1");

        securityGroup1.setName("group3");
        securityGroupConfiguration.update(securityGroup1);

        assertThat(securityGroupConfiguration.get(securityGroupKey1).get().getName())
                .isEqualTo("group3");
    }

    @Test
    void userUpdateMultipleTest() {
        securityGroupConfiguration.insert(securityGroup1);
        securityGroupConfiguration.insert(securityGroup2);
        Optional<SecurityGroup> fetchedUser1 = securityGroupConfiguration.get(securityGroupKey1);
        assertThat(fetchedUser1)
                .isPresent()
                .hasValue(securityGroup1);
        assertThat(fetchedUser1.get().getName())
                .isEqualTo("group1");
        Optional<SecurityGroup> fetchedUser2 = securityGroupConfiguration.get(securityGroupKey2);
        assertThat(fetchedUser2)
                .isPresent()
                .hasValue(securityGroup2);
        assertThat(fetchedUser2.get().getName())
                .isEqualTo("group2");

        securityGroup1.setName("group3");
        securityGroupConfiguration.update(securityGroup1);

        assertThat(securityGroupConfiguration.get(securityGroupKey1).get().getName())
                .isEqualTo("group3");
        assertThat(securityGroupConfiguration.get(securityGroupKey2).get().getName())
                .isEqualTo("group2");
    }
}
