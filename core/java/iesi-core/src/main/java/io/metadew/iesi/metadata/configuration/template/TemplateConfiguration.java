package io.metadew.iesi.metadata.configuration.template;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;

import java.util.List;
import java.util.Optional;

public class TemplateConfiguration extends Configuration<Template, TemplateKey> {

    private static TemplateConfiguration INSTANCE;

    public synchronized static TemplateConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateConfiguration();
        }
        return INSTANCE;
    }

    private TemplateConfiguration() {
    }

    @Override
    public Optional<Template> get(TemplateKey metadataKey) {
        return Optional.empty();
    }

    @Override
    public List<Template> getAll() {
        return null;
    }

    @Override
    public void delete(TemplateKey metadataKey) {

    }

    @Override
    public void insert(Template metadata) {

    }
}
