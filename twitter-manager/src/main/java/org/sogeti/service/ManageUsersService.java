package org.sogeti.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sogeti.BeanMapper;
import org.sogeti.bo.UserBean;
import org.sogeti.service.TwitterService;
import org.sogeti.service.bo.RestServiceResponse;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.google.appengine.api.ThreadManager;


public class ManageUsersService {

	private boolean isStarted = false;
	private boolean stopRequired = false;
	private Map<String, UserBean> mapFriendUserBean;
	private List<Long> followersIdList;
	private List<Long> friendsIdList;
	private static Logger LOGGER = Logger.getLogger(ManageUsersService.class.toString());
	
	private void traitementPrincipal() {
		LOGGER.log(Level.INFO,"Demmarrage du service ManageUsersService");
		// on recupere les friends et followers du user API
		init();
		// on nettoie la liste de friends
		clean();
		// on boucle sur la map nettoyee
		for (UserBean userBean : mapFriendUserBean.values()) {
			if (friendsIdList.size() < 2000 && !stopRequired) {
				findNewFriends(userBean.getId());
			} else {
				break;
			}
		}
		stopRequired = false;
		isStarted = false;
		LOGGER.log(Level.INFO,"Arrêt du service ManageUsersService");
	}

	private void init() {
		TwitterService.getInstance();
		// recherche des friends du user API
		mapFriendUserBean = TwitterService.getInstance().getFriendsUserBeanMap(
				TwitterService.APP_ACCOUNT_SCREENNAME);
		// recherche des followers du user API
		followersIdList = TwitterService.getInstance().getFollowersIDList(
				TwitterService.APP_ACCOUNT_SCREENNAME);
		friendsIdList = new ArrayList<Long>();
	}

	private void clean() {
		// on boucle sur tous les friends et on nettoie
		for (UserBean friend : mapFriendUserBean.values()) {
			friendsIdList = MajManager.maj(followersIdList, friendsIdList,
					false, friend);
		}
	}
	
	//TODO faire m�thode maj

	private void findNewFriends(Long userId) {
		Twitter twitter = TwitterService.getInstance().getTwitter();
		try {
			// recup�ration des 5000 premiers ids
			IDs ids = null;
			boolean isFriend2000 = false;
			do {
				if (ids != null && ids.getRateLimitStatus().getRemaining() == 0) {
					Thread.sleep(ids.getRateLimitStatus()
							.getSecondsUntilReset() * 1000 + 5);
				}
				if (ids == null) {
					// on recuperation des premiers 5000 ids
					ids = twitter.getFollowersIDs(userId, -1);
				} else {
					// recup�ration des ids au del� des 50000 premiers
					ids = twitter.getFollowersIDs(userId, ids.getNextCursor());
				}
				// � partir des ids, on recup�re de vrais users 100 par 100
				long[] ids5000 = ids.getIDs();
				int startCurs = 0;
				ResponseList<User> newUserList100 = null;
				while (!stopRequired && startCurs < ids5000.length && !isFriend2000) {
					long[] tab = Arrays.copyOfRange(ids5000, startCurs,
							startCurs + 100);
					newUserList100 = twitter.lookupUsers(tab);
					// on pause pour pas bouuffer la limite imposer par Twitter
					Thread.sleep(newUserList100.getRateLimitStatus()
							.getSecondsUntilReset()
							* 1000
							/ newUserList100.getRateLimitStatus()
									.getRemaining());
					for (User user : newUserList100) {
						// on teste si le user peut �tre ajouter ou non
						if (friendsIdList.size() < 2000) {
							friendsIdList = MajManager.maj(followersIdList,
									friendsIdList, true,
									BeanMapper.getUserBeanFromUser(user));
						} else {
							isFriend2000 = true;
							break;
						}
					}
					startCurs = startCurs + 100;
					// TODO A voir si vraiment n�cc�ssaire �vite 1 traitement en +
					if (!(friendsIdList.size() < 2000)) {
						isFriend2000 = true;
					} 
				}
			} while (!stopRequired && ids != null && ids.hasNext() && !isFriend2000);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE,e.getMessage());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE,e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE,e.getMessage());
		}

	}

	public RestServiceResponse startManagement() {
		LOGGER.log(Level.INFO,"Demarrage du service ManageUsersService");
		if (!this.isStarted) {
			this.isStarted = true;
			try {
				Runnable manage = new Runnable() {
					public void run() {
						manageFriends();
					}
				};

				ThreadFactory threadFactory = ThreadManager
						.backgroundThreadFactory();
				Thread thread = threadFactory.newThread(manage);
				thread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE,e.getMessage());
				this.isStarted = false;
			}
		}
		return new RestServiceResponse("startManagement",
				String.valueOf(this.isStarted), new ArrayList<String>());
	}

	public RestServiceResponse stopManagement() {
		LOGGER.log(Level.INFO,"Demande d'arrêt du service ManageUsersService");
		if (this.isStarted) {
			this.stopRequired = true;
		}
		
		return new RestServiceResponse("stopManagement",
				String.valueOf(this.isStarted), new ArrayList<String>());
	}

	public RestServiceResponse isRunning() {
		return new RestServiceResponse("isRunning",
				String.valueOf(this.isStarted), new ArrayList<String>());
	}

	private void manageFriends() {
		int count = 1;
		Thread thread = Thread.currentThread();

		synchronized (thread) {
			if (this.isStarted) {
				try {
					traitementPrincipal();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.SEVERE,e.getMessage());
				}
			}
		}

	}

}
