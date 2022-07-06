package io.metadew.iesi.metadata.service.template;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.script.execution.ExecutionRuntime;

import java.util.List;
import java.util.Optional;

public interface ITemplateService {

    List<Template> getAll();

    boolean exists(TemplateKey templateKey);

    boolean exists(String templateName, Long version);

    void insert(Template template);

    Optional<Template> get(TemplateKey templateKey);

    Optional<Template> get(String templatename, long version);

    void update(Template template);

    void delete(TemplateKey templateKey);

    void delete(String name, long version);

    boolean matches(DataType dataType, Template template, ExecutionRuntime executionRuntime);

}
