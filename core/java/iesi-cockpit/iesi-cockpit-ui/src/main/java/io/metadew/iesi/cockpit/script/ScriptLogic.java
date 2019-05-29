package io.metadew.iesi.cockpit.script;

import com.vaadin.flow.component.UI;
import io.metadew.iesi.cockpit.authentication.AccessControl;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.backend.ScriptDataService;
import io.metadew.iesi.metadata.definition.Script;

import java.io.Serializable;

public class ScriptLogic implements Serializable {

	private static final long serialVersionUID = 1L;
	private ScriptView view;

    public ScriptLogic(ScriptView scriptView) {
        view = scriptView;
    }

    public void init() {
        editScript(null);
        // Hide and disable if not admin
        if (!AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewScriptEnabled(false);
        }
    }

    public void cancelScript() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String scriptName) {
        String fragmentParameter;
        if (scriptName == null || scriptName.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = scriptName;
        }

        UI.getCurrent().navigate(ScriptView.class, fragmentParameter);
    }

    public void enter(String scriptName) {
        if (scriptName != null && !scriptName.isEmpty()) {
            if (scriptName.equals("new")) {
                newScript();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    Script script = findScript(scriptName);
                    view.selectRow(script);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Script findScript(String scriptName) {
        return ScriptDataService.get().getScriptByName(scriptName);
    }

    public void saveScript(Script script) {
        boolean newScript = script.isEmpty();
        view.clearSelection();
        view.updateScript(script);
        setFragmentParameter("");
        view.showSaveNotification(script.getName()
                + (newScript ? " created" : " updated"));
    }

    public void deleteScript(Script script) {
        view.clearSelection();
        view.removeScript(script);
        setFragmentParameter("");
        view.showSaveNotification(script.getName() + " deleted");
    }

    public void editScript(Script script) {
        if (script == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(script.getName() + "");
        }
        view.editScript(script);
    }

    public void newScript() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editScript(new Script());
    }

    public void rowSelected(Script script) {
        if (AccessControlFactory.getInstance().createAccessControl()
                .isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editScript(script);
        }
    }
}
