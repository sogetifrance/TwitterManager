package org.sogeti.service;

import java.util.Calendar;
import java.util.List;

import org.sogeti.bo.UserBean;
import org.sogeti.service.ScoreService;
import org.sogeti.service.TwitterService;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class MajManager {

	public static List<Long> maj(List<Long> followersIds, List<Long> friendIds,
			boolean isNew, UserBean user) {
		// On regarde si c'est potentiellement un nouveau friend et si il n'est
		// pas déjà un friends du compte
		if (!user.getScreenName().equals(TwitterService.APP_ACCOUNT_SCREENNAME)) {
			if (!isNew || (isNew && !isFriend(friendIds, user.getId()))) {
				// On regarde si le user n'est pas déjà un des follower du
				// compte.
				if (isfollower(followersIds, user.getId())) {
					// Si oui on passe le user en delete
					user.setDelete(true);
				} else {
					// On regarde si son score est bon
					if (!getScoreOk(user.getDescription())) {
						// Si non on passe le user en delete
						user.setDelete(true);
					}
				}
				// On regarde si le user existe déjà dans la base
				Objectify ofy = ObjectifyService.ofy();
				UserBean userBdd = ofy.load().type(UserBean.class)
						.id(user.getId()).now();
				if (userBdd != null) {
					// Si oui On regarde si c'est un user delete en bdd
					if (userBdd.isDelete()) {
						user.setDelete(true);
					}
					// On regarde si les user sont identique
					if (user.isDelete()) {
						// Si non on fait une maj en bdd
						majBdd(user, userBdd);
					}
				} else {
					if (!user.isDelete()) {
						user.setFriendSince(Calendar.getInstance().getTime());
					}
					// Si non on fait une maj en bdd
					majBdd(user, null);
				}
				// On regarde si c'est potentiellement un nouveau friend
				if (isNew) {
					// Si oui On regarde si c'est un user delete
					if (!user.isDelete()) {
						// Si non on l'ajoute au amis du compte
						TwitterService.getInstance().createFriendship(
								user.getId());
						friendIds.add(user.getId());
					}
				} else {
					// Si non on regarde si c'est un user delete
					if (user.isDelete()) {
						// Si oui on suprime le friend du compte.
						TwitterService.getInstance().destroyFriendship(
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
	private static boolean getScoreOk(String description) {
		return ScoreService.isScoreOk(description);
	}
}
