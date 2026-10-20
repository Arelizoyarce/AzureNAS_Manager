<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="titulo" value="Explorador"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Explorador: <c:out value="${recurso.nombre}"/></h1>
<div class="tarjeta">
    <p class="miga">
        <a href="<c:url value='/recursos'/>">&laquo; Recursos</a> |
        <a href="<c:url value='/recursos'><c:param name='accion' value='explorar'/><c:param name='id' value='${recurso.id}'/></c:url>">Ra&iacute;z</a>
        <c:if test="${not empty ruta}">
            | <a href="<c:url value='/recursos'><c:param name='accion' value='explorar'/><c:param name='id' value='${recurso.id}'/><c:param name='ruta' value='${padre}'/></c:url>">&uarr; Subir un nivel</a>
            | Ruta: <c:out value="/${ruta}"/>
        </c:if>
    </p>
    <c:choose>
        <c:when test="${empty archivos}"><p class="vacio">Carpeta vac&iacute;a.</p></c:when>
        <c:otherwise>
            <table>
                <tr><th>Nombre</th><th></th></tr>
                <c:forEach var="f" items="${archivos}">
                    <c:set var="destino" value="${ruta}${(empty ruta or fn:endsWith(ruta, '/')) ? '' : '/'}${f}"/>
                    <tr>
                        <c:choose>
                            <c:when test="${fn:endsWith(f, '/')}">
                                <td>&#128193; <a href="<c:url value='/recursos'><c:param name='accion' value='explorar'/><c:param name='id' value='${recurso.id}'/><c:param name='ruta' value='${destino}'/></c:url>"><c:out value="${f}"/></a></td>
                                <td></td>
                            </c:when>
                            <c:otherwise>
                                <td>&#128196; <c:out value="${f}"/></td>
                                <td><a href="<c:url value='/transferencias'><c:param name='accion' value='descargar'/><c:param name='recurso' value='${recurso.id}'/><c:param name='ruta' value='${destino}'/></c:url>">Descargar</a></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
