package io.metadew.iesi.cockpit.template.instance;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class RequestTemplateInstanceForm extends Div {

	private static final long serialVersionUID = 1L;

	private HorizontalLayout content;

    private TextField requestTemplateInstanceName;
    private TextField requestTemplateInstanceDescription;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private RequestTemplateInstanceLogic viewLogic;

    public RequestTemplateInstanceForm(RequestTemplateInstanceLogic RequestTemplateInstanceLogic) {
        setClassName("RequestTemplateInstance-form");

        content = new HorizontalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = RequestTemplateInstanceLogic;

        requestTemplateInstanceName = new TextField("Name");
        requestTemplateInstanceName.setWidth("100%");
        requestTemplateInstanceName.setRequired(true);
        requestTemplateInstanceName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(requestTemplateInstanceName);

        requestTemplateInstanceDescription = new TextField("Description");
        requestTemplateInstanceDescription.setWidth("100%");
        requestTemplateInstanceDescription.setRequired(false);
        requestTemplateInstanceDescription.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(requestTemplateInstanceDescription);

        save = new Button("Save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
                Notification.show("ok");
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(
                event -> Notification.show("ok"));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelRequestTemplateInstance());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> Notification.show("ok"))
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
        	Notification.show("ok");
        });

        content.add(save, discard, delete, cancel);
    }

    public void editRequestTemplateInstance(String requestTemplateInstance) {
        delete.setVisible(!requestTemplateInstance.isEmpty());
    }

}
