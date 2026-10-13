<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="ubicaciones"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Eliminar ${tipo.nombre.toLowerCase()} | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb"><a href="${ctx}/ubicaciones">Ubicaciones</a> / <a href="${ctx}${urlVolver}">${tipo.nombrePlural}</a> / Eliminar</p>

    <section class="tarjeta confirmacion">
        <h1>¿Eliminar ${tipo.este} ${tipo.nombre.toLowerCase()}?</h1>
        <p class="subtitulo">Está a punto de eliminar el siguiente registro:</p>

        <div class="resumen">
            <div><span>Tipo</span><strong>${tipo.nombre}</strong></div>
            <div><span>Ubigeo</span><strong><c:out value="${ubicacion.codigo}"/></strong></div>
            <div><span>Nombre</span><strong><c:out value="${ubicacion.nombre}"/></strong></div>
            <c:if test="${not empty ubicacion.padre}">
                <div><span>Pertenece a</span><strong><c:out value="${ubicacion.padre.nombre}"/></strong></div>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${not empty motivoBloqueo}">
                <div class="alerta alerta-error"><strong>No se puede eliminar:</strong> <c:out value="${motivoBloqueo}"/></div>
                <div class="acciones">
                    <a class="boton-secundario" href="${ctx}${urlVolver}">Volver</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alerta alerta-error"><strong>Advertencia:</strong> esta acción no se puede deshacer.</div>
                <form method="post" action="${ctx}/ubicaciones/eliminar" class="acciones">
                    <input type="hidden" name="tipo" value="${tipo.clave}">
                    <input type="hidden" name="id" value="${ubicacion.id}">
                    <button class="boton boton-eliminar" type="submit">Confirmar eliminación</button>
                    <a class="boton-secundario" href="${ctx}${urlVolver}">Cancelar</a>
                </form>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
