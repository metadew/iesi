package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.script.ScriptsController;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc
@ActiveProfiles({"test", "security"})
class ScritpsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScriptsController scriptsController;

    @Test
    void testGetAllNoUser() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isForbidden());
    }

    //retrieve all
    @Test
    @WithMockUser(username = "spring")
    void testGetAllNoRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testGetAllAdminRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
    void testGetAllTechnicalEngineerRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testGetAllTestEngineerRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testGetAllExecutorRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"VIEWER"})
    void testGetAllViewerRole() throws Exception {
        mvc.perform(get("/scripts"))
                .andExpect(status().isOk());
    }

    //retrieve by name
    @Test
    @WithMockUser(username = "spring")
    void testGetByNameNoRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testGetByNameAdminRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
    void testGetByNameTechnicalEngineerRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testGetByNameAllTestEngineerRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testGetByNameAllExecutorRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"VIEWER"})
    void testGetByNameViewerRole() throws Exception {
        mvc.perform(get("/scripts/name"))
                .andExpect(status().isOk());
    }

    //retrieve by name and version
    @Test
    @WithMockUser(username = "spring")
    void testGetByNameAndVersionNoRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testGetByNameAndVersionAdminRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
    void testGetByNameAndVersionTechnicalEngineerRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testGetByNameAndVersionAllTestEngineerRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testGetByNameAndVersionAllExecutorRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"VIEWER"})
    void testGetByNameAndVersionViewerRole() throws Exception {
        mvc.perform(get("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    // create scripts
    @Test
    void testCreateNoUser() throws Exception {
        mvc.perform(post("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testCreateNoRole() throws Exception {
        mvc.perform(post("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testCreateWrongRoles() throws Exception {
        mvc.perform(post("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testCreateAdminRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                post("/scripts")
                        .content(jacksonObjectMapper.writeValueAsString(scriptDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testCreateTestEngineerRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                post("/scripts")
                        .content(jacksonObjectMapper.writeValueAsString(scriptDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    // update bulk scripts
    @Test
    void testUpdateBulkNoUser() throws Exception {
        mvc.perform(put("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testUpdateBulkNoRole() throws Exception {
        mvc.perform(put("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testUpdateBulkWrongRoles() throws Exception {
        mvc.perform(put("/scripts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testUpdateBulkAdminRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                put("/scripts")
                        .content(jacksonObjectMapper.writeValueAsString(singletonList(scriptDto)))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testUpdateBulkTestEngineerRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                put("/scripts")
                        .content(jacksonObjectMapper.writeValueAsString(singletonList(scriptDto)))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    // update single script
    @Test
    void testUpdateSingleNoUser() throws Exception {
        mvc.perform(put("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testUpdateSingleNoRole() throws Exception {
        mvc.perform(put("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testUpdateSingleWrongRoles() throws Exception {
        mvc.perform(put("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testUpdateSingleAdminRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                put("/scripts/name/1")
                        .content(jacksonObjectMapper.writeValueAsString(scriptDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testUpdateSingleTestEngineerRole() throws Exception {
        ScriptDto scriptDto = ScriptDto.builder()
                .name("script")
                .description("description")
                .version(new ScriptVersionDto(1, "description"))
                .build();
        mvc.perform(
                put("/scripts/name/1")
                        .content(jacksonObjectMapper.writeValueAsString(scriptDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //delete all
//    @Test
//    void testDeleteAllNoUser() throws Exception {
//        mvc.perform(delete("/scripts"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testDeleteAllNoRole() throws Exception {
//        mvc.perform(delete("/scripts"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testDeleteAllWrongRoles() throws Exception {
//        mvc.perform(delete("/scripts"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testDeleteAllAdminRole() throws Exception {
//        mvc.perform(delete("/scripts"))
//                .andExpect(status().isOk());
//    }

    //delete by name
    @Test
    void testDeleteByNameNoUser() throws Exception {
        mvc.perform(delete("/scripts/name"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testDeleteByNameNoRole() throws Exception {
        mvc.perform(delete("/scripts/name"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testDeleteByNameWrongRoles() throws Exception {
        mvc.perform(delete("/scripts/name"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testDeleteByNameAdminRole() throws Exception {
        mvc.perform(delete("/scripts/name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testDeleteByNameTestEngineerRole() throws Exception {
        mvc.perform(delete("/scripts/name"))
                .andExpect(status().isOk());
    }

    //delete by name and version
    @Test
    void testDeleteByNameAndVersionNoUser() throws Exception {
        mvc.perform(delete("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "spring")
    void testDeleteByNameAndVersionNoRole() throws Exception {
        mvc.perform(delete("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testDeleteByNameAndVersionWrongRoles() throws Exception {
        mvc.perform(delete("/scripts/name/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testDeleteByNameAndVersionAdminRole() throws Exception {
        mvc.perform(delete("/scripts/name/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testDeleteByNameAndVersionTestEngineerRole() throws Exception {
        mvc.perform(delete("/scripts/name/1"))
                .andExpect(status().isOk());
    }

}
