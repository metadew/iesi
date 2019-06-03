package io.metadew.iesi.cockpit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.metadew.iesi.cockpit.authentication.AccessControlFactory;
import io.metadew.iesi.cockpit.component.ComponentView;
import io.metadew.iesi.cockpit.connection.ConnectionView;
import io.metadew.iesi.cockpit.define.environment.EnvironmentView;
import io.metadew.iesi.cockpit.script.ScriptView;
import io.metadew.iesi.cockpit.template.TemplateView;

/**
 * The main layout. Contains the navigation menu.
 */
@HtmlImport("css/shared-styles.html")
@Theme(value = Lumo.class)
@PWA(name = "IESI Cockpit", shortName = "IESI")
public class MainLayout extends FlexLayout implements RouterLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
	private Menu menu;

	public MainLayout() {
        VaadinSession.getCurrent()
        .setErrorHandler((ErrorHandler) errorEvent -> {
            log.error("Uncaught exception occurred",
                    errorEvent.getThrowable());
            Notification.show(
                    "Internal error occurred");
        });
        
        setSizeFull();
        setClassName("main-layout");

        menu = new Menu();
        menu.addView(TemplateView.class, TemplateView.VIEW_NAME,VaadinIcon.LOCATION_ARROW.create());
        menu.addView(ScriptView.class, ScriptView.VIEW_NAME,VaadinIcon.CODE.create());
        menu.addView(ComponentView.class, ComponentView.VIEW_NAME,VaadinIcon.CUBES.create());
        menu.addView(ConnectionView.class, ConnectionView.VIEW_NAME,VaadinIcon.CONNECT.create());
        menu.addView(EnvironmentView.class, EnvironmentView.VIEW_NAME,VaadinIcon.MAP_MARKER.create());

        add(menu);
        
    }

	@SuppressWarnings("rawtypes")
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

		attachEvent.getUI().addShortcutListener(
				() -> AccessControlFactory.getInstance().createAccessControl().signOut(), Key.KEY_L,
				KeyModifier.CONTROL);

		// add the admin view menu item if/when it is registered dynamically
		Command addAdminMenuItemCommand = () -> menu.addView(AdminView.class, AdminView.VIEW_NAME,
				VaadinIcon.DOCTOR.create());
		RouteConfiguration sessionScopedConfiguration = RouteConfiguration.forSessionScope();
		if (sessionScopedConfiguration.isRouteRegistered(AdminView.class)) {
			addAdminMenuItemCommand.execute();
		} else {
			sessionScopedConfiguration.addRoutesChangeListener(event -> {
				for (RouteBaseData data : event.getAddedRoutes()) {
					if (data.getNavigationTarget().equals(AdminView.class)) {
						addAdminMenuItemCommand.execute();
					}
				}
			});
		}
	}
}
