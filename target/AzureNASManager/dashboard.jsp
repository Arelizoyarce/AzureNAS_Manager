<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Panel principal"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Panel principal</h1>
<div class="grid">
    <div class="dato"><div class="num"><c:out value="${totalRecursos}" default="0"/></div><div class="eti">Recursos disponibles</div></div>
    <c:if test="${sessionScope.usuario.admin}">
        <div class="dato"><div class="num"><c:out value="${totalUsuarios}" default="0"/></div><div class="eti">Usuarios</div></div>
        <div class="dato"><div class="num"><c:out value="${totalCuentas}" default="0"/></div><div class="eti">Cuentas de almacenamiento</div></div>
    </c:if>
</div>
<div class="tarjeta">
    <h2>Transferencias recientes</h2>
    <c:choose>
        <c:when test="${empty transferencias}"><p class="vacio">Todav&iacute;a no hay transferencias.</p></c:when>
        <c:otherwise>
            <div class="tabla-scroll"><table>
                <tr><th>Fecha</th><th>Tipo</th><th>Recurso</th><th>Ruta</th><th>Tama&ntilde;o</th><th>Estado</th></tr>
                <c:forEach var="t" items="${transferencias}">
                    <tr>
                        <td><c:out value="${t.fechaSolicitudTexto}"/></td>
                        <td><c:out value="${t.tipo}"/></td>
                        <td>#<c:out value="${t.recursoId}"/></td>
                        <td><c:out value="${t.rutaRemota}"/></td>
                        <td><c:out value="${t.tamanoLegible}"/></td>
                        <td><span class="estado ${t.estado}"><c:out value="${t.estado}"/></span></td>
                    </tr>
                </c:forEach>
            </table></div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
