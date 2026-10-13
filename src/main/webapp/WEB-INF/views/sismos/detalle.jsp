<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="seccion" value="sismos"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle | CSSP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb"><a href="${pageContext.request.contextPath}/sismos">Sismos</a> / <c:out value="${sismo.codigo}"/></p>

    <c:if test="${sismoCreado}">
        <div class="alerta alerta-exito">Sismo registrado correctamente.</div>
    </c:if>
    <c:if test="${param.actualizado eq '1'}">
        <div class="alerta alerta-exito">Sismo actualizado correctamente.</div>
    </c:if>

    <div style="display:flex; justify-content: space-between; align-items: flex-start;">
        <div>
            <h1 style="display:inline-block; margin-right:10px;">Sismo <c:out value="${sismo.codigo}"/></h1>
            <span class="${sismo.claseEstado}"><c:out value="${sismo.estado}"/></span>
            <p class="subtitulo"><c:out value="${sismo.distrito.ubicacionCompleta}"/></p>
        </div>
        <div class="acciones">
            <a class="boton" href="${pageContext.request.contextPath}/sismos/editar?id=${sismo.id}">Editar</a>
            <a class="boton-peligro" href="${pageContext.request.contextPath}/sismos/eliminar?id=${sismo.id}">Eliminar</a>
            <a class="boton-secundario" href="${pageContext.request.contextPath}/sismos">Volver a la lista</a>
        </div>
    </div>

    <section class="tarjeta" style="margin-top:16px;">
        <div class="detalle-grid">
            <div><div class="etiqueta">Código</div><div class="valor"><c:out value="${sismo.codigo}"/></div></div>
            <div><div class="etiqueta">Fecha y hora</div><div class="valor"><c:out value="${sismo.fechaHoraTexto}"/></div></div>
            <div><div class="etiqueta">Estado</div><div class="valor"><span class="${sismo.claseEstado}"><c:out value="${sismo.estado}"/></span></div></div>

            <div><div class="etiqueta">Magnitud</div><div class="valor">M <c:out value="${sismo.magnitud}"/></div></div>
            <div><div class="etiqueta">Profundidad</div><div class="valor"><c:out value="${sismo.profundidad}"/> km</div></div>
            <div><div class="etiqueta">Intensidad</div><div class="valor"><c:out value="${sismo.intensidad}"/></div></div>

            <div><div class="etiqueta">Latitud</div><div class="valor"><c:out value="${sismo.latitud}"/></div></div>
            <div><div class="etiqueta">Longitud</div><div class="valor"><c:out value="${sismo.longitud}"/></div></div>
            <div><div class="etiqueta">Estación</div><div class="valor"><c:out value="${empty sismo.estacion ? 'Sin estación' : sismo.estacion.codigo.concat(' · ').concat(sismo.estacion.nombre)}"/></div></div>

            <div><div class="etiqueta">Departamento</div><div class="valor"><c:out value="${sismo.distrito.provincia.departamento.nombre}"/></div></div>
            <div><div class="etiqueta">Provincia</div><div class="valor"><c:out value="${sismo.distrito.provincia.nombre}"/></div></div>
            <div><div class="etiqueta">Distrito</div><div class="valor"><c:out value="${sismo.distrito.nombre}"/> <span class="nota">(ubigeo <c:out value="${sismo.distrito.codigo}"/>)</span></div></div>
            <div><div class="etiqueta">Referencia</div><div class="valor"><c:out value="${empty sismo.referencia ? '—' : sismo.referencia}"/></div></div>
        </div>
    </section>
</main>
</body>
</html>