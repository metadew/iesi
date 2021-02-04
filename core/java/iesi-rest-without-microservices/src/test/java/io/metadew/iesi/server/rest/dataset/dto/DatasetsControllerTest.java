package io.metadew.iesi.server.rest.dataset.dto;

import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.server.rest.configuration.IesiConfiguration;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.dataset.*;


import io.metadew.iesi.server.rest.dataset.implementation.DatasetImplementationDtoModelAssembler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {TestConfiguration.class, IesiConfiguration.class,
        DatasetDtoModelAssembler.class, IDatasetService.class, DatasetController.class,
        IDatasetImplementationService.class, DatasetImplementationDtoModelAssembler.class
        })
@ActiveProfiles("test")
public class DatasetsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DatasetDtoService datasetDtoService;


    @Test
    void getAll() throws Exception {

        Pageable pageable = PageRequest.of(0, 20);

        List<DatasetDto> datasetDtoList = new ArrayList<>();
        Page<DatasetDto> page = new PageImpl<>(datasetDtoList, pageable, 1);
        given(datasetDtoService.fetchAll(pageable,new DatasetFiltersBuilder()
                .build()))
                .willReturn(page);

        mvc.perform(get("/datasets/implementations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllOnlyUuid() throws Exception {

        Pageable pageable = PageRequest.of(0, 20);

        List<DatasetNoImplDto> datasetDtoList = new ArrayList<>();
        Page<DatasetNoImplDto> page = new PageImpl<>(datasetDtoList, pageable, 1);
        given(datasetDtoService.fetchAllOnlyUuid(pageable,new DatasetFiltersBuilder()
                .build()))
                .willReturn(page);

        mvc.perform(get("/datasets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
