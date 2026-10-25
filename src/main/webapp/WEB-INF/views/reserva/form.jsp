<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reserva tu cita | El Padrino</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/app.css" rel="stylesheet">
</head>
<body>
<header class="py-4 text-center text-white" style="background: var(--padrino-oscuro);">
    <h1 class="h3 fw-bold mb-1"><i class="bi bi-scissors" style="color: var(--padrino-dorado);"></i> Barbería El Padrino</h1>
    <p class="mb-0 text-white-50">Reserva tu cita en línea · Atendemos de lunes a sábado de 9:00 a 20:00</p>
</header>

<div class="container py-4" style="max-width: 720px;">
    <c:if test="${not empty error}">
        <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-1"></i><c:out value="${error}"/></div>
    </c:if>
    <div class="card shadow-sm border-0">
        <div class="card-body p-4">
            <form method="post" action="${ctx}/reserva">
                <h2 class="h5 fw-bold mb-3">1. Tus datos</h2>
                <div class="row g-3 mb-4">
                    <div class="col-12">
                        <label class="form-label">Nombres y apellidos *</label>
                        <input type="text" name="nombres" class="form-control" maxlength="100" value="<c:out value='${nombres}'/>" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Celular *</label>
                        <input type="tel" name="telefono" class="form-control" pattern="9[0-9]{8}" maxlength="9"
                               title="9 dígitos que empiecen con 9" placeholder="9XXXXXXXX" value="<c:out value='${telefono}'/>" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Correo (opcional)</label>
                        <input type="email" name="correo" class="form-control" maxlength="100" value="<c:out value='${correo}'/>">
                    </div>
                </div>

                <h2 class="h5 fw-bold mb-3">2. Tu cita</h2>
                <div class="row g-3">
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
                                <option value="${b.idBarbero}" ${b.idBarbero == barberoId ? 'selected' : ''}><c:out value="${b.nombres}"/> · <c:out value="${b.especialidad}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Fecha *</label>
                        <input type="date" name="fecha" id="fecha" class="form-control" min="${f.iso(hoy)}" max="${f.iso(maximo)}"
                               value="${f.iso(fecha)}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Hora *</label>
                        <select name="hora" id="hora" class="form-select" required>
                            <option value="">Elija barbero y fecha</option>
                        </select>
                        <div id="ayudaHora" class="form-text">Solo verás los horarios disponibles.</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-padrino btn-lg w-100 mt-4"><i class="bi bi-calendar-check me-1"></i>Reservar cita</button>
            </form>
        </div>
    </div>
    <p class="text-center mt-3"><a href="${ctx}/login" class="text-muted small">Acceso para el personal</a></p>
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
            fetch('${ctx}/reserva/horarios?barberoId=' + barbero.value + '&fecha=' + fecha.value)
                .then(function (r) { return r.json(); })
                .then(function (lista) {
                    if (lista.length === 0) {
                        hora.innerHTML = '<option value="">Sin horarios disponibles</option>';
                        ayuda.textContent = 'No hay horarios libres ese día. Prueba con otra fecha u otro barbero.';
                        ayuda.className = 'form-text text-danger';
                        return;
                    }
                    ayuda.textContent = 'Horarios disponibles: ' + lista.length;
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
</body>
</html>
