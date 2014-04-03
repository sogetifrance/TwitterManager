package org.sogeti;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.sogeti.bo.UserBean;

import twitter4j.User;


/**
 * Classe permettant le mapping entre User et UserBean
 * @author FBRIZOU
 *
 */
public class BeanMapper {
	public static UserBean getUserBeanFromUser(User user) {
		UserBean userBean = new UserBean();
		try {
			BeanUtils.copyProperties(userBean, user);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userBean;
	}
}
