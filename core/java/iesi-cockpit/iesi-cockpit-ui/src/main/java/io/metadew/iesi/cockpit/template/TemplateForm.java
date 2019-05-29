package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import io.metadew.iesi.metadata.definition.Template;

public class TemplateForm extends Div {

	private static final long serialVersionUID = 1L;

	private VerticalLayout content;

    private TextField requestTemplateName;
    private TextField requestTemplateDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private TemplateLogic viewLogic;
    private Binder<Template> binder;
    private Template currentRequestTemplate;

    public TemplateForm(TemplateLogic TemplateLogic) {
        setClassName("Template-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = TemplateLogic;

        requestTemplateName = new TextField("Name");
        requestTemplateName.setWidth("100%");
        requestTemplateName.setRequired(true);
        requestTemplateName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(requestTemplateName);

        requestTemplateDescription = new TextField("Description");
        requestTemplateDescription.setWidth("100%");
        requestTemplateDescription.setRequired(false);
        requestTemplateDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(requestTemplateDescription);

        binder = new BeanValidationBinder<>(Template.class);
        binder.forField(requestTemplateName).bind("name");
        binder.forField(requestTemplateDescription).bind("description");
        binder.bindInstanceFields(this);

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
        });

        save = new Button("Save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentRequestTemplate != null
                    && binder.writeBeanIfValid(currentRequestTemplate)) {
                viewLogic.saveRequestTemplate(currentRequestTemplate);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> viewLogic.editRequestTemplate(currentRequestTemplate));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelRequestTemplate());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelRequestTemplate())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentRequestTemplate != null) {
                viewLogic.deleteRequestTemplate(currentRequestTemplate);
            }
        });

        content.add(save, discard, delete, cancel);
    }

    public void editRequestTemplate(Template template) {
        if (template == null) {
        	template = new Template();
        }
        delete.setVisible(!template.isEmpty());
        currentRequestTemplate = template;
        binder.readBean(template);
    }

}
