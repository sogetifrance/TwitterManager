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
<script src="../js/jquery-1.11.0.min.js" type="text/javascript" ></script>
</head>

<body>
	<c:out
		value="Cette page permet d'envoyer un directMessage à tous les followers du compte paramétré en indiquant 'allUsers' ou à un user unique en renseignant son screenName" />
	
	<c:out
		value=""/>
	<br />
	<p>${erreurMessage}</p>
	<form name="tweetSenderForm" method="post">

		<table >
			<tr>
				<td><label>Expediteur</label></td>
				<td colspan="2"><c:out value="${expediteur}"/></td>
			</tr>
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
				<td><input type="button" name="refresh" class="button"
					style="width: 200px;" value="envoyer" onclick="test()" /></td>
			</tr>
		</table>
	</form>
</body>
<script type="text/javascript">

function refreshAvancement() {
	//try to call http://localhost:8888/_ah/api/tweetSenderService/v1/isRunning	
	// Appel de service Rest
	var enCours = "false";
	
	$.get('tweetsender',{action:"refresh"},function(resp) {
		console.log(resp);
		console.log(resp.result);
		console.log(resp.serviceRunning);
        document.getElementById("progressBar").value = resp.result[0];
		document.getElementById("progressBar").max = resp.result[1];
		
		enCours = resp.serviceRunning;
		document.getElementById("avancementString").innerHTML = resp.result[0]+"/"+resp.result[1];
		
		if(enCours=="true"){
				setTimeout(refreshAvancement, 3000);
		} else{
			document.getElementById("avancementString").innerHTML = "Envoi de message terminé";
		}
    });
}

$(document).ready(function() {
	refreshAvancement();
});	
	
	
	
</script>
</html>