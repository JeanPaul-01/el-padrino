<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Venta N.º ${venta.idVenta}"/>
<c:set var="menu" value="ventas"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2 no-print">
    <h1 class="h3 fw-bold mb-0">Comprobante de venta</h1>
    <div class="d-flex gap-2">
        <button onclick="window.print()" class="btn btn-outline-dark"><i class="bi bi-printer me-1"></i>Imprimir</button>
        <a href="${ctx}/ventas/nueva" class="btn btn-padrino"><i class="bi bi-plus-lg me-1"></i>Nueva venta</a>
    </div>
</div>

<div class="card shadow-sm border-0" style="max-width: 720px;">
    <div class="card-body p-4">
        <div class="d-flex justify-content-between mb-3">
            <div>
                <h2 class="h5 fw-bold mb-0"><i class="bi bi-scissors"></i> Barbería El Padrino</h2>
                <small class="text-muted">Venta N.º ${venta.idVenta}</small>
            </div>
            <div class="text-end small">
                ${f.fechaHora(venta.fecha)}<br>
                Atendido por: <c:out value="${venta.usuario.nombreUsuario}"/>
            </div>
        </div>
        <div class="row small mb-3">
            <div class="col-6"><strong>Cliente:</strong> <c:out value="${venta.cliente.nombres}"/></div>
            <div class="col-6"><strong>Barbero:</strong> <c:out value="${venta.barbero.nombres}"/></div>
            <div class="col-6"><strong>Pago:</strong> ${venta.metodoPago.etiqueta}</div>
        </div>
        <table class="table table-sm">
            <thead class="table-light"><tr><th>Descripción</th><th>Tipo</th><th class="text-center">Cant.</th><th class="text-end">P. unit.</th><th class="text-end">Subtotal</th></tr></thead>
            <tbody>
            <c:forEach var="d" items="${venta.detalles}">
                <tr><td><c:out value="${d.descripcion}"/></td><td>${d.tipo}</td><td class="text-center">${d.cantidad}</td>
                    <td class="text-end">${f.soles(d.precioUnitario)}</td><td class="text-end">${f.soles(d.subtotal)}</td></tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr><td colspan="4" class="text-end">Subtotal</td><td class="text-end">${f.soles(venta.subtotal)}</td></tr>
            <c:if test="${venta.conCorteGratis}">
                <tr class="text-success"><td colspan="4" class="text-end">Corte gratis (fidelización)</td><td class="text-end">- ${f.soles(venta.descuento)}</td></tr>
            </c:if>
            <tr class="fw-bold fs-5"><td colspan="4" class="text-end">Total</td><td class="text-end">${f.soles(venta.total)}</td></tr>
            </tfoot>
        </table>
        <a href="${ctx}/clientes/${venta.cliente.idCliente}" class="btn btn-sm btn-link px-0 no-print">Ver historial del cliente</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
