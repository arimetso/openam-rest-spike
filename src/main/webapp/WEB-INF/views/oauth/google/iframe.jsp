<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Google OAuth in IFrame - OpenAM SSO Test App</title>
    <link rel="stylesheet" href="/static/style.css">
    <style type="text/css">
        #authframe { border: 2px solid black; width: 800px; height: 600px; margin: 50px auto 0; }
    </style>
</head>
<body>
    <h1>Google OAuth in IFrame</h1>
    <iframe id="authframe" src="/auth/google/start"></iframe>
</body>
</html>
