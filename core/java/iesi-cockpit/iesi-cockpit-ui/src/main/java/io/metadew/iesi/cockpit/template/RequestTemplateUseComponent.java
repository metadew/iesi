package io.metadew.iesi.cockpit.template;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.icon.VaadinIcon;

@Tag(Tag.IMG)
public class RequestTemplateUseComponent extends Component {

	private static final long serialVersionUID = 1L;

	public RequestTemplateUseComponent() {
		VaadinIcon.PLAY_CIRCLE_O.create();
	}
}