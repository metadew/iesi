package io.metadew.iesi.metadata.configuration.template;

import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TemplateListResultSetExtractor implements ResultSetExtractor<List<Template>> {

    @Override
    public List<Template> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, Template> templateMap = new HashMap<>();
        Template template;
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("template_id"));
            template = templateMap.get(uuid);
            if (template == null) {
                template = mapRow(rs);
                templateMap.put(uuid, template);
            }
            addMapping(template, rs);
        }
        return new ArrayList<>(templateMap.values());
    }


    private Template mapRow(ResultSet rs) throws SQLException {
        return Template.builder()
                .metadataKey(
                        TemplateKey.builder()
                                .id(UUID.fromString(rs.getString("template_id")))
                                .build())
                .name(rs.getString("template_name"))
                .version(Long.parseLong(rs.getString("template_version")))
                .description(rs.getString("template_description"))
                .matchers(new ArrayList<>())
                .build();
    }

    private void addMapping(Template template, ResultSet rs) throws SQLException {
        MatcherKey matcherKey = MatcherKey.builder()
                .id(UUID.fromString(rs.getString("matcher_id")))
                .build();
        MatcherValue matcherValue;
        if (rs.getString("any_matcher_value_id") != null) {
            matcherValue = MatcherAnyValue.builder()
                    .matcherValueKey(MatcherValueKey.builder()
                            .id(UUID.fromString(rs.getString("any_matcher_value_id")))
                            .build())
                    .matcherKey(matcherKey)
                    .build();
        } else if (rs.getString("fixed_matcher_value_id") != null) {
            matcherValue = MatcherFixedValue.builder()
                    .metadataKey(MatcherValueKey.builder()
                            .id(UUID.fromString(rs.getString("fixed_matcher_value_id")))
                            .build())
                    .value(rs.getString("fixed_matcher_value_value"))
                    .matcherKey(matcherKey)
                    .build();
        } else if (rs.getString("templ_matcher_value_id") != null) {
            matcherValue = MatcherTemplate.builder()
                    .metadataKey(MatcherValueKey.builder()
                            .id(UUID.fromString(rs.getString("templ_matcher_value_id")))
                            .build())
                    .templateName(rs.getString("templ_matcher_value_templ_name"))
                    .templateVersion(rs.getLong("templ_matcher_value_templ_vrs"))
                    .matcherKey(matcherKey)
                    .build();
        } else {
            throw new RuntimeException("Matcher " + matcherKey.toString() + " not of a known type");
        }

        Matcher matcher = Matcher.builder()
                .matcherKey(matcherKey)
                .key(rs.getString("matcher_key"))
                .templateKey(template.getMetadataKey())
                .matcherValue(matcherValue)
                .build();
        template.addMatcher(matcher);
    }
}
