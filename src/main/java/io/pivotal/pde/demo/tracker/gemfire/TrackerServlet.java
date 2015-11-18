package io.pivotal.pde.demo.tracker.gemfire;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ServiceDestroyEvent;
import com.vaadin.server.ServiceDestroyListener;
import com.vaadin.server.VaadinServlet;

import hello.VaadinUI;

@WebServlet(urlPatterns = "/*")
@VaadinServletConfiguration(ui = TrackerUI.class, productionMode = false)
public class TrackerServlet extends VaadinServlet implements ServiceDestroyListener {
	private static final long serialVersionUID = 1L;

	ClassPathXmlApplicationContext ctx = null;

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();

		this.getService().addServiceDestroyListener(this);

		ctx = new ClassPathXmlApplicationContext("tracker.xml");
		this.getServletContext().setAttribute("spring-context", ctx);
		
		CheckInRepository repo = ctx.getBean(CheckInRepository.class);
	}

	@Override
	public void serviceDestroy(ServiceDestroyEvent event) {
		if (ctx != null)
			ctx.close();
	}

}
