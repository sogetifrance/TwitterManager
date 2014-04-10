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
<title>TwitterManager</title>
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
						<li ><a href="/tweetsender">TweetSender</a></li>
						<li class="active"><a href="/twittermanager">TwitterManager</a></li>
						<li><a href="/manageconfiguration">Configuration</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>

	<div class="container">
		<p>

			<c:out
				value="Cet écran permet de lancer le service qui entretient la liste d'amis twitter en fonction des paramètres renseignés dans l'écran de configuration" />
		</p>
		<c:choose>
			<c:when test="${isRunning=='true'}">le service est lancé </c:when>
			<c:when test="${isRunning=='false'}">le service est eteint </c:when>
			<c:when test="${isRunning=='erreur'}">le service est en erreur </c:when>
		</c:choose>

		<form name="manageServiceForm" method="post">
			<table border=0>
				<tr>
					<td><input type="button" name="start" class="button"
						style="width: 200px;" value="demarrer"
						onclick="callServlet(this.form,'start');" /></td>

					<td><input type="button" name="stop" class="button"
						style="width: 200px;" value="arreter"
						onclick="callServlet(this.form,'stop');" /></td>

					<td><input type="button" name="refresh" class="button"
						style="width: 200px;" value="refresh"
						onclick="callServlet(this.form,'refresh');" /></td>

					<td><input id="typeSubmit" type="hidden" name="typeSubmit" /></td>
					<td><input id="isRunning" type="hidden" name="isRunning"
						value="${isRunning}" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
<script type="text/javascript">
	function callServlet(form, button) {

		document.getElementById('typeSubmit').value = button;
		form.submit();
	}
</script>
</html>