package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.RequestTemplateDataService;
import io.metadew.iesi.metadata.definition.RequestTemplate;

import java.io.Serializable;

public class RequestTemplateLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private RequestTemplateView view;

    public RequestTemplateLogic(RequestTemplateView requestTemplateView) {
        view = requestTemplateView;
    }

    public void init() {
        editRequestTemplate(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewRequestTemplateEnabled(false);
        }
    }

    public void cancelRequestTemplate() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String requestTemplateName) {
        String fragmentParameter;
        if (requestTemplateName == null || requestTemplateName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = requestTemplateName;
        }

        UI.getCurrent().navigate(RequestTemplateView.class, fragmentParameter);
    }

    public void enter(String requestTemplateName) {
        if (requestTemplateName != null && !requestTemplateName.isEmpty()) {
            if (requestTemplateName.equals("new")) {
                newRequestTemplate();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    RequestTemplate requestTemplate = findRequestTemplate(requestTemplateName);
                    view.selectRow(requestTemplate);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private RequestTemplate findRequestTemplate(String requestTemplateName) {
        return RequestTemplateDataService.get().getRequestTemplateByName(requestTemplateName);
    }

    public void saveRequestTemplate(RequestTemplate requestTemplate) {
        boolean newRequestTemplate = requestTemplate.isEmpty();
        view.clearSelection();
        view.updateRequestTemplate(requestTemplate);
        setFragmentParameter("");
        view.showSaveNotification(requestTemplate.getName()
                + (newRequestTemplate ? " created" : " updated"));
    }

    public void deleteRequestTemplate(RequestTemplate requestTemplate) {
        view.clearSelection();
        view.removeRequestTemplate(requestTemplate);
        setFragmentParameter("");
        view.showSaveNotification(requestTemplate.getName() + " deleted");
    }

    public void editRequestTemplate(RequestTemplate requestTemplate) {
        if (requestTemplate == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(requestTemplate.getName() + "");
        }
        view.editRequestTemplate(requestTemplate);
    }

    public void newRequestTemplate() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editRequestTemplate(new RequestTemplate());
    }

    public void rowSelected(RequestTemplate requestTemplate) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editRequestTemplate(requestTemplate);
        	
        }
    }
}
