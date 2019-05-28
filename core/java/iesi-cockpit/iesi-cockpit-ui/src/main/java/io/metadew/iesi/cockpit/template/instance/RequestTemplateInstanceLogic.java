package io.metadew.iesi.cockpit.template.instance;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;

import java.io.Serializable;

public class RequestTemplateInstanceLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private RequestTemplateInstanceView view;

    public RequestTemplateInstanceLogic(RequestTemplateInstanceView requestTemplateInstanceView) {
        view = requestTemplateInstanceView;
    }

    public void init() {
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewRequestTemplateInstanceEnabled(false);
        }
    }

    public void cancelRequestTemplateInstance() {
        setFragmentParameter("");
    }

    private void setFragmentParameter(String requestTemplateInstanceName) {
        String fragmentParameter;
        if (requestTemplateInstanceName == null || requestTemplateInstanceName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = requestTemplateInstanceName;
        }

        UI.getCurrent().navigate(RequestTemplateInstanceView.class, fragmentParameter);
    }

}
