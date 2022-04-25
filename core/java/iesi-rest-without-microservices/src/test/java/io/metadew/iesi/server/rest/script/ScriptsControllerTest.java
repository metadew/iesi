package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.builder.script.ScriptDtoBuilder;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiSecurityChecker;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.dataset.FilterService;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.script.audit.ScriptDesignAuditService;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.ScriptDtoModelAssembler;
import io.metadew.iesi.server.rest.script.dto.ScriptDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.action.ScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDtoService;
import io.metadew.iesi.server.rest.user.UserDtoRepository;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScriptsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ScriptsController.class, CustomGlobalExceptionHandler.class, ScriptDtoModelAssembler.class,
        ScriptPostDtoService.class, ScriptParameterDtoService.class, ScriptLabelDtoService.class, ScriptActionDtoService.class,
        ScriptVersionDtoService.class, ScriptService.class, TestConfiguration.class, IesiConfiguration.class, IesiSecurityChecker.class,
        ScriptDesignAuditService.class, UserDtoRepository.class, ScriptDesignAuditConfiguration.class, FilterService.class})
@ActiveProfiles("test")
@DirtiesContext
class ScriptsControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScriptDtoService scriptDtoService;

    @MockBean
    private ScriptService scriptService;

    @Test
    void getAllNoResult() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        List<ScriptDto> scriptDtoList = new ArrayList<>();
        Page<ScriptDto> page = new PageImpl<>(scriptDtoList, pageable, 1);
        given(scriptDtoService.getAll(any(), eq(pageable), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>())))
                .willReturn(page);

        mvc.perform(get("/scripts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts").exists())
                .andExpect(jsonPath("$._embedded.scripts").isArray())
                .andExpect(jsonPath("$._embedded.scripts").isEmpty());
    }

    @Test
    void getAllPaginationTest() throws Exception {
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("Script1", 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto("Script2", 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto("Script3", 2);
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList2 = Stream.of(scriptDto2).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList3 = Stream.of(scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList1, pageable1, 3);
        Page<ScriptDto> page2 = new PageImpl<>(scriptDtoList2, pageable2, 3);
        Page<ScriptDto> page3 = new PageImpl<>(scriptDtoList3, pageable3, 3);

        given(scriptDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>())))
                .willReturn(page1);
        mvc.perform(get("/scripts?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/scripts?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/scripts?page=2&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllPaginationOrderedByNameDefaultOrdering() throws Exception {
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("ScriptA", 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto("ScriptB", 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto("ScriptC", 2);
        int size = 1;
        Sort sortDefaultAsc = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefaultAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortDefaultAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortDefaultAsc);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList2 = Stream.of(scriptDto2).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList3 = Stream.of(scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList1, pageable1, 3);
        Page<ScriptDto> page2 = new PageImpl<>(scriptDtoList2, pageable2, 3);
        Page<ScriptDto> page3 = new PageImpl<>(scriptDtoList3, pageable3, 3);

        given(scriptDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/scripts?page=0&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/scripts?page=1&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/scripts?page=2&size=1&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllPaginationOrderedByNameAscTest() throws Exception {
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("ScriptA", 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto("ScriptB", 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto("ScriptC", 2);
        int size = 1;
        Sort sortAsc = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortAsc);
        Pageable pageable2 = PageRequest.of(1, size, sortAsc);
        Pageable pageable3 = PageRequest.of(2, size, sortAsc);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList2 = Stream.of(scriptDto2).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList3 = Stream.of(scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList1, pageable1, 3);
        Page<ScriptDto> page2 = new PageImpl<>(scriptDtoList2, pageable2, 3);
        Page<ScriptDto> page3 = new PageImpl<>(scriptDtoList3, pageable3, 3);

        given(scriptDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/scripts?page=0&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/scripts?page=1&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/scripts?page=2&size=1&sort=name,asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllPaginationOrderedByNameDescTest() throws Exception {
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("ScriptA", 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto("ScriptB", 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto("ScriptC", 2);
        int size = 1;
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDesc);
        Pageable pageable2 = PageRequest.of(1, size, sortDesc);
        Pageable pageable3 = PageRequest.of(2, size, sortDesc);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList2 = Stream.of(scriptDto2).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList3 = Stream.of(scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList3, pageable1, 3);
        Page<ScriptDto> page2 = new PageImpl<>(scriptDtoList2, pageable2, 3);
        Page<ScriptDto> page3 = new PageImpl<>(scriptDtoList1, pageable3, 3);

        given(scriptDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/scripts?page=0&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable2), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page2);
        mvc.perform(get("/scripts?page=1&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto2.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        given(scriptDtoService.getAll(any(), eq(pageable3), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page3);
        mvc.perform(get("/scripts?page=2&size=1&sort=name,desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));

    }

    @Test
    void getAllPaginationSizeEqualAllOrderedByNameDescTest() throws Exception {
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("ScriptA", 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto("ScriptB", 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto("ScriptC", 2);
        int size = 3;
        Sort sortDefault = Sort.by(Sort.DEFAULT_DIRECTION, "name");
        Pageable pageable1 = PageRequest.of(0, size, sortDefault);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList1, pageable1, 3);

        given(scriptDtoService.getAll(any(), eq(pageable1), eq(new ArrayList<>()), eq(false), eq(new ArrayList<>()))).willReturn(page1);
        mvc.perform(get("/scripts?page=0&size=3&sort=name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$._embedded.scripts[1].name", is(scriptDto2.getName())))
                .andExpect(jsonPath("$._embedded.scripts[2].name", is(scriptDto3.getName())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

    }

//    @Test
//    void getByNameSerializationTest() {
//        // Todo: Write Test
//    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void getByNamePaginationTest() throws Exception {
        String name = "scriptNameTest";
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto(name, 0);
        ScriptDto scriptDto2 = ScriptDtoBuilder.simpleScriptDto(name, 1);
        ScriptDto scriptDto3 = ScriptDtoBuilder.simpleScriptDto(name, 2);
        int size = 1;
        Pageable pageable1 = PageRequest.of(0, size);
        Pageable pageable2 = PageRequest.of(1, size);
        Pageable pageable3 = PageRequest.of(2, size);
        List<ScriptDto> scriptDtoList1 = Stream.of(scriptDto1).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList2 = Stream.of(scriptDto2).collect(Collectors.toList());
        List<ScriptDto> scriptDtoList3 = Stream.of(scriptDto3).collect(Collectors.toList());
        List<ScriptDto> scriptDtoTotalList = Stream.of(scriptDto1, scriptDto2, scriptDto3).collect(Collectors.toList());
        Page<ScriptDto> page1 = new PageImpl<>(scriptDtoList1, pageable1, 3);
        Page<ScriptDto> page2 = new PageImpl<>(scriptDtoList2, pageable2, 3);
        Page<ScriptDto> page3 = new PageImpl<>(scriptDtoList3, pageable3, 3);

        given(scriptDtoService.getByName(any(), eq(pageable1), eq(name), eq(new ArrayList<>()), eq(false))).willReturn(page1);
        given(scriptDtoService.getByName(any(), eq(pageable2), eq(name), eq(new ArrayList<>()), eq(false))).willReturn(page2);
        given(scriptDtoService.getByName(any(), eq(pageable3), eq(name), eq(new ArrayList<>()), eq(false))).willReturn(page3);

        mvc.perform(get("/scripts/" + name + "?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.scripts[0].version.number", is((int) scriptDto1.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList1.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable1.getPageNumber())));

        mvc.perform(get("/scripts/" + name + "?page=1&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.scripts[0].version.number", is((int) scriptDto2.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList2.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable2.getPageNumber())));

        mvc.perform(get("/scripts/" + name + "?page=2&size=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.scripts[0].name", is(name)))
                .andExpect(jsonPath("$._embedded.scripts[0].version.number", is((int) scriptDto3.getVersion().getNumber())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(scriptDtoTotalList.size())))
                .andExpect(jsonPath("$.page.totalPages", is((int) Math.ceil(((double) scriptDtoTotalList.size() / scriptDtoList3.size())))))
                .andExpect(jsonPath("$.page.number", is(pageable3.getPageNumber())));
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void getByNameAndVersion400AndPropertyPresenceCheck() throws Exception {
        Optional<ScriptDto> optionalScriptDto = Optional.of(ScriptDtoBuilder.simpleScriptDto("nameTest", 0));
        given(scriptDtoService.getByNameAndVersion(null, "nameTest", 0, new ArrayList<>()))
                .willReturn(optionalScriptDto);

        mvc.perform(get("/scripts/nameTest/0").contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(jsonPath("$.actions").exists())
                .andExpect(jsonPath("$.actions[0].number").exists())
                .andExpect(jsonPath("$.actions[0].name").exists())
                .andExpect(jsonPath("$.actions[0].type").exists())
                .andExpect(jsonPath("$.actions[0].description").exists())
                .andExpect(jsonPath("$.actions[0].component").exists())
                .andExpect(jsonPath("$.actions[0].condition").exists())
                .andExpect(jsonPath("$.actions[0].iteration").exists())
                .andExpect(jsonPath("$.actions[0].errorExpected").exists())
                .andExpect(jsonPath("$.actions[0].errorStop").exists())
                .andExpect(jsonPath("$.actions[0].retries").exists())
                .andExpect(jsonPath("$.actions[0].parameters[0].name").exists())
                .andExpect(jsonPath("$.actions[0].parameters[0].value").exists())
                .andExpect(jsonPath("$.actions[0].parameters[1].name").exists())
                .andExpect(jsonPath("$.actions[0].parameters[1].value").exists())
                .andExpect(jsonPath("$.labels[0].name").exists())
                .andExpect(jsonPath("$.labels[0].value").exists())
                .andExpect(jsonPath("$.labels[1].name").exists())
                .andExpect(jsonPath("$.labels[1].value").exists());

    }

    @Test
    void getByNameAndVersion404() throws Exception {
        Optional<ScriptDto> optionalScriptDto = Optional.empty();
        given(scriptDtoService.getByNameAndVersion(any(), eq("nameTest"), eq(0), eq(new ArrayList<>())))
                .willReturn(optionalScriptDto);

        mvc.perform(get("/scripts/nameTest/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = {"SCRIPTS_READ@PUBLIC"})
    void getByNameAndVersionFile() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder("nameTest", 0);
        Script script = scriptBuilder
                .name("nameTest")
                .securityGroupName("PUBLIC")
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .build();
        Optional<Script> optionalScript = Optional.of(script);

        given(scriptService.getByNameAndVersion( "nameTest", 0))
                .willReturn(optionalScript);


        mvc.perform(get("/scripts/nameTest/0/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(jsonPath("$.type", is("script")))
                .andExpect(jsonPath("$.data.name", is("nameTest")))
                .andExpect(jsonPath("$.data.description").exists())
                .andExpect(jsonPath("$.data.version.number").exists())
                .andExpect(jsonPath("$.data.version.description").exists())
                .andExpect(jsonPath("$.data.parameters").exists())
                .andExpect(jsonPath("$.data.actions").exists())
                .andExpect(jsonPath("$.data.labels").exists());
    }


    @Test
    void getByNameAndVersionFileDoesNotExist() throws Exception {

        Optional<ScriptDto> optionalScriptDto = Optional.of(ScriptDtoBuilder.simpleScriptDto("nameTest", 0));
        given(scriptDtoService.getByNameAndVersion(any(), eq("nameTest"), eq(0), eq(new ArrayList<>())))
                .willReturn(optionalScriptDto);

        mvc.perform(get("/scripts/nameTest/1/download"))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void getByNameAndVersionSerializationTest() {
//        // Todo: Write Test : use getByNameAndVersion400AndPropertyPresenceCheck and replace "jsonPath(x).exist()" by "jsonPath(x, is(y))"
//    }
//
//    @Test
//    void post() {
//        // Todo: Write Test
//    }
//
//    @Test
//    void putAll() {
//        // Todo: Write Test
//    }
//
//    @Test
//    void put() {
//        // Todo: Write Test
//    }
//
//    @Test
//    void deleteByName() {
//        // Todo: Write Test
//    }
//
//    @Test
//    void delete() {
//        // Todo: Write Test
//    }

}
