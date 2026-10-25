<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Registro de venta"/>
<c:set var="menu" value="ventas"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Registro de venta y pago</h1>

<c:if test="${not empty cita}">
    <div class="alert alert-info">
        <i class="bi bi-calendar-check me-1"></i>
        Atendiendo la cita de <strong><c:out value="${cita.cliente.nombres}"/></strong>:
        <c:out value="${cita.servicio.nombre}"/> con <c:out value="${cita.barbero.nombres}"/>
        (${f.fecha(cita.fecha)} ${f.hora(cita.hora)}). Al registrar la venta la cita quedará como <strong>Atendida</strong>.
    </div>
</c:if>

<form method="post" action="${ctx}/ventas" id="formVenta">
    <c:if test="${not empty cita}"><input type="hidden" name="citaId" value="${cita.idCita}"></c:if>
    <div class="row g-3">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0 mb-3">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Cliente *</label>
                            <select name="clienteId" id="clienteId" class="form-select" required>
                                <option value="">Seleccione</option>
                                <c:forEach var="cl" items="${clientes}">
                                    <option value="${cl.idCliente}" ${cl.idCliente == clienteId ? 'selected' : ''}><c:out value="${cl.nombres}"/> — ${cl.telefono}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Barbero que atendió *</label>
                            <select name="barberoId" class="form-select" required>
                                <option value="">Seleccione</option>
                                <c:forEach var="b" items="${barberos}">
                                    <option value="${b.idBarbero}" ${b.idBarbero == barberoId ? 'selected' : ''}><c:out value="${b.nombres}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div id="avisoFidelizacion" class="mt-3"></div>
                </div>
            </div>

            <div class="card shadow-sm border-0">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <span class="fw-semibold">Servicios y productos</span>
                    <div class="d-flex gap-2">
                        <button type="button" class="btn btn-sm btn-outline-dark" onclick="agregarLinea('SERVICIO')"><i class="bi bi-plus"></i> Servicio</button>
                        <button type="button" class="btn btn-sm btn-outline-dark" onclick="agregarLinea('PRODUCTO')"><i class="bi bi-plus"></i> Producto</button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead class="table-light"><tr><th style="width: 110px;">Tipo</th><th>Descripción</th><th style="width: 100px;">Cant.</th>
                            <th class="text-end" style="width: 110px;">Precio</th><th class="text-end" style="width: 120px;">Subtotal</th><th style="width: 50px;"></th></tr></thead>
                        <tbody id="lineas"></tbody>
                    </table>
                </div>
                <div id="sinLineas" class="text-center text-muted py-3">Agregue al menos un servicio o producto</div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <label class="form-label">Método de pago *</label>
                    <select name="metodoPago" class="form-select mb-3" required>
                        <c:forEach var="m" items="${metodosPago}">
                            <option value="${m}" ${m == metodoPago ? 'selected' : ''}>${m.etiqueta}</option>
                        </c:forEach>
                    </select>
                    <div class="d-flex justify-content-between"><span>Subtotal</span><span id="txtSubtotal">S/ 0.00</span></div>
                    <div class="d-flex justify-content-between text-success"><span>Corte gratis</span><span id="txtDescuento">S/ 0.00</span></div>
                    <hr>
                    <div class="d-flex justify-content-between fs-4 fw-bold"><span>Total</span><span id="txtTotal">S/ 0.00</span></div>
                    <button type="submit" class="btn btn-padrino w-100 mt-3 py-2"><i class="bi bi-check2-circle me-1"></i>Confirmar venta</button>
                    <p class="small text-muted mt-2 mb-0">Al confirmar se descuenta el stock y se actualiza la tarjeta de fidelización del cliente.</p>
                </div>
            </div>
        </div>
    </div>
</form>

<%-- Catálogos usados por el script para armar las líneas de la venta --%>
<select id="catalogoSERVICIO" class="d-none">
    <c:forEach var="s" items="${servicios}">
        <option value="${s.idServicio}" data-precio="${s.precio}"><c:out value="${s.nombre}"/></option>
    </c:forEach>
</select>
<select id="catalogoPRODUCTO" class="d-none">
    <c:forEach var="i" items="${inventario}">
        <c:if test="${i.producto.activo}">
            <option value="${i.producto.idProducto}" data-precio="${i.producto.precio}" data-stock="${i.stockActual}" ${i.stockActual == 0 ? 'disabled' : ''}>
                <c:out value="${i.producto.nombre}"/> (stock: ${i.stockActual})</option>
        </c:if>
    </c:forEach>
</select>

<script>
    var corteGratis = false;
    var tbody = document.getElementById('lineas');

    function soles(n) {
        return 'S/ ' + n.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
    }

    function agregarLinea(tipo, itemId) {
        var tr = document.createElement('tr');
        var select = document.getElementById('catalogo' + tipo).cloneNode(true);
        select.removeAttribute('id');
        select.className = 'form-select form-select-sm';
        select.name = 'itemIds';
        select.required = true;
        select.insertAdjacentHTML('afterbegin', '<option value="" selected>Seleccione</option>');
        if (itemId) { select.value = itemId; }

        tr.innerHTML = '<td><input type="hidden" name="tipos" value="' + tipo + '">' +
            '<span class="badge ' + (tipo === 'SERVICIO' ? 'text-bg-dark' : 'text-bg-secondary') + '">' +
            (tipo === 'SERVICIO' ? 'Servicio' : 'Producto') + '</span></td>' +
            '<td class="celda-item"></td>' +
            '<td><input type="number" name="cantidades" class="form-control form-control-sm" min="1" value="1" required></td>' +
            '<td class="text-end precio">S/ 0.00</td><td class="text-end subtotal fw-semibold">S/ 0.00</td>' +
            '<td><button type="button" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button></td>';
        tr.dataset.tipo = tipo;
        tr.querySelector('.celda-item').appendChild(select);
        tr.querySelector('button').addEventListener('click', function () { tr.remove(); recalcular(); });
        select.addEventListener('change', recalcular);
        tr.querySelector('input[name=cantidades]').addEventListener('input', recalcular);
        tbody.appendChild(tr);
        recalcular();
    }

    function recalcular() {
        var subtotal = 0, primerServicio = null;
        tbody.querySelectorAll('tr').forEach(function (tr) {
            var opcion = tr.querySelector('select').selectedOptions[0];
            var precio = opcion && opcion.dataset.precio ? parseFloat(opcion.dataset.precio) : 0;
            var cantidadInput = tr.querySelector('input[name=cantidades]');
            var cantidad = parseInt(cantidadInput.value || '0', 10);
            if (opcion && opcion.dataset.stock) { cantidadInput.max = opcion.dataset.stock; }
            tr.querySelector('.precio').textContent = soles(precio);
            tr.querySelector('.subtotal').textContent = soles(precio * cantidad);
            subtotal += precio * cantidad;
            if (tr.dataset.tipo === 'SERVICIO' && precio > 0 && primerServicio === null) { primerServicio = precio; }
        });
        var descuento = corteGratis && primerServicio !== null ? primerServicio : 0;
        document.getElementById('txtSubtotal').textContent = soles(subtotal);
        document.getElementById('txtDescuento').textContent = '- ' + soles(descuento);
        document.getElementById('txtTotal').textContent = soles(Math.max(subtotal - descuento, 0));
        document.getElementById('sinLineas').style.display = tbody.children.length ? 'none' : 'block';
    }

    function consultarFidelizacion() {
        var aviso = document.getElementById('avisoFidelizacion');
        var id = document.getElementById('clienteId').value;
        corteGratis = false;
        aviso.innerHTML = '';
        if (!id) { recalcular(); return; }
        fetch('${ctx}/fidelizacion/cliente/' + id)
            .then(function (r) { return r.json(); })
            .then(function (t) {
                corteGratis = t.corteGratis;
                aviso.innerHTML = t.corteGratis
                    ? '<div class="alert alert-success mb-0 py-2"><i class="bi bi-gift me-1"></i>El cliente tiene un <strong>corte gratis</strong>: se descontará el primer servicio.</div>'
                    : '<div class="alert alert-light border mb-0 py-2"><i class="bi bi-award me-1"></i>Fidelización: ' + t.cortes + ' de ' + t.requeridos + ' cortes acumulados.</div>';
                recalcular();
            });
    }

    document.getElementById('clienteId').addEventListener('change', consultarFidelizacion);
    <c:choose>
        <c:when test="${not empty cita}">agregarLinea('SERVICIO', '${cita.servicio.idServicio}');</c:when>
        <c:otherwise>agregarLinea('SERVICIO');</c:otherwise>
    </c:choose>
    consultarFidelizacion();
</script>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
