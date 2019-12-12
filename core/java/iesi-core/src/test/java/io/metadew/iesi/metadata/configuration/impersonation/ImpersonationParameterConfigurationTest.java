package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImpersonationParameterConfigurationTest {

    ImpersonationParameter impersonationParameter;
    ImpersonationParameterKey impersonationParameterKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        impersonationParameterKey = new ImpersonationParameterKey("impersonationParameter", "parameter name");
        impersonationParameter = new ImpersonationParameter(impersonationParameterKey,
                "impersonation connection", "impersonation description");
        try{
            ImpersonationParameterConfiguration.getInstance().insert(impersonationParameter);
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
    public void impersonationParameterNotExistsTest() {
        ImpersonationParameterKey nonExistImpersonationParameterKey = new ImpersonationParameterKey("non_exist",
                "non exist par name");
        assertFalse(ImpersonationParameterConfiguration.getInstance().exists(nonExistImpersonationParameterKey));
    }

    @Test
    public void impersonationParameterExistsTest(){
        assertTrue(ImpersonationParameterConfiguration.getInstance().exists(impersonationParameter.getMetadataKey()));
    }

    @Test
    public void impersonationParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ImpersonationParameterConfiguration.getInstance().getAll().size();
        ImpersonationParameter newImpersonationParameter = createImpersonationParameter();
        ImpersonationParameterConfiguration.getInstance().insert(newImpersonationParameter);
        int nbAfter = ImpersonationParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void impersonationParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ImpersonationParameterConfiguration.getInstance().insert(impersonationParameter));
    }

    @Test
    public void impersonationParameterDeleteTest() throws MetadataDoesNotExistException {
        ImpersonationParameterConfiguration.getInstance().delete(impersonationParameter.getMetadataKey());
    }

    @Test
    public void impersonationParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ImpersonationParameter deleteScript = createImpersonationParameter();
        assertThrows(MetadataDoesNotExistException.class,() -> ImpersonationParameterConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void impersonationParameterGetTest() {
        Optional<ImpersonationParameter> newImpersonationParameter = ImpersonationParameterConfiguration.getInstance().get(impersonationParameter.getMetadataKey());
        assertTrue(newImpersonationParameter.isPresent());
        assertEquals(impersonationParameter.getMetadataKey().getImpersonationName(), newImpersonationParameter.get().getMetadataKey().getImpersonationName());
        assertEquals(impersonationParameter.getConnection(), newImpersonationParameter.get().getConnection());
        assertEquals(impersonationParameter.getImpersonatedConnection(), newImpersonationParameter.get().getImpersonatedConnection());
        assertEquals(impersonationParameter.getDescription(), newImpersonationParameter.get().getDescription());
    }

    @Test
    public void impersonationParameterGetNotExistsTest(){
        ImpersonationParameterKey impersonationParameterParameterKey = new ImpersonationParameterKey("not exist",
                "not exist par name");
        assertFalse(ImpersonationParameterConfiguration.getInstance().exists(impersonationParameterParameterKey));
        assertFalse(ImpersonationParameterConfiguration.getInstance().get(impersonationParameterParameterKey).isPresent());
    }

    @Test
    public void impersonationParameterUpdateTest() throws MetadataDoesNotExistException {
        ImpersonationParameter impersonationParameterUpdate = impersonationParameter;
        String newDescription = "new description";
        impersonationParameterUpdate.setDescription(newDescription);
        ImpersonationParameterConfiguration.getInstance().update(impersonationParameterUpdate);
        Optional<ImpersonationParameter> checkScript = ImpersonationParameterConfiguration.getInstance().get(impersonationParameterUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getDescription().equals(newDescription));
    }

    private ImpersonationParameter createImpersonationParameter(){
        ImpersonationParameterKey newImpersonationParameterKey = new ImpersonationParameterKey("new impersonationParameterkey",
                "new par name");
        return new ImpersonationParameter(newImpersonationParameterKey, "new imperson conn", 
                "" + "new desc");
    }
}
