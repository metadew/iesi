package io.metadew.iesi.cockpit.template;

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
import com.vaadin.flow.router.RouteAlias;

import io.metadew.iesi.cockpit.MainLayout;
import io.metadew.iesi.metadata.definition.RequestTemplate;

@Route(value = "Templates", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class RequestTemplateView extends HorizontalLayout implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "templates";
	private RequestTemplateGrid grid;
	private RequestTemplateForm form;
	private TextField filter;

	private RequestTemplateLogic viewLogic = new RequestTemplateLogic(this);
	private Button newRequestTemplate;

	private RequestTemplateDataProvider dataProvider = new RequestTemplateDataProvider();

	public RequestTemplateView() {
		setSizeFull();
		HorizontalLayout topLayout = createTopBar();

		grid = new RequestTemplateGrid();
		grid.setDataProvider(dataProvider);
		grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

		form = new RequestTemplateForm(viewLogic);

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

		newRequestTemplate = new Button("New Template");
		newRequestTemplate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		newRequestTemplate.setIcon(VaadinIcon.PLUS_CIRCLE.create());
		newRequestTemplate.addClickListener(click -> viewLogic.newRequestTemplate());
		// CTRL+N will create a new window which is unavoidable
		newRequestTemplate.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth("100%");
		topLayout.add(filter);
		topLayout.add(newRequestTemplate);
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

	public void setNewRequestTemplateEnabled(boolean enabled) {
		newRequestTemplate.setEnabled(enabled);
	}

	public void clearSelection() {
		grid.getSelectionModel().deselectAll();
	}

	public void selectRow(RequestTemplate row) {
		grid.getSelectionModel().select(row);
	}

	public RequestTemplate getSelectedRow() {
		return grid.getSelectedRow();
	}

	public void updateRequestTemplate(RequestTemplate RequestTemplate) {
		dataProvider.save(RequestTemplate);
	}

	public void removeRequestTemplate(RequestTemplate RequestTemplate) {
		dataProvider.delete(RequestTemplate);
	}

	public void editRequestTemplate(RequestTemplate requestTemplate) {
		showForm(requestTemplate != null);
		form.editRequestTemplate(requestTemplate);
	}

	public void showForm(boolean show) {
		form.setVisible(show);

		/*
		 * FIXME The following line should be uncommented when the CheckboxGroup issue
		 * is resolved. The category CheckboxGroup throws an IllegalArgumentException
		 * when the form is disabled.
		 */
		// form.setEnabled(show);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		viewLogic.enter(parameter);
	}
}
