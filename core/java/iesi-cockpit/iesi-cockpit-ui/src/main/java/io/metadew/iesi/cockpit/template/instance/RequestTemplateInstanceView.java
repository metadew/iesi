package io.metadew.iesi.cockpit.template.instance;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import io.metadew.iesi.cockpit.MainLayout;

@Route(value = "templateinstance", layout = MainLayout.class)
public class RequestTemplateInstanceView extends HorizontalLayout
        implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "templateinstance";
    private RequestTemplateInstanceForm form;
    private TextField filter;

    private RequestTemplateInstanceLogic viewLogic = new RequestTemplateInstanceLogic(this);
    private Button newRequestTemplateInstance;


    public RequestTemplateInstanceView() {
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        form = new RequestTemplateInstanceForm(viewLogic);

        VerticalLayout verticalLayout = new VerticalLayout();
        //HorizontalLayout barAndGridLayout = new HorizontalLayout();
        verticalLayout.add(topLayout);
        verticalLayout.add(form);
        verticalLayout.setFlexGrow(0, topLayout);
        verticalLayout.setSizeFull();
        verticalLayout.expand(form);

        add(verticalLayout);

        viewLogic.init();
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter name or description");
        // Apply the filter to grid's data provider. TextField value is never null
        //filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newRequestTemplateInstance = new Button("New RequestTemplateInstance");
        newRequestTemplateInstance.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newRequestTemplateInstance.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newRequestTemplateInstance.addClickListener(click -> Notification.show("ok"));
        // CTRL+N will create a new window which is unavoidable
        newRequestTemplateInstance.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newRequestTemplateInstance);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    public void showError(String msg) {
        Notification.show(msg);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    public void setNewRequestTemplateInstanceEnabled(boolean enabled) {
        newRequestTemplateInstance.setEnabled(enabled);
    }

    public void showForm(boolean show) {
        form.setVisible(show);

        /* FIXME The following line should be uncommented when the CheckboxGroup
         * issue is resolved. The category CheckboxGroup throws an
         * IllegalArgumentException when the form is disabled.
         */
        //form.setEnabled(show);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        //viewLogic.enter(parameter);
    }
}
