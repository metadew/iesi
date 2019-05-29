package io.metadew.iesi.cockpit.script;

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

import io.metadew.iesi.metadata.definition.Script;

public class ScriptForm extends Div {

	private static final long serialVersionUID = 1L;

	private VerticalLayout content;

    private TextField scriptName;
    private TextField scriptDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private ScriptLogic viewLogic;
    private Binder<Script> binder;
    private Script currentScript;

    public ScriptForm(ScriptLogic ScriptLogic) {
        setClassName("Script-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = ScriptLogic;

        scriptName = new TextField("Name");
        scriptName.setWidth("100%");
        scriptName.setRequired(true);
        scriptName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(scriptName);

        scriptDescription = new TextField("Description");
        scriptDescription.setWidth("100%");
        scriptDescription.setRequired(false);
        scriptDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(scriptDescription);

        binder = new BeanValidationBinder<>(Script.class);
        binder.forField(scriptName).bind("name");
        binder.forField(scriptDescription).bind("description");
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
            if (currentScript != null
                    && binder.writeBeanIfValid(currentScript)) {
                viewLogic.saveScript(currentScript);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> viewLogic.editScript(currentScript));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelScript());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelScript())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentScript != null) {
                viewLogic.deleteScript(currentScript);
            }
        });

        content.add(save, discard, delete, cancel);
    }

    public void editScript(Script script) {
        if (script == null) {
        	script = new Script();
        }
        delete.setVisible(!script.isEmpty());
        currentScript = script;
        binder.readBean(script);
    }

}
