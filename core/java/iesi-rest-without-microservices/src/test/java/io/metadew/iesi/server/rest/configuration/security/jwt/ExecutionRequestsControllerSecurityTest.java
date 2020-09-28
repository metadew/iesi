package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
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

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@AutoConfigureMockMvc
@ActiveProfiles({"test", "security"})
class ExecutionRequestsControllerSecurityTest {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ExecutionRequestController executionRequestController;

    @Test
    void testGetAllNoUser() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    //retrieve all
    @Test
    @WithMockUser(username = "spring")
    void testGetAllNoRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testGetAllAdminRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
    void testGetAllTechnicalEngineerRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testGetAllTestEngineerRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testGetAllExecutorRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"VIEWER"})
    void testGetAllViewerRole() throws Exception {
        mvc.perform(get("/execution-requests"))
                .andExpect(status().isOk());
    }

    //retrieve by id
    @Test
    @WithMockUser(username = "spring")
    void testGetByNameNoRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testGetByNameAdminRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER"})
    void testGetByNameTechnicalEngineerRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testGetByNameAllTestEngineerRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testGetByNameAllExecutorRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"VIEWER"})
    void testGetByNameViewerRole() throws Exception {
        mvc.perform(get("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

    // create components
    @Test
    void testCreateNoUser() throws Exception {
        mvc.perform(post("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testCreateNoRole() throws Exception {
        mvc.perform(post("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "VIEWER"})
    void testCreateWrongRoles() throws Exception {
        mvc.perform(post("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testCreateAdminRole() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                .name("component")
                .description("description")
                .requestTimestamp(LocalDateTime.now())
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .build();
        mvc.perform(
                post("/execution-requests")
                        .content(jacksonObjectMapper.writeValueAsString(executionRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER"})
    void testCreateTestEngineerRole() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                .name("component")
                .description("description")
                .requestTimestamp(LocalDateTime.now())
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .build();
        mvc.perform(
                post("/execution-requests")
                        .content(jacksonObjectMapper.writeValueAsString(executionRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"EXECUTOR"})
    void testCreateExecutorRole() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                .name("component")
                .description("description")
                .requestTimestamp(LocalDateTime.now())
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .build();
        mvc.perform(
                post("/execution-requests")
                        .content(jacksonObjectMapper.writeValueAsString(executionRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    // update bulk components
    @Test
    void testUpdateBulkNoUser() throws Exception {
        mvc.perform(put("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testUpdateBulkNoRole() throws Exception {
        mvc.perform(put("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "TECHNICAL_ENGINEER", "EXECUTOR", "VIEWER"})
    void testUpdateBulkWrongRoles() throws Exception {
        mvc.perform(put("/execution-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testUpdateBulkAdminRole() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                .name("component")
                .description("description")
                .requestTimestamp(LocalDateTime.now())
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .build();
        mvc.perform(
                put("/execution-requests")
                        .content(jacksonObjectMapper.writeValueAsString(singletonList(executionRequestDto)))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    // update single component
    @Test
    void testUpdateSingleNoUser() throws Exception {
        mvc.perform(put("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testUpdateSingleNoRole() throws Exception {
        mvc.perform(put("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER", "TECHNICAL_ENGINEER"})
    void testUpdateSingleWrongRoles() throws Exception {
        mvc.perform(put("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testUpdateSingleAdminRole() throws Exception {
        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId("d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                .name("component")
                .description("description")
                .requestTimestamp(LocalDateTime.now())
                .executionRequestStatus(ExecutionRequestStatus.SUBMITTED)
                .build();
        mvc.perform(
                put("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81")
                        .content(jacksonObjectMapper.writeValueAsString(executionRequestDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    //delete all
//    @Test
//    void testDeleteAllNoUser() throws Exception {
//        mvc.perform(delete("/execution-requests"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring")
//    void testDeleteAllNoRole() throws Exception {
//        mvc.perform(delete("/execution-requests"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"TECHNICAL_ENGINEER", "TEST_ENGINEER", "EXECUTOR", "VIEWER"})
//    void testDeleteAllWrongRoles() throws Exception {
//        mvc.perform(delete("/execution-requests"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @WithMockUser(username = "spring", roles = {"ADMIN"})
//    void testDeleteAllAdminRole() throws Exception {
//        mvc.perform(delete("/execution-requests"))
//                .andExpect(status().isOk());
//    }

    //delete by name
    @Test
    void testDeleteByNameNoUser() throws Exception {
        mvc.perform(delete("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring")
    void testDeleteByNameNoRole() throws Exception {
        mvc.perform(delete("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"TEST_ENGINEER", "EXECUTOR", "VIEWER", "TECHNICAL_ENGINEER"})
    void testDeleteByNameWrongRoles() throws Exception {
        mvc.perform(delete("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "spring", roles = {"ADMIN"})
    void testDeleteByNameAdminRole() throws Exception {
        mvc.perform(delete("/execution-requests/d40e1e0d-aa9d-421e-ad72-45123a8d9e81"))
                .andExpect(status().isOk());
    }

}
