<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Content Page <c:out value="${pagenum}" /> of 5 - OpenAM SSO Test App</title>
    <link rel="stylesheet" href="/static/style.css">
</head>
<body>
    <h1>Content Page <c:out value="${pagenum}" /> of 5</h1>
    
    <c:if test="${pagenum > 1}">
    <p><c:url var="prevPageUrl" value="/content/page/${pagenum - 1}"/><a href="${prevPageUrl}">Previous Page</a></p>
    </c:if>
    <c:if test="${pagenum < 5}">
    <p><c:url var="nextPageUrl" value="/content/page/${pagenum + 1}"/><a href="${nextPageUrl}">Next Page</a></p>
    </c:if>
    
    <hr>
    <p><a href="/home">Home</a> &bull; <a href="/about">About</a> &bull; <a href="/logout">Logout</a></p>
</body>
</html>