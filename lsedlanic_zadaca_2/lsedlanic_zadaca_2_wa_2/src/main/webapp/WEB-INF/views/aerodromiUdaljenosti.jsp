<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Aerodromi</title>
</head>
<body>
<table>
<tr><th>Drzava</th><th>Udaljenost</th></tr>
<c:forEach var="udaljenost" items="${udaljenosti}">
    <tr><td>${udaljenost.drzava}</td><td>${udaljenost.udaljenost}</td></tr>
</c:forEach>
</table>
</body>
</html>