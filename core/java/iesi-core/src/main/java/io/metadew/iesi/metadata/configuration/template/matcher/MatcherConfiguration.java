package io.metadew.iesi.metadata.configuration.template.matcher;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.template.matcher.value.MatcherValueConfiguration;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Component
public class MatcherConfiguration extends Configuration<Matcher, MatcherKey> {

    private final MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    private final MetadataTablesConfiguration metadataTablesConfiguration;
    private final MatcherValueConfiguration matcherValueConfiguration;

    private String insertMatcherQuery() {
        return "INSERT INTO " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " (ID, \"KEY\", TEMPLATE_ID) VALUES ({0}, {1}, {2});";
    }

    private String deleteMatchersByTemplateIdQuery() {
        return "DELETE FROM " + metadataTablesConfiguration.getMetadataTableNameByLabel("Matchers").getName() + " where template_id={0};";
    }


    public MatcherConfiguration(MetadataRepositoryConfiguration metadataRepositoryConfiguration, MetadataTablesConfiguration metadataTablesConfiguration, MatcherValueConfiguration matcherValueConfiguration) {
        this.metadataRepositoryConfiguration = metadataRepositoryConfiguration;
        this.metadataTablesConfiguration = metadataTablesConfiguration;
        this.matcherValueConfiguration = matcherValueConfiguration;
    }


    @PostConstruct
    private void postConstruct() {
        setMetadataRepository(metadataRepositoryConfiguration.getDesignMetadataRepository());
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
        matcherValueConfiguration.deleteByTemplateId(templateKey);
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatchersByTemplateIdQuery(),
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
    }


    public void insert(Matcher matcher) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(insertMatcherQuery(),
                        SQLTools.getStringForSQL(matcher.getMetadataKey().getId()),
                        SQLTools.getStringForSQL(matcher.getKey()),
                        SQLTools.getStringForSQL(matcher.getTemplateKey().getId())));
        MatcherValue matcherValue = matcher.getMatcherValue();
        matcherValueConfiguration.insert(matcherValue);
    }
}
