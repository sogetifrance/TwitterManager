<%@page import="org.sogeti.bo.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
						<li><a href="/logout">Logout</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="container" style="margin-top:60px">
		<p>
			<c:out
				value="Cette page permet de modifier la configuration des applications utilisant Twitter" />
		</p>
		<div class="alert alert-success">${messages}</div>
		<form name="configurationForm" method="post">
			<table border=0>
				<tr>
					<td colspan="2"><input type="hidden" id="screenname"
						name="screenname" placeholder="User name" value="${mainUser}" /></td>
				</tr>
				<tr>
					<td><label>Utilisateur connecté</label></td>
					<td colspan="2"><c:out value="${mainUser}" /></td>
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
					<td><label>Suprimer friends si friend depuis plus de : (en jours)</label></td>
					<td colspan="2"><input type="text" id="nbJourToDelete" name="nbJourToDelete"
						required value="${config.nbJourToDelete
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
