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
<tr><th>ICAO</th><th>Naziv</th><th>Država</th></tr>
<c:forEach var="aerodrom" items="${aerodromi}">
    <tr><td>${aerodrom.icao}</td><td>${aerodrom.naziv}</td><td>${aerodrom.drzava}</td></tr>
</c:forEach>
</table>
</body>
<form method="get">
  <label for="odBroja">Od broja:</label>
  <input type="text" name="odBroja" value="${odBroja}" />
  <label for="broj">Broj elemenata:</label>
  <input type="text" name="broj" value="${broj}" />
  <button type="submit">Prikaži</button>
</form>

    <form method="get">
        <input type="hidden" name="brojRedova" value="${param.brojRedova}">
        <input type="hidden" name="odBroja" value="${param.odBroja}">
        <input type="submit" name="action" value="Starter">
        <input type="submit" name="action" value="Prethodni">
        <input type="submit" name="action" value="Sledeći">
    </form>
    
    <c:set var="starter" value="0" />
<c:set var="prethodni" value="${odBroja - brojRedova}" />
<c:set var="sledeci" value="${odBroja + brojRedova}" />

<c:choose>
  <c:when test="${param.action == 'Starter' || odBroja == 0}">
    <c:set var="odBroja" value="${starter}" />
  </c:when>
  <c:when test="${param.action == 'Prethodni'}">
    <c:set var="odBroja" value="${prethodni}" />
  </c:when>
  <c:when test="${param.action == 'Sledeći'}">
    <c:set var="odBroja" value="${sledeci}" />
  </c:when>
</c:choose>
</html>