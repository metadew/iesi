package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImpersonationConfigurationTest {


    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private Impersonation impersonation;
    private Impersonation impersonation2;

    @BeforeEach
    void setup() {
        connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        connectivityMetadataRepository.createAllTables();
        List<ImpersonationParameter> impersonationParameterList = new ArrayList<>();
        ImpersonationParameter impersonationParameter = ImpersonationParameter.builder().impersonationParameterKey(ImpersonationParameterKey.builder()
                .impersonationKey(ImpersonationKey.builder().name("name").build()).parameterName("param").build()).impersonatedConnection("conn").description("desc").build();
        impersonationParameterList.add(impersonationParameter);
        impersonation = Impersonation.builder()
                .impersonationKey(ImpersonationKey.builder().name("name").build()).description("desc").parameters(impersonationParameterList).build();

        List<ImpersonationParameter> impersonationParameterList2 = new ArrayList<>();
        ImpersonationParameter impersonationParameter2 = ImpersonationParameter.builder().impersonationParameterKey(ImpersonationParameterKey.builder()
                .impersonationKey(ImpersonationKey.builder().name("name2").build()).parameterName("param2").build()).impersonatedConnection("conn2").description("desc2").build();
        impersonationParameterList2.add(impersonationParameter2);
        impersonation2 = Impersonation.builder()
                .impersonationKey(ImpersonationKey.builder().name("name2").build()).description("des2c").parameters(impersonationParameterList2).build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the connectivityMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    void impersonationNotExistsKeyTest() {
        assertTrue(ImpersonationConfiguration.getInstance().getAll().isEmpty());
    }
    @Test
    void getImpersonationTest() {
        ImpersonationConfiguration.getInstance().insert(impersonation);
        assertEquals(1, ImpersonationConfiguration.getInstance().getAll().size());

        Optional<Impersonation> impersonation1 = ImpersonationConfiguration.getInstance().get(impersonation.getMetadataKey());

        assertEquals(impersonation, impersonation1.get());
    }

    @Test
    void getAllImpersonationTest() {
        ImpersonationConfiguration.getInstance().insert(impersonation);
        ImpersonationConfiguration.getInstance().insert(impersonation2);
        assertEquals(2, ImpersonationConfiguration.getInstance().getAll().size());

        Optional<Impersonation> impersonation1 = ImpersonationConfiguration.getInstance().get(impersonation.getMetadataKey());

        assertEquals(impersonation, impersonation1.get());
    }

    @Test
    void deleteImpersonationTest() {
        ImpersonationConfiguration.getInstance().insert(impersonation);
        ImpersonationConfiguration.getInstance().insert(impersonation2);
        assertEquals(2, ImpersonationConfiguration.getInstance().getAll().size());

        ImpersonationConfiguration.getInstance().delete(impersonation2.getMetadataKey());

        assertEquals(1, ImpersonationConfiguration.getInstance().getAll().size());
    }
}
