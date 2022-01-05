package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.server.rest.builder.ComponentDtoBuilder;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.ComponentDtoResourceAssembler;
import io.metadew.iesi.server.rest.component.dto.ComponentDtoService;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
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
@WebMvcTest(ComponentsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ComponentsController.class, CustomGlobalExceptionHandler.class, ComponentDtoService.class,
        ComponentDtoResourceAssembler.class, TestConfiguration.class,
        IesiConfiguration.class, IesiSecurityChecker.class})
@ActiveProfiles("test")
@DirtiesContext
class ComponentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ComponentService componentService;
    @MockBean
    private ComponentDtoService componentDtoService;

    @Test
    void getAllNoResult() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        List<ComponentDto> components = new ArrayList<>();
        Page<ComponentDto> page = new PageImpl<>(components, pageable, 1);
        given(componentDtoService.getAll(any(), eq(pageable), eq(new ArrayList<>())))
                .willReturn(page);

        mvc.perform(get("/components").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components").exists())
                .andExpect(jsonPath("$._embedded.components").isArray())
                .andExpect(jsonPath("$._embedded.components").isEmpty());
    }

    @Test
    void getAllPaginationTest() throws Exception {
        ComponentDto componentDto1 = ComponentDtoBuilder.simpleComponentDto("Component1", 0);
        ComponentDto componentDto2 = ComponentDtoBuilder.simpleComponentDto("Component2", 1);
        ComponentDto componentDto3 = ComponentDtoBuilder.simpleComponentDto("Component3", 2);
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ComponentDto> components1 = Stream.of(componentDto1).collect(Collectors.toList());
        List<ComponentDto> components2 = Stream.of(componentDto2).collect(Collectors.toList());
        List<ComponentDto> components3 = Stream.of(componentDto3).collect(Collectors.toList());
        List<ComponentDto> componentDtoTotalList = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        Page<ComponentDto> page1 = new PageImpl<>(components1, pageable1, 3);
        Page<ComponentDto> page2 = new PageImpl<>(components2, pageable2, 3);
        Page<ComponentDto> page3 = new PageImpl<>(components3, pageable3, 3);

        given(componentDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>())))
                .willReturn(page1);
        mvc.perform(get("/components?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/components?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/components?page=2&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameDefaultOrdering() throws Exception {
        ComponentDto componentDto1 = ComponentDtoBuilder.simpleComponentDto("Component1", 0);
        ComponentDto componentDto2 = ComponentDtoBuilder.simpleComponentDto("Component2", 1);
        ComponentDto componentDto3 = ComponentDtoBuilder.simpleComponentDto("Component3", 2);
        int size = 1;
        Sort sortDefaultAsc = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefaultAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortDefaultAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortDefaultAsc);
        List<ComponentDto> components1 = Stream.of(componentDto1).collect(Collectors.toList());
        List<ComponentDto> components2 = Stream.of(componentDto2).collect(Collectors.toList());
        List<ComponentDto> components3 = Stream.of(componentDto3).collect(Collectors.toList());
        List<ComponentDto> componentDtoTotalList = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        Page<ComponentDto> page1 = new PageImpl<>(components1, pageable1, 3);
        Page<ComponentDto> page2 = new PageImpl<>(components2, pageable2, 3);
        Page<ComponentDto> page3 = new PageImpl<>(components3, pageable3, 3);

        given(componentDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/components?page=0&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/components?page=1&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/components?page=2&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllPaginationOrderedByNameDescTest() throws Exception {
        ComponentDto componentDto1 = ComponentDtoBuilder.simpleComponentDto("Component1", 0);
        ComponentDto componentDto2 = ComponentDtoBuilder.simpleComponentDto("Component2", 1);
        ComponentDto componentDto3 = ComponentDtoBuilder.simpleComponentDto("Component3", 2);
        int size = 1;
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDesc);
        Pageable pageable2 = PageRequest.of(1, size, sortDesc);
        Pageable pageable3 = PageRequest.of(2, size, sortDesc);
        List<ComponentDto> components1 = Stream.of(componentDto1).collect(Collectors.toList());
        List<ComponentDto> components2 = Stream.of(componentDto2).collect(Collectors.toList());
        List<ComponentDto> components3 = Stream.of(componentDto3).collect(Collectors.toList());
        List<ComponentDto> componentDtoTotalList = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        // Here Script are given in the Desc Order
        Page<ComponentDto> page1 = new PageImpl<>(components3, pageable1, 3);
        Page<ComponentDto> page2 = new PageImpl<>(components2, pageable2, 3);
        Page<ComponentDto> page3 = new PageImpl<>(components1, pageable3, 3);

        given(componentDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/components?page=0&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/components?page=1&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(componentDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/components?page=2&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationSizeEqualAllOrderedByNameDescTest() throws Exception {
        ComponentDto componentDto1 = ComponentDtoBuilder.simpleComponentDto("Component1", 0);
        ComponentDto componentDto2 = ComponentDtoBuilder.simpleComponentDto("Component2", 1);
        ComponentDto componentDto3 = ComponentDtoBuilder.simpleComponentDto("Component3", 2);
        int size = 3;
        Sort sortDefault = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefault);
        List<ComponentDto> components1 = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        List<ComponentDto> componentDtoTotalList = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        // Here Script are given in the Desc Order
        Page<ComponentDto> page1 = new PageImpl<>(components1, pageable1, 3);

        given(componentDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/components?page=0&size=3&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(componentDto1.getName())))
                .andExpect(jsonPath("$._embedded.components[1].name", is(componentDto2.getName())))
                .andExpect(jsonPath("$._embedded.components[2].name", is(componentDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));
    }

    @Test
    void getByNamePaginationTest() throws Exception {
        String name = "componentNameTest";
        ComponentDto componentDto1 = ComponentDtoBuilder.simpleComponentDto(name, 0);
        ComponentDto componentDto2 = ComponentDtoBuilder.simpleComponentDto(name, 1);
        ComponentDto componentDto3 = ComponentDtoBuilder.simpleComponentDto(name, 2);
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ComponentDto> components1 = Stream.of(componentDto1).collect(Collectors.toList());
        List<ComponentDto> components2 = Stream.of(componentDto2).collect(Collectors.toList());
        List<ComponentDto> components3 = Stream.of(componentDto3).collect(Collectors.toList());
        List<ComponentDto> componentDtoTotalList = Stream.of(componentDto1, componentDto2, componentDto3).collect(Collectors.toList());
        Page<ComponentDto> page1 = new PageImpl<>(components1, pageable1, 3);
        Page<ComponentDto> page2 = new PageImpl<>(components2, pageable2, 3);
        Page<ComponentDto> page3 = new PageImpl<>(components3, pageable3, 3);

        given(componentDtoService.getByName(any(), eq(pageable1), eq(name))).willReturn(page1);
        given(componentDtoService.getByName(any(), eq(pageable2), eq(name))).willReturn(page2);
        given(componentDtoService.getByName(any(), eq(pageable3), eq(name))).willReturn(page3);

        mvc.perform(get("/components/" + name + "?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.components[0].version.number", is((int) componentDto1.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        mvc.perform(get("/components/" + name + "?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.components[0].version.number", is((int) componentDto2.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        mvc.perform(get("/components/" + name + "?page=2&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.components[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.components[0].version.number", is((int) componentDto3.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(componentDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) componentDtoTotalList.size() / components3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getByNameAndVersionAndPropertyPresenceCheck() throws Exception {
        Optional<ComponentDto> optionalComponentDto = Optional.of(ComponentDtoBuilder.simpleComponentDto("nameTest", 0));
        given(componentDtoService.getByNameAndVersion(null, "nameTest", 0))
                .willReturn(optionalComponentDto);

        mvc.perform(get("/components/nameTest/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name", is("nameTest")))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.version.number").exists())
                .andExpect(jsonPath("$.version.description").exists())
                .andExpect(jsonPath("$.parameters").exists())
                .andExpect(jsonPath("$.parameters[0].name").exists())
                .andExpect(jsonPath("$.parameters[0].value").exists())
                .andExpect(jsonPath("$.parameters[1].name").exists())
                .andExpect(jsonPath("$.parameters[1].value").exists())
                .andExpect(jsonPath("$.parameters[2].name").exists())
                .andExpect(jsonPath("$.parameters[2].value").exists())
                .andExpect(jsonPath("$.attributes").exists());
    }

    @Test
    void getByNameAndVersion404() throws Exception {
        Optional<ComponentDto> optionalComponentDto = Optional.empty();
        given(componentDtoService.getByNameAndVersion(any(), eq("nameTest"), eq(0)))
                .willReturn(optionalComponentDto);

        mvc.perform(get("/components/nameTest/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
