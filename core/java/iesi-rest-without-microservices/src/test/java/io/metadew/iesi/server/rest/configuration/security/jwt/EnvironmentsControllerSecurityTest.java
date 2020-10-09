//package io.metadew.iesi.server.rest.configuration.security.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.metadew.iesi.server.rest.Application;
//import io.metadew.iesi.server.rest.configuration.TestConfiguration;
//import io.metadew.iesi.server.rest.environment.EnvironmentsController;
//import io.metadew.iesi.server.rest.environment.dto.EnvironmentDto;
//import io.metadew.iesi.server.rest.environment.dto.EnvironmentParameterDto;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static java.util.Collections.singletonList;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Log4j2
//@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
//@ContextConfiguration(classes = TestConfiguration.class)
//@ExtendWith({MockitoExtension.class, SpringExtension.class})
//@AutoConfigureMockMvc
//@ActiveProfiles({"test", "security"})
//class EnvironmentsControllerSecurityTest {
//
//    @Autowired
//    private ObjectMapper jacksonObjectMapper;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private EnvironmentsController environmentsController;
//
//    @Test
//    void testGetAllNoUser() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    //retrieve all
//    @Test
//    @WithMockUser(username = "spring")
//    void testGetAllNoRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testGetAllAdminRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testGetAllTechnicalEngineerRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
//    void testGetAllTestEngineerRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
//    void testGetAllExecutorRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"VIEWER"})
//    void testGetAllViewerRole() throws Exception {
//        mvc.perform(get("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    //retrieve by name
//    @Test
//    @WithMockUser(username = "spring")
//    void testGetByNameNoRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testGetByNameAdminRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testGetByNameTechnicalEngineerRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
//    void testGetByNameAllTestEngineerRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
//    void testGetByNameAllExecutorRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"VIEWER"})
//    void testGetByNameViewerRole() throws Exception {
//        mvc.perform(get("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    // create components
//    @Test
//    void testCreateNoUser() throws Exception {
//        mvc.perform(post("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testCreateNoRole() throws Exception {
//        mvc.perform(post("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testCreateWrongRoles() throws Exception {
//        mvc.perform(post("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testCreateAdminRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                post("/environments")
//                        .content(jacksonObjectMapper.writeValueAsString(environmentDto))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testCreateTestEngineerRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                post("/environments")
//                        .content(jacksonObjectMapper.writeValueAsString(environmentDto))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    // update bulk components
//    @Test
//    void testUpdateBulkNoUser() throws Exception {
//        mvc.perform(put("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testUpdateBulkNoRole() throws Exception {
//        mvc.perform(put("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testUpdateBulkWrongRoles() throws Exception {
//        mvc.perform(put("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testUpdateBulkAdminRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                put("/environments")
//                        .content(jacksonObjectMapper.writeValueAsString(singletonList(environmentDto)))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testUpdateBulkTestEngineerRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                put("/environments")
//                        .content(jacksonObjectMapper.writeValueAsString(singletonList(environmentDto)))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    // update single component
//    @Test
//    void testUpdateSingleNoUser() throws Exception {
//        mvc.perform(put("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testUpdateSingleNoRole() throws Exception {
//        mvc.perform(put("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testUpdateSingleWrongRoles() throws Exception {
//        mvc.perform(put("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testUpdateSingleAdminRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                put("/environments/name")
//                        .content(jacksonObjectMapper.writeValueAsString(environmentDto))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testUpdateSingleTestEngineerRole() throws Exception {
//        EnvironmentDto environmentDto = EnvironmentDto.builder()
//                .name("component")
//                .description("description")
//                .parameters(Stream.of(new EnvironmentParameterDto("param1", "value1")).collect(Collectors.toList()))
//                .build();
//        mvc.perform(
//                put("/environments/name")
//                        .content(jacksonObjectMapper.writeValueAsString(environmentDto))
//                        .contentType("application/json"))
//                .andExpect(status().isOk());
//    }
//
//    //delete all
//    @Test
//    void testDeleteAllNoUser() throws Exception {
//        mvc.perform(delete("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testDeleteAllNoRole() throws Exception {
//        mvc.perform(delete("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testDeleteAllWrongRoles() throws Exception {
//        mvc.perform(delete("/environments"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testDeleteAllAdminRole() throws Exception {
//        mvc.perform(delete("/environments"))
//                .andExpect(status().isOk());
//    }
//
//    //delete by name
//    @Test
//    void testDeleteByNameNoUser() throws Exception {
//        mvc.perform(delete("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testDeleteByNameNoRole() throws Exception {
//        mvc.perform(delete("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testDeleteByNameWrongRoles() throws Exception {
//        mvc.perform(delete("/environments/name"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testDeleteByNameAdminRole() throws Exception {
//        mvc.perform(delete("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
//    void testDeleteByNameTestEngineerRole() throws Exception {
//        mvc.perform(delete("/environments/name"))
//                .andExpect(status().isOk());
//    }
//
//}
