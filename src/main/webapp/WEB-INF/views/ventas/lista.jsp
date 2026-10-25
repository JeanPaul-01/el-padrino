<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Ventas"/>
<c:set var="menu" value="ventas"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Ventas</h1>
    <a href="${ctx}/ventas/nueva" class="btn btn-padrino"><i class="bi bi-plus-lg me-1"></i>Registrar venta</a>
</div>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <form method="get" action="${ctx}/ventas" class="row g-2 align-items-end mb-3">
            <div class="col-sm-4 col-lg-3">
                <label class="form-label small text-muted mb-1">Desde</label>
                <input type="date" name="desde" class="form-control" value="${f.iso(desde)}" required>
            </div>
            <div class="col-sm-4 col-lg-3">
                <label class="form-label small text-muted mb-1">Hasta</label>
                <input type="date" name="hasta" class="form-control" value="${f.iso(hasta)}" required>
            </div>
            <div class="col-auto"><button class="btn btn-dark">Filtrar</button></div>
            <div class="col text-end">
                <span class="text-muted">${ventas.size()} venta(s) ·</span>
                <span class="fw-bold fs-5">${f.soles(total)}</span>
            </div>
        </form>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr><th>N.º</th><th>Fecha</th><th>Cliente</th><th>Barbero</th><th>Detalle</th><th>Pago</th>
                    <th class="text-end">Descuento</th><th class="text-end">Total</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="v" items="${ventas}">
                    <tr>
                        <td>${v.idVenta}</td>
                        <td class="text-nowrap">${f.fechaHora(v.fecha)}</td>
                        <td><c:out value="${v.cliente.nombres}"/></td>
                        <td><c:out value="${v.barbero.nombres}"/></td>
                        <td class="small">
                            <c:forEach var="d" items="${v.detalles}" varStatus="st">
                                <c:out value="${d.descripcion}"/><c:if test="${d.cantidad > 1}"> x${d.cantidad}</c:if><c:if test="${not st.last}">, </c:if>
                            </c:forEach>
                        </td>
                        <td>${v.metodoPago.etiqueta}</td>
                        <td class="text-end">${v.conCorteGratis ? f.soles(v.descuento) : '—'}</td>
                        <td class="text-end fw-semibold">${f.soles(v.total)}</td>
                        <td><a href="${ctx}/ventas/${v.idVenta}" class="btn btn-sm btn-outline-dark">Ver</a></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty ventas}">
                    <tr><td colspan="9" class="text-center text-muted py-4">No hay registros para el periodo seleccionado</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
