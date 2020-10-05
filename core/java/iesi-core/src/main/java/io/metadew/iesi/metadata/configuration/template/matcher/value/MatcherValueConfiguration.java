package io.metadew.iesi.metadata.configuration.template.matcher.value;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class MatcherValueConfiguration extends Configuration<MatcherValue, MatcherValueKey> {

    private static MatcherValueConfiguration INSTANCE;
    private static final String insertMatcherValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " (ID, MATCHER_ID) VALUES ({0}, {1});";
    private static final String insertMatcherAnyValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " (ID) VALUES (:id);";
    private static final String insertMatcherFixedValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " (ID, VALUE) VALUES (:id, :value);";
    private static final String insertMatcherTemplateValueQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " (ID, TEMPLATE_NAME, TEMPLATE_VERSION) VALUES (:id, :name, :version);";

    private static final String deleteMatcherValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " " +
            "WHERE id in (select matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "WHERE template.id= :id);";
    private static final String deleteMatcherAnyValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " where id in (select any_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher.id=any_matcher_value.id " +
            "WHERE template.id= :id);";
    private static final String deleteMatcherFixedValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " where id in (select fixed_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher.id=fixed_matcher_value.id " +
            "WHERE template.id= :id);";
    private static final String deleteMatcherTemplateValuesByTemplateQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " where id in (select template_matcher_value.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " template_matcher_value on matcher.id=template_matcher_value.id " +
            "WHERE template.id= :id);";

    public synchronized static MatcherValueConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherValueConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MatcherValueConfiguration() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(MetadataRepositoryConfiguration.getInstance()
                .getDesignMetadataRepository()
                .getRepositoryCoordinator()
                .getDatabases().values().stream()
                .findFirst()
                .map(Database::getConnectionPool)
                .orElseThrow(RuntimeException::new));
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
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("id", matcherValue.getMetadataKey().getId());
            namedParameterJdbcTemplate.update(
                    insertMatcherAnyValueQuery,
                    sqlParameterSource);
        } else if (matcherValue instanceof MatcherFixedValue) {

            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("id", matcherValue.getMetadataKey().getId())
                    .addValue("value", ((MatcherFixedValue) matcherValue).getValue());
            namedParameterJdbcTemplate.update(
                    insertMatcherFixedValueQuery,
                    sqlParameterSource);
        } else if (matcherValue instanceof MatcherTemplate) {

            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("id", matcherValue.getMetadataKey().getId())
                    .addValue("name", ((MatcherTemplate) matcherValue).getTemplateName())
                    .addValue("version", ((MatcherTemplate) matcherValue).getTemplateVersion());
            namedParameterJdbcTemplate.update(
                    insertMatcherTemplateValueQuery,
                    sqlParameterSource);
        } else {
            throw new RuntimeException("Cannot insert MatcherValue of type " + matcherValue.getClass().getSimpleName());
        }
        getMetadataRepository().executeUpdate(MessageFormat.format(insertMatcherValueQuery, SQLTools.GetStringForSQL(matcherValue.getMetadataKey().getId()),
                SQLTools.GetStringForSQL(matcherValue.getMatcherKey().getId())));
    }

    public void deleteByTemplateId(TemplateKey templateKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", templateKey.getId());
        namedParameterJdbcTemplate.update(
                deleteMatcherAnyValuesByTemplateQuery,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteMatcherFixedValuesByTemplateQuery,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteMatcherTemplateValuesByTemplateQuery,
                sqlParameterSource);
        namedParameterJdbcTemplate.update(
                deleteMatcherValuesByTemplateQuery,
                sqlParameterSource);
    }
}
