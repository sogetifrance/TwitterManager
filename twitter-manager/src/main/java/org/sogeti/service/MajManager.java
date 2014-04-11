package org.sogeti.service;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sogeti.bo.ParamBean;
import org.sogeti.bo.UserBean;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class MajManager {
	private static Logger LOGGER = Logger
			.getLogger(MajManager.class.toString());

	public static List<Long> maj(Twitter twitter, List<Long> followersIds,
			List<Long> friendIds, boolean isNew, UserBean user)
			throws IllegalStateException, TwitterException {
		// On regarde si c'est potentiellement un nouveau friend et si il n'est
		// pas déjà un friends du compte
		Objectify ofy = ObjectifyService.ofy();
		ParamBean param = ofy.load().type(ParamBean.class)
				.id(twitter.getScreenName()).now();
		LOGGER.log(Level.INFO,
				"Criteria 1 " + param.getCriterian1());
		if (!user.getScreenName().equals(twitter.getScreenName())) {
			if (!isNew || (isNew && !isFriend(friendIds, user.getId()))) {
				// On regarde si le user n'est pas déjà un des follower du
				// compte.
				if (isfollower(followersIds, user.getId())) {
					// Si oui on passe le user en delete
					user.setDelete(true);
				} else {
					// On regarde si son score est bon
					if (!getScoreOk(user.getDescription(), param)) {
						// Si non on passe le user en delete
						user.setDelete(true);
					}
				}
				// On regarde si le user existe déjà dans la base
				UserBean userBdd = ofy.load().type(UserBean.class)
						.id(user.getId()).now();
				if (userBdd != null) {
					// Si oui On regarde si c'est un user delete en bdd
					if (userBdd.isDelete()) {
						user.setDelete(true);
					} else if (Calendar.getInstance().getTime()
							.compareTo(userBdd.getFriendSince()) > Integer
							.parseInt(param.getNbJourToDelete())) {
						user.setDelete(true);
					}
					// On regarde si les user sont identique
					if (user.isDelete()) {
						// Si non on fait une maj en bdd
						majBdd(user, userBdd);
					}
				} else {
					user.setFriendSince(Calendar.getInstance().getTime());
					// Si non on fait une maj en bdd
					majBdd(user, null);
				}
				// On regarde si c'est potentiellement un nouveau friend
				if (isNew) {
					// Si oui On regarde si c'est un user delete
					if (!user.isDelete()) {
						// Si non on l'ajoute au amis du compte

						LOGGER.log(Level.INFO,
								"Friend ajouté : " + user.getName());
						TwitterService.getInstance().createFriendship(twitter,

						user.getId());
						friendIds.add(user.getId());
					}
				} else {
					// Si non on regarde si c'est un user delete
					if (user.isDelete()) {
						// Si oui on suprime le friend du compte.
						TwitterService.getInstance().destroyFriendship(twitter,
								user.getId());
					} else {
						friendIds.add(user.getId());
					}
				}

			}
		}
		return friendIds;
	}

	// Permet de regarder si le user est déjà friend
	private static boolean isFriend(List<Long> userFriendIds, Long id) {
		if (userFriendIds.contains(id)) {
			return true;
		} else {
			return false;
		}
	}

	// Permet de regarder si le user est déjà follower
	private static boolean isfollower(List<Long> followersIds, Long id) {
		if (followersIds.contains(id)) {
			return true;
		} else {
			return false;
		}
	}

	// Permet de faire la maj en BDD
	private static void majBdd(UserBean user, UserBean userBdd) {
		if (userBdd != null) {
			user.setFriendSince(userBdd.getFriendSince());
		}
		Objectify ofy = ObjectifyService.ofy();
		ofy.save().entities(user);
	}

	// Permet de calculer le score d'un user par rapport à sa description
	private static boolean getScoreOk(String description, ParamBean param) {
		return ScoreService.isScoreOk(description, param);
	}
}
