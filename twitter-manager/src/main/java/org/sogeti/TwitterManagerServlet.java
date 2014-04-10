package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sogeti.bo.ParamBean;
import org.sogeti.bo.UserBean;
import org.sogeti.service.ManageUsersService;
import org.sogeti.service.bo.ServiceResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class TwitterManagerServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(TwitterManagerServlet.class
			.toString());

	static {
		ObjectifyService.register(UserBean.class); // Fait conna�tre votre
													// classe-entit� � Objectify
	}
	private ManageUsersService managerService;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet TwitterManagerServlet");

		try {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("user") == null) {
				System.out.println("User indéfini redirection vers login");
				resp.sendRedirect("/login");
			} else {
				Twitter twitter = (Twitter) session.getAttribute("twitter");
				Objectify ofy = ObjectifyService.ofy();
				ParamBean config = null;
				try {
					config = ofy.load().type(ParamBean.class)
							.id(twitter.getScreenName()).now();
				} catch (IllegalStateException e) {
					LOGGER.log(Level.SEVERE,
							"Un problème est survenu lors du chargement des données de la base");
					e.printStackTrace();
				} catch (TwitterException e) {
					LOGGER.log(Level.SEVERE,
							"Un problème est survenu lors du chargement des données de la base");
					e.printStackTrace();
				}
				if (config != null) {

					if (this.managerService == null) {
						this.managerService = new ManageUsersService(twitter);
					}
					req.setAttribute("isRunning",
							isRunnningService().contains("true") ? "true"
									: "false");
					if (req.getPathInfo() != null
							&& req.getPathInfo().contains("cron")) {
						if (req.getPathInfo().contains("start")) {
							LOGGER.log(Level.INFO,
									"Lancement de la tache cron 'start'");
							startService();
						} else {
							LOGGER.log(Level.INFO,
									"Tache cron demandée inconnue");
						}
					} else {
						this.getServletContext()
								.getRequestDispatcher(
										"/WEB-INF/jsp/twitterManager.jsp")
								.forward(req, resp);
					}
				}
				else{
					this.getServletContext()
					.getRequestDispatcher(
							"/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
				}					
			}
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
		if (typeSubmit == null) {
			isRunning = isRunnningService().contains("true") ? "true" : "false";
		} else if (typeSubmit.equals("start")) {
			startService();
		} else if (typeSubmit.equals("stop")) {
			stopService();
		} else {
			isRunning = isRunnningService().contains("true") ? "true" : "false";
		}
		isRunning = isRunnningService().contains("true") ? "true" : "false";
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
		ServiceResponse reponse = managerService.startManagement();
		return reponse.getServiceRunning();
	}

	private String stopService() {

		ServiceResponse reponse = managerService.stopManagement();
		return reponse.getServiceRunning();
	}

	private String isRunnningService() {
		
		ServiceResponse reponse = managerService.isRunning();
		return reponse.getServiceRunning();
	}

}
