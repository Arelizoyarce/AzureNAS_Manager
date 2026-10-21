<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Recursos"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Recursos</h1>
<p>
    <a class="boton" href="<c:url value='/reportes'><c:param name='tipo' value='recursos'/><c:param name='q' value='${q}'/><c:param name='tr' value='${tipoSel}'/></c:url>">Exportar a Excel (.xlsx)</a>
</p>
<div class="tarjeta">
    <h2>B&uacute;squeda avanzada</h2>
    <form class="fila" method="get" action="${ctx}/recursos">
        <label>Nombre contiene <input type="text" name="q" value="<c:out value='${q}'/>"></label>
        <label>Tipo
            <select name="tipo">
                <option value="">Todos</option>
                <option value="FILE_SHARE" ${tipoSel == 'FILE_SHARE' ? 'selected' : ''}>FILE_SHARE</option>
                <option value="BLOB_CONTAINER" ${tipoSel == 'BLOB_CONTAINER' ? 'selected' : ''}>BLOB_CONTAINER</option>
            </select>
        </label>
        <button type="submit">Buscar</button>
    </form>
</div>
<div class="tarjeta">
    <c:choose>
        <c:when test="${empty recursos}"><p class="vacio">No hay recursos para mostrar.</p></c:when>
        <c:otherwise>
            <div class="tabla-scroll"><table>
                <tr><th>ID</th><th>Nombre</th><th>Tipo</th><th>Cuenta</th><th>&Aacute;rea</th><th>Tama&ntilde;o</th><th>Cuota (GB)</th><th>&Uacute;ltima modificaci&oacute;n</th><th></th></tr>
                <c:forEach var="r" items="${recursos}">
                    <tr>
                        <td><c:out value="${r.id}"/></td>
                        <td><c:out value="${r.nombre}"/></td>
                        <td><c:out value="${r.tipo}"/></td>
                        <td>#<c:out value="${r.cuentaId}"/></td>
                        <td>#<c:out value="${r.areaId}"/></td>
                        <td><c:out value="${r.tamanoLegible}"/></td>
                        <td><c:out value="${r.cuotaGb}" default="-"/></td>
                        <td><c:out value="${r.ultimaModificacionTexto}"/></td>
                        <td><a class="boton" href="<c:url value='/recursos'><c:param name='accion' value='explorar'/><c:param name='id' value='${r.id}'/></c:url>">Explorar</a></td>
                    </tr>
                </c:forEach>
            </table></div>
        </c:otherwise>
    </c:choose>
</div>
<c:if test="${sessionScope.usuario.admin}">
    <div class="tarjeta">
        <h2>Crear recurso</h2>
        <form class="fila" method="post" action="${ctx}/recursos">
            <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
            <input type="hidden" name="accion" value="crearRecurso">
            <label>Cuenta
                <select name="cuentaId" required>
                    <c:forEach var="c" items="${cuentas}"><option value="${c.id}"><c:out value="${c.nombre}"/></option></c:forEach>
                </select>
            </label>
            <label>&Aacute;rea propietaria
                <select name="areaId" required>
                    <c:forEach var="a" items="${areas}"><option value="${a.id}"><c:out value="${a.nombre}"/></option></c:forEach>
                </select>
            </label>
            <label>Tipo
                <select name="tipo"><option value="FILE_SHARE">FILE_SHARE</option><option value="BLOB_CONTAINER">BLOB_CONTAINER</option></select>
            </label>
            <label>Nombre (3-63, min&uacute;sculas, n&uacute;meros, guiones) <input type="text" name="nombre" required pattern="[a-z0-9][a-z0-9-]{1,61}[a-z0-9]"></label>
            <label>Cuota (GB) <input type="number" name="cuotaGb" min="1"></label>
            <button type="submit">Crear</button>
        </form>
    </div>
    <div class="tarjeta">
        <h2>Asignar acceso a un recurso</h2>
        <form class="fila" method="post" action="${ctx}/recursos">
            <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
            <input type="hidden" name="accion" value="asignarAcceso">
            <label>Usuario
                <select name="usuarioId" required>
                    <c:forEach var="u" items="${usuarios}"><option value="${u.id}"><c:out value="${u.username}"/></option></c:forEach>
                </select>
            </label>
            <label>Recurso
                <select name="recursoId" required>
                    <c:forEach var="r" items="${recursos}"><option value="${r.id}"><c:out value="${r.nombre}"/></option></c:forEach>
                </select>
            </label>
            <label>Nivel
                <select name="nivel"><option value="LECTURA">LECTURA</option><option value="ESCRITURA">ESCRITURA</option><option value="ADMIN">ADMIN</option></select>
            </label>
            <button type="submit">Asignar</button>
        </form>
    </div>
</c:if>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
