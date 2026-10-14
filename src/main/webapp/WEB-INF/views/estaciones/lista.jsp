<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="estaciones"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Estaciones | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <div class="encabezado">
        <div>
            <h1>Estaciones de monitoreo</h1>
            <p class="subtitulo">Estaciones que participan en la captura de eventos sísmicos.</p>
        </div>
        <a class="boton" href="${ctx}/estaciones/nuevo">+ Nueva estación</a>
    </div>

    <c:if test="${not empty param.creado}">
        <div class="alerta alerta-exito">La estación <c:out value="${param.creado}"/> se registró correctamente.</div>
    </c:if>
    <c:if test="${not empty param.actualizado}">
        <div class="alerta alerta-exito">La estación <c:out value="${param.actualizado}"/> se actualizó correctamente.</div>
    </c:if>

    <section class="tarjeta">
        <h2 class="titulo-tarjeta">Estaciones registradas: <c:out value="${totalEstaciones}"/></h2>
        <div class="tabla-scroll">
            <table>
                <thead>
                <tr>
                    <th>Código</th><th>Nombre</th><th>Ubicación</th>
                    <th>Instalación</th><th>Estado</th><th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty estaciones}">
                        <tr><td colspan="6" class="vacio">No hay estaciones registradas.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${estaciones}" var="est">
                            <tr>
                                <td><c:out value="${est.codigo}"/></td>
                                <td><c:out value="${est.nombre}"/></td>
                                <td><c:out value="${est.distrito.nombre}"/>, <c:out value="${est.distrito.provincia.nombre}"/></td>
                                <td><c:out value="${est.fechaInstalacionTexto}"/></td>
                                <td>
                                    <span class="badge ${est.activa ? 'badge-registrado' : 'badge-cerrado'}">
                                        <c:out value="${est.estado}"/>
                                    </span>
                                </td>
                                <td class="acciones-tabla">
                                    <a href="${ctx}/estaciones/editar?id=${est.id}">Editar</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>
</body>
</html>