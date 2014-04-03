package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sogeti.bo.UserBean;
import org.sogeti.service.ManageUsersService;
import org.sogeti.service.bo.RestServiceResponse;

import com.googlecode.objectify.ObjectifyService;


@SuppressWarnings("serial")
public class TwitterManagerServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(TwitterManagerServlet.class
			.toString());
	

	static {
		ObjectifyService.register(UserBean.class); // Fait conna�tre votre
													// classe-entit� � Objectify
	}
	private ManageUsersService managerService = new ManageUsersService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet TwitterManagerServlet");
		req.setAttribute("isRunning", isRunnningService().contains("true")?"true":"false");
		
		try {
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/twitterManager.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un probl�me est survenu avec le traitement de la jsp '/WEB-INF/jsp/twitterManager.jsp'");
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String isRunning = req.getParameter("isRunning");
		String typeSubmit = req.getParameter("typeSubmit");
		if(typeSubmit==null) {
			isRunning=isRunnningService().contains("true")?"true":"false";
		} else if(typeSubmit.equals("start")){
			startService();
		} else if(typeSubmit.equals("stop")){
			stopService();
		} else {
			isRunning=isRunnningService().contains("true")?"true":"false";
		}
		isRunning=isRunnningService().contains("true")?"true":"false";
		req.setAttribute("isRunning", isRunning);
		try {
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/twitterManager.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un probl�me est survenu avec le traitement de la jsp '/WEB-INF/jsp/twitterManager.jsp'");
			e.printStackTrace();
		}
	}
	
	private String startService() {
		RestServiceResponse reponse = managerService.startManagement();
		return reponse.getServiceRunning();
	}
	
	private String stopService() {
		
		RestServiceResponse reponse = managerService.stopManagement();
		return reponse.getServiceRunning();
	}
	
	private String isRunnningService() {
		 RestServiceResponse reponse = managerService.isRunning();
		 return reponse.getServiceRunning();
	}


	
	
}
