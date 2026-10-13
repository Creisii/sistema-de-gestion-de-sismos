<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  HU-09: formulario compartido para registrar y editar departamentos, provincias y distritos.
  Atributos: modo ("nuevo" o "editar"), tipo, formulario, padre, errores, urlVolver y ubicacion (al editar).
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="ubicaciones"/>
<c:set var="esEditar" value="${modo eq 'editar'}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${esEditar ? 'Editar' : tipo.nuevo} ${tipo.nombre.toLowerCase()} | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <p class="breadcrumb"><a href="${ctx}/ubicaciones">Ubicaciones</a> /
        <a href="${ctx}${urlVolver}">${tipo.nombrePlural}<c:if test="${not empty padre}"> de <c:out value="${padre.nombre}"/></c:if></a> /
        ${esEditar ? 'Editar' : tipo.nuevo}</p>
    <h1>${esEditar ? 'Editar' : tipo.nuevo} ${tipo.nombre.toLowerCase()}</h1>
    <p class="subtitulo">
        <c:if test="${not empty padre}">Pertenece a ${padre.tipo.nombre.toLowerCase()} <strong><c:out value="${padre.nombre}"/></strong>. </c:if>
        Los campos marcados con * son obligatorios.
    </p>

    <c:if test="${not empty errores}">
        <div class="alerta alerta-error">
            <strong>No se pudo ${esEditar ? 'guardar' : 'registrar'}. Revise los siguientes datos:</strong>
            <ul>
                <c:forEach items="${errores}" var="error"><li><c:out value="${error.value}"/></li></c:forEach>
            </ul>
        </div>
    </c:if>

    <section class="tarjeta formulario-corto">
        <form method="post" action="${ctx}/ubicaciones/${esEditar ? 'editar' : 'nuevo'}" novalidate>
            <input type="hidden" name="tipo" value="${tipo.clave}">
            <c:if test="${esEditar}"><input type="hidden" name="id" value="${ubicacion.id}"></c:if>
            <c:if test="${not esEditar and not empty padre}"><input type="hidden" name="padreId" value="${padre.id}"></c:if>

            <div class="formulario-grid dos-columnas">
                <label>
                    <span class="${esEditar ? '' : 'campo-obligatorio'}">Código de ubigeo</span>
                    <c:choose>
                        <c:when test="${esEditar}">
                            <input value="${fn:escapeXml(formulario.codigo)}" disabled>
                            <span class="ayuda">El código de ubigeo no se modifica.</span>
                        </c:when>
                        <c:otherwise>
                            <input name="codigo" maxlength="${tipo.longitudCodigo}" inputmode="numeric"
                                   value="${fn:escapeXml(formulario.codigo)}"
                                   class="${not empty errores.codigo ? 'campo-error' : ''}">
                            <c:choose>
                                <c:when test="${not empty errores.codigo}"><span class="ayuda-error"><c:out value="${errores.codigo}"/></span></c:when>
                                <c:otherwise><span class="ayuda">${tipo.longitudCodigo} dígitos<c:if test="${not empty padre}">, empieza con ${padre.codigo}</c:if>.</span></c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </label>
                <label>
                    <span class="campo-obligatorio">Nombre</span>
                    <input name="nombre" maxlength="80" value="${fn:escapeXml(formulario.nombre)}"
                           class="${not empty errores.nombre ? 'campo-error' : ''}">
                    <c:if test="${not empty errores.nombre}"><span class="ayuda-error"><c:out value="${errores.nombre}"/></span></c:if>
                </label>
            </div>

            <div class="acciones" style="margin-top: 20px;">
                <button class="boton" type="submit">${esEditar ? 'Guardar cambios' : 'Guardar'}</button>
                <a class="boton-secundario" href="${ctx}${urlVolver}">Cancelar</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>
