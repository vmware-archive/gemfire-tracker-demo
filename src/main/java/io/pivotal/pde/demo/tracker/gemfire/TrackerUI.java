package io.pivotal.pde.demo.tracker.gemfire;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
@Push
public class TrackerUI extends UI implements CheckInCacheListener.ChangeHandler {

	private CheckInRepository repo;

	private CheckInEditor editor;

	private Grid grid;

	private TextField filter;

	private Button addNewBtn;

	public TrackerUI() {
	}

	@Override
	protected void init(VaadinRequest request) {
		this.grid = new Grid();
		this.filter = new TextField();
		this.addNewBtn = new Button("Check In", FontAwesome.PLUS);

		ApplicationContext ctx = (ApplicationContext) VaadinServlet.getCurrent().getServletContext()
				.getAttribute("spring-context");
		this.repo = ctx.getBean(CheckInRepository.class);
		this.editor = ctx.getBean(CheckInEditor.class);

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
		setContent(mainLayout);

		// Configure layouts and components
		actions.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		grid.setHeight(300, Unit.PIXELS);
		grid.setWidth(550, Unit.PIXELS);
		grid.setColumns("plate", "city", "timestamp");
		grid.setContainerDataSource(new BeanItemContainer<CheckIn>(CheckIn.class));

		filter.setInputPrompt("Filter by License Plate");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.addTextChangeListener(e -> refillGrid(e.getText()));

		// Show the form when new button is clicked
		addNewBtn.addClickListener(e -> editor.newCheckIn());

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(new CheckInEditor.ChangeHandler() {

			@Override
			public void onChange() {
				editor.setVisible(false);
				refillGrid(filter.getValue());
			}

		});

		CheckInCacheListener changeListener = ctx.getBean(CheckInCacheListener.class);
		changeListener.setHandler(this);

		// this.setPollInterval(5000);

		// Initialize listing
		refillGrid(null);
	}

	// public void itemAdded(CheckIn newItem) {
	// access(() -> {
	// BeanItemContainer<CheckIn> bic = (BeanItemContainer<CheckIn>)
	// grid.getContainerDataSource();
	// String filterVal = filter.getValue();
	//
	// if (StringUtils.isEmpty(filterVal)) {
	// if (!newItem.getPlate().toLowerCase().startsWith(filterVal)) {
	// return;
	// }
	// }
	//
	// bic.addBean(newItem);
	//
	// });
	// }

	public void itemAdded(CheckIn newItem) {
		access(() -> {
			refillGrid(filter.getValue());
		});
	}

	private void refillGrid(String text) {
		BeanItemContainer<CheckIn> bic = (BeanItemContainer<CheckIn>) grid.getContainerDataSource();
		bic.removeAllItems();

		if (StringUtils.isEmpty(text)) {
			for (CheckIn c : repo.findAll()) {
				bic.addBean(c);
			}
		} else {
			for (CheckIn c : repo.findByPlateStartsWithIgnoreCase(text)) {
				bic.addBean(c);
			}
		}
		
		grid.sort("timestamp",SortDirection.DESCENDING);
	}

}
