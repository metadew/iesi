//package io.metadew.iesi.metadata.operation;
//
//import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
//import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.File;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class TypeConfigurationOperationTest {
//
//    @Mock
//    private FrameworkExecution frameworkExecution;
//
//    @Mock
//    private FrameworkConfiguration frameworkConfiguration;
//
//    @Mock
//    private FrameworkFolderConfiguration frameworkFolderConfiguration;
//
//    @Test
//    void getMappingConfigurationFileTest() {
//        String mappingPath = "resources" + File.separator + "test" + File.separator + "io.metadew.iesi.metadata.operation";
//        String expectedPath = "resources" + File.separator + "test" + File.separator + "io.metadew.iesi.metadata.operation" + File.separator + "mapping_configuration.json";
//        when(frameworkExecution.getFrameworkConfiguration()).thenReturn(frameworkConfiguration);
//        when(frameworkConfiguration.getFolderConfiguration()).thenReturn(frameworkFolderConfiguration);
//        when(frameworkFolderConfiguration.getFolderAbsolutePath("data.mapping"))
//                .thenReturn(mappingPath);
//        String filePath = TypeConfigurationOperation.getMappingConfigurationFile(frameworkExecution, null, "mapping_configuration");
//        assertEquals(expectedPath, filePath);
//    }
//
//    @Test
//    void getMappingConfigurationFileMappingNotFoundTest() {
//        String mappingPath = "resources" + File.separator + "test" + File.separator + "io.metadew.iesi.metadata.operation";
//        when(frameworkExecution.getFrameworkConfiguration()).thenReturn(frameworkConfiguration);
//        when(frameworkConfiguration.getFolderConfiguration()).thenReturn(frameworkFolderConfiguration);
//        when(frameworkFolderConfiguration.getFolderAbsolutePath("data.mapping"))
//                .thenReturn(mappingPath);
//        assertThrows(RuntimeException.class, () -> TypeConfigurationOperation.getMappingConfigurationFile(frameworkExecution, null, "not_founc"));
//    }
//}
