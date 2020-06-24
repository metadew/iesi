package io.metadew.iesi.metadata.configuration.template;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

@Log4j2
public class TemplateConfiguration extends Configuration<Template, TemplateKey> {

    private static final String fetchSingleQuery = "SELECT template.id as template_id, template.name as template_name, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id " +
            "WHERE template.id={0};";
    private static final String fetchByNameQuery = "SELECT template.id as template_id, template.name as template_name, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id " +
            "WHERE template.name={0};";
    private static final String fetchAllQuery = "SELECT template.id as template_id, template.name as template_name, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id ";

    private static final String deleteMatcherValuesByTemplateQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " " +
            "WHERE id in (select matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherAnyValuesByTemplateQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " where id in (select any_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher.id=any_matcher_value.id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherFixedValuesByTemplateQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " where id in (select fixed_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher.id=fixed_matcher_value.id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherTemplateValuesByTemplateQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " where id in (select template_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " template_matcher_value on matcher.id=template_matcher_value.id " +
            "WHERE template.id={0});";
    private static final String deleteMatchersByTemplateIdQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " where template_id={0};";
    private static final String deleteByTemplateIdQuery = "delete from " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " where id={0});";

    private static final String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " (ID, NAME) VALUES ({0}, {1});";
    private static final String insertMatcherQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " (ID, KEY, TEMPLATE_ID) VALUES ({0}, {1}, {2});";
    private static final String insertMatcherValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " (ID, MATCHER_ID) VALUES ({0}, {1});";
    private static final String insertMatcherAnyValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " (ID) VALUES ({0});";
    private static final String insertMatcherFixedValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " (ID, VALUE) VALUES ({0}, {1});";
    private static final String insertMatcherTemplateValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " (ID, TEMPLATE_NAME) VALUES ({0}, {1});";

    private static final String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " " +
            "SET NAME={0} WHERE ID={1};";

    private static TemplateConfiguration INSTANCE;

    public synchronized static TemplateConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateConfiguration();
        }
        return INSTANCE;
    }

    private TemplateConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<Template> get(TemplateKey metadataKey) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchSingleQuery,
                            SQLTools.GetStringForSQL(metadataKey.getId())
                    ),
                    "reader");
            Template template = null;
            while (cachedRowSet.next()) {
                if (template == null) {
                    template = mapRow(cachedRowSet);
                }
                addMapping(template, cachedRowSet);
            }
            return Optional.ofNullable(template);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Template> getByName(String name) {
        try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(
                    MessageFormat.format(fetchByNameQuery,
                            SQLTools.GetStringForSQL(name)
                    ),
                    "reader");
            Template template = null;
            while (cachedRowSet.next()) {
                if (template == null) {
                    template = mapRow(cachedRowSet);
                }
                addMapping(template, cachedRowSet);
            }
            return Optional.ofNullable(template);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Template> getAll() throws SQLException {
        List<Template> templates = new ArrayList<>();
        Map<UUID, Template> templateMap = new HashMap<>();
        //try {
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(fetchAllQuery, "reader");
            Template template;
            while (cachedRowSet.next()) {
                UUID uuid = UUID.fromString(cachedRowSet.getString("template_id"));
                template = templateMap.get(uuid);
                if (template == null) {
                    template = mapRow(cachedRowSet);
                    templateMap.put(uuid, template);
                    templates.add(template);
                }
                addMapping(template, cachedRowSet);
            }
            return templates;
        //} catch (SQLException e) {
        //    throw new RuntimeException(e);
        //}
    }

    @Override
    public void delete(TemplateKey metadataKey) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherAnyValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherFixedValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherTemplateValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatchersByTemplateIdQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteByTemplateIdQuery,
                        SQLTools.GetStringForSQL(metadataKey.getId())
                ));
    }

    @Override
    public void insert(Template template) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(insertQuery, SQLTools.GetStringForSQL(template.getMetadataKey().getId()),
                        SQLTools.GetStringForSQL(template.getName())));
        for (Matcher matcher : template.getMatchers()) {
            insert(matcher);
        }
    }

    public void insert(Matcher matcher) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(insertMatcherQuery, SQLTools.GetStringForSQL(matcher.getMetadataKey().getId()),
                        SQLTools.GetStringForSQL(matcher.getKey()), SQLTools.GetStringForSQL(matcher.getTemplateKey().getId())));
        MatcherValue matcherValue = matcher.getMatcherValue();
        if (matcherValue instanceof MatcherAnyValue) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherAnyValueQuery, SQLTools.GetStringForSQL(matcherValue.getMetadataKey().getId())));
        } else if (matcherValue instanceof MatcherFixedValue) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherFixedValueQuery, SQLTools.GetStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.GetStringForSQL(((MatcherFixedValue) matcherValue).getValue())));
        } else if (matcherValue instanceof MatcherTemplate) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherTemplateValueQuery, SQLTools.GetStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.GetStringForSQL(((MatcherTemplate) matcherValue).getTemplateName())));
        } else {
            throw new RuntimeException("");
        }
        getMetadataRepository().executeUpdate(MessageFormat.format(insertMatcherValueQuery, SQLTools.GetStringForSQL(matcherValue.getMetadataKey().getId()),
                SQLTools.GetStringForSQL(matcherValue.getMatcherKey().getId())));
    }

    public void update(Template template) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(updateQuery, SQLTools.GetStringForSQL(template.getName()),
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherAnyValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherFixedValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherTemplateValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherValuesByTemplateQuery,
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatchersByTemplateIdQuery,
                        SQLTools.GetStringForSQL(template.getMetadataKey().getId())
                ));
        for (Matcher matcher : template.getMatchers()) {
            insert(matcher);
        }
    }

    private Template mapRow(CachedRowSet cachedRowSet) throws SQLException {
        return Template.builder()
                .metadataKey(
                        TemplateKey.builder()
                                .id(UUID.fromString(cachedRowSet.getString("template_id")))
                                .build())
                .name(cachedRowSet.getString("template_name"))
                .matchers(new ArrayList<>())
                .build();
    }

    private void addMapping(Template template, CachedRowSet cachedRowSet) throws SQLException {
        MatcherKey matcherKey = MatcherKey.builder()
                .id(UUID.fromString(cachedRowSet.getString("matcher_id")))
                .build();
        MatcherValue matcherValue;
        if (cachedRowSet.getString("any_matcher_value_id") != null) {
            matcherValue = MatcherAnyValue.builder()
                    .metadataKey(MatcherValueKey.builder()
                            .id(UUID.fromString(cachedRowSet.getString("any_matcher_value_id")))
                            .build())
                    .matcherKey(matcherKey)
                    .build();
        } else if (cachedRowSet.getString("fixed_matcher_value_id") != null) {
            matcherValue = MatcherFixedValue.builder()
                    .metadataKey(MatcherValueKey.builder()
                            .id(UUID.fromString(cachedRowSet.getString("fixed_matcher_value_id")))
                            .build())
                    .value(cachedRowSet.getString("fixed_matcher_value_value"))
                    .matcherKey(matcherKey)
                    .build();
        } else if (cachedRowSet.getString("templ_matcher_value_id") != null) {
            matcherValue = MatcherTemplate.builder()
                    .metadataKey(MatcherValueKey.builder()
                            .id(UUID.fromString(cachedRowSet.getString("templ_matcher_value_id")))
                            .build())
                    .templateName(cachedRowSet.getString("templ_matcher_value_templ_name"))
                    .matcherKey(matcherKey)
                    .build();
        } else {
            throw new RuntimeException("");
        }

        Matcher matcher = Matcher.builder()
                .matcherKey(matcherKey)
                .key(cachedRowSet.getString("matcher_key"))
                .templateKey(template.getMetadataKey())
                .matcherValue(matcherValue)
                .build();
        template.addMatcher(matcher);
    }

}
