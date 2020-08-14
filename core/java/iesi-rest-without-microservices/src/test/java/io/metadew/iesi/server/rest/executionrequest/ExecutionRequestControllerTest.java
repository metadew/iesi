package io.metadew.iesi.server.rest.executionrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestController;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestDtoRepository;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestService;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDtoResourceAssembler;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDtoResourceAssembler;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import io.metadew.iesi.server.rest.pagination.TotalPages;
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

import java.time.LocalDateTime;
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
        List<ExecutionRequestDto> executionRequestDtos = new ArrayList<>();
        ExecutionRequestDto executionRequest1 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        executionRequestDtos.add(executionRequest1);
        TotalPages totalPages = TotalPages.builder()
                .totalPages(10)
                .payload(executionRequestDtos)
                .build();
        given(executionRequestDtoRepository.getAll())
                .willReturn(executionRequestDtos);

        MockHttpServletResponse response = mvc.perform(
                get("/execution-requests?limit=1&pageNumber=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void getAllNoResultTest() throws Exception {
        List<ExecutionRequestDto> executionRequests = new ArrayList<>();
        given(executionRequestService.getAll()).willReturn(executionRequests);
        mvc.perform(get("/execution-requests?limit=1&pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllResultWithPagination() throws Exception {
        List<ExecutionRequestDto> executionRequestDtos = new ArrayList<>();
        ExecutionRequestDto executionRequest1 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId")
                .name("name")
                .context("context1")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        ExecutionRequestDto executionRequest2 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId2")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        ExecutionRequestDto executionRequest3 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId3")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        ExecutionRequestDto executionRequest4 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        ExecutionRequestDto executionRequest5 = ExecutionRequestDto.builder()
                .executionRequestId("newExecutionRequestId")
                .name("name")
                .context("context")
                .description("description")
                .scope("scope")
                .requestTimestamp(LocalDateTime.now())
                .build();
        executionRequestDtos.add(executionRequest1);
        executionRequestDtos.add(executionRequest2);
        executionRequestDtos.add(executionRequest3);
        executionRequestDtos.add(executionRequest4);
        executionRequestDtos.add(executionRequest5);
        when(executionRequestService.getAll()).thenReturn(executionRequestDtos);
        List<ExecutionRequestDto> result = executionRequestService.getAll();
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getContext())
                .isEqualTo(executionRequest1.getContext());

        mvc.perform(get("/execution-requests?limit=2&pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
