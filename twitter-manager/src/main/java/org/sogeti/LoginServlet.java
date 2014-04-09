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
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(LoginServlet.class
			.toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet LoginServlet");
		try {
			
			if(req.getParameter("oauth_verifier")!=null) {
				//log depuis twitter avec verifier envoyé par twitter
				String verifier = req.getParameter("oauth_verifier");
				HttpSession session = req.getSession();
				Twitter twitter = (Twitter)session.getAttribute("twitter");
				RequestToken requestToken = new RequestToken(
						(String) session.getAttribute("token"),
						(String) session.getAttribute("tokenSecret"));
					AccessToken accesToken = twitter.getOAuthAccessToken(requestToken,
							verifier);
					User user = twitter.verifyCredentials();
					if (user != null) {
						session.setAttribute("accesToken", accesToken);
						//suppression des informations concernant le requestToken
						session.removeAttribute("token");
						session.removeAttribute("tokenSecret");
						
						session.setAttribute("user", user);
						resp.sendRedirect("/manageconfiguration");
					}

				
			} else if (req.getRequestURI() != null
					&& req.getRequestURI().contains("enterPin")) {
				// Dans le cas d'un verifier à saisir manuellement
				// sur le retour de twitter, l'utilisateur doit entrer un code
				// pin
				req.setAttribute("twitterReturn", true);
			} else {
				HttpSession session = req.getSession(false);
				if(session ==null){
					session = req.getSession();
					Configuration conf = TwitterService.getInstance().getConf();
					TwitterFactory tf = new TwitterFactory(conf);
					Twitter twitter = tf.getInstance();
					RequestToken requestToken = twitter.getOAuthRequestToken("http://sogeti-twitter-manager.appspot.com/login/enterPin");
					String token = requestToken.getToken();
					String tokenSecret = requestToken.getTokenSecret();
					
					session.setAttribute("token", token);
					session.setAttribute("tokenSecret", tokenSecret);
					
					String authUrl = requestToken.getAuthorizationURL();
					session.setAttribute("authUrl", authUrl);
					session.setAttribute("twitter", twitter);
				}
				
				req.setAttribute("twitterReturn", false);
				req.setAttribute("authUrl", session.getAttribute("authUrl"));
			}
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(Level.SEVERE,
					"Un problème est survenu lors du traitement de la jsp '/WEB-INF/jsp/login.jsp'");
			e.printStackTrace();
		} catch (TwitterException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu lors de la recupération de OauthRequestToken pour twitter"
							+ e.getMessage() + e.getCause());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			HttpSession session = req.getSession();
			Twitter twitter = (Twitter)session.getAttribute("twitter");
			RequestToken requestToken = new RequestToken(
					(String) session.getAttribute("token"),
					(String) session.getAttribute("tokenSecret"));
			if (req.getParameter("pin") != null && requestToken != null) {

				AccessToken accesToken = twitter.getOAuthAccessToken(requestToken,
						req.getParameter("pin"));
				User user = twitter.verifyCredentials();
				if (user != null) {
					session.setAttribute("accesToken", accesToken);
					session.setAttribute("user", user);
					//suppression des informations concernant le requestToken
					session.removeAttribute("token");
					session.removeAttribute("tokenSecret");
					resp.sendRedirect("/manageconfiguration");
				}

			} else {
				req.setAttribute("erreurMessage",
						"le pin fournit par twitter est obligatoire");
			}

			
		} catch (TwitterException e) {
			LOGGER.log(Level.SEVERE,
					"Un problème lors de l'authentification du user twitter"
							+ e.getMessage() + e.getCause());
			req.setAttribute("erreurMessage",
					"Un erreur est survenue pendant le processus d'autentification "
							+ e.getMessage() + e.getCause());
		}

	}

}
