package io.metadew.iesi.server.rest.executionrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilder;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestService;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoRepository;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoResourceAssembler;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDtoResourceAssembler;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExecutionRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ExecutionRequestController.class, ScriptExecutionRequestDto.class, ScriptExecutionRequestImpersonationDto.class, ScriptExecutionRequestParameterDto.class, ScriptExecutionRequestDtoResourceAssembler.class, ExecutionRequestDtoResourceAssembler.class, ExecutionRequestLabelDto.class, ExecutionRequestDto.class, CustomGlobalExceptionHandler.class})
@ExtendWith(MockitoExtension.class)

public class ExecutionRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ExecutionRequestService executionRequestService;
    @MockBean
    private ScriptExecutionRequestImpersonationDto scriptExecutionRequestImpersonationDto;
    @MockBean
    private ScriptExecutionRequestParameterDto scriptExecutionRequestParameterDto;
    @MockBean
    private ScriptExecutionRequestDtoResourceAssembler scriptExecutionRequestDtoResourceAssembler;
    @MockBean
    private ExecutionRequestLabelDto executionRequestLabelDto;
    @MockBean
    private ExecutionRequestDtoRepository executionRequestDtoRepository;

    @InjectMocks
    private ExecutionRequestController executionRequestController;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(executionRequestController)
                .build();
    }

    @Test
    public void testPagination() throws Exception {
        List<ExecutionRequest> executionRequests = new ArrayList<>();
        ExecutionRequest executionRequest1 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .build();
        executionRequests.add(executionRequest1);
        given(executionRequestDtoRepository.getAll(1, 1, null, null, null, null, null, null))
                .willReturn(executionRequests);

        MockHttpServletResponse response = mvc.perform(
                get("/execution_requests?limit=1&pageNumber=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void getAllNoResultTest() throws Exception {
        List<ExecutionRequest> executionRequests = new ArrayList<>();
        given(executionRequestService.getAll()).willReturn(executionRequests);
        System.out.println(MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("ExecutionRequests").getName());
        mvc.perform(get("/execution_requests?limit=1&pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllResultWithPagination() throws Exception {
        List<ExecutionRequest> executionRequests = new ArrayList<>();
        ExecutionRequest executionRequest1 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context1")
                .description("description")
                .scope("scope")
                .build();
        ExecutionRequest executionRequest2 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .build();
        ExecutionRequest executionRequest3 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .build();
        ExecutionRequest executionRequest4 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .build();
        ExecutionRequest executionRequest5 = new ExecutionRequestBuilder()
                .id("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .build();
        executionRequests.add(executionRequest1);
        executionRequests.add(executionRequest2);
        executionRequests.add(executionRequest3);
        executionRequests.add(executionRequest4);
        executionRequests.add(executionRequest5);
        when(executionRequestDtoRepository.getAll(5, 1, null, null, null, null, null, null)).thenReturn(executionRequests);
        List<ExecutionRequest> result = executionRequestDtoRepository.getAll(5, 1, null, null, null, null, null, null);
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getContext())
                .isEqualTo(executionRequest1.getContext());

        mvc.perform(get("/execution_requests?limit=2&pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
