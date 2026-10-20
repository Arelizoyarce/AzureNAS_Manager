<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Consola PowerShell"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Consola PowerShell</h1>
<div class="tarjeta">
    <p>Solo se permiten cmdlets <code>Get-*</code> (por ejemplo <code>Get-Date</code> o <code>Get-ChildItem C:\</code>). Cada ejecuci&oacute;n queda registrada.</p>
    <form method="post" action="${ctx}/powershell">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <label>Comando <input type="text" name="comando" required maxlength="300" style="width:100%" placeholder="Get-Date"></label>
        <p><button type="submit">Ejecutar</button></p>
    </form>
</div>
<div class="tarjeta">
    <h2>Historial de ejecuciones</h2>
    <c:forEach var="e" items="${ejecuciones}">
        <div style="margin-bottom:14px">
            <b><c:out value="${e.fechaHoraTexto}"/></b> &middot; admin #<c:out value="${e.administradorId}"/> &middot; <c:out value="${e.ipOrigen}"/>
            <span class="estado ${e.estado}"><c:out value="${e.estado}"/></span><br>
            <code><c:out value="${e.comando}"/></code>
            <pre class="salida"><c:out value="${e.salida}"/></pre>
        </div>
    </c:forEach>
    <c:if test="${empty ejecuciones}"><p class="vacio">Sin ejecuciones.</p></c:if>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
