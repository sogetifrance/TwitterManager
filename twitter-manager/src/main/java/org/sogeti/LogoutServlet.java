package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.Twitter;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(LogoutServlet.class
			.toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet LogoutServlet");
		HttpSession session = req.getSession();
		if (session.getAttribute("twitter") != null) {
			Twitter twitter = (Twitter) session.getAttribute("twitter");
			twitter.setOAuthAccessToken(null);
			session = null;
			LOGGER.log(Level.INFO, "session -> null");
		}
		resp.sendRedirect("/login");
		return;
	}

}
