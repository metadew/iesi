package io.metadew.iesi.metadata.configuration.template.matcher.value;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Component
public class MatcherValueConfiguration extends Configuration<MatcherValue, MatcherValueKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;

    private String insertMatcherValueQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " (ID, MATCHER_ID) VALUES ({0}, {1});";
    }

    private String insertMatcherAnyValueQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("AnyMatcherValues").getName() + " (ID) VALUES ({0});";
    }

    private String insertMatcherFixedValueQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("FixedMatcherValues").getName() + " (ID, \"VALUE\") VALUES ({0}, {1});";
    }

    private String insertMatcherTemplateValueQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " (ID, TEMPLATE_NAME, TEMPLATE_VERSION) VALUES ({0}, {1}, {2});";
    }

    private String deleteMatcherValuesByTemplateQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " " +
                "WHERE id in (select matcher_value.id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Templates").getName() + " template " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
                "WHERE template.id={0});";
    }

    private String deleteMatcherAnyValuesByTemplateQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("AnyMatcherValues").getName() + " where id in (select any_matcher_value.id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Templates").getName() + " template " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " matcherValue on matcher.id=matcherValue.matcher_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcherValue.id=any_matcher_value.id " +
                "WHERE template.id={0});";
    }

    private String deleteMatcherFixedValuesByTemplateQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("FixedMatcherValues").getName() + " where id in (select fixed_matcher_value.id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Templates").getName() + " template " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " matcherValue on matcher.id=matcherValue.matcher_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcherValue.id=fixed_matcher_value.id " +
                "WHERE template.id={0});";
    }

    private String deleteMatcherTemplateValuesByTemplateQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " where id in (select template_matcher_value.id " +
                "FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Templates").getName() + " template " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("MatcherValues").getName() + " matcherValue on matcher.id=matcherValue.matcher_id " +
                "INNER JOIN " + metadataTablesConfiguration.getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " template_matcher_value on matcherValue.id=template_matcher_value.id " +
                "WHERE template.id={0});";
    }

    public MatcherValueConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDesignMetadataRepository());
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
                    MessageFormat.format(insertMatcherAnyValueQuery(),
                            SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId())));
        } else if (matcherValue instanceof MatcherFixedValue) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherFixedValueQuery(),
                            SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.getStringForSQLClob(((MatcherFixedValue) matcherValue).getValue(),
                                    getMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                                            .findFirst()
                                            .orElseThrow(RuntimeException::new))));
        } else if (matcherValue instanceof MatcherTemplate) {
            getMetadataRepository().executeUpdate(
                    MessageFormat.format(insertMatcherTemplateValueQuery(),
                            SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                            SQLTools.getStringForSQL(((MatcherTemplate) matcherValue).getTemplateName()),
                            SQLTools.getStringForSQL(((MatcherTemplate) matcherValue).getTemplateVersion())));
        } else {
            throw new RuntimeException("Cannot insert MatcherValue of type " + matcherValue.getClass().getSimpleName());
        }
        getMetadataRepository().executeUpdate(MessageFormat.format(insertMatcherValueQuery(),
                SQLTools.getStringForSQL(matcherValue.getMetadataKey().getId()),
                SQLTools.getStringForSQL(matcherValue.getMatcherKey().getId())));
    }

    public void deleteByTemplateId(TemplateKey templateKey) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherAnyValuesByTemplateQuery(),
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherFixedValuesByTemplateQuery(),
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherTemplateValuesByTemplateQuery(),
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatcherValuesByTemplateQuery(),
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
    }
}
