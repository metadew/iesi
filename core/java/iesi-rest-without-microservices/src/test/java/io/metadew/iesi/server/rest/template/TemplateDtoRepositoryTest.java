package io.metadew.iesi.server.rest.template;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.configuration.template.matcher.MatcherConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.template.TemplateBuilder;
import io.metadew.iesi.server.rest.builder.template.TemplateDtoBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.WithIesiUser;
import io.metadew.iesi.server.rest.template.dto.ITemplateDtoRepository;
import io.metadew.iesi.server.rest.template.dto.TemplateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = { "spring.main.allow-bean-definition-overriding=true" })
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext
class TemplateDtoRepositoryTest {

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private ITemplateDtoRepository templateDtoRepository;

    @BeforeEach
    void cleanup() {
        MetadataRepositoryConfiguration.getInstance().clearAllTables();
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = { "TEMPLATES_READ@PUBLIC"})
    void getAllPaginated() {
        UUID templateUuid1 = UUID.randomUUID();
        UUID templateUuid2 = UUID.randomUUID();
        UUID templateUuid3 = UUID.randomUUID();

        Template template1 = TemplateBuilder.simpleTemplate(templateUuid1, "templateName1", 1L);
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(templateUuid1,"templateName1", 1L);
        Template template2 = TemplateBuilder.simpleTemplate(templateUuid2, "templateName2", 1L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(templateUuid2,"templateName2", 1L);
        Template template3 = TemplateBuilder.simpleTemplate(templateUuid3, "templateName3", 1L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(templateUuid3,"templateName3", 1L);
        templateService.insert(template1);
        templateService.insert(template2);
        templateService.insert(template3);
        Pageable pageable = PageRequest.of(0, 3);

        assertThat(
                templateDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), pageable, true, new HashSet<>()))
                .hasSize(3)
                .contains(templateDto1, templateDto2, templateDto3);
    }

    @Test
    @WithIesiUser(username = "spring",
            authorities = { "TEMPLATES_READ@PUBLIC"})
    void getAllPaginatedMultipleversions() {
        UUID templateUuid1 = UUID.randomUUID();
        UUID templateUuid2 = UUID.randomUUID();
        UUID templateUuid3 = UUID.randomUUID();

        Template template1 = TemplateBuilder.simpleTemplate(templateUuid1, "templateName1", 1L);
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(templateUuid1,"templateName1", 1L);
        Template template2 = TemplateBuilder.simpleTemplate(templateUuid2, "templateName1", 2L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(templateUuid2,"templateName1", 2L);
        Template template3 = TemplateBuilder.simpleTemplate(templateUuid3, "templateName1", 3L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(templateUuid3,"templateName1", 3L);
        templateService.insert(template1);
        templateService.insert(template2);
        templateService.insert(template3);
        Pageable pageable = PageRequest.of(0, 3);

        assertThat(
                templateDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), pageable, false, new HashSet<>()))
                .hasSize(3)
                .contains(templateDto1, templateDto2, templateDto3);
    }

    @Test
    void getAllPaginatedFilterName() {
        UUID templateUuid1 = UUID.randomUUID();
        UUID templateUuid2 = UUID.randomUUID();
        UUID templateUuid3 = UUID.randomUUID();

        Template template1 = TemplateBuilder.simpleTemplate(templateUuid1, "templateName1", 1L);
        Template template2 = TemplateBuilder.simpleTemplate(templateUuid2, "templateName2", 2L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(templateUuid2,"templateName2", 2L);
        Template template3 = TemplateBuilder.simpleTemplate(templateUuid3, "templateName3", 3L);
        templateService.insert(template1);
        templateService.insert(template2);
        templateService.insert(template3);
        Pageable pageable = PageRequest.of(0, 3);
        Set<TemplateFilter> templateFilters = Stream.of(new TemplateFilter(TemplateFilterOption.NAME, "name2", false)).collect(Collectors.toSet());

        assertThat(
                templateDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), pageable, true, templateFilters))
                .hasSize(1)
                .contains(templateDto2);
    }

    @Test
    void GetAllSortedCaseTest() {
        UUID templateUuid1 = UUID.randomUUID();
        UUID templateUuid2 = UUID.randomUUID();
        UUID templateUuid3 = UUID.randomUUID();

        Template template1 = TemplateBuilder.simpleTemplate(templateUuid1, "A", 1L);
        TemplateDto templateDto1 = TemplateDtoBuilder.simpleTemplateDto(templateUuid1,"A", 1L);
        Template template2 = TemplateBuilder.simpleTemplate(templateUuid2, "Z", 1L);
        TemplateDto templateDto2 = TemplateDtoBuilder.simpleTemplateDto(templateUuid2,"Z", 1L);
        Template template3 = TemplateBuilder.simpleTemplate(templateUuid3, "b", 1L);
        TemplateDto templateDto3 = TemplateDtoBuilder.simpleTemplateDto(templateUuid3,"b", 1L);
        templateService.insert(template1);
        templateService.insert(template2);
        templateService.insert(template3);
        Pageable pageableASC = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "name"));
        Pageable pageableDESC = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "name"));

        assertThat(
                templateDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), pageableASC, true, new HashSet<>()))
                .hasSize(3)
                .containsExactly(templateDto1, templateDto3, templateDto2);

        assertThat(
                templateDtoRepository.getAll(SecurityContextHolder.getContext().getAuthentication(), pageableDESC, true, new HashSet<>()))
                .hasSize(3)
                .containsExactly(templateDto2, templateDto3, templateDto1);
    }
}
