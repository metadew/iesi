package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImpersonationConfigurationTest {

    ImpersonationParameter impersonationParameter;
    Impersonation impersonation;
    ImpersonationKey impersonationKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        ImpersonationParameterKey impersonationParameterKey = new ImpersonationParameterKey("1", "firstParameter");
        impersonationParameter = new ImpersonationParameter(impersonationParameterKey,
                "impersonated connection", "impersonation description");
        List<ImpersonationParameter> impersonationParameters = new ArrayList<>();
        impersonationParameters.add(impersonationParameter);
        impersonationKey = new ImpersonationKey("impersonation");
        impersonation = new Impersonation(impersonationKey, "impersonation for testing", impersonationParameters);
        try{
            ImpersonationConfiguration.getInstance().insert(impersonation);
        }catch(MetadataAlreadyExistsException ignored){
            // if script already is in database do nothing
            System.out.println("something went wrong");
        }
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    public void impersonationNotExistsTest() {
        ImpersonationKey nonExistImpersonationKey = new ImpersonationKey("non_exist");
        assertFalse(ImpersonationConfiguration.getInstance().exists(nonExistImpersonationKey));
    }

    @Test
    public void impersonationParameterExistsTest(){
        assertTrue(ImpersonationParameterConfiguration.getInstance().exists(impersonationParameter.getMetadataKey()));
    }

    @Test
    public void impersonationExistsTest(){
        assertTrue(ImpersonationConfiguration.getInstance().exists(impersonation.getMetadataKey()));
    }

    @Test
    public void impersonationInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ImpersonationConfiguration.getInstance().getAll().size();
        Impersonation newImpersonation = createImpersonation();
        ImpersonationConfiguration.getInstance().insert(newImpersonation);
        int nbAfter = ImpersonationConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void impersonationInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ImpersonationConfiguration.getInstance().insert(impersonation));
    }

    @Test
    public void impersonationDeleteTest() throws MetadataDoesNotExistException {
        ImpersonationConfiguration.getInstance().delete(impersonation.getMetadataKey());
    }

    @Test
    public void impersonationDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Impersonation deleteScript = createImpersonation();
        assertThrows(MetadataDoesNotExistException.class,() -> ImpersonationConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void impersonationGetTest() {
        Optional<Impersonation> newImpersonation = ImpersonationConfiguration.getInstance().get(impersonation.getMetadataKey());
        assertTrue(newImpersonation.isPresent());
        assertEquals(impersonation.getMetadataKey().getName(), newImpersonation.get().getMetadataKey().getName());
        assertEquals(impersonation.getDescription(), newImpersonation.get().getDescription());
    }

    @Test
    public void impersonationGetNotExistsTest(){
        ImpersonationKey impersonationParameterKey = new ImpersonationKey("not exist");
        assertFalse(ImpersonationConfiguration.getInstance().exists(impersonationParameterKey));
        assertFalse(ImpersonationConfiguration.getInstance().get(impersonationParameterKey).isPresent());
    }

    @Test
    public void impersonationUpdateTest() throws MetadataDoesNotExistException {
        Impersonation impersonationUpdate = impersonation;
        String newDescription = "new description";
        impersonationUpdate.setDescription(newDescription);
        ImpersonationConfiguration.getInstance().update(impersonationUpdate);
        Optional<Impersonation> checkScript = ImpersonationConfiguration.getInstance().get(impersonationUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getDescription().equals(newDescription));
    }

    private Impersonation createImpersonation(){
        ImpersonationKey newImpersonationKey = new ImpersonationKey("new impersonationkey");
        return new Impersonation(newImpersonationKey, "created impersonation", new ArrayList<>());
    }
}
