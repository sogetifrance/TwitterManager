<%@page import="org.sogeti.bo.UserBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="java.util.List"%>
<%@ page import="org.sogeti.bo.UserBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TweetSender</title>
</head>

<body>
	<c:out
		value="Cette page permet d'envoyer un directMessage à tous les followers du compte paramétré en indiquant 'allUsers' ou à un user unique en renseignant son screenName" />
	<br />
	<p>${erreurMessage}</p>
	<form name="tweetSenderForm" method="post">

		<table border=0>
			<tr>
				<td><label>User(s) de destination</label></td>
				<td colspan="2"><input type="text" id=userTest name="userTest"
					placeholder="screenName du user de test ou allUsers" /></td>
			</tr>
			<tr>
				<td><label>Message</label></td>
				<td><textarea name="message" id="message" rows="7" cols="30"
						maxlength="140"
						placeholder="Veuiller indiquer un message de 140 caractères maximum"> </textarea></td>
				<td><input type="submit" name="send" class="button"
					style="width: 200px;" value="envoyer" /></td>
			</tr>
			<tr>
				<td colspan="2"><progress id="progressBar" value="0" max="0"></progress></td>
				<td><label id="avancementString"></label></td>
			</tr>
		</table>
	</form>
</body>
<script type="text/javascript">
	function refreshAvancement() {
			//try to call http://localhost:8888/_ah/api/tweetSenderService/v1/isRunning	
			// Appel de service Rest
			var enCours = "false";
			var request = gapi.client.tweetSenderService.tweetSenderService
					.isRunning();
			// Gestion Reponse
			request.execute(function(resp) {
				document.getElementById("progressBar").value = resp.result[0];
				document.getElementById("progressBar").max = resp.result[1];
				console.log(resp);
				console.log(resp.result[0]);
				console.log(resp.serviceRunning);
				enCours = resp.serviceRunning;
				document.getElementById("avancementString").innerHTML = resp.result[0]+"/"+resp.result[1];
				
				if(enCours=="true"){
	 				setTimeout(refreshAvancement, 3000);
				} else{
					document.getElementById("avancementString").innerHTML = "Envoi de message terminé";
				}
			});
			
			
	}
	
	function test2() {
		console.log('tweetSender api ready');
		refreshAvancement();
	}
	
	function init() {
		var ROOT = "http://localhost:8888/_ah/api";
		gapi.client.load('tweetSenderService', 'v1', function() {
			test2();
		}, ROOT);
	}
</script>
<script src="https://apis.google.com/js/client.js?onload=init"></script>
</html>