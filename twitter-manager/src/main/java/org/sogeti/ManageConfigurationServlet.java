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

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class ManageConfigurationServlet extends HttpServlet {

	private static Logger LOGGER = Logger
			.getLogger(ManageConfigurationServlet.class.toString());

	static {
		ObjectifyService.register(ParamBean.class); // Fait connaître votre
													// classe-entité à Objectify
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet ManageConfigurationServlet");

		try {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("user") == null) {
				System.out.println("User null redirection vers login");
				resp.sendRedirect("/login");
			} else {
				Twitter twitter = (Twitter) session.getAttribute("twitter");
				LOGGER.log(Level.INFO, "User = " + twitter.getScreenName());
				req.setAttribute("mainUser", twitter.getScreenName());
				Objectify ofy = ObjectifyService.ofy();
				ParamBean config = ofy.load().type(ParamBean.class)
						.id(twitter.getScreenName()).now();
				if (config == null) {
					config = new ParamBean();
				}
				req.setAttribute("config", config);
				this.getServletContext()
						.getRequestDispatcher(
								"/WEB-INF/jsp/manageConfiguration.jsp")
						.forward(req, resp);
			}

		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		} catch (TwitterException e) {
			LOGGER.log(Level.SEVERE,
					"Un problème lors de l'authentification du user twitter"
							+ e.getMessage() + e.getCause());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Objectify ofy = ObjectifyService.ofy();
		ParamBean config = new ParamBean();
		config.setScreenname(req.getParameter("screenname"));				
		config.setCriterian1(req.getParameter("criterian1").toLowerCase());
		config.setCriterian1conditions(req.getParameter("criterian1conditions")
				.toLowerCase());
		config.setCriterian2(req.getParameter("criterian2").toLowerCase());
		config.setCriterian2conditions(req.getParameter("criterian2conditions")
				.toLowerCase());
		config.setCriterian3(req.getParameter("criterian3").toLowerCase());
		config.setCriterian3conditions(req.getParameter("criterian3conditions")
				.toLowerCase());
		config.setScoreOk(req.getParameter("scoreOk"));
		ofy.save().entities(config);
		try {
			this.getServletContext()
					.getRequestDispatcher(
							"/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		}
	}

}
