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

import org.sogeti.BeanMapper;
import org.sogeti.bo.UserBean;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {
	
	
	private static Logger LOGGER = Logger.getLogger(TwitterService.class.toString());
	public static Properties PROP;
	private ConfigurationBuilder cb ;
	public static String APP_ACCOUNT_SCREENNAME;
	private Twitter twitter;
	
	
	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	private TwitterService() {
		init();
	}

	/** Holder */
	private static class SingletonHolder {
		
		private final static TwitterService instance = new TwitterService();
	}

	/** Point d'acc�s pour l'instance unique du singleton */
	public static TwitterService getInstance() {
		return SingletonHolder.instance;
	}

	private void init() {
		LOGGER.log(Level.INFO,"Initialisation du service TwitterService");
		this.cb = new ConfigurationBuilder();
		PROP = this.load("WEB-INF/tweetbot.properties");
		APP_ACCOUNT_SCREENNAME = PROP
				.getProperty("account.screenname");// a remplacer par
		cb.setPrettyDebugEnabled(true);
		cb.setOAuthConsumerKey(PROP.getProperty("oauth.consumer.key"));// API key
		cb.setOAuthConsumerSecret(PROP.getProperty("oauth.consumer.secret"));// API
																				// secret
		cb.setOAuthAccessToken(PROP.getProperty("oauth.acces.token"));
		cb.setOAuthAccessTokenSecret(PROP.getProperty("oauth.acces.token.secret"));

		twitter = new TwitterFactory(cb.build()).getInstance();
	}
	
	
	public Properties load(String filename) {
		
		LOGGER.log(Level.INFO,"Chargement des properties "+ filename);
		Properties properties = new Properties();
		InputStream input;
		
		input = this.getClass().getClassLoader()
                    .getResourceAsStream(filename);
		if(input != null){
			try {
				properties.load(input);
				input.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE,"Erreur inattendue lors du chargement du fichier de configuration "+filename+" \n"+e.getMessage());
				e.printStackTrace();
			}	
		} else {
			LOGGER.log(Level.SEVERE,"Le fichier de configuration de l'application  'WEB-INF/tweetbot.properties' est introuvable");
		}
		
		return properties;
	}
	
	
	
	
	/**
	 *  Retourne la liste des followers sous forme de Twitter4J.User potentiellement
	 * cette methode explose les quotats twitter et retourne une liste incomplete
	 * @param screenName (si non renseign� le user TwitterService.APP_ACCOUNT_SCREENNAME est utilis�.
	 * @return
	 */
	public List<User> getFollowersList(String screenName) {
		
		IDs result = null;
		ArrayList<User> usersList = new ArrayList<User>();
		
		if (screenName == null) {
			screenName = TwitterService.APP_ACCOUNT_SCREENNAME;
		}
		LOGGER.log(Level.INFO,"Chargement des followers du user : "+ screenName);
		long cursor = -1;
		do { 	
			try {
				result = twitter.getFollowersIDs(screenName, cursor);
				int startCurs=0;
				while(startCurs<result.getIDs().length){
					long[] tab = Arrays.copyOfRange(result.getIDs(), startCurs, startCurs+100);
					usersList.addAll(twitter.lookupUsers(tab));
					startCurs=startCurs+100;
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,"Twitter service or network is probably unavailable\n "+e.getMessage());
				cursor=0l;
			}
		}
		while (cursor != 0);
		return usersList;
	}
	
	/**
	 * Retourne la liste des followers sous forme de UserBean potentiellement
	 * cette methode explose les quotats twitter et retourne une liste incomplete
	 * @param screenName (si non renseign� le user TwitterService.APP_ACCOUNT_SCREENNAME est utilis�.
	 * @return
	 */
	public List<UserBean> getFollowersUserBean(String screenName){
		
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		List<User> usersList = this.getFollowersList(screenName);
		if(!usersList.isEmpty()){
			for (User user : usersList) {
				UserBean ub = BeanMapper.getUserBeanFromUser(user);
				userBeanList.add(ub);
			}
		}
		return userBeanList;
	}
	
 	/**
 	 * Recupere les ids des followers du user pass� en param�tre
 	 * @param screenName (si non renseign� le user TwitterService.APP_ACCOUNT_SCREENNAME est utilis�.
 	 * @return liste des ids
 	 */
 	public List<Long> getFollowersIDList(String screenName) {
		LOGGER.log(Level.INFO,"Chargement des id des followers du user : "+ screenName);
		IDs result = null;
 		List<Long> usersIdList = new ArrayList<Long>();
		if (screenName == null) {
			screenName = TwitterService.APP_ACCOUNT_SCREENNAME;
		}
		long cursor = -1;
		do {
			try {
				result = twitter.getFollowersIDs(screenName, cursor);
				for (long id : result.getIDs()) {
					usersIdList.add(new Long(id));
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,"Twitter service or network is probably unavailable " + e.getMessage() );
				cursor=0;
			}
		} while (cursor != 0);
		return usersIdList;
		
	}

	
 	
	/**
	 *  Retourne la liste des friends sous forme de Twitter4J.User potentiellement
	 * cette methode explose les quotats twitter et retourne une liste incomplete
	 * @param screenName (si non renseign� le user TwitterService.APP_ACCOUNT_SCREENNAME est utilis�.
	 * @return liste des friends
	 */
	public List<User> getFriendsList(String screenName) {
		LOGGER.log(Level.INFO,"Chargement des friends du user : "+ screenName);
		IDs result = null;
		ArrayList<User> usersList = new ArrayList<User>();
		
		if (screenName == null) {
			screenName = TwitterService.APP_ACCOUNT_SCREENNAME;
		}
		
		long cursor = -1;
		do { 	
			try {
				result = twitter.getFriendsIDs(screenName, cursor);
				int startCurs=0;
				while(startCurs<result.getIDs().length){
					long[] tab = Arrays.copyOfRange(result.getIDs(), startCurs, startCurs+100);
					usersList.addAll(twitter.lookupUsers(tab));
					startCurs=startCurs+100;
				}
				cursor = result.getNextCursor();
			} catch (TwitterException e) {
				LOGGER.log(Level.SEVERE,"Twitter service or network is probably unavailable\n "+e.getMessage());
				cursor=0l;
			}
		}
		while (cursor != 0);
		return usersList;
	}
	
	
	/**
	 * Retourne la liste des friends sous forme de UserBean potentiellement
	 * cette methode explose les quotats twitter et retourne une liste incomplete
	 * @param screenName (si non renseign� le user TwitterService.APP_ACCOUNT_SCREENNAME est utilis�.
	 * @return
	 */
	public Map<String,UserBean> getFriendsUserBeanMap(String screenName){
		
		Map<String,UserBean> userBeanMap = new HashMap<String,UserBean>();
		List<User> usersList = this.getFriendsList(screenName);
		if(!usersList.isEmpty()){
			for (User user : usersList) {
				UserBean ub = BeanMapper.getUserBeanFromUser(user);
				userBeanMap.put(ub.getId().toString(), ub);
			}
		}
		return userBeanMap;
	}
	
	public void createFriendship(Long id) {
		try {
			twitter.createFriendship(id);

		} catch (TwitterException e1) {
			LOGGER.log(Level.SEVERE,"Twitter service or network is probably unavailable");
		}
	}

	public void destroyFriendship(Long id) {
		try {
			twitter.destroyFriendship(id);

		} catch (TwitterException e1) {
			LOGGER.log(Level.SEVERE,e1.getMessage()+"\n" + e1.getCause()+"\nTwitter service or network is probably unavailable or Rate limit exceeded ");
		}
	}
	
	
}
