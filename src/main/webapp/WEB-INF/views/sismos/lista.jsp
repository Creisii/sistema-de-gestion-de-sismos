<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="sismos"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sismos | CSSP</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/views/comun/barra.jspf" %>
<main class="contenedor">
    <div class="encabezado">
        <div>
            <h1>Sismos registrados</h1>
            <p class="subtitulo">Busque, filtre y ordene los eventos sísmicos registrados.</p>
        </div>
        <a class="boton" href="${ctx}/sismos/nuevo">+ Nuevo sismo</a>
    </div>

    <c:if test="${not empty param.eliminado}">
        <div class="alerta alerta-exito">El sismo <c:out value="${param.eliminado}"/> se eliminó correctamente.</div>
    </c:if>
    <c:if test="${not empty busqueda.errores}">
        <div class="alerta alerta-error">
            <strong>Algunos filtros no se aplicaron:</strong>
            <ul>
                <c:forEach items="${busqueda.errores}" var="error"><li><c:out value="${error}"/></li></c:forEach>
            </ul>
        </div>
    </c:if>

    <%-- HU-15: filtros por departamento, rango de magnitud y periodo; HU-31: código o ubicación --%>
    <section class="tarjeta">
        <form method="get" action="${ctx}/sismos" class="filtros">
            <label>
                <span>Código o referencia geográfica</span>
                <input name="buscar" type="search" maxlength="150" placeholder="Ej. SIS-2026-001, Chivay o Caylloma"
                       value="<c:out value='${busqueda.terminoTexto}'/>">
            </label>
            <label>
                <span>Departamento</span>
                <select name="departamentoId">
                    <option value="">Todos</option>
                    <c:forEach items="${departamentos}" var="d">
                        <option value="${d.id}" ${busqueda.departamentoIdTexto eq d.id.toString() ? 'selected' : ''}><c:out value="${d.nombre}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>Estado</span>
                <select name="estado">
                    <option value="">Todos</option>
                    <option value="Registrado" ${busqueda.estado eq 'Registrado' ? 'selected' : ''}>Registrado</option>
                    <option value="En evaluación" ${busqueda.estado eq 'En evaluación' ? 'selected' : ''}>En evaluación</option>
                    <option value="En seguimiento" ${busqueda.estado eq 'En seguimiento' ? 'selected' : ''}>En seguimiento</option>
                </select>
            </label>
            <label>
                <span>Magnitud desde</span>
                <input name="magnitudMin" type="number" step="0.1" min="0" max="10" placeholder="Ej. 4.0"
                       value="<c:out value='${busqueda.magnitudMinTexto}'/>">
            </label>
            <label>
                <span>Magnitud hasta</span>
                <input name="magnitudMax" type="number" step="0.1" min="0" max="10" placeholder="Ej. 6.0"
                       value="<c:out value='${busqueda.magnitudMaxTexto}'/>">
            </label>
            <label>
                <span>Fecha desde</span>
                <input name="desde" type="date" value="<c:out value='${busqueda.desdeTexto}'/>">
            </label>
            <label>
                <span>Fecha hasta</span>
                <input name="hasta" type="date" value="<c:out value='${busqueda.hastaTexto}'/>">
            </label>
            <input type="hidden" name="orden" value="${busqueda.orden}">
            <input type="hidden" name="dir" value="${busqueda.descendente ? 'desc' : 'asc'}">
            <div class="acciones">
                <button class="boton" type="submit">Buscar</button>
                <a class="boton-secundario" href="${ctx}/sismos">Limpiar</a>
            </div>
        </form>
    </section>

    <section class="tarjeta">
        <div class="barra-resultados">
            <span>
                <c:choose>
                    <c:when test="${pagina.totalRegistros == 0}">No se encontraron sismos</c:when>
                    <c:otherwise>Mostrando <strong>${pagina.desde}</strong> a <strong>${pagina.hasta}</strong>
                        de <strong>${pagina.totalRegistros}</strong> sismos</c:otherwise>
                </c:choose>
                <c:if test="${busqueda.tieneFiltros}"> con los filtros aplicados</c:if>
            </span>
            <span class="nota">Haga clic en una columna para ordenar.</span>
        </div>
        <div class="tabla-scroll">
            <table>
                <thead>
                <tr>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('codigo')}">Código${busqueda.indicadorOrden('codigo')}</a></th>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('fecha')}">Fecha y hora${busqueda.indicadorOrden('fecha')}</a></th>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('magnitud')}">Magnitud${busqueda.indicadorOrden('magnitud')}</a></th>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('profundidad')}">Profundidad${busqueda.indicadorOrden('profundidad')}</a></th>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('departamento')}">Departamento${busqueda.indicadorOrden('departamento')}</a></th>
                    <th>Distrito</th>
                    <th><a href="${ctx}/sismos${busqueda.urlOrden('estado')}">Estado${busqueda.indicadorOrden('estado')}</a></th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty sismos}">
                        <tr><td colspan="8" class="vacio">
                            <c:choose>
                                <c:when test="${not empty busqueda.terminoTexto}">
                                    No se encontraron sismos para &quot;<c:out value="${busqueda.terminoTexto}"/>&quot;.
                                    Verifique el código o la referencia geográfica.
                                </c:when>
                                <c:when test="${busqueda.tieneFiltros}">Ningún sismo cumple los filtros indicados.</c:when>
                                <c:otherwise>No hay sismos registrados.</c:otherwise>
                            </c:choose>
                        </td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${sismos}" var="sismo">
                            <tr>
                                <td><c:out value="${sismo.codigo}"/></td>
                                <td><c:out value="${sismo.fechaHoraTexto}"/></td>
                                <td><strong>M <fmt:formatNumber value="${sismo.magnitud}" minFractionDigits="1" maxFractionDigits="1"/></strong></td>
                                <td><fmt:formatNumber value="${sismo.profundidad}" maxFractionDigits="1"/> km</td>
                                <td><c:out value="${sismo.distrito.provincia.departamento.nombre}"/></td>
                                <td><c:out value="${sismo.distrito.nombre}"/></td>
                                <td><span class="${sismo.claseEstado}"><c:out value="${sismo.estado}"/></span></td>
                                <td class="acciones-tabla">
                                    <a href="${ctx}/sismos/detalle?id=${sismo.id}">Ver</a>
                                    <a href="${ctx}/sismos/editar?id=${sismo.id}">Editar</a>
                                    <a class="enlace-peligro" href="${ctx}/sismos/eliminar?id=${sismo.id}">Eliminar</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <%-- HU-15: navegación entre páginas --%>
        <c:if test="${pagina.totalPaginas > 1}">
            <nav class="paginacion">
                <c:choose>
                    <c:when test="${pagina.tieneAnterior}"><a href="${ctx}/sismos${busqueda.urlPagina(pagina.numero - 1)}">« Anterior</a></c:when>
                    <c:otherwise><span class="deshabilitado">« Anterior</span></c:otherwise>
                </c:choose>
                <c:forEach begin="1" end="${pagina.totalPaginas}" var="n">
                    <c:choose>
                        <c:when test="${n == pagina.numero}"><span class="actual">${n}</span></c:when>
                        <c:otherwise><a href="${ctx}/sismos${busqueda.urlPagina(n)}">${n}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:choose>
                    <c:when test="${pagina.tieneSiguiente}"><a href="${ctx}/sismos${busqueda.urlPagina(pagina.numero + 1)}">Siguiente »</a></c:when>
                    <c:otherwise><span class="deshabilitado">Siguiente »</span></c:otherwise>
                </c:choose>
            </nav>
        </c:if>
    </section>
</main>
</body>
</html>
