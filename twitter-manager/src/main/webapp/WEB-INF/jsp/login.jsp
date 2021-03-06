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
<title>TweetSender</title>
</head>

<body>
	<c:out value="Loggez vous via votre compte twitter svp" />
	<br />
	<p>${erreurMessage}</p>
	<form name="loginForm" method="post">
		<c:choose>
			<c:when test="${twitterReturn}">
				<p>
					<label>Entrer le code pin fournit par twitter ici</label> <input type="text" placeholder="#pin" name="pin" value="${pin}" />
					<input id="validButton" type="submit" name="validButon" value="Valider" />
				</p>
			</c:when>
			<c:otherwise>
				<p>
					<a href='<%=request.getAttribute("authUrl")%>'>Authentifiez vous via
						Twitter</a>
				</p>
			</c:otherwise>
		</c:choose>
	</form>
</body>

</html>