<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Áreas"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>&Aacute;reas</h1>
<div class="tarjeta">
    <h2>Nueva &aacute;rea</h2>
    <form class="fila" method="post" action="${ctx}/areas">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <label>Nombre <input type="text" name="nombre" required maxlength="100"></label>
        <button type="submit">Crear</button>
    </form>
</div>
<div class="tarjeta">
    <table>
        <tr><th>ID</th><th>Nombre</th></tr>
        <c:forEach var="a" items="${areas}"><tr><td><c:out value="${a.id}"/></td><td><c:out value="${a.nombre}"/></td></tr></c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
