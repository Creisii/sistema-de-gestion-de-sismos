<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value=""/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>No encontrado | CSSP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <h1>404 - No encontrado</h1>
    <section class="tarjeta">
        <p><c:out value="${empty mensajeError ? 'El recurso solicitado no existe.' : mensajeError}"/></p>
        <a class="boton" href="${pageContext.request.contextPath}/sismos">Volver a la lista de sismos</a>
    </section>
</main>
</body>
</html>
