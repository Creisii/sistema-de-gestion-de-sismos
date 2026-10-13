<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value="sismos"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Eliminar sismo | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb"><a href="${ctx}/sismos">Sismos</a> /
        <a href="${ctx}/sismos/detalle?id=${sismo.id}"><c:out value="${sismo.codigo}"/></a> / Eliminar</p>

    <section class="tarjeta confirmacion">
        <h1>¿Eliminar este sismo?</h1>
        <p class="subtitulo">Está a punto de eliminar el siguiente registro:</p>

        <div class="resumen">
            <div><span>Código</span><strong><c:out value="${sismo.codigo}"/></strong></div>
            <div><span>Fecha y hora</span><strong><c:out value="${sismo.fechaHoraTexto}"/></strong></div>
            <div><span>Magnitud</span><strong>M <c:out value="${sismo.magnitud}"/></strong></div>
            <div><span>Ubicación</span><strong><c:out value="${sismo.distrito.ubicacionCompleta}"/></strong></div>
            <div><span>Estado</span><strong><span class="${sismo.claseEstado}"><c:out value="${sismo.estado}"/></span></strong></div>
        </div>

        <c:choose>
            <c:when test="${not empty motivoBloqueo}">
                <div class="alerta alerta-error"><strong>No se puede eliminar:</strong> <c:out value="${motivoBloqueo}"/></div>
                <div class="acciones">
                    <a class="boton" href="${ctx}/sismos/editar?id=${sismo.id}">Editar estado</a>
                    <a class="boton-secundario" href="${ctx}/sismos/detalle?id=${sismo.id}">Volver</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alerta alerta-error"><strong>Advertencia:</strong> esta acción no se puede deshacer.
                    Los sismos en estado <strong>En seguimiento</strong> no pueden eliminarse.</div>
                <form method="post" action="${ctx}/sismos/eliminar" class="acciones">
                    <input type="hidden" name="id" value="${sismo.id}">
                    <button class="boton boton-eliminar" type="submit">Confirmar eliminación</button>
                    <a class="boton-secundario" href="${ctx}/sismos/detalle?id=${sismo.id}">Cancelar</a>
                </form>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
