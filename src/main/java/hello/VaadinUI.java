package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

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
public class VaadinUI extends UI {

	private  CustomerRepository repo;

	private  CustomerEditor editor;

	private  Grid grid;

	private  TextField filter;

	private  Button addNewBtn;

	public VaadinUI(){
	}
	
	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
	}

	@Override
	protected void init(VaadinRequest request) {
		this.grid = new Grid();
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);

		
		ApplicationContext ctx = (ApplicationContext) VaadinServlet.getCurrent().getServletContext().getAttribute("spring-context");
		this.repo = ctx.getBean(CustomerRepository.class);
		this.editor = ctx.getBean(CustomerEditor.class);
		
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
		setContent(mainLayout);

		// Configure layouts and components
		actions.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setInputPrompt("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.addTextChangeListener(e -> listCustomers(e.getText()));

		// Connect selected Customer to editor or hide if none is selected
		grid.addSelectionListener(e -> {
			if (e.getSelected().isEmpty()) {
				editor.setVisible(false);
			} else {
				editor.editCustomer((Customer) e.getSelected().iterator().next());
			}
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer(null,"", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});

		// Initialize listing
		listCustomers(null);
	}

	// tag::listCustomers[]
	private void listCustomers(String text) {
		if (text == null || text.length() == 0) {
			if (StringUtils.isEmpty(text)) {
				BeanItemContainer<Customer> bic = new BeanItemContainer<Customer>(Customer.class);
				for(Customer c: repo.findAll()) bic.addBean(c);
				grid.setContainerDataSource(bic);
			} else {
				grid.setContainerDataSource(
						new BeanItemContainer(Customer.class, repo.findByLastNameStartsWithIgnoreCase(text)));
			}
		}
	}
	// end::listCustomers[]

}
