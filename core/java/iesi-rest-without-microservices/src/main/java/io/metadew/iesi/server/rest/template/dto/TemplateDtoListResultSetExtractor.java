package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.metadata.definition.template.TemplateKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.relational.core.sql.SQL;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class TemplateDtoListResultSetExtractor {
    public List<TemplateDto> extractData(CachedRowSet rs) throws SQLException {
        Map<UUID, TemplateDtoBuilder> templateDtoBuilderMap = new LinkedHashMap<>();
        TemplateDtoBuilder templateDtoBuilder;

        while (rs.next()) {
            templateDtoBuilder = templateDtoBuilderMap.get(UUID.fromString(rs.getString("template_id")));
            if (templateDtoBuilder == null) {
                templateDtoBuilder = mapTemplateDtoBuilder(rs);
                templateDtoBuilderMap.put(UUID.fromString(rs.getString("template_id")), templateDtoBuilder);
            }
            addMatchers(templateDtoBuilder, rs);
        }

        return templateDtoBuilderMap.values().stream().map(TemplateDtoBuilder::build).collect(Collectors.toList());
    }
    private TemplateDtoBuilder mapTemplateDtoBuilder(CachedRowSet rs) throws SQLException {
        return new TemplateDtoBuilder(
                UUID.fromString(rs.getString("template_id")),
                rs.getString("template_name"),
                rs.getLong("template_version"),
                rs.getString("template_description"),
                new HashSet<>()
        );
    }

    private void addMatchers(TemplateDtoBuilder templateDtoBuilder, CachedRowSet rs) throws SQLException {
        if (rs.getString("matcherValue_type").equals("any")) {
            templateDtoBuilder.getMatcherDtoBuilders().add(new TemplateMatcherAnyValueDtoBuilder(rs.getString("matcher_key")));
        } else if (rs.getString("matcherValue_type").equals("fixed")) {
            templateDtoBuilder.getMatcherDtoBuilders().add(new TemplateMatcherFixedValueDtoBuilder(rs.getString("matcher_key"), rs.getString("matcherValue_fixedValue")));
        } else if (rs.getString("matcherValue_type").equals("template")) {
            templateDtoBuilder.getMatcherDtoBuilders().add(new TemplateMatcherTemplateValueDtoBuilder(rs.getString("matcher_key"), rs.getString("matcherValue_templateName"), rs.getLong("matcherValue_templateVersion")));
        }
    }




    @AllArgsConstructor
    @Getter
    private static class TemplateDtoBuilder {
        private final UUID uuid;
        private final String name;
        private final Long version;
        private final String description;
        private final Set<TemplateMatcherDtoBuilder> matcherDtoBuilders;

        public TemplateDto build() {
            return new TemplateDto(
                    uuid,
                    name,
                    version,
                    description,
                    matcherDtoBuilders.stream()
                            .map(TemplateMatcherDtoBuilder::build)
                            .collect(Collectors.toSet())
            );
        }
    }

    @AllArgsConstructor
    @Getter
    private static abstract class TemplateMatcherDtoBuilder {
        private final String key;

        public abstract MatcherDto build();
    }

    @Getter
    private static class TemplateMatcherAnyValueDtoBuilder extends TemplateMatcherDtoBuilder {
        public TemplateMatcherAnyValueDtoBuilder(String key) {
            super(key);
        }

        @Override
        public MatcherDto build() {
            return new MatcherDto(getKey(), new MatcherAnyDto());
        }
    }

    @Getter
    private static class TemplateMatcherFixedValueDtoBuilder extends TemplateMatcherDtoBuilder {
        private final String value;

        public TemplateMatcherFixedValueDtoBuilder(String key, String value) {
            super(key);
            this.value = value;
        }

        @Override
        public MatcherDto build() {
            return new MatcherDto(getKey(), new MatcherFixedDto(getValue()));
        }
    }

    @Getter
    private static class TemplateMatcherTemplateValueDtoBuilder extends TemplateMatcherDtoBuilder {
        private final String templateName;
        private final Long templateVersion;

        public TemplateMatcherTemplateValueDtoBuilder(String key, String templateName, Long templateVersion) {
            super(key);
            this.templateName = templateName;
            this.templateVersion = templateVersion;
        }

        @Override
        public MatcherDto build() {
            return new MatcherDto(getKey(), new MatcherTemplateDto(getTemplateName(), getTemplateVersion()));
        }
    }


}
