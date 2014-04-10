package org.sogeti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sogeti.service.bo.ServiceResponse;

import twitter4j.Twitter;

import com.google.appengine.api.ThreadManager;


public class TweetSenderService {

	private boolean serviceRunning;
	private int nbFollowersTotal;
	private int nbSent;
	private Twitter twitter;

	private static Logger LOGGER = Logger.getLogger(TweetSenderService.class.toString());

	public TweetSenderService(Twitter twitter) {
		super();
		this.twitter = twitter;
	}

	public ServiceResponse sendMessage(String message) {
		if (!serviceRunning) {
			final String messageToSend = message;
			nbFollowersTotal=0;
			nbSent=0;
			this.serviceRunning = true;
			try {
				Runnable send = new Runnable() {
					public void run() {
						sendDirecMessage(messageToSend);
					}
				};

				ThreadFactory threadFactory = ThreadManager
						.backgroundThreadFactory();
				Thread thread = threadFactory.newThread(send);
				thread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return new ServiceResponse("sendMessage", String.valueOf(this.serviceRunning),
					new ArrayList<String>());
		}
		return new ServiceResponse("sendMessage", String.valueOf(this.serviceRunning),
				new ArrayList<String>());
	}

	private void sendDirecMessage(String message) {
		LOGGER.log(Level.INFO,"Recuperation des ids des followers ");
		List<Long> listIds = TwitterService.getInstance().getFollowersIDList(twitter);
		this.nbFollowersTotal = listIds.size();
		//envoi des messages
		LOGGER.log(Level.INFO,"envoi du message à "+listIds.size()+" utilisateurs");
		for (Long id : listIds) {
			try {
				LOGGER.log(Level.INFO,"Service running? :"+this.serviceRunning + " envoi du message à "+id);
				Thread.sleep(5000);
				this.nbSent++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.serviceRunning = false;
	}

	public ServiceResponse isRunning() {
		LOGGER.log(Level.INFO,"Service TweetSender en cours? "+this.serviceRunning);
		List<String> result = new ArrayList<String>();
		result.add(String.valueOf(this.nbSent));
		result.add(String.valueOf(this.nbFollowersTotal));
		ServiceResponse rsr = new ServiceResponse("sendMessage", String.valueOf(this.serviceRunning),
				result);
		return rsr;
	}
}
