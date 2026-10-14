<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Formulario compartido para Nuevo (HU-02) y Editar (HU-06).
  Atributos que debe enviar el servlet:
    modo         -> "nuevo" o "editar"
    formulario   -> SismoFormulario con los valores a mostrar
    errores      -> Map campo -> mensaje (vacío si no hay errores)
    distritos, estaciones, intensidades, estados -> listas para los <select>
    sismoId      -> solo en modo "editar"

  HU-21: el distrito se elige en cascada Departamento -> Provincia -> Distrito.
  Solo el <select name="distritoId"> se envía al servidor; los otros dos son
  auxiliares en el navegador para filtrar las opciones, con los datos de ${distritos}.
--%>
<c:set var="esEditar" value="${modo eq 'editar'}"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="seccion" value="sismos"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${esEditar ? 'Editar sismo' : 'Nuevo sismo'} | CSSP</title>
        <link rel="stylesheet" href="${ctx}/css/estilos.css">
    </head>
    <body>
        <%@ include file="/WEB-INF/views/comun/barra.jspf" %>
        <main class="contenedor">
            <c:choose>
                <c:when test="${esEditar}">
                    <p class="breadcrumb"><a href="${ctx}/sismos">Sismos</a> /
                        <a href="${ctx}/sismos/detalle?id=${sismoId}"><c:out value="${formulario.codigo}"/></a> / Editar</p>
                    <h1>Editar sismo <c:out value="${formulario.codigo}"/></h1>
                    <p class="subtitulo">Se aplican las mismas validaciones que en el registro. Los campos marcados con * son obligatorios.</p>
                </c:when>
                <c:otherwise>
                    <p class="breadcrumb"><a href="${ctx}/sismos">Sismos</a> / Nuevo sismo</p>
                    <h1>Nuevo sismo</h1>
                    <p class="subtitulo">Los campos marcados con * son obligatorios.</p>
                </c:otherwise>
            </c:choose>

            <%-- Zona de mensajes de validación: resumen de todos los errores --%>
            <c:if test="${not empty errores}">
                <div class="alerta alerta-error">
                    <strong>No se pudo ${esEditar ? 'guardar' : 'registrar'} el sismo. Revise los siguientes datos:</strong>
                    <ul>
                        <c:forEach items="${errores}" var="error">
                            <li><c:out value="${error.value}"/></li>
                            </c:forEach>
                    </ul>
                </div>
            </c:if>

            <section class="tarjeta">
                <form method="post" action="${ctx}/sismos/${esEditar ? 'editar' : 'nuevo'}" novalidate>
                    <c:if test="${esEditar}">
                        <input type="hidden" name="id" value="${sismoId}">
                    </c:if>

                    <div class="seccion-titulo">Datos del evento</div>
                    <div class="formulario-grid">
                        <label>
                            <span class="campo-obligatorio">Código</span>
                            <input name="codigo" maxlength="20" placeholder="Ej. SIS-006"
                                   value="${fn:escapeXml(formulario.codigo)}"
                                   class="${not empty errores.codigo ? 'campo-error' : ''}">
                            <c:if test="${not empty errores.codigo}"><span class="ayuda-error"><c:out value="${errores.codigo}"/></span></c:if>
                            </label>
                            <label>
                                <span class="campo-obligatorio">Fecha y hora</span>
                                <input name="fechaHora" type="datetime-local"
                                       value="${fn:escapeXml(formulario.fechaHora)}"
                                class="${not empty errores.fechaHora ? 'campo-error' : ''}">
                            <c:choose>
                                <c:when test="${not empty errores.fechaHora}"><span class="ayuda-error"><c:out value="${errores.fechaHora}"/></span></c:when>
                                <c:otherwise><span class="ayuda">No puede ser una fecha futura.</span></c:otherwise>
                            </c:choose>
                        </label>
                        <label>
                            <span>Intensidad</span>
                            <select name="intensidad" class="${not empty errores.intensidad ? 'campo-error' : ''}">
                                <option value="">Sin especificar</option>
                                <c:forEach items="${intensidades}" var="i">
                                    <option value="${i}" ${i eq formulario.intensidad ? 'selected' : ''}>${i}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty errores.intensidad}"><span class="ayuda-error"><c:out value="${errores.intensidad}"/></span></c:if>
                            </label>

                            <label>
                                <span class="campo-obligatorio">Magnitud</span>
                                <input name="magnitud" type="number" step="0.1"
                                       value="${fn:escapeXml(formulario.magnitud)}"
                                class="${not empty errores.magnitud ? 'campo-error' : ''}">
                            <c:choose>
                                <c:when test="${not empty errores.magnitud}"><span class="ayuda-error"><c:out value="${errores.magnitud}"/></span></c:when>
                                <c:otherwise><span class="ayuda">Debe ser mayor que cero.</span></c:otherwise>
                            </c:choose>
                        </label>
                        <label>
                            <span class="campo-obligatorio">Profundidad (km)</span>
                            <input name="profundidad" type="number" step="0.1"
                                   value="${fn:escapeXml(formulario.profundidad)}"
                                   class="${not empty errores.profundidad ? 'campo-error' : ''}">
                            <c:choose>
                                <c:when test="${not empty errores.profundidad}"><span class="ayuda-error"><c:out value="${errores.profundidad}"/></span></c:when>
                                <c:otherwise><span class="ayuda">No puede ser negativa.</span></c:otherwise>
                            </c:choose>
                        </label>
                        <label>
                            <span>Estación que lo registró</span>
                            <select name="estacionId" class="${not empty errores.estacionId ? 'campo-error' : ''}">
                                <option value="">Sin estación</option>
                                <c:forEach items="${estaciones}" var="e">
                                    <c:set var="seleccionada" value="${e.id.toString() eq formulario.estacionId}"/>
                                    <option value="${e.id}" ${seleccionada ? 'selected' : ''} ${!e.activa and !seleccionada ? 'disabled' : ''}>
                                        <c:out value="${e.codigo} · ${e.nombre}"/>${e.activa ? '' : ' (inactiva)'}
                                    </option>
                                </c:forEach>
                            </select>
                            <c:choose>
                                <c:when test="${not empty errores.estacionId}"><span class="ayuda-error"><c:out value="${errores.estacionId}"/></span></c:when>
                                <c:otherwise><span class="ayuda">Las estaciones inactivas no pueden recibir nuevos registros.</span></c:otherwise>
                            </c:choose>
                        </label>
                    </div>

                    <div class="seccion-titulo">Ubicación</div>
                    <div class="formulario-grid">
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
                            <span class="campo-obligatorio">Departamento</span>
                            <select id="cascadaDepartamento" onchange="alCambiarDepartamento()">
                                <option value="">Seleccione el departamento</option>
                            </select>
                            <span class="ayuda">Filtra las provincias disponibles.</span>
                        </label>
                        <label>
                            <span class="campo-obligatorio">Provincia</span>
                            <select id="cascadaProvincia" onchange="alCambiarProvincia()" disabled>
                                <option value="">Seleccione primero el departamento</option>
                            </select>
                            <span class="ayuda">Filtra los distritos disponibles.</span>
                        </label>
                        <label>
                            <span class="campo-obligatorio">Distrito</span>
                            <select name="distritoId" id="cascadaDistrito" disabled
                                    class="${not empty errores.distritoId ? 'campo-error' : ''}">
                                <option value="">Seleccione primero la provincia</option>
                            </select>
                            <c:choose>
                                <c:when test="${not empty errores.distritoId}"><span class="ayuda-error"><c:out value="${errores.distritoId}"/></span></c:when>
                                <c:otherwise><span class="ayuda">Ubicación exacta del sismo.</span></c:otherwise>
                            </c:choose>
                        </label>

                        <label>
                            <span>Referencia</span>
                            <input name="referencia" maxlength="150" placeholder="Ej. 12 km al SO de Chivay"
                                   value="${fn:escapeXml(formulario.referencia)}"
                                   class="${not empty errores.referencia ? 'campo-error' : ''}">
                            <c:if test="${not empty errores.referencia}"><span class="ayuda-error"><c:out value="${errores.referencia}"/></span></c:if>
                            </label>
                        <c:choose>
                            <c:when test="${esEditar}">
                                <label>
                                    <span class="campo-obligatorio">Estado</span>
                                    <select name="estado" class="${not empty errores.estado ? 'campo-error' : ''}">
                                        <c:forEach items="${estados}" var="est">
                                            <option value="${est}" ${est eq formulario.estado ? 'selected' : ''}>${est}</option>
                                        </c:forEach>
                                    </select>
                                    <c:choose>
                                        <c:when test="${not empty errores.estado}"><span class="ayuda-error"><c:out value="${errores.estado}"/></span></c:when>
                                        <c:otherwise><span class="ayuda">Registrado · En evaluación · En seguimiento · Cerrado</span></c:otherwise>
                                    </c:choose>
                                </label>
                            </c:when>
                            <c:otherwise>
                                <label>
                                    <span>Estado</span>
                                    <input value="Registrado" disabled>
                                    <span class="ayuda">Se asigna automáticamente al registrar.</span>
                                </label>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="acciones" style="margin-top: 20px;">
                        <button class="boton" type="submit">${esEditar ? 'Guardar cambios' : 'Guardar'}</button>
                        <c:choose>
                            <c:when test="${esEditar}"><a class="boton-secundario" href="${ctx}/sismos/detalle?id=${sismoId}">Cancelar</a></c:when>
                            <c:otherwise><a class="boton-secundario" href="${ctx}/sismos">Cancelar</a></c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </section>
        </main>

        <script>
            // HU-21: selección de ubicación en cascada Departamento -> Provincia -> Distrito.
            // Los datos salen de ${distritos}, que ya trae cargada su provincia y su departamento (EAGER),
            // así que no se necesita ninguna llamada adicional al servidor para filtrar.
            const distritos = [
            <c:forEach items="${distritos}" var="dis" varStatus="st">
            {
    id: ${dis.id},
                    nombre: "<c:out value='${dis.nombre}' escapeXml='true'/>",
                    provinciaId: ${dis.provincia.id},
                    provinciaNombre: "<c:out value='${dis.provincia.nombre}' escapeXml='true'/>",
                    departamentoId: ${dis.provincia.departamento.id},
                    departamentoNombre: "<c:out value='${dis.provincia.departamento.nombre}' escapeXml='true'/>"
            }<c:if test="${!st.last}">,</c:if>
            </c:forEach>
            ];

            const distritoIdActual = "${formulario.distritoId}";

            function departamentosUnicos() {
                const vistos = new Map();
                distritos.forEach(d => {
                    if (!vistos.has(d.departamentoId)) {
                        vistos.set(d.departamentoId, d.departamentoNombre);
                    }
                });
                return [...vistos.entries()].sort((a, b) => a[1].localeCompare(b[1]));
            }

            function provinciasDe(departamentoId) {
                const vistos = new Map();
                distritos
                        .filter(d => d.departamentoId === departamentoId)
                        .forEach(d => {
                            if (!vistos.has(d.provinciaId)) {
                                vistos.set(d.provinciaId, d.provinciaNombre);
                            }
                        });
                return [...vistos.entries()].sort((a, b) => a[1].localeCompare(b[1]));
            }

            function distritosDe(provinciaId) {
                return distritos
                        .filter(d => d.provinciaId === provinciaId)
                        .sort((a, b) => a.nombre.localeCompare(b.nombre));
            }

            function llenarSelect(select, opciones, textoVacio) {
                select.innerHTML = "<option value=''>" + textoVacio + "</option>";
                opciones.forEach(([valor, texto]) => {
                    const opt = document.createElement("option");
                    opt.value = valor;
                    opt.textContent = texto;
                    select.appendChild(opt);
                });
            }

            function alCambiarDepartamento(preseleccionarProvinciaId, preseleccionarDistritoId) {
                const departamentoId = Number(document.getElementById("cascadaDepartamento").value) || null;
                const selectProvincia = document.getElementById("cascadaProvincia");
                const selectDistrito = document.getElementById("cascadaDistrito");

                selectDistrito.innerHTML = "<option value=''>Seleccione primero la provincia</option>";
                selectDistrito.disabled = true;

                if (!departamentoId) {
                    selectProvincia.innerHTML = "<option value=''>Seleccione primero el departamento</option>";
                    selectProvincia.disabled = true;
                    return;
                }

                llenarSelect(selectProvincia, provinciasDe(departamentoId), "Seleccione la provincia");
                selectProvincia.disabled = false;

                if (preseleccionarProvinciaId) {
                    selectProvincia.value = preseleccionarProvinciaId;
                    alCambiarProvincia(preseleccionarDistritoId);
                }
            }

            function alCambiarProvincia(preseleccionarDistritoId) {
                const provinciaId = Number(document.getElementById("cascadaProvincia").value) || null;
                const selectDistrito = document.getElementById("cascadaDistrito");

                if (!provinciaId) {
                    selectDistrito.innerHTML = "<option value=''>Seleccione primero la provincia</option>";
                    selectDistrito.disabled = true;
                    return;
                }

                const opciones = distritosDe(provinciaId).map(d => [d.id, d.nombre]);
                llenarSelect(selectDistrito, opciones, "Seleccione el distrito");
                selectDistrito.disabled = false;

                if (preseleccionarDistritoId) {
                    selectDistrito.value = preseleccionarDistritoId;
                }
            }

            // Inicializa los selects al cargar la página (y precarga la cadena completa en modo Editar).
            (function inicializar() {
                const selectDepartamento = document.getElementById("cascadaDepartamento");
                llenarSelect(selectDepartamento, departamentosUnicos(), "Seleccione el departamento");

                if (distritoIdActual) {
                    const distritoActual = distritos.find(d => String(d.id) === distritoIdActual);
                    if (distritoActual) {
                        selectDepartamento.value = distritoActual.departamentoId;
                        alCambiarDepartamento(distritoActual.provinciaId, distritoActual.id);
                    }
                }
            })();
        </script>
    </body>
</html>