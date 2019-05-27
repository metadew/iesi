package io.metadew.iesi.cockpit.component;

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

import io.metadew.iesi.metadata.definition.Component;

public class ComponentForm extends Div {

	private static final long serialVersionUID = 1L;

	private VerticalLayout content;

    private TextField componentName;
    private TextField componentDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private ComponentLogic viewLogic;
    private Binder<Component> binder;
    private Component currentComponent;

    public ComponentForm(ComponentLogic ComponentLogic) {
        setClassName("Component-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = ComponentLogic;

        componentName = new TextField("Name");
        componentName.setWidth("100%");
        componentName.setRequired(true);
        componentName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(componentName);

        componentDescription = new TextField("Description");
        componentDescription.setWidth("100%");
        componentDescription.setRequired(false);
        componentDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(componentDescription);

        binder = new BeanValidationBinder<>(Component.class);
        binder.forField(componentName).bind("name");
        binder.forField(componentDescription).bind("description");
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
            if (currentComponent != null
                    && binder.writeBeanIfValid(currentComponent)) {
                viewLogic.saveComponent(currentComponent);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> viewLogic.editComponent(currentComponent));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelComponent());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelComponent())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentComponent != null) {
                viewLogic.deleteComponent(currentComponent);
            }
        });

        content.add(save, discard, delete, cancel);
    }

    public void editComponent(Component component) {
        if (component == null) {
        	component = new Component();
        }
        delete.setVisible(!component.isEmpty());
        currentComponent = component;
        binder.readBean(component);
    }

}
