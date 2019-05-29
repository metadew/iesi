package io.metadew.iesi.cockpit.script;

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
import io.metadew.iesi.metadata.definition.Script;

@Route(value = "scripts", layout = MainLayout.class)
public class ScriptView extends HorizontalLayout
        implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "scripts";
    private ScriptGrid grid;
    private ScriptForm form;
    private TextField filter;

    private ScriptLogic viewLogic = new ScriptLogic(this);
    private Button newScript;

    private ScriptDataProvider dataProvider = new ScriptDataProvider();

    public ScriptView() {
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new ScriptGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new ScriptForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPlaceholder("Filter name or description");
        // Apply the filter to grid's data provider. TextField value is never null
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newScript = new Button("New Script");
        newScript.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newScript.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newScript.addClickListener(click -> viewLogic.newScript());
        // CTRL+N will create a new window which is unavoidable
        newScript.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newScript);
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

    public void setNewScriptEnabled(boolean enabled) {
        newScript.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void selectRow(Script row) {
        grid.getSelectionModel().select(row);
    }

    public Script getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void updateScript(Script Script) {
        dataProvider.save(Script);
    }

    public void removeScript(Script Script) {
        dataProvider.delete(Script);
    }

    public void editScript(Script script) {
        showForm(script != null);
        form.editScript(script);
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
        viewLogic.enter(parameter);
    }
}
