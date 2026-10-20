<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Usuarios"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Usuarios</h1>
<div class="tarjeta">
    <h2>Registrar usuario</h2>
    <form class="fila" method="post" action="${ctx}/UsuarioServlet">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="accion" value="registrar">
        <label>Usuario <input type="text" name="username" required minlength="3" maxlength="50"></label>
        <label>Nombre completo <input type="text" name="nombreCompleto" required maxlength="150"></label>
        <label>Contrase&ntilde;a <input type="password" name="password" required minlength="8"></label>
        <label>Rol
            <select name="rol">
                <option value="OPERADOR">OPERADOR</option>
                <option value="LECTOR">LECTOR</option>
                <option value="ADMIN">ADMIN</option>
            </select>
        </label>
        <label>&Aacute;rea
            <select name="areaId">
                <option value="">(sin &aacute;rea)</option>
                <c:forEach var="a" items="${areas}"><option value="${a.id}"><c:out value="${a.nombre}"/></option></c:forEach>
            </select>
        </label>
        <button type="submit">Registrar</button>
    </form>
</div>
<div class="tarjeta">
    <h2>Lista de usuarios</h2>
    <div class="tabla-scroll"><table>
        <tr><th>ID</th><th>Usuario</th><th>Nombre</th><th>Rol</th><th>&Aacute;rea</th><th>Activo</th><th>Creado</th><th>&Uacute;ltimo acceso</th><th></th></tr>
        <c:forEach var="u" items="${usuarios}">
            <tr>
                <td><c:out value="${u.id}"/></td>
                <td><c:out value="${u.username}"/></td>
                <td><c:out value="${u.nombreCompleto}"/></td>
                <td><c:out value="${u.rol}"/></td>
                <td><c:out value="${u.areaId}" default="-"/></td>
                <td><c:out value="${u.activo ? 'S&iacute;' : 'No'}" escapeXml="false"/></td>
                <td><c:out value="${u.fechaCreacionTexto}"/></td>
                <td><c:out value="${u.ultimoAccesoTexto}" default="-"/></td>
                <td>
                    <form method="post" action="${ctx}/UsuarioServlet" style="margin:0">
                        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="id" value="${u.id}">
                        <input type="hidden" name="activo" value="${u.activo ? 'false' : 'true'}">
                        <button type="submit" class="${u.activo ? 'peligro' : 'sec'}">${u.activo ? 'Desactivar' : 'Activar'}</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table></div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
