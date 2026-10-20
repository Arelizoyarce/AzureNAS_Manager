<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesi&oacute;n &middot; AzureNAS Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="login-caja">
    <h1>&#9729; AzureNAS Manager</h1>
    <c:if test="${not empty requestScope.error}">
        <div class="alerta error"><c:out value="${requestScope.error}"/></div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <label>Usuario
            <input type="text" name="username" value="<c:out value='${username}'/>" required autofocus>
        </label>
        <label>Contrase&ntilde;a
            <input type="password" name="password" required>
        </label>
        <button type="submit">Ingresar</button>
    </form>
</div>
</body>
</html>
