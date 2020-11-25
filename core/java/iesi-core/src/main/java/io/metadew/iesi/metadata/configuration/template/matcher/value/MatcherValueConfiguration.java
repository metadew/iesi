package io.metadew.iesi.metadata.configuration.template.matcher.value;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class MatcherValueConfiguration extends Configuration<MatcherValue, MatcherValueKey> {

    private static MatcherValueConfiguration INSTANCE;
    private static final String insertMatcherValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " (ID, MATCHER_ID) VALUES ({0}, {1});";
    private static final String insertMatcherAnyValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " (ID) VALUES ({0});";
    private static final String insertMatcherFixedValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " (ID, VALUE) VALUES ({0}, {1});";
    private static final String insertMatcherTemplateValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " (ID, TEMPLATE_NAME, TEMPLATE_VERSION) VALUES ({0}, {1}, {2});";

    private static final String deleteMatcherValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " " +
            "WHERE id in (select matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherAnyValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " where id in (select any_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher.id=any_matcher_value.id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherFixedValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " where id in (select fixed_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher.id=fixed_matcher_value.id " +
            "WHERE template.id={0});";
    private static final String deleteMatcherTemplateValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " where id in (select template_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " template_matcher_value on matcher.id=template_matcher_value.id " +
            "WHERE template.id={0});";

    public synchronized static MatcherValueConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherValueConfiguration();
        }
        return INSTANCE;
    }

    private MatcherValueConfiguration() {
        setMetadataRepository(MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository());
    }

    @Override
    public Optional<MatcherValue> get(MatcherValueKey metadataKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MatcherValue> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(MatcherValueKey metadataKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(MatcherValue matcherValue) {
        if (matcherValue instanceof MatcherAnyValue) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherAnyValueQuery, SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId())));
        } else if (matcherValue instanceof MatcherFixedValue) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherFixedValueQuery, SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.getStringForSQL(((MatcherFixedValue) matcherValue).getValue())));
        } else if (matcherValue instanceof MatcherTemplate) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherTemplateValueQuery,
                            SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.getStringForSQL(((MatcherTemplate) matcherValue).getTemplateName()),
                            SQLTools.getStringForSQL(((MatcherTemplate) matcherValue).getTemplateVersion())));
        } else {
            throw new RuntimeException("Cannot insert MatcherValue of type " + matcherValue.getClass().getSimpleName());
        }
        getMetadataRepository().executeUpdate(MessageFormat.format(insertMatcherValueQuery, SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                SQLTools.getStringForSQL(matcherValue.getMatcherKey().getId())));
    }

    public void deleteByTemplateId(TemplateKey templateKey) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherAnyValuesByTemplateQuery,
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherFixedValuesByTemplateQuery,
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherTemplateValuesByTemplateQuery,
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherValuesByTemplateQuery,
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
    }
}
