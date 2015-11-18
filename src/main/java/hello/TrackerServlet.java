package hello;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ServiceDestroyEvent;
import com.vaadin.server.ServiceDestroyListener;
import com.vaadin.server.VaadinServlet;

@WebServlet(urlPatterns = "/*")
@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
public class TrackerServlet extends VaadinServlet implements ServiceDestroyListener {
	private static final long serialVersionUID = 1L;

	ClassPathXmlApplicationContext ctx = null;

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();

		this.getService().addServiceDestroyListener(this);

		ctx = new ClassPathXmlApplicationContext("tracker.xml");
		this.getServletContext().setAttribute("spring-context", ctx);
		
		CustomerRepository repo = ctx.getBean(CustomerRepository.class);
		this.loadData(repo);
	}

	@Override
	public void serviceDestroy(ServiceDestroyEvent event) {
		if (ctx != null)
			ctx.close();
	}

	private void loadData(CustomerRepository repository) {
		// save a couple of customers
		repository.save(new Customer(1l, "Jack", "Bauer"));
		repository.save(new Customer(2l, "Chloe", "O'Brian"));
		repository.save(new Customer(3l, "Kim", "Bauer"));
		repository.save(new Customer(4l, "David", "Palmer"));
		repository.save(new Customer(5l, "Michelle", "Dessler"));

	}

}
