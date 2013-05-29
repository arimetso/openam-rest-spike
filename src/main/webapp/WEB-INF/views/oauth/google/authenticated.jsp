<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Google Authentication Success - OpenAM SSO Test App</title>
    <link rel="stylesheet" href="/static/style.css">
</head>
<body>
    <h1>Authentication Successful &ndash; Welcome <c:out value="${userName}"/> &lt;<c:out value="${userEmail}"/>&gt;</h1>
    
    <h2>Protected Content</h2>
    <p><a href="/content/page/1">Page 1</a></p>
    <p><a href="/content/page/2">Page 2</a></p>
    
    <hr>
    <p><a href="/about">About</a></p>
</body>
</html>