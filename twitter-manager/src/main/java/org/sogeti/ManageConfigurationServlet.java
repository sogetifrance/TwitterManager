package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sogeti.service.TwitterService;

import twitter4j.Twitter;
import twitter4j.TwitterException;


@SuppressWarnings("serial")
public class ManageConfigurationServlet extends HttpServlet {

	private static Logger LOGGER = Logger
			.getLogger(ManageConfigurationServlet.class.toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet ManageConfigurationServlet");
		
		try {
			HttpSession session = req.getSession(false);
			if(session==null || session.getAttribute("user")==null){
				System.out.println("User null redirection vers login");
				resp.sendRedirect("/login");
			} else {
				Twitter twitter = (Twitter)session.getAttribute("twitter");
				LOGGER.log(Level.INFO, "User = "+ twitter.getScreenName());
				req.setAttribute("mainUser", twitter.getScreenName());
					this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
			}
			
		
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		} catch (TwitterException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème lors de l'authentification du user twitter"+ e.getMessage() +e.getCause());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO
		try {
			this.getServletContext()
					.getRequestDispatcher(
							"/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un probl�me est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		}
	}
}
