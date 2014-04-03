package org.sogeti;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sogeti.service.TwitterService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import twitter4j.TwitterException;

@SuppressWarnings("serial")
public class ManageConfigurationServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(ManageConfigurationServlet.class
			.toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Entree servlet ManageConfigurationServlet");
		try {
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//TODO
		try {
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/jsp/manageConfiguration.jsp")
					.forward(req, resp);
		} catch (ServletException e) {
			LOGGER.log(
					Level.SEVERE,
					"Un problème est survenu avec le traitement de la jsp '/WEB-INF/jsp/manageConfiguration.jsp'");
			e.printStackTrace();
		}
	}
	
	private String callSendDirectMessage(String message) {

		String reponse = "";
		try {

			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);
			WebResource webResource = client
					.resource("http://localhost:8888/_ah/api/tweetSenderService/v1/send");
			String messageJson = "{\"message\":\""+message+ "\"}";
			ClientResponse response = webResource.type("application/json")
					.post(ClientResponse.class, messageJson);

			if (response.getStatus() != 200) {
				throw new RuntimeException(
						" Erreur lors de l'appel du service 'http://localhost:8888/_ah/api/tweetSenderService/send' : "
								+ response.getStatus());
			}

			reponse = response
					.getEntity(String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return reponse;
	}

}
