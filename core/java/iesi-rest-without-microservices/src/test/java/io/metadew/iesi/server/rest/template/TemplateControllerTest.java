package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.server.rest.builder.template.TemplateDtoBuilder;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoRepository;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoResourceAssembler;
import io.metadew.iesi.server.rest.template.dto.TemplateDtoService;
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

import java.util.*;
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
@WebMvcTest(TemplateController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = { TemplateController.class, CustomGlobalExceptionHandler.class, TemplateDtoService.class, TemplateDtoRepository.class,
        TemplateDtoResourceAssembler.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class, FilterService.class})
@ActiveProfiles("test")
@DirtiesContext
public class TemplateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TemplateDtoService templateDtoService;

    @Test
    void getAllNoResult() throws Exception {
        Pageable pageable = PageRequest.of(0,20);
        List<TemplateDto> templateDtoList = new ArrayList<>();
        Page<TemplateDto> page = new PageImpl<>(templateDtoList, pageable, 1);

        given(templateDtoService.fetchAll(any(), eq(pageable), eq(false), eq(new HashSet<>())))
                .willReturn(page);

        mvc.perform(get("/templates").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates").exists())
                .andExpect(jsonPath("$._embedded.templates").isArray())
                .andExpect(jsonPath("$._embedded.templates").isEmpty());
    }

    @Test
    void getAllPagination() throws Exception {
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(), "template1", 1L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(), "template2", 2L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(), "template2", 2L);
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(0, size);
        Pageable pageable3 = PageRequest.of(0, size);
        List<TemplateDto> templateDtoList1 = Stream.of(templateDto1).collect(Collectors.toList());
        List<TemplateDto> templateDtoList2 = Stream.of(templateDto2).collect(Collectors.toList());
        List<TemplateDto> templateDtoList3 = Stream.of(templateDto3).collect(Collectors.toList());
        List<TemplateDto> templateDtoTotalList = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        Page<TemplateDto> page1 = new PageImpl<>(templateDtoList1, pageable1, 3);
        Page<TemplateDto> page2 = new PageImpl<>(templateDtoList2, pageable2, 3);
        Page<TemplateDto> page3 = new PageImpl<>(templateDtoList3, pageable3, 3);

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page1);
        mvc.perform(get("/templates?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page2);
        mvc.perform(get("/templates?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page3);
        mvc.perform(get("/templates?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameDefaultOrdering() throws Exception {
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template1", 0L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template2", 0L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template3", 0L);
        int size = 1;
        Sort sortDefaultAsc = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefaultAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortDefaultAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortDefaultAsc);
        List<TemplateDto> templateDtoList1 = Stream.of(templateDto1).collect(Collectors.toList());
        List<TemplateDto> templateDtoList2 = Stream.of(templateDto2).collect(Collectors.toList());
        List<TemplateDto> templateDtoList3 = Stream.of(templateDto3).collect(Collectors.toList());
        List<TemplateDto> templateDtoTotalList = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        Page<TemplateDto> page1 = new PageImpl<>(templateDtoList1, pageable1, 3);
        Page<TemplateDto> page2 = new PageImpl<>(templateDtoList2, pageable2, 3);
        Page<TemplateDto> page3 = new PageImpl<>(templateDtoList3, pageable3, 3);

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page1);
        mvc.perform(get("/templates?page=0&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable2), eq(false), eq(new HashSet<>())))
                .willReturn(page2);
        mvc.perform(get("/templates?page=1&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable3), eq(false), eq(new HashSet<>())))
                .willReturn(page3);
        mvc.perform(get("/templates?page=2&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameAsc() throws Exception {
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template1", 0L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template2", 0L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template3", 0L);
        int size = 1;
        Sort sortAsc = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortAsc);
        List<TemplateDto> templateDtoList1 = Stream.of(templateDto1).collect(Collectors.toList());
        List<TemplateDto> templateDtoList2 = Stream.of(templateDto2).collect(Collectors.toList());
        List<TemplateDto> templateDtoList3 = Stream.of(templateDto3).collect(Collectors.toList());
        List<TemplateDto> templateDtoTotalList = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        Page<TemplateDto> page1 = new PageImpl<>(templateDtoList1, pageable1, 3);
        Page<TemplateDto> page2 = new PageImpl<>(templateDtoList2, pageable2, 3);
        Page<TemplateDto> page3 = new PageImpl<>(templateDtoList3, pageable3, 3);

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page1);
        mvc.perform(get("/templates?page=0&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable2), eq(false), eq(new HashSet<>())))
                .willReturn(page2);
        mvc.perform(get("/templates?page=1&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable3), eq(false), eq(new HashSet<>())))
                .willReturn(page3);
        mvc.perform(get("/templates?page=2&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationOrderedByNameDesc() throws Exception {
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template1", 0L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template2", 0L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template3", 0L);
        int size = 1;
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDesc);
        Pageable pageable2 = PageRequest.of(1, size, sortDesc);
        Pageable pageable3 = PageRequest.of(2, size, sortDesc);
        List<TemplateDto> templateDtoList1 = Stream.of(templateDto1).collect(Collectors.toList());
        List<TemplateDto> templateDtoList2 = Stream.of(templateDto2).collect(Collectors.toList());
        List<TemplateDto> templateDtoList3 = Stream.of(templateDto3).collect(Collectors.toList());
        List<TemplateDto> templateDtoTotalList = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        Page<TemplateDto> page1 = new PageImpl<>(templateDtoList3, pageable1, 3);
        Page<TemplateDto> page2 = new PageImpl<>(templateDtoList2, pageable2, 3);
        Page<TemplateDto> page3 = new PageImpl<>(templateDtoList1, pageable3, 3);


        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>())))
                .willReturn(page1);
        mvc.perform(get("/templates?page=0&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable2), eq(false), eq(new HashSet<>())))
                .willReturn(page2);
        mvc.perform(get("/templates?page=1&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(templateDtoService.fetchAll(any(), eq(pageable3), eq(false), eq(new HashSet<>())))
                .willReturn(page3);
        mvc.perform(get("/templates?page=2&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    void getAllPaginationSizeEqualAllOrderedByNameDesc() throws Exception {
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template1", 0L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template2", 0L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(),"template3", 0L);
        int size = 3;
        Sort sortDefault = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefault);
        List<TemplateDto> templateDtoList1 = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        List<TemplateDto> templateDtoTotalList = Stream.of(templateDto1, templateDto2, templateDto3).collect(Collectors.toList());
        Page<TemplateDto> page1 = new PageImpl<>(templateDtoList1, pageable1, 3);

        given(templateDtoService.fetchAll(any(), eq(pageable1), eq(false), eq(new HashSet<>()))).willReturn(page1);
        mvc.perform(get("/templates?page=0&size=3&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.templates[0].name", is(templateDto1.getName())))
                .andExpect(jsonPath("$._embedded.templates[1].name", is(templateDto2.getName())))
                .andExpect(jsonPath("$._embedded.templates[2].name", is(templateDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(templateDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) templateDtoTotalList.size() / templateDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));
    }

    @Test
    @WithIesiUser(username = "spring",
        authorities = { "TEMPLATES_READ@PUBLIC"})
    void getByName() throws Exception {
        String name = "templateNameTest";
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(UUID.randomUUID(), name, 0L);

        given(templateDtoService.fetchByName(any(), eq(name), eq(templateDto1.getVersion())))
                .willReturn(Optional.of(templateDto1));
        mvc.perform(get("/templates/templateNameTest/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.version", is(0)))
                .andExpect(jsonPath("$.description", is("templateNameTest desc")))
                .andExpect(jsonPath("$.matchers").exists())
                .andExpect(jsonPath("$.matchers[0].key").exists())
                .andExpect(jsonPath("$.matchers[0].key", is("key1")))
                .andExpect(jsonPath("$.matchers[0].matcherValue").exists())
                .andExpect(jsonPath("$.matchers[0].matcherValue.type", is("any")))
                .andExpect(jsonPath("$.matchers[1].key").exists())
                .andExpect(jsonPath("$.matchers[1].key", is("key2")))
                .andExpect(jsonPath("$.matchers[1].matcherValue").exists())
                .andExpect(jsonPath("$.matchers[1].matcherValue.type", is("fixed")))
                .andExpect(jsonPath("$.matchers[1].matcherValue.value", is("key2")))
                .andExpect(jsonPath("$.matchers[2].key").exists())
                .andExpect(jsonPath("$.matchers[2].key", is("key3")))
                .andExpect(jsonPath("$.matchers[2].matcherValue").exists())
                .andExpect(jsonPath("$.matchers[2].matcherValue.type", is("template")))
                .andExpect(jsonPath("$.matchers[2].matcherValue.templateName", is("templateName")))
                .andExpect(jsonPath("$.matchers[2].matcherValue.templateVersion", is(1)));
    }

    @Test
    void getByNameAndVersion404() throws Exception {
        given(templateDtoService.fetchByName(any(), eq("test"), eq(1)))
                .willReturn(Optional.empty());

        mvc.perform(get("/templates/test/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
