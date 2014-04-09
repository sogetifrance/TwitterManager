package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.sogeti.service.TweetSenderService;
import org.sogeti.service.TwitterService;
import org.sogeti.service.bo.ServiceResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;

@SuppressWarnings("serial")
public class TweetSenderServlet extends HttpServlet {

	private TweetSenderService tweetSenderService;
	private static Logger LOGGER = Logger.getLogger(TweetSenderServlet.class
			.toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		LOGGER.log(Level.INFO, "Entree servlet TweetSenderServlet");
		try {
			HttpSession session = req.getSession(false);
			if (session == null || session.getAttribute("user") == null) {
				System.out.println("User indéfini redirection vers login");
				resp.sendRedirect("/login");
			} else {
				Twitter twitter = (Twitter)session.getAttribute("twitter");
				tweetSenderService = new TweetSenderService(twitter);
				String action = req.getParameter("action");
				if (action != null && action.equals("refresh")) {
					ServiceResponse response = tweetSenderService.isRunning();
					resp.setContentType("application/json");
					resp.setCharacterEncoding("UTF-8");
					ObjectMapper mapper = new ObjectMapper();
					String repService = mapper.writerWithDefaultPrettyPrinter()
							.writeValueAsString(response);
					resp.getWriter().write(repService);

				} else {
					req.setAttribute("expediteur", twitter.getScreenName());
					this.getServletContext()
							.getRequestDispatcher(
									"/WEB-INF/jsp/tweetSender.jsp")
							.forward(req, resp);
				}
			}
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/tweetSender.jsp'");
			e.printStackTrace();
		} catch (IllegalStateException e) {
			LOGGER.log(Level.SEVERE,
					"Un problème est survenu lors de la recupération du user Twitter");
		} catch (TwitterException e) {
			LOGGER.log(Level.SEVERE,
					"Un problème est survenu lors de la recupération du user Twitter");
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// pour etre sur qu'on va recuperer un screenName
		HttpSession session = req.getSession(false);
		Twitter twitter = (Twitter) session.getAttribute("twitter");
		String userTest = req.getParameter("userTest");
		String message = req.getParameter("message");
		if (userTest.isEmpty()) {
			req.setAttribute("erreurMessage",
					"Le user de destination ne peut pas être vide");
			req.setAttribute("message", message);
			req.setAttribute("userTest", userTest);
		} else if (message.isEmpty()) {
			req.setAttribute("erreurMessage",
					"Le message a envoyé ne peut pas être vide");
			req.setAttribute("message", message);
			req.setAttribute("userTest", userTest);
		} else if (userTest.equals("allUsers")) {
			// envoi du message a tous les followers
			// appel e service rest
			callSendDirectMessage(message);
		} else {
			// envoi du message à un userUnique
			try {
				twitter.sendDirectMessage(userTest, message);
			} catch (TwitterException e) {
				if (e.getErrorCode() == 150) {
					req.setAttribute("erreurMessage",
							"Vous ne pouvez pas envoyer de message à un utilisateur qui ne vous suit pas");
				}
				req.setAttribute("message", message);
				req.setAttribute("userTest", userTest);
			}
		}
		req.setAttribute("accountScreenName",
				TwitterService.APP_ACCOUNT_SCREENNAME);
		try {
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/tweetSender.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/twitterManager.jsp'");
			e.printStackTrace();
		}
	}

	private String isRunning() {
		ServiceResponse response = tweetSenderService.isRunning();
		return response.getServiceRunning();
	}

	private String callSendDirectMessage(String message) {
		ServiceResponse response = tweetSenderService.sendMessage(message);
		return response.getServiceRunning();
	}
}
