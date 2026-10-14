<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="esEditar" value="${modo eq 'editar'}"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="estaciones"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${esEditar ? 'Editar estación' : 'Nueva estación'} | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb"><a href="${ctx}/estaciones">Estaciones</a> / ${esEditar ? 'Editar' : 'Nueva estación'}</p>
    <h1>${esEditar ? 'Editar estación' : 'Nueva estación'} <c:if test="${esEditar}"><c:out value="${formulario.codigo}"/></c:if></h1>
    <p class="subtitulo">Los campos marcados con * son obligatorios.</p>

    <c:if test="${not empty errores}">
        <div class="alerta alerta-error">
            <strong>No se pudo ${esEditar ? 'guardar' : 'registrar'} la estación. Revise los siguientes datos:</strong>
            <ul>
                <c:forEach items="${errores}" var="error">
                    <li><c:out value="${error.value}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <section class="tarjeta">
        <form method="post" action="${ctx}/estaciones/${esEditar ? 'editar' : 'nuevo'}" novalidate>
            <c:if test="${esEditar}">
                <input type="hidden" name="id" value="${estacionId}">
            </c:if>

            <div class="formulario-grid">
                <label>
                    <span class="campo-obligatorio">Código</span>
                    <input name="codigo" maxlength="10" placeholder="Ej. ARE-02"
                           value="${fn:escapeXml(formulario.codigo)}"
                           class="${not empty errores.codigo ? 'campo-error' : ''}">
                    <c:if test="${not empty errores.codigo}"><span class="ayuda-error"><c:out value="${errores.codigo}"/></span></c:if>
                </label>
                <label>
                    <span class="campo-obligatorio">Nombre</span>
                    <input name="nombre" maxlength="100" placeholder="Ej. Estación Arequipa Norte"
                           value="${fn:escapeXml(formulario.nombre)}"
                           class="${not empty errores.nombre ? 'campo-error' : ''}">
                    <c:if test="${not empty errores.nombre}"><span class="ayuda-error"><c:out value="${errores.nombre}"/></span></c:if>
                </label>
                <label>
                    <span class="campo-obligatorio">Distrito</span>
                    <select name="distritoId" class="${not empty errores.distritoId ? 'campo-error' : ''}">
                        <option value="">Seleccione el distrito</option>
                        <c:set var="grupoActual" value=""/>
                        <c:forEach items="${distritos}" var="dis" varStatus="st">
                            <c:set var="grupo" value="${dis.provincia.departamento.nombre} / ${dis.provincia.nombre}"/>
                            <c:if test="${grupo ne grupoActual}">
                                <c:if test="${not st.first}"></optgroup></c:if>
                                <optgroup label="<c:out value='${grupo}'/>">
                                <c:set var="grupoActual" value="${grupo}"/>
                            </c:if>
                            <option value="${dis.id}" ${dis.id.toString() eq formulario.distritoId ? 'selected' : ''}><c:out value="${dis.nombre}"/></option>
                            <c:if test="${st.last}"></optgroup></c:if>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty errores.distritoId}"><span class="ayuda-error"><c:out value="${errores.distritoId}"/></span></c:if>
                </label>

                <label>
                    <span class="campo-obligatorio">Latitud</span>
                    <input name="latitud" type="number" step="0.0001"
                           value="${fn:escapeXml(formulario.latitud)}"
                           class="${not empty errores.latitud ? 'campo-error' : ''}">
                    <c:choose>
                        <c:when test="${not empty errores.latitud}"><span class="ayuda-error"><c:out value="${errores.latitud}"/></span></c:when>
                        <c:otherwise><span class="ayuda">Entre -90 y 90.</span></c:otherwise>
                    </c:choose>
                </label>
                <label>
                    <span class="campo-obligatorio">Longitud</span>
                    <input name="longitud" type="number" step="0.0001"
                           value="${fn:escapeXml(formulario.longitud)}"
                           class="${not empty errores.longitud ? 'campo-error' : ''}">
                    <c:choose>
                        <c:when test="${not empty errores.longitud}"><span class="ayuda-error"><c:out value="${errores.longitud}"/></span></c:when>
                        <c:otherwise><span class="ayuda">Entre -180 y 180.</span></c:otherwise>
                    </c:choose>
                </label>
                <label>
                    <span class="campo-obligatorio">Fecha de instalación</span>
                    <input name="fechaInstalacion" type="date"
                           value="${fn:escapeXml(formulario.fechaInstalacion)}"
                           class="${not empty errores.fechaInstalacion ? 'campo-error' : ''}">
                    <c:choose>
                        <c:when test="${not empty errores.fechaInstalacion}"><span class="ayuda-error"><c:out value="${errores.fechaInstalacion}"/></span></c:when>
                        <c:otherwise><span class="ayuda">No puede ser una fecha futura.</span></c:otherwise>
                    </c:choose>
                </label>
                <label>
                    <span class="campo-obligatorio">Estado</span>
                    <select name="estado" class="${not empty errores.estado ? 'campo-error' : ''}">
                        <c:forEach items="${estados}" var="est">
                            <option value="${est}" ${est eq formulario.estado ? 'selected' : ''}>${est}</option>
                        </c:forEach>
                    </select>
                    <c:choose>
                        <c:when test="${not empty errores.estado}"><span class="ayuda-error"><c:out value="${errores.estado}"/></span></c:when>
                        <c:otherwise><span class="ayuda">Una estación inactiva no puede recibir nuevos sismos.</span></c:otherwise>
                    </c:choose>
                </label>
            </div>

            <div class="acciones" style="margin-top: 20px;">
                <button class="boton" type="submit">${esEditar ? 'Guardar cambios' : 'Guardar'}</button>
                <a class="boton-secundario" href="${ctx}/estaciones">Cancelar</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>