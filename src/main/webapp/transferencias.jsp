<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Transferencias"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Transferencias</h1>
<p><a class="boton" href="${ctx}/reportes?tipo=transferencias">Exportar a Excel (.xlsx)</a></p>
<div class="tarjeta">
    <h2>Subir archivo</h2>
    <form class="fila" method="post" enctype="multipart/form-data"
          action="${ctx}/transferencias?_csrf=${sessionScope.csrf}">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="accion" value="subir">
        <label>Recurso destino
            <select name="recursoId" required>
                <c:forEach var="r" items="${recursos}"><option value="${r.id}"><c:out value="${r.nombre}"/></option></c:forEach>
            </select>
        </label>
        <label>Carpeta (opcional) <input type="text" name="carpeta" placeholder="documentos/2026"></label>
        <label>Archivo (m&aacute;x. 100 MB) <input type="file" name="archivo" required></label>
        <label class="check"><input type="checkbox" name="sobrescribir"> Sobrescribir si existe</label>
        <button type="submit">Subir</button>
    </form>
</div>
<div class="tarjeta">
    <h2>Sincronizar recursos</h2>
    <form class="fila" method="post" action="${ctx}/transferencias">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="accion" value="sincronizar">
        <label>Origen
            <select name="origenId" required>
                <c:forEach var="r" items="${recursos}"><option value="${r.id}"><c:out value="${r.nombre}"/></option></c:forEach>
            </select>
        </label>
        <label>Destino
            <select name="destinoId" required>
                <c:forEach var="r" items="${recursos}"><option value="${r.id}"><c:out value="${r.nombre}"/></option></c:forEach>
            </select>
        </label>
        <label class="check"><input type="checkbox" name="sobrescribir"> Sobrescribir existentes</label>
        <button type="submit">Sincronizar</button>
    </form>
</div>
<div class="tarjeta">
    <h2>Historial</h2>
    <c:choose>
        <c:when test="${empty transferencias}"><p class="vacio">Sin transferencias.</p></c:when>
        <c:otherwise>
            <div class="tabla-scroll"><table>
                <tr><th>ID</th><th>Solicitud</th><th>Tipo</th><th>Recurso</th><th>Ruta / archivo</th><th>Tama&ntilde;o</th><th>Objetos</th><th>Progreso</th><th>Estado</th><th>Detalle</th></tr>
                <c:forEach var="t" items="${transferencias}">
                    <tr>
                        <td><c:out value="${t.id}"/></td>
                        <td><c:out value="${t.fechaSolicitudTexto}"/></td>
                        <td><c:out value="${t.tipo}"/></td>
                        <td>#<c:out value="${t.recursoId}"/><c:if test="${not empty t.recursoDestinoId}"> &rarr; #<c:out value="${t.recursoDestinoId}"/></c:if></td>
                        <td><c:out value="${t.rutaRemota}"/></td>
                        <td><c:out value="${t.tamanoLegible}"/></td>
                        <td><c:out value="${t.objetosProcesados}" default="-"/> / <c:out value="${t.objetosTotales}" default="-"/></td>
                        <td><c:out value="${t.progreso}"/>%</td>
                        <td><span class="estado ${t.estado}"><c:out value="${t.estado}"/></span></td>
                        <td><c:out value="${t.mensajeError}"/></td>
                    </tr>
                </c:forEach>
            </table></div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
