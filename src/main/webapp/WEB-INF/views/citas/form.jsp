<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Nueva cita"/>
<c:set var="menu" value="citas"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Nueva cita</h1>

<div class="card shadow-sm border-0" style="max-width: 760px;">
    <div class="card-body p-4">
        <form method="post" action="${ctx}/citas" id="formCita">
            <div class="row g-3">
                <div class="col-md-8">
                    <label class="form-label">Cliente *</label>
                    <select name="clienteId" class="form-select" required>
                        <option value="">Seleccione un cliente</option>
                        <c:forEach var="cl" items="${clientes}">
                            <option value="${cl.idCliente}" ${cl.idCliente == clienteId ? 'selected' : ''}><c:out value="${cl.nombres}"/> — ${cl.telefono}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <a href="${ctx}/clientes/nuevo" class="btn btn-outline-secondary w-100"><i class="bi bi-person-plus me-1"></i>Cliente nuevo</a>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Servicio *</label>
                    <select name="servicioId" class="form-select" required>
                        <option value="">Seleccione</option>
                        <c:forEach var="s" items="${servicios}">
                            <option value="${s.idServicio}" ${s.idServicio == servicioId ? 'selected' : ''}><c:out value="${s.nombre}"/> — ${f.soles(s.precio)}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Barbero *</label>
                    <select name="barberoId" id="barberoId" class="form-select" required>
                        <option value="">Seleccione</option>
                        <c:forEach var="b" items="${barberos}">
                            <option value="${b.idBarbero}" ${b.idBarbero == barberoId ? 'selected' : ''}><c:out value="${b.nombres}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Fecha *</label>
                    <input type="date" name="fecha" id="fecha" class="form-control" value="${f.iso(fecha)}" min="${f.iso(hoy)}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Hora *</label>
                    <select name="hora" id="hora" class="form-select" required>
                        <option value="">Elija barbero y fecha</option>
                    </select>
                    <div id="ayudaHora" class="form-text">Solo se muestran los horarios libres del barbero.</div>
                </div>
                <div class="col-12">
                    <label class="form-label">Observaciones</label>
                    <input type="text" name="observaciones" maxlength="255" class="form-control" value="<c:out value='${observaciones}'/>">
                </div>
            </div>
            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-padrino"><i class="bi bi-save me-1"></i>Registrar cita</button>
                <a href="${ctx}/citas" class="btn btn-outline-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<script>
    (function () {
        var barbero = document.getElementById('barberoId');
        var fecha = document.getElementById('fecha');
        var hora = document.getElementById('hora');
        var ayuda = document.getElementById('ayudaHora');

        function cargarHorarios() {
            hora.innerHTML = '<option value="">Elija barbero y fecha</option>';
            if (!barbero.value || !fecha.value) { return; }
            fetch('${ctx}/citas/horarios?barberoId=' + barbero.value + '&fecha=' + fecha.value)
                .then(function (r) { return r.json(); })
                .then(function (lista) {
                    if (lista.length === 0) {
                        hora.innerHTML = '<option value="">Sin horarios disponibles</option>';
                        ayuda.textContent = 'El barbero no tiene horarios libres en esa fecha. Pruebe otra fecha u otro barbero.';
                        ayuda.className = 'form-text text-danger';
                        return;
                    }
                    ayuda.textContent = lista.length + ' horario(s) disponible(s).';
                    ayuda.className = 'form-text text-success';
                    hora.innerHTML = '<option value="">Seleccione la hora</option>' + lista.map(function (h) {
                        return '<option value="' + h + '">' + h + '</option>';
                    }).join('');
                });
        }
        barbero.addEventListener('change', cargarHorarios);
        fecha.addEventListener('change', cargarHorarios);
        cargarHorarios();
    })();
</script>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
