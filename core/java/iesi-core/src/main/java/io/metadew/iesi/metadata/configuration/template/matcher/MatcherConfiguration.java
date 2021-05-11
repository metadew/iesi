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

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

public class MatcherConfiguration extends Configuration<Matcher, MatcherKey> {

    private static MatcherConfiguration INSTANCE;

    private static final String insertMatcherQuery = "INSERT INTO " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " (ID, KEY, TEMPLATE_ID) VALUES ({0}, {1}, {2});";

    private static final String deleteMatchersByTemplateIdQuery = "DELETE FROM " + MetadataTablesConfiguration.getInstance().getMetadataTableNameByLabel("Matchers").getName() + " where template_id={0};";

    public synchronized static MatcherConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MatcherConfiguration();
        }
        return INSTANCE;
    }

    private MatcherConfiguration() {
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
        getMetadataRepository().executeUpdate(
                MessageFormat.format(deleteMatchersByTemplateIdQuery,
                        SQLTools.getStringForSQL(templateKey.getId())
                ));
        MatcherValueConfiguration.getInstance().deleteByTemplateId(templateKey);
    }


    public void insert(Matcher matcher) {
        getMetadataRepository().executeUpdate(
                MessageFormat.format(insertMatcherQuery,
                        SQLTools.getStringForSQL(matcher.getMetadataKey().getId()),
                        SQLTools.getStringForSQL(matcher.getKey()),
                        SQLTools.getStringForSQL(matcher.getTemplateKey().getId())));
        MatcherValue matcherValue = matcher.getMatcherValue();
        MatcherValueConfiguration.getInstance().insert(matcherValue);
    }
}
