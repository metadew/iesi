package io.metadew.iesi.server.rest.connection;


import io.metadew.iesi.server.rest.builder.connection.ConnectionDtoBuilder;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDto;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoResourceAssembler;
import io.metadew.iesi.server.rest.connection.dto.ConnectionDtoService;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ConnectionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ConnectionsController.class, CustomGlobalExceptionHandler.class, ConnectionDtoService.class,
        ConnectionDtoResourceAssembler.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class})
@ActiveProfiles("test")
@DirtiesContext
class ConnectionControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConnectionService connectionService;
    @MockBean
    private ConnectionDtoService connectionDtoService;

    @Test
    void getAllNoResult() throws Exception {
        // Mock Service
        Pageable pageable = PageRequest.of(0, 20);
        List<ConnectionDto> connectionDtoList = new ArrayList<>();
        Page<ConnectionDto> page = new PageImpl<>(connectionDtoList, pageable, 1);
        given(connectionDtoService.getAll(any(), eq(pageable), eq(new ArrayList<>())))
                .willReturn(page);

        mvc.perform(get("/connections").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList").exists())
                .andExpect(jsonPath("$._embedded.connectionDtoList").isArray())
                .andExpect(jsonPath("$._embedded.connectionDtoList").isEmpty());
    }

    @Test
    void getAllPaginationTest() throws Exception {
        ConnectionDto connectionDto1 = ConnectionDtoBuilder.simpleConnectionDto("Connection1");
        ConnectionDto connectionDto2 = ConnectionDtoBuilder.simpleConnectionDto("Connection2");
        ConnectionDto connectionDto3 = ConnectionDtoBuilder.simpleConnectionDto("Connection3");
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ConnectionDto> componentDtoList1 = Stream.of(connectionDto1).collect(Collectors.toList());
        List<ConnectionDto> componentDtoList2 = Stream.of(connectionDto2).collect(Collectors.toList());
        List<ConnectionDto> componentDtoList3 = Stream.of(connectionDto3).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoTotalList = Stream.of(connectionDto1, connectionDto2, connectionDto3).collect(Collectors.toList());
        Page<ConnectionDto> page1 = new PageImpl<>(componentDtoList1, pageable1, 3);
        Page<ConnectionDto> page2 = new PageImpl<>(componentDtoList2, pageable2, 3);
        Page<ConnectionDto> page3 = new PageImpl<>(componentDtoList3, pageable3, 3);

        given(connectionDtoService.getAll(any(),eq(pageable1), eq(new ArrayList<>())))
                .willReturn(page1);
        mvc.perform(get("/connections?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/connections?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/connections?page=2&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameDefaultOrdering() throws Exception {
        ConnectionDto connectionDto1 = ConnectionDtoBuilder.simpleConnectionDto("Connection1");
        ConnectionDto connectionDto2 = ConnectionDtoBuilder.simpleConnectionDto("Connection2");
        ConnectionDto connectionDto3 = ConnectionDtoBuilder.simpleConnectionDto("Connection3");
        int size = 1;
        Sort sortDefaultAsc = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefaultAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortDefaultAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortDefaultAsc);
        List<ConnectionDto> connectionDtoList1 = Stream.of(connectionDto1).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoList2 = Stream.of(connectionDto2).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoList3 = Stream.of(connectionDto3).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoTotalList = Stream.of(connectionDto1, connectionDto2, connectionDto3).collect(Collectors.toList());
        Page<ConnectionDto> page1 = new PageImpl<>(connectionDtoList1, pageable1, 3);
        Page<ConnectionDto> page2 = new PageImpl<>(connectionDtoList2, pageable2, 3);
        Page<ConnectionDto> page3 = new PageImpl<>(connectionDtoList3, pageable3, 3);

        given(connectionDtoService.getAll(any(),eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/connections?page=0&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / connectionDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/connections?page=1&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / connectionDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/connections?page=2&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / connectionDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameDescTest() throws Exception {
        ConnectionDto connectionDto1 = ConnectionDtoBuilder.simpleConnectionDto("Connection1");
        ConnectionDto connectionDto2 = ConnectionDtoBuilder.simpleConnectionDto("Connection2");
        ConnectionDto connectionDto3 = ConnectionDtoBuilder.simpleConnectionDto("Connection3");
        int size = 1;
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDesc);
        Pageable pageable2 = PageRequest.of(1, size, sortDesc);
        Pageable pageable3 = PageRequest.of(2, size, sortDesc);
        List<ConnectionDto> componentDtoList1 = Stream.of(connectionDto1).collect(Collectors.toList());
        List<ConnectionDto> componentDtoList2 = Stream.of(connectionDto2).collect(Collectors.toList());
        List<ConnectionDto> componentDtoList3 = Stream.of(connectionDto3).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoTotalList = Stream.of(connectionDto1, connectionDto2, connectionDto3).collect(Collectors.toList());
        // Here Script are given in the Desc Order
        Page<ConnectionDto> page1 = new PageImpl<>(componentDtoList1, pageable1, 3);
        Page<ConnectionDto> page2 = new PageImpl<>(componentDtoList2, pageable2, 3);
        Page<ConnectionDto> page3 = new PageImpl<>(componentDtoList3, pageable3, 3);

        given(connectionDtoService.getAll(any(),eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/connections?page=0&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/connections?page=1&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(connectionDtoService.getAll(any(),eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/connections?page=2&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / componentDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationSizeEqualAllOrderedByNameDescTest() throws Exception {
        ConnectionDto connectionDto1 = ConnectionDtoBuilder.simpleConnectionDto("Connection1");
        ConnectionDto connectionDto2 = ConnectionDtoBuilder.simpleConnectionDto("Connection2");
        ConnectionDto connectionDto3 = ConnectionDtoBuilder.simpleConnectionDto("Connection3");
        int size = 3;
        Sort sortDefault = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefault);
        List<ConnectionDto> connectionDtoList = Stream.of(connectionDto1, connectionDto2, connectionDto3).collect(Collectors.toList());
        List<ConnectionDto> connectionDtoTotalList = Stream.of(connectionDto1, connectionDto2, connectionDto3).collect(Collectors.toList());
        // Here Script are given in the Desc Order
        Page<ConnectionDto> page1 = new PageImpl<>(connectionDtoList, pageable1, 3);

        given(connectionDtoService.getAll(any(),eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/connections?page=0&size=3&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.connectionDtoList[0].name", is(connectionDto1.getName())))
                .andExpect(jsonPath("$._embedded.connectionDtoList[1].name", is(connectionDto2.getName())))
                .andExpect(jsonPath("$._embedded.connectionDtoList[2].name", is(connectionDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(connectionDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) connectionDtoTotalList.size() / connectionDtoTotalList.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));
    }

    @Test
    void getByNamePaginationTest() throws Exception {
        String name = "connectionNameTest";
        ConnectionDto connectionDto1 = ConnectionDtoBuilder.simpleConnectionDto(name);

        given(connectionDtoService.getByName(any(),eq(name))).willReturn(Optional.of(connectionDto1));

        mvc.perform(get("/connections/" + name + "?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.environments[0].environment", is("env1")))
                .andExpect(jsonPath("$.environments[1].environment", is("env2")));
    }


    @Test
    void getByName404() throws Exception {
        // Mock Service
        String name = "nameTest";
        Optional<ConnectionDto> optionalConnectionDto = Optional.empty();
        given(connectionDtoService.getByName(any(),eq(name)))
                .willReturn(optionalConnectionDto);

        mvc.perform(get("/connections/" + name + "/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }




}
