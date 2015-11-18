package io.pivotal.pde.demo.tracker.gemfire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
@Push
public class TrackerUI extends UI {

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
		grid.setColumns("plate", "city", "timestamp");

		filter.setInputPrompt("Filter by License Plate");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.addTextChangeListener(e -> listCheckIns(e.getText()));

		// Show the form when new button is clicked
		addNewBtn.addClickListener(e -> editor.newCheckIn());

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(new CheckInEditor.ChangeHandler() {

			@Override
			public void onChange() {
				editor.setVisible(false);
				listCheckIns(filter.getValue());
			}

		});

		// Initialize listing
		listCheckIns(null);
	}

	private void listCheckIns(String text) {
		if (StringUtils.isEmpty(text)) {
			BeanItemContainer<CheckIn> bic = new BeanItemContainer<CheckIn>(CheckIn.class);
			for (CheckIn c : repo.findAll())
				bic.addBean(c);
			grid.setContainerDataSource(bic);
		} else {
			grid.setContainerDataSource(
					new BeanItemContainer<CheckIn>(CheckIn.class, repo.findByPlateStartsWithIgnoreCase(text)));
		}
	}

}
