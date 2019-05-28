package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.TemplateDataService;
import io.metadew.iesi.metadata.definition.Template;

import java.io.Serializable;

public class TemplateLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private TemplateView view;

    public TemplateLogic(TemplateView templateView) {
        view = templateView;
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

        UI.getCurrent().navigate(TemplateView.class, fragmentParameter);
    }

    public void enter(String requestTemplateName) {
        if (requestTemplateName != null && !requestTemplateName.isEmpty()) {
            if (requestTemplateName.equals("new")) {
                newRequestTemplate();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    Template template = findRequestTemplate(requestTemplateName);
                    view.selectRow(template);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Template findRequestTemplate(String requestTemplateName) {
        return TemplateDataService.get().getRequestTemplateByName(requestTemplateName);
    }

    public void saveRequestTemplate(Template template) {
        boolean newRequestTemplate = template.isEmpty();
        view.clearSelection();
        view.updateRequestTemplate(template);
        setFragmentParameter("");
        view.showSaveNotification(template.getName()
                + (newRequestTemplate ? " created" : " updated"));
    }

    public void deleteRequestTemplate(Template template) {
        view.clearSelection();
        view.removeRequestTemplate(template);
        setFragmentParameter("");
        view.showSaveNotification(template.getName() + " deleted");
    }

    public void editRequestTemplate(Template template) {
        if (template == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(template.getName() + "");
        }
        view.editRequestTemplate(template);
    }

    public void newRequestTemplate() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editRequestTemplate(new Template());
    }

    public void rowSelected(Template template) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editRequestTemplate(template);
        	
        }
    }
}
