<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error &middot; AzureNAS Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="login-caja" style="text-align:center">
    <h1>Ocurri&oacute; un problema</h1>
    <p>La p&aacute;gina solicitada no est&aacute; disponible o hubo un error en el servidor.</p>
    <p><a class="boton" href="${pageContext.request.contextPath}/dashboard">Volver al inicio</a></p>
</div>
</body>
</html>
