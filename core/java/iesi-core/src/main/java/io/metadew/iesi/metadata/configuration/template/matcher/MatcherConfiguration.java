package io.metadew.iesi.metadata.configuration.template.matcher;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.template.matcher.value.MatcherValueConfiguration;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Optional;

public class MatcherConfiguration extends Configuration<Matcher, MatcherKey> {

    private static MatcherConfiguration INSTANCE;

    private static final String insertMatcherQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " (ID, KEY, TEMPLATE_ID) VALUES (:id, :key, :template_id);";

    private static final String deleteMatchersByTemplateIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " where template_id= :id;";

    public synchronized static MatcherConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherConfiguration();
        }
        return INSTANCE;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MatcherConfiguration() {
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
    public Optional<Matcher> get(MatcherKey metadataKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Matcher> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(MatcherKey metadataKey) {
        throw new UnsupportedOperationException();
    }

    public void deleteByTemplateId(TemplateKey templateKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", templateKey.getId());
        namedParameterJdbcTemplate.update(
                deleteMatchersByTemplateIdQuery,
                sqlParameterSource);
        MatcherValueConfiguration.getInstance().deleteByTemplateId(templateKey);
    }


    public void insert(Matcher matcher) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", matcher.getMetadataKey().getId())
                .addValue("key", matcher.getKey())
                .addValue("template_id", matcher.getTemplateKey().getId());
        namedParameterJdbcTemplate.update(
                insertMatcherQuery,
                sqlParameterSource);
        MatcherValue matcherValue = matcher.getMatcherValue();
        MatcherValueConfiguration.getInstance().insert(matcherValue);
    }
}
