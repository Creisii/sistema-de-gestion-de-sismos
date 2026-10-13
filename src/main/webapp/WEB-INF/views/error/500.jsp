<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value=""/>
<%-- Se muestra cuando ocurre una PersistenciaException no controlada (configurado en web.xml). --%>
<c:set var="mensaje" value="${not empty mensajeError ? mensajeError : requestScope['jakarta.servlet.error.message']}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error | CSSP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <h1>No se pudo completar la operación</h1>
    <section class="tarjeta">
        <div class="alerta alerta-error">
            <c:out value="${empty mensaje ? 'Ocurrió un error al acceder a la base de datos.' : mensaje}"/>
        </div>
        <p class="subtitulo">Si el problema continúa, verifique que MySQL esté iniciado y que la base de datos
            <strong>cssp_sismos</strong> exista (scripts de la carpeta <code>database</code>).</p>
        <a class="boton" href="${pageContext.request.contextPath}/sismos">Volver a la lista de sismos</a>
    </section>
</main>
</body>
</html>
