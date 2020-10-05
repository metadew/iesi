//package io.metadew.iesi.metadata.configuration.user;
//
//import io.metadew.iesi.common.configuration.Configuration;
//import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
//import io.metadew.iesi.metadata.definition.user.Privilege;
//import io.metadew.iesi.metadata.definition.user.PrivilegeKey;
//import org.junit.jupiter.api.*;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class PrivilegeConfigurationTest {
//
//    private UUID uuid1;
//    private Privilege privilege1;
//    private UUID uuid2;
//    private Privilege privilege2;
//
//    @BeforeAll
//    static void prepare() {
//        Configuration.getInstance();
//        MetadataRepositoryConfiguration.getInstance()
//                .getControlMetadataRepository()
//                .createAllTables();
//    }
//
//    @BeforeEach
//    void setup() {
//        uuid1 = UUID.randomUUID();
//        privilege1 = Privilege.builder()
//                .privilegeKey(PrivilegeKey.builder()
//                        .uuid(uuid1)
//                        .build())
//                .authority("authority1")
//                .build();
//        uuid2 = UUID.randomUUID();
//        privilege2 = Privilege.builder()
//                .privilegeKey(PrivilegeKey.builder()
//                        .uuid(uuid2)
//                        .build())
//                .authority("authority2")
//                .build();
//    }
//
//    @AfterEach
//    void clearDatabase() {
//        MetadataRepositoryConfiguration.getInstance()
//                .getControlMetadataRepository().cleanAllTables();
//    }
//
//    @AfterAll
//    static void teardown() {
//        MetadataRepositoryConfiguration.getInstance()
//                .getControlMetadataRepository().dropAllTables();
//    }
//
//    @Test
//    void userDoesNotExistsTest() {
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1))).isFalse();
//    }
//
//    @Test
//    void userExistsTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1))).isTrue();
//    }
//
//    @Test
//    void userExistsByNameTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(privilege1.getPrivilege())).isTrue();
//    }
//
//    @Test
//    void userGetDoesNotExistsTest() {
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1))).isEmpty();
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid2))).isEmpty();
//    }
//
//    @Test
//    void userGetByNameDoesNotExistsTest() {
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1))).isEmpty();
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().get(privilege2.getPrivilege())).isEmpty();
//    }
//
//    @Test
//    void userGetExistsTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)))
//                .isPresent()
//                .hasValue(privilege1);
//    }
//
//    @Test
//    void userGetByNameExistsTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().get(privilege1.getPrivilege()))
//                .isPresent()
//                .hasValue(privilege1);
//    }
//
//    @Test
//    void userInsertTest() {
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isFalse();
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isTrue();
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)))
//                .isPresent()
//                .hasValue(privilege1);
//    }
//
//    @Test
//    void userInsertAlreadyExistingTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThatThrownBy(() -> AuthorityConfiguration.getInstance().insert(privilege1))
//                .isInstanceOf(RuntimeException.class);
//    }
//
//    @Test
//    void userInsertMultipleUsersTest() {
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isFalse();
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isTrue();
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)))
//                .isPresent()
//                .hasValue(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid2)))
//                .isTrue();
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isTrue();
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid2)))
//                .isPresent()
//                .hasValue(privilege2);
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)))
//                .isPresent()
//                .hasValue(privilege1);
//    }
//
//    @Test
//    void userDeleteDoesNotExistTest() {
//        AuthorityConfiguration.getInstance().delete(privilege1.getMetadataKey());
//    }
//
//    @Test
//    void userDeleteByNameDoesNotExistTest() {
//        AuthorityConfiguration.getInstance().delete(privilege1.getPrivilege());
//    }
//
//    @Test
//    void userDeleteTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isTrue();
//        AuthorityConfiguration.getInstance().delete(privilege1.getMetadataKey());
//
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isFalse();
//    }
//
//    @Test
//    void userDeleteByNameTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isTrue();
//        AuthorityConfiguration.getInstance().delete(privilege1.getPrivilege());
//
//        assertThat(AuthorityConfiguration.getInstance().exists(new PrivilegeKey(uuid1)))
//                .isFalse();
//    }
//
//    @Test
//    void userUpdateTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        Optional<Privilege> user = AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1));
//        assertThat(user)
//                .isPresent()
//                .hasValue(privilege1);
//        assertThat(user.get().getPrivilege())
//                .isEqualTo("authority1");
//
//        privilege1.setPrivilege("authorityA");
//        AuthorityConfiguration.getInstance().update(privilege1);
//
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)).get().getPrivilege())
//                .isEqualTo("authorityA");
//    }
//
//
//    @Test
//    void userUpdateMultipleTest() {
//        AuthorityConfiguration.getInstance().insert(privilege1);
//        AuthorityConfiguration.getInstance().insert(privilege2);
//        Optional<Privilege> fetchedUser1 = AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1));
//        assertThat(fetchedUser1)
//                .isPresent()
//                .hasValue(privilege1);
//        assertThat(fetchedUser1.get().getPrivilege())
//                .isEqualTo("authority1");
//        Optional<Privilege> fetchedUser2 = AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid2));
//        assertThat(fetchedUser2)
//                .isPresent()
//                .hasValue(privilege2);
//        assertThat(fetchedUser2.get().getPrivilege())
//                .isEqualTo("authority2");
//
//        privilege1.setPrivilege("authorityA");
//        AuthorityConfiguration.getInstance().update(privilege1);
//
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid1)).get().getPrivilege())
//                .isEqualTo("authorityA");
//        assertThat(AuthorityConfiguration.getInstance().get(new PrivilegeKey(uuid2)).get().getPrivilege())
//                .isEqualTo("authority2");
//    }
//
//}
