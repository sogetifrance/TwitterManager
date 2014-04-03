<%@page import="org.sogeti.bo.UserBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.datastore.*"%>
<%@ page import="java.util.List"%>
<%@ page import="org.sogeti.bo.UserBean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TODO</title>
</head>
<body>
	<c:out value="Bonjour" />
	<br />
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
				<td><input id="isRunning" type="hidden" name="isRunning" value="${isRunning}"/></td>
			</tr>
		</table>
	</form>

</body>
<script type="text/javascript">
	function callServlet(form, button) {

		document.getElementById('typeSubmit').value = button;
		form.submit();
	}
</script>
</html>