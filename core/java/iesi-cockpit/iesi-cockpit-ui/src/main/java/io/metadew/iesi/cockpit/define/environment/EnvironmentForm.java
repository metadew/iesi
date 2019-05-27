package io.metadew.iesi.cockpit.define.environment;

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

import io.metadew.iesi.metadata.definition.Environment;

public class EnvironmentForm extends Div {

	private static final long serialVersionUID = 1L;

	private VerticalLayout content;

    private TextField environmentName;
    private TextField environmentDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private EnvironmentLogic viewLogic;
    private Binder<Environment> binder;
    private Environment currentEnvironment;

    public EnvironmentForm(EnvironmentLogic EnvironmentLogic) {
        setClassName("Environment-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = EnvironmentLogic;

        environmentName = new TextField("Name");
        environmentName.setWidth("100%");
        environmentName.setRequired(true);
        environmentName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(environmentName);

        environmentDescription = new TextField("Description");
        environmentDescription.setWidth("100%");
        environmentDescription.setRequired(false);
        environmentDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(environmentDescription);

        binder = new BeanValidationBinder<>(Environment.class);
        binder.forField(environmentName).bind("name");
        binder.forField(environmentDescription).bind("description");
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
            if (currentEnvironment != null
                    && binder.writeBeanIfValid(currentEnvironment)) {
                viewLogic.saveEnvironment(currentEnvironment);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> viewLogic.editEnvironment(currentEnvironment));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelEnvironment());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelEnvironment())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentEnvironment != null) {
                viewLogic.deleteEnvironment(currentEnvironment);
            }
        });

        content.add(save, discard, delete, cancel);
    }

    public void editEnvironment(Environment environment) {
        if (environment == null) {
        	environment = new Environment();
        }
        delete.setVisible(!environment.isEmpty());
        currentEnvironment = environment;
        binder.readBean(environment);
    }

}
