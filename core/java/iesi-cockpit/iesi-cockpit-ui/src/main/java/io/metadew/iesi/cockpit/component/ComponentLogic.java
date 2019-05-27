package io.metadew.iesi.cockpit.component;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.ComponentDataService;
import io.metadew.iesi.metadata.definition.Component;

import java.io.Serializable;

public class ComponentLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private ComponentView view;

    public ComponentLogic(ComponentView componentView) {
        view = componentView;
    }

    public void init() {
        editComponent(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewComponentEnabled(false);
        }
    }

    public void cancelComponent() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String componentName) {
        String fragmentParameter;
        if (componentName == null || componentName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = componentName;
        }

        UI.getCurrent().navigate(ComponentView.class, fragmentParameter);
    }

    public void enter(String componentName) {
        if (componentName != null && !componentName.isEmpty()) {
            if (componentName.equals("new")) {
                newComponent();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    Component component = findComponent(componentName);
                    view.selectRow(component);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Component findComponent(String componentName) {
        return ComponentDataService.get().getComponentByName(componentName);
    }

    public void saveComponent(Component component) {
        boolean newComponent = component.isEmpty();
        view.clearSelection();
        view.updateComponent(component);
        setFragmentParameter("");
        view.showSaveNotification(component.getName()
                + (newComponent ? " created" : " updated"));
    }

    public void deleteComponent(Component component) {
        view.clearSelection();
        view.removeComponent(component);
        setFragmentParameter("");
        view.showSaveNotification(component.getName() + " deleted");
    }

    public void editComponent(Component component) {
        if (component == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(component.getName() + "");
        }
        view.editComponent(component);
    }

    public void newComponent() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editComponent(new Component());
    }

    public void rowSelected(Component component) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editComponent(component);
        }
    }
}
