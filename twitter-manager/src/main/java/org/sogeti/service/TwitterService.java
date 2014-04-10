package org.sogeti.service;

import java.io.IOException; 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sogeti.bo.BeanMapper;
import org.sogeti.bo.ParamBean;
import org.sogeti.bo.UserBean;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {
	
	private static Logger LOGGER = Logger.getLogger(TwitterService.class.toString());
	public static Properties consumerProperties;
	private Configuration conf;

	public Configuration getConf() {
		return conf;
	}

	private TwitterService() {
		init();
	}

	/** Holder */
	private static class SingletonHolder {

		private final static TwitterService instance = new TwitterService();
	}

	/** Point d'accès pour l'instance unique du singleton */
	public static TwitterService getInstance() {
		return SingletonHolder.instance;
	}

	private void init() {
		LOGGER.log(Level.INFO, "Initialisation du service TwitterService");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		consumerProperties = this.load("tweetbot.properties");
		cb.setPrettyDebugEnabled(true);
		cb.setOAuthConsumerKey(consumerProperties.getProperty("oauth.consumer.key"));// API key
		cb.setOAuthConsumerSecret(consumerProperties.getProperty("oauth.consumer.secret"));// API
																				// secret

		this.conf = cb.build();																		
	}

	public Properties load(String filename) {

		LOGGER.log(Level.INFO, "Chargement des properties " + filename);
		Properties properties = new Properties();
		InputStream input;

		input = this.getClass().getClassLoader().getResourceAsStream(filename);
		if (input != null) {
			try {
				properties.load(input);
				input.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE,
						"Erreur inattendue lors du chargement du fichier de configuration "
								+ filename + " \n" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			LOGGER.log(Level.SEVERE,"Le fichier de configuration de l'application  "+ filename +" est introuvable");
		}

		return properties;
	}

	/**
	 * Retourne la liste des followers sous forme de Twitter4J.User
	 * potentiellement cette methode explose les quotats twitter et retourne une
	 * liste incomplete
	 * 
	 * @param screenName
	 *            (si non renseigné le user
	 *            TwitterService.APP_ACCOUNT_SCREENNAME est utilisé.
	 * @return
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	public List<User> getFollowersList(Twitter twitter, String screenName) throws IllegalStateException, TwitterException {
		IDs result = null;
		ArrayList<User> usersList = new ArrayList<User>();

		if (screenName == null) {
			screenName = twitter.getScreenName();
		}
		LOGGER.log(Level.INFO, "Chargement des followers du user : "
				+ screenName);
		long cursor = -1;
		do {
			try {
				result = twitter.getFollowersIDs(screenName, cursor);
				int startCurs = 0;
				while (startCurs < result.getIDs().length) {
					long[] tab = Arrays.copyOfRange(result.getIDs(), startCurs,
							startCurs + 100);
					usersList.addAll(twitter.lookupUsers(tab));
					startCurs = startCurs + 100;
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,
						"Twitter service or network is probably unavailable\n "
								+ e.getMessage());
				cursor = 0l;
			}
		} while (cursor != 0);
		return usersList;
	}

	/**
	 * Retourne la liste des followers sous forme de UserBean potentiellement
	 * cette methode explose les quotats twitter et retourne une liste
	 * incomplete
	 * 
	 * @param screenName
	 *            (si non renseigné le user
	 *            TwitterService.APP_ACCOUNT_SCREENNAME est utilisé.
	 * @return
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	public List<UserBean> getFollowersUserBean(Twitter twitter, String screenName) throws IllegalStateException, TwitterException{
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		List<User> usersList = this.getFollowersList(twitter,screenName);
		if(!usersList.isEmpty()){
			for (User user : usersList) {
				UserBean ub = BeanMapper.getUserBeanFromUser(user);
				userBeanList.add(ub);
			}
		}
		return userBeanList;
	}

	/**
	 * Recupere les ids des followers du user passé en paramètre
	 * 
	 * @param screenName
	 *            (si non renseigné le user
	 *            TwitterService.APP_ACCOUNT_SCREENNAME est utilisé.
	 * @return liste des ids
	 */
	public List<Long> getFollowersIDList(Twitter twitter) {
		LOGGER.log(Level.INFO, "Chargement des id des followers ");
		IDs result = null;
		List<Long> usersIdList = new ArrayList<Long>();
		long cursor = -1;
		do {
			try {
				result = twitter.getFollowersIDs(twitter.getScreenName(), cursor);
				for (long id : result.getIDs()) {
					usersIdList.add(new Long(id));
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,
						"Twitter service or network is probably unavailable "
								+ e.getMessage());
				cursor = 0;
			}
		} while (cursor != 0);
		return usersIdList;

	}

	/**
	 * Retourne la liste des friends sous forme de Twitter4J.User
	 * potentiellement cette methode explose les quotats twitter et retourne une
	 * liste incomplete
	 * 
	 * @param screenName
	 *            (si non renseigné le user
	 *            TwitterService.APP_ACCOUNT_SCREENNAME est utilisé.
	 * @return liste des friends
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	public List<User> getFriendsList(Twitter twitter, String screenName) throws IllegalStateException, TwitterException {
		LOGGER.log(Level.INFO,"Chargement des friends du user : "+ screenName);

		IDs result = null;
		ArrayList<User> usersList = new ArrayList<User>();

		if (screenName == null) {
			screenName = twitter.getScreenName();
		}

		long cursor = -1;
		do {
			try {
				result = twitter.getFriendsIDs(screenName, cursor);
				int startCurs = 0;
				while (startCurs < result.getIDs().length) {
					long[] tab = Arrays.copyOfRange(result.getIDs(), startCurs,
							startCurs + 100);
					usersList.addAll(twitter.lookupUsers(tab));
					startCurs = startCurs + 100;
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,
						"Twitter service or network is probably unavailable\n "
								+ e.getMessage());
				cursor = 0l;
			}
		} while (cursor != 0);
		return usersList;
	}

	/**
	 * Retourne la liste des friends sous forme de UserBean potentiellement
	 * cette methode explose les quotats twitter et retourne une liste
	 * incomplete
	 * 
	 * @param screenName
	 *            (si non renseigné le user
	 *            TwitterService.APP_ACCOUNT_SCREENNAME est utilisé.
	 * @return
	 * @throws TwitterException 
	 * @throws IllegalStateException 
	 */
	public Map<String,UserBean> getFriendsUserBeanMap(Twitter twitter, String screenName) throws IllegalStateException, TwitterException{
		
		Map<String,UserBean> userBeanMap = new HashMap<String,UserBean>();
		List<User> usersList = this.getFriendsList(twitter, screenName);
		if(!usersList.isEmpty()){
			for (User user : usersList) {
				UserBean ub = BeanMapper.getUserBeanFromUser(user);
				userBeanMap.put(ub.getId().toString(), ub);
			}
		}
		return userBeanMap;
	}
	
	public void createFriendship(Twitter twitter, Long id) {
		try {
			twitter.createFriendship(id);

		} catch (TwitterException e1) {
			LOGGER.log(Level.SEVERE,
					"Twitter service or network is probably unavailable");
		}
	}

	public void destroyFriendship(Twitter twitter, Long id) {
		try {
			twitter.destroyFriendship(id);

		} catch (TwitterException e1) {
			LOGGER.log(
					Level.SEVERE,
					e1.getMessage()
							+ "\n"
							+ e1.getCause()
							+ "\nTwitter service or network is probably unavailable or Rate limit exceeded ");
		}
	}

}
