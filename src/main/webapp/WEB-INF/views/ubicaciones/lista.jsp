<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  HU-09: catálogo de ubicaciones, un nivel a la vez.
  Atributos: nivel (TipoUbicacion), ubicaciones, conteos (id -> cantidad), etiquetaConteo,
             padre, departamento y provincia (según el nivel).
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="ubicaciones"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ubicaciones | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb">
        <a href="${ctx}/ubicaciones">Departamentos</a>
        <c:if test="${not empty departamento}">
            / <a href="${ctx}/ubicaciones?departamentoId=${departamento.id}"><c:out value="${departamento.nombre}"/></a>
        </c:if>
        <c:if test="${not empty provincia}">
            / <c:out value="${provincia.nombre}"/>
        </c:if>
    </p>

    <div class="encabezado">
        <div>
            <h1>${nivel.nombrePlural}<c:if test="${not empty padre}"> de <c:out value="${padre.nombre}"/></c:if></h1>
            <p class="subtitulo">
                <c:choose>
                    <c:when test="${empty padre}">Catálogo de ubicaciones del Perú con códigos de ubigeo del INEI.</c:when>
                    <c:otherwise>${padre.tipo.nombre} con ubigeo <c:out value="${padre.codigo}"/>.</c:otherwise>
                </c:choose>
            </p>
        </div>
        <a class="boton" href="${ctx}/ubicaciones/nuevo?tipo=${nivel.clave}${empty padre ? '' : ('&padreId=' += padre.id)}">+ ${nivel.nuevo} ${nivel.nombre.toLowerCase()}</a>
    </div>

    <c:if test="${not empty param.creado}">
        <div class="alerta alerta-exito"><c:out value="${param.creado}"/> se registró correctamente.</div>
    </c:if>
    <c:if test="${not empty param.actualizado}">
        <div class="alerta alerta-exito"><c:out value="${param.actualizado}"/> se actualizó correctamente.</div>
    </c:if>
    <c:if test="${not empty param.eliminado}">
        <div class="alerta alerta-exito"><c:out value="${param.eliminado}"/> se eliminó correctamente.</div>
    </c:if>

    <section class="tarjeta">
        <h2 class="titulo-tarjeta">${nivel.nombrePlural} ${nivel.registrados}: ${ubicaciones.size()}</h2>
        <div class="tabla-scroll">
            <table>
                <thead>
                <tr>
                    <th>Ubigeo</th>
                    <th>Nombre</th>
                    <th><c:out value="${etiquetaConteo}"/></th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty ubicaciones}">
                        <tr><td colspan="4" class="vacio">No hay ${nivel.nombrePlural.toLowerCase()} ${nivel.registrados}.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${ubicaciones}" var="u">
                            <tr>
                                <td><c:out value="${u.codigo}"/></td>
                                <td><strong><c:out value="${u.nombre}"/></strong></td>
                                <td>${empty conteos[u.id] ? 0 : conteos[u.id]}</td>
                                <td class="acciones-tabla">
                                    <c:choose>
                                        <c:when test="${nivel.clave eq 'departamento'}">
                                            <a href="${ctx}/ubicaciones?departamentoId=${u.id}">Ver provincias</a>
                                        </c:when>
                                        <c:when test="${nivel.clave eq 'provincia'}">
                                            <a href="${ctx}/ubicaciones?provinciaId=${u.id}">Ver distritos</a>
                                        </c:when>
                                    </c:choose>
                                    <a href="${ctx}/ubicaciones/editar?tipo=${nivel.clave}&id=${u.id}">Editar</a>
                                    <a class="enlace-peligro" href="${ctx}/ubicaciones/eliminar?tipo=${nivel.clave}&id=${u.id}">Eliminar</a>
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
