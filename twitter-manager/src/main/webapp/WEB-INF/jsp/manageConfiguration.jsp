<%@page import="org.sogeti.bo.UserBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="java.util.List"%>
<%@ page import="org.sogeti.bo.UserBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="../css/bootstrap.css">
<title>ManageConfiguration</title>
</head>

<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<button class="btn btn-navbar" data-target=".nav-collapse"
					data-toggle="collapse" type="button">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="brand" href="/index.html">SogetiTwitterManager</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li><a href="/tweetsender">TweetSender</a></li>
						<li><a href="/twittermanager">TwitterManager</a></li>
						<li class="active"><a href="/manageconfiguration">Configuration</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="container">
		<p>
			<c:out
				value="Cette page permet de modifier la configuration des applications utilisant Twitter" />
		</p>
		<p>${erreurMessage}</p>
		<form name="configurationForm" method="post">
			<table border=0>
				<tr>
					<td><label>Utilisateur connecté</label></td>
					<td colspan="2"><c:out value="${mainUser}" /></td>
				</tr>
				<tr>
					<td><label>User name</label></td>
					<td colspan="2"><input type="text" id="screenname"
						name="screenname" placeholder="User name" required
						value="${config.screenname
					}" /></td>
				</tr>
				<tr>
					<td><label>Oauth Consumer Key</label></td>
					<td colspan="2"><input type="text" id="oauthConsumerKey"
						name="oauthConsumerKey" value="${config.consumerKey
					}"
						placeholder="Api key" required /></td>
				</tr>
				<tr>
					<td><label>Oauth Consumer Secret</label></td>
					<td colspan="2"><input type="password"
						id="oauthConsumerSecret" name="oauthConsumerSecret"
						value="${config.consumerSecret
					}"
						placeholder="API key secret" required /></td>
				</tr>
				<tr>
					<td><label>oauth Acces Token</label></td>
					<td colspan="2"><input type="text" id="oauthAccesToken"
						name="oauthAccesToken" value="${config.accesToken
					}"
						placeholder="Access Token" required /></td>
				</tr>
				<tr>
					<td><label>oauth Acces Token Secret</label></td>
					<td colspan="2"><input type="password"
						id="oauthAccesTokenSecret" name="oauthAccesTokenSecret" required
						value="${config.tokenSecret }" placeholder="Access Token secret" /></td>
				</tr>
				<tr>
					<td><label>criteres niveau 1</label></td>
					<td colspan="2"><input type="text" id="criterian1"
						name="criterian1" value="${config.criterian1
					}"
						placeholder="criterian1" required /></td>
				</tr>
				<tr>
					<td><label>Liste de criteres avec condition,
							conditions séparées par "#" , listes de mots séparées par des ";"
							et mots sparés par ",", la fin de ligne ce termine sans rien</label></td>
					<td colspan="2"><input type="text" id="criterian1conditions"
						name="criterian1conditions"
						value="${config.criterian1conditions
					}"
						placeholder="exemple: Software; Developer, Architect, Engineer# Java, J2EE; Spring, Struts, JSF, JSP" /></td>
				</tr>
				<tr>
					<td><label>criteres niveau 2</label></td>
					<td colspan="2"><input type="text" id="criterian2"
						name="criterian2" required value="${config.criterian2
					}"
						placeholder="criterian2" /></td>
				</tr>
				<tr>
					<td><label>Liste de criteres avec condition,
							conditions séparées par "#" , listes de mots séparées par des ";"
							et mots sparés par ",", la fin de ligne ce termine sans rien</label></td>
					<td colspan="2"><input type="text" id="criterian2conditions"
						name="criterian2conditions"
						value="${config.criterian2conditions
					}"
						placeholder="exemple: Software; Developer, Architect, Engineer" /></td>
				</tr>
				<tr>
					<td><label>criteres niveau 3</label></td>
					<td colspan="2"><input type="text" id="criterian3"
						name="criterian3" required value="${config.criterian3
					}"
						placeholder="criterian3" /></td>
				</tr>
				<tr>
					<td><label>Liste de criteres avec condition,
							conditions séparées par "#" , listes de mots séparées par des ";"
							et mots sparés par ",", la fin de ligne ce termine sans rien</label></td>
					<td colspan="2"><input type="text" id="criterian3conditions"
						name="criterian3conditions"
						value="${config.criterian3conditions
					}"
						placeholder="exemple: Software; Developer, Architect, Engineer" /></td>
				</tr>
				<tr>
					<td><label>Score requis</label></td>
					<td colspan="2"><input type="text" id="scoreOk" name="scoreOk"
						required value="${config.scoreOk
					}" placeholder="score" /></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" name="send"
						class="button" style="width: 200px;" value="enregistrer" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>
