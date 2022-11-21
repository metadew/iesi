package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IESIGrantedAuthority;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=false"})
@ContextConfiguration(classes = {TestConfiguration.class, MethodSecurityConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
class ExecutionRequestControllerMvcTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ExecutionRequestService executionRequestService;

    @BeforeEach
    void setup() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                Stream.of(
                        new IESIGrantedAuthority("PUBLIC", "EXECUTION_REQUESTS_WRITE"),
                        new IESIGrantedAuthority("PUBLIC", "EXECUTION_REQUESTS_READ"),
                        new IESIGrantedAuthority("PUBLIC", "EXECUTION_REQUESTS_WRITE")
                ).collect(Collectors.toSet())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    void getAllNoResult() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        List<ExecutionRequestDto> executionRequestDtoList = new ArrayList<>();
        Page<ExecutionRequestDto> page = new PageImpl<>(executionRequestDtoList, pageable, 0);
        given(executionRequestService.getAll(any(), any(), any())).willReturn(page);

        mvc.perform(get("/execution-requests").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.execution_requests").exists())
                .andExpect(jsonPath("$._embedded.execution_requests").isArray())
                .andExpect(jsonPath("$._embedded.execution_requests").isEmpty());
    }

    @Test
    void getAllResultWithPagination() throws Exception {
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
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ExecutionRequestDto> executionRequestDtoList1 = Stream.of(executionRequest1).collect(Collectors.toList());
        List<ExecutionRequestDto> executionRequestDtoList2 = Stream.of(executionRequest2).collect(Collectors.toList());
        List<ExecutionRequestDto> executionRequestDtoList3 = Stream.of(executionRequest3).collect(Collectors.toList());
        List<ExecutionRequestDto> executionRequestDtoList = Stream.of(executionRequest1, executionRequest2, executionRequest3).collect(Collectors.toList());
        Page<ExecutionRequestDto> page1 = new PageImpl<>(executionRequestDtoList1, pageable1, 3);
        Page<ExecutionRequestDto> page2 = new PageImpl<>(executionRequestDtoList2, pageable2, 3);
        Page<ExecutionRequestDto> page3 = new PageImpl<>(executionRequestDtoList3, pageable3, 3);

        given(executionRequestService.getAll(any(), any(), any())).willReturn(page1);
        mvc.perform(get("/execution-requests?page=0&size=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.execution_requests[0].name", is(executionRequestDtoList1.get(0).getName())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].executionRequestId", is(executionRequestDtoList1.get(0).getExecutionRequestId())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].description", is(executionRequestDtoList1.get(0).getDescription())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].scope", is(executionRequestDtoList1.get(0).getScope())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].context", is(executionRequestDtoList1.get(0).getContext())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].email", is(executionRequestDtoList1.get(0).getEmail())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].executionRequestStatus", is(executionRequestDtoList1.get(0).getExecutionRequestStatus())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].scriptExecutionRequests", is(executionRequestDtoList1.get(0).getScriptExecutionRequests())))
                .andExpect(jsonPath("$._embedded.execution_requests[0].executionRequestLabels", is(executionRequestDtoList1.get(0).getExecutionRequestLabels())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(executionRequestDtoList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) executionRequestDtoList.size() / executionRequestDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));
        ;

        given(executionRequestService.getAll(any(), any(), any())).willReturn(page2);
        mvc.perform(get("/execution-requests?page=1&size=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.execution_requests[0].name", is(executionRequestDtoList2.get(0).getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(executionRequestDtoList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) executionRequestDtoList.size() / executionRequestDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(executionRequestService.getAll(any(), any(), any())).willReturn(page3);
        mvc.perform(get("/execution-requests?page=2&size=1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.execution_requests[0].name", is(executionRequestDtoList3.get(0).getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(executionRequestDtoList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) executionRequestDtoList.size() / executionRequestDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllFilterRequester() throws Exception {

        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .executionRequestId(UUID.randomUUID().toString())
                .requestTimestamp(LocalDateTime.now())
                .name("name")
                .description("description")
                .scope("scope")
                .context("context")
                .email("email")
                .userId(UUID.randomUUID().toString())
                .username("spring")
                .debugMode(false)
                .executionRequestStatus(ExecutionRequestStatus.COMPLETED)
                .scriptExecutionRequests(new HashSet<>())
                .build();

        List<ExecutionRequestDto> executionRequestDtos = Stream.of(executionRequestDto)
                .collect(Collectors.toList());

        List<ExecutionRequestFilter> executionRequestFilters = Stream.of(new ExecutionRequestFilter(
                ExecutionRequestFilterOption.REQUESTER,
                "spring",
                true)).collect(Collectors.toList());

        Pageable page= PageRequest.of(0, 2);

        Page<ExecutionRequestDto> executionRequestDtoPage = new PageImpl<>(executionRequestDtos, page, 1);

        when(executionRequestService.getAll(any(), any(), eq(executionRequestFilters)))
                .thenReturn(executionRequestDtoPage);

        mvc.perform(get("/execution-requests?page=1&size=1&requester=spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(executionRequestDtos.size())))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    void getAllFilterRequesterNoFound() throws Exception {

        List<ExecutionRequestFilter> executionRequestFilters = Stream.of(new ExecutionRequestFilter(
                ExecutionRequestFilterOption.REQUESTER,
                "spring",
                true)).collect(Collectors.toList());

        Pageable page= PageRequest.of(0, 2);

        Page<ExecutionRequestDto> executionRequestDtoPage = new PageImpl<>(new ArrayList<>(), page, 0);

        when(executionRequestService.getAll(any(), any(), eq(executionRequestFilters)))
                .thenReturn(executionRequestDtoPage);

        mvc.perform(get("/execution-requests?page=1&size=1&requester=spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(0)))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }
}