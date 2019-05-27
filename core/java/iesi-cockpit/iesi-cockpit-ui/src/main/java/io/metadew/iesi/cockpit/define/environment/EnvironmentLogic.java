package io.metadew.iesi.cockpit.define.environment;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.EnvironmentDataService;
import io.metadew.iesi.metadata.definition.Environment;

import java.io.Serializable;

public class EnvironmentLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private EnvironmentView view;

    public EnvironmentLogic(EnvironmentView environmentView) {
        view = environmentView;
    }

    public void init() {
        editEnvironment(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewEnvironmentEnabled(false);
        }
    }

    public void cancelEnvironment() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String environmentName) {
        String fragmentParameter;
        if (environmentName == null || environmentName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = environmentName;
        }

        UI.getCurrent().navigate(EnvironmentView.class, fragmentParameter);
    }

    public void enter(String environmentName) {
        if (environmentName != null && !environmentName.isEmpty()) {
            if (environmentName.equals("new")) {
                newEnvironment();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    Environment environment = findEnvironment(environmentName);
                    view.selectRow(environment);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Environment findEnvironment(String environmentName) {
        return EnvironmentDataService.get().getEnvironmentByName(environmentName);
    }

    public void saveEnvironment(Environment environment) {
        boolean newEnvironment = environment.isEmpty();
        view.clearSelection();
        view.updateEnvironment(environment);
        setFragmentParameter("");
        view.showSaveNotification(environment.getName()
                + (newEnvironment ? " created" : " updated"));
    }

    public void deleteEnvironment(Environment environment) {
        view.clearSelection();
        view.removeEnvironment(environment);
        setFragmentParameter("");
        view.showSaveNotification(environment.getName() + " deleted");
    }

    public void editEnvironment(Environment environment) {
        if (environment == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(environment.getName() + "");
        }
        view.editEnvironment(environment);
    }

    public void newEnvironment() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editEnvironment(new Environment());
    }

    public void rowSelected(Environment environment) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editEnvironment(environment);
        }
    }
}
