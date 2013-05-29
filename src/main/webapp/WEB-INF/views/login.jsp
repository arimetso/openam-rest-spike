<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Login - OpenAM SSO Test App</title>
    <link rel="stylesheet" href="/static/style.css">
</head>
<body>
    <h1>Login</h1>
    
    <form method="post" action="/j_spring_security_check">
        <p><label>Username: <input type="text" name="j_username"></label></p>
        <p><label>Password: <input type="password" name="j_password"></label></p>
        <p><button type="submit">Login</button></p>
    </form>
    <hr>
    <p><a href="/home">Home</a></p>
</body>
</html>