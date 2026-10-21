<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Auditoría"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Registro de auditor&iacute;a</h1>
<p><a class="boton" href="${ctx}/reportes?tipo=auditoria">Exportar a Excel (.xlsx)</a></p>
<div class="tarjeta">
    <p>&Uacute;ltimos 200 eventos. La columna <b>Integridad</b> recalcula el hash SHA-256 de cada registro para detectar alteraciones.</p>
    <div class="tabla-scroll"><table>
        <tr><th>ID</th><th>Fecha</th><th>Usuario</th><th>Operaci&oacute;n</th><th>Recurso</th><th>Ruta</th><th>Transf.</th><th>Resultado</th><th>IP</th><th>Detalle</th><th>Integridad</th></tr>
        <c:forEach var="r" items="${registros}">
            <tr>
                <td><c:out value="${r.id}"/></td>
                <td><c:out value="${r.fechaHoraTexto}"/></td>
                <td>#<c:out value="${r.usuarioId}"/></td>
                <td><c:out value="${r.operacion}"/></td>
                <td><c:out value="${r.recursoId}" default="-"/></td>
                <td><c:out value="${r.ruta}"/></td>
                <td><c:out value="${r.transferenciaId}" default="-"/></td>
                <td><span class="estado ${r.resultado}"><c:out value="${r.resultado}"/></span></td>
                <td><c:out value="${r.ipOrigen}"/></td>
                <td><c:out value="${r.detalle}"/></td>
                <td><c:choose><c:when test="${r.integro}">&#10003; OK</c:when><c:otherwise><b style="color:#b02a37">&#10007; Alterado</b></c:otherwise></c:choose></td>
            </tr>
        </c:forEach>
    </table></div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
