package io.metadew.iesi.metadata.configuration.template;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.template.matcher.MatcherConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Optional;


@Log4j2
public class TemplateConfiguration extends Configuration<Template, TemplateKey> {

    private static final String fetchSingleQuery = "SELECT template.id as template_id, template.name as template_name, template.version as template_version, template.description as template_description, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name, templ_matcher_value.template_version as templ_matcher_value_templ_vrs " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id " +
            "WHERE template.id= :id;";
    private static final String fetchByNameAndVersionQuery = "SELECT template.id as template_id, template.name as template_name, template.version as template_version, template.description as template_description, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name, templ_matcher_value.template_version as templ_matcher_value_templ_vrs " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id " +
            "WHERE template.name= :name and template.version= :version;";
    private static final String fetchAllQuery = "SELECT template.id as template_id, template.name as template_name, template.version as template_version, template.description as template_description, matcher.id as matcher_id, " +
            "matcher.key as matcher_key, matcher_value.id as matcher_value_id, any_matcher_value.id as any_matcher_value_id, " +
            "fixed_matcher_value.id as fixed_matcher_value_id, fixed_matcher_value.value as fixed_matcher_value_value, " +
            "templ_matcher_value.id as templ_matcher_value_id, templ_matcher_value.template_name as templ_matcher_value_templ_name, templ_matcher_value.template_version as templ_matcher_value_templ_vrs " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " template " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " matcher on template.id=matcher.template_id " +
            "INNER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("MatcherValues").getName() + " matcher_value on matcher.id=matcher_value.matcher_id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("AnyMatcherValues").getName() + " any_matcher_value on matcher_value.id=any_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("FixedMatcherValues").getName() + " fixed_matcher_value on matcher_value.id=fixed_matcher_value.id " +
            "LEFT OUTER JOIN " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("TemplateMatcherValues").getName() + " templ_matcher_value on matcher_value.id=templ_matcher_value.id ";

    private static final String existsByNameQuery = "SELECT template.id " +
            "FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " " +
            "WHERE template.name= :name;";
    private static final String deleteByTemplateIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " where id= :id;";

    private static final String insertQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " (ID, NAME, VERSION, DESCRIPTION) VALUES (:id, :name, :version, :description);";

    private static final String updateQuery = "UPDATE " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Templates").getName() + " " +
            "SET NAME= :name, VERSION= :version, DESCRIPTION= :description WHERE ID= :id;";

    private static TemplateConfiguration INSTANCE;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public synchronized static TemplateConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateConfiguration();
        }
        return INSTANCE;
    }

    private TemplateConfiguration() {
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
    public Optional<Template> get(TemplateKey metadataKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", metadataKey.getId());
        return Optional.ofNullable(
                DataAccessUtils.singleResult(
                        namedParameterJdbcTemplate.query(
                                fetchSingleQuery,
                                sqlParameterSource,
                                new TemplateListResultSetExtractor())));
    }

    public boolean exists(String name) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", name);
        List<Template> templates = namedParameterJdbcTemplate.query(
                existsByNameQuery, sqlParameterSource, new TemplateListResultSetExtractor());
        return templates.size() >= 1;
    }

    public Optional<Template> getByNameAndVersion(String name, Long version) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("version", version);
        return Optional.ofNullable(
                DataAccessUtils.singleResult(
                        namedParameterJdbcTemplate.query(
                                fetchByNameAndVersionQuery,
                                sqlParameterSource,
                                new TemplateListResultSetExtractor())));
    }

    @Override
    public List<Template> getAll() {
        return namedParameterJdbcTemplate.query(fetchAllQuery, new TemplateListResultSetExtractor());
    }

    @Override
    public void delete(TemplateKey templateKey) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", templateKey.getId());
        MatcherConfiguration.getInstance().deleteByTemplateId(templateKey);
        namedParameterJdbcTemplate.update(
                deleteByTemplateIdQuery,
                sqlParameterSource);
    }

    public void deleteByNameAndVersion(String name, long version) {
        getByNameAndVersion(name, version).ifPresent(
                template -> {
                    SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", template.getMetadataKey().getId());
                    MatcherConfiguration.getInstance().deleteByTemplateId(template.getMetadataKey());
                    namedParameterJdbcTemplate.update(
                            deleteByTemplateIdQuery,
                            sqlParameterSource
                    );
                }
        );
    }

    @Override
    public void insert(Template template) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", template.getMetadataKey().getId())
                .addValue("name", template.getName())
                .addValue("version", template.getVersion())
                .addValue("description", template.getDescription());
        namedParameterJdbcTemplate.update(
                insertQuery,
                sqlParameterSource);
        for (Matcher matcher : template.getMatchers()) {
            MatcherConfiguration.getInstance().insert(matcher);
        }
    }

    public void update(Template template) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", template.getName())
                .addValue("version", template.getVersion())
                .addValue("description", template.getDescription())
                .addValue("id", template.getMetadataKey().getId());

        namedParameterJdbcTemplate.update(
                updateQuery,
                sqlParameterSource);

        MatcherConfiguration.getInstance().deleteByTemplateId(template.getMetadataKey());
        for (Matcher matcher : template.getMatchers()) {
            MatcherConfiguration.getInstance().insert(matcher);
        }
    }
}
