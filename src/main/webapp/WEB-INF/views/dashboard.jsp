<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Dashboard"/>
<c:set var="menu" value="dashboard"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
    <div>
        <h1 class="h3 fw-bold mb-0">Resumen del día</h1>
        <span class="text-muted">${f.fecha(hoy)}</span>
    </div>
    <div class="d-flex gap-2">
        <a href="${ctx}/citas/nueva" class="btn btn-padrino"><i class="bi bi-calendar-plus me-1"></i>Nueva cita</a>
        <a href="${ctx}/ventas/nueva" class="btn btn-outline-dark"><i class="bi bi-cash me-1"></i>Registrar venta</a>
        <a href="${ctx}/clientes/nuevo" class="btn btn-outline-dark"><i class="bi bi-person-plus me-1"></i>Nuevo cliente</a>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-4">
        <a href="${ctx}/citas" class="text-decoration-none">
            <div class="card kpi shadow-sm h-100">
                <div class="card-body d-flex justify-content-between">
                    <div>
                        <div class="text-muted">Citas de hoy</div>
                        <div class="valor text-dark">${resumen.citasHoy}</div>
                        <span class="badge text-bg-warning">${resumen.pendientes} pendientes</span>
                        <span class="badge text-bg-success">${resumen.confirmadas} confirmadas</span>
                    </div>
                    <i class="bi bi-calendar-check icono text-dark"></i>
                </div>
            </div>
        </a>
    </div>
    <div class="col-md-4">
        <a href="${ctx}/reportes/ingresos" class="text-decoration-none">
            <div class="card kpi shadow-sm h-100">
                <div class="card-body d-flex justify-content-between">
                    <div>
                        <div class="text-muted">Ingresos del día</div>
                        <div class="valor text-dark">${f.soles(resumen.ingresosDia)}</div>
                        <span class="text-muted small">Ventas registradas hoy</span>
                    </div>
                    <i class="bi bi-cash-stack icono text-dark"></i>
                </div>
            </div>
        </a>
    </div>
    <div class="col-md-4">
        <a href="${ctx}/inventario" class="text-decoration-none">
            <div class="card kpi shadow-sm h-100 ${resumen.productosStockCritico > 0 ? 'border-start border-danger border-4' : ''}">
                <div class="card-body d-flex justify-content-between">
                    <div>
                        <div class="text-muted">Stock crítico</div>
                        <div class="valor ${resumen.productosStockCritico > 0 ? 'text-danger' : 'text-dark'}">${resumen.productosStockCritico}</div>
                        <span class="text-muted small">productos por reponer</span>
                    </div>
                    <i class="bi bi-exclamation-octagon icono text-danger"></i>
                </div>
            </div>
        </a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-8">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold"><i class="bi bi-clock me-1"></i> Citas de hoy</div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light"><tr><th>Hora</th><th>Cliente</th><th>Servicio</th><th>Barbero</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
                    <tbody>
                    <c:forEach var="c" items="${resumen.citas}">
                        <tr>
                            <td class="fw-semibold">${f.hora(c.hora)}</td>
                            <td><a href="${ctx}/clientes/${c.cliente.idCliente}"><c:out value="${c.cliente.nombres}"/></a></td>
                            <td><c:out value="${c.servicio.nombre}"/></td>
                            <td><c:out value="${c.barbero.nombres}"/></td>
                            <td><span class="badge text-bg-${c.estado.color}">${c.estado.etiqueta}</span></td>
                            <td class="text-end">
                                <c:if test="${c.estado.modificable}">
                                    <c:if test="${c.estado == 'PENDIENTE'}">
                                        <form method="post" action="${ctx}/citas/${c.idCita}/estado" class="d-inline">
                                            <input type="hidden" name="estado" value="CONFIRMADA"><input type="hidden" name="volver" value="/dashboard">
                                            <button class="btn btn-sm btn-outline-success" title="Confirmar"><i class="bi bi-check2"></i></button>
                                        </form>
                                    </c:if>
                                    <a href="${ctx}/citas/${c.idCita}/atender" class="btn btn-sm btn-padrino" title="Atender y cobrar"><i class="bi bi-cash"></i> Atender</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty resumen.citas}">
                        <tr><td colspan="6" class="text-center text-muted py-4">No hay citas programadas para hoy</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold"><i class="bi bi-box-seam me-1"></i> Productos con stock crítico</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="i" items="${resumen.stockCritico}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span><c:out value="${i.producto.nombre}"/><br><small class="text-muted">Mínimo: ${i.producto.stockMinimo}</small></span>
                        <span class="badge ${i.stockActual == 0 ? 'text-bg-danger' : 'text-bg-warning'}">${i.estadoStock}: ${i.stockActual}</span>
                    </li>
                </c:forEach>
                <c:if test="${empty resumen.stockCritico}">
                    <li class="list-group-item text-muted">Todo el inventario está en niveles normales</li>
                </c:if>
            </ul>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
