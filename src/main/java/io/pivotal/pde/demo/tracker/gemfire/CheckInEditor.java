package io.pivotal.pde.demo.tracker.gemfire;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */

@Component
public class CheckInEditor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final CheckInRepository repository;

	/* Fields to edit properties in Customer entity */
	TextField plate = new TextField("License Plate");
	TextField location = new TextField("Location");

	/* Action buttons */
	Button save = new Button("Save", FontAwesome.SAVE);
	Button cancel = new Button("Cancel");
	CssLayout actions = new CssLayout(save, cancel);

	@Autowired
	public CheckInEditor(CheckInRepository repository) {
		this.repository = repository;

		
		addComponents(plate, location, actions);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		cancel.addClickListener(e -> cancel());
		
		setVisible(false);
	}

	public void save(){
		UUID newid = UUID.randomUUID();
		CheckIn c = new CheckIn();
		c.setId(newid.toString());
		c.setPlate(plate.getValue());
		c.setCity(location.getValue());
		c.setTimestamp(new Date());
		repository.save(c);
		
		if (changeHandler != null) changeHandler.onChange();
	}
	
	public void cancel() {
		setVisible(false);
	}
	
	public  void newCheckIn() {
		plate.clear();
		location.clear();
		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
	}
	
	public static interface ChangeHandler {
		public void onChange();
	}

	private ChangeHandler changeHandler;
	
	public void setChangeHandler(ChangeHandler handler){
		changeHandler = handler;
	}
}
