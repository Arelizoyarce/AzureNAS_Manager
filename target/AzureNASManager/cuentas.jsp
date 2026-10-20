<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="titulo" value="Cuentas de almacenamiento"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<h1>Cuentas de almacenamiento</h1>
<div class="tarjeta">
    <h2>Nueva cuenta (Azure Storage Account simulada)</h2>
    <form class="fila" method="post" action="${ctx}/cuentas">
        <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
        <label>Nombre (3-24, min&uacute;sculas y n&uacute;meros) <input type="text" name="nombre" required pattern="[a-z0-9]{3,24}"></label>
        <label>Regi&oacute;n <input type="text" name="region" value="eastus" required maxlength="50"></label>
        <label>Tipo de credencial
            <select name="tipoCredencial">
                <option value="ACCOUNT_KEY">ACCOUNT_KEY</option>
                <option value="SAS">SAS</option>
                <option value="CONNECTION_STRING">CONNECTION_STRING</option>
            </select>
        </label>
        <label>Credencial (se guarda cifrada) <input type="password" name="credencial" required autocomplete="off"></label>
        <label>Espacio (GB) <input type="number" name="espacioGb" min="1" value="100" required></label>
        <button type="submit">Crear</button>
    </form>
</div>
<div class="tarjeta">
    <table>
        <tr><th>ID</th><th>Nombre</th><th>Regi&oacute;n</th><th>Credencial</th><th>Espacio asignado</th></tr>
        <c:forEach var="c" items="${cuentas}">
            <tr>
                <td><c:out value="${c.id}"/></td>
                <td><c:out value="${c.nombre}"/></td>
                <td><c:out value="${c.region}"/></td>
                <td><c:out value="${c.tipoCredencial}"/> (cifrada)</td>
                <td><c:out value="${c.espacioLegible}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
