package io.metadew.iesi.metadata.service.template;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.List;
import java.util.Optional;

public interface ITemplateService {

    public List<Template> getAll();

    public boolean exists(TemplateKey templateKey);

    public boolean exists(String templateName);

    public void addUser(Template template);

    public Optional<Template> get(TemplateKey templateKey);

    public Optional<Template> get(String templatename, long version);

    public void update(Template template);

    public void delete(TemplateKey templateKey);

    public void delete(String name, long version);

    public boolean matches(DataType dataType, Template template, ExecutionRuntime executionRuntime);

}
