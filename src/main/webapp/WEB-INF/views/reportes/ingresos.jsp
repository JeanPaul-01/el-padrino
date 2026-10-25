<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Reporte de ingresos"/>
<c:set var="menu" value="reportes"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Reporte de ingresos</h1>
    <ul class="nav nav-pills no-print">
        <li class="nav-item"><a class="nav-link active bg-dark" href="${ctx}/reportes/ingresos">Ingresos</a></li>
        <li class="nav-item"><a class="nav-link text-dark" href="${ctx}/reportes/stock-clientes">Stock bajo y clientes frecuentes</a></li>
    </ul>
</div>

<div class="card shadow-sm border-0 mb-3 no-print">
    <div class="card-body">
        <form method="get" action="${ctx}/reportes/ingresos" class="row g-2 align-items-end">
            <div class="col-sm-6 col-lg-2">
                <label class="form-label small text-muted mb-1">Desde *</label>
                <input type="date" name="desde" class="form-control" value="${f.iso(desde)}" required>
            </div>
            <div class="col-sm-6 col-lg-2">
                <label class="form-label small text-muted mb-1">Hasta *</label>
                <input type="date" name="hasta" class="form-control" value="${f.iso(hasta)}" required>
            </div>
            <div class="col-sm-6 col-lg-2">
                <label class="form-label small text-muted mb-1">Barbero</label>
                <select name="barbero" class="form-select">
                    <option value="">Todos</option>
                    <c:forEach var="b" items="${barberos}"><option value="${b.idBarbero}" ${b.idBarbero == barbero ? 'selected' : ''}><c:out value="${b.nombres}"/></option></c:forEach>
                </select>
            </div>
            <div class="col-sm-6 col-lg-2">
                <label class="form-label small text-muted mb-1">Servicio</label>
                <select name="servicio" class="form-select">
                    <option value="">Todos</option>
                    <c:forEach var="s" items="${servicios}"><option value="${s.idServicio}" ${s.idServicio == servicio ? 'selected' : ''}><c:out value="${s.nombre}"/></option></c:forEach>
                </select>
            </div>
            <div class="col-sm-6 col-lg-2">
                <label class="form-label small text-muted mb-1">Agrupar por</label>
                <select name="agrupacion" class="form-select">
                    <option value="DIA" ${agrupacion == 'DIA' ? 'selected' : ''}>Día</option>
                    <option value="MES" ${agrupacion == 'MES' ? 'selected' : ''}>Mes</option>
                </select>
            </div>
            <div class="col-sm-6 col-lg-2 d-flex gap-1">
                <button class="btn btn-dark flex-fill">Consultar</button>
                <c:if test="${usuarioActual.administrador and not empty reporte}">
                    <a class="btn btn-success" title="Exportar a Excel"
                       href="${ctx}/reportes/ingresos/exportar?desde=${desde}&hasta=${hasta}&barbero=${barbero}&servicio=${servicio}&agrupacion=${agrupacion}"><i class="bi bi-file-earmark-excel"></i></a>
                </c:if>
                <button type="button" class="btn btn-outline-dark" title="Imprimir / PDF" onclick="window.print()"><i class="bi bi-printer"></i></button>
            </div>
        </form>
    </div>
</div>

<c:if test="${not empty reporte}">
    <p class="text-muted small">Periodo: ${f.fecha(desde)} al ${f.fecha(hasta)}</p>
    <div class="row g-3 mb-3">
        <div class="col-6 col-md-3"><div class="card kpi shadow-sm"><div class="card-body"><div class="text-muted small">Total del periodo</div><div class="valor fs-3">${f.soles(reporte.total)}</div></div></div></div>
        <div class="col-6 col-md-3"><div class="card kpi shadow-sm"><div class="card-body"><div class="text-muted small">Ventas</div><div class="valor fs-3">${reporte.ventas}</div></div></div></div>
        <div class="col-6 col-md-3"><div class="card kpi shadow-sm"><div class="card-body"><div class="text-muted small">Servicios</div><div class="valor fs-3">${reporte.servicios}</div></div></div></div>
        <div class="col-6 col-md-3"><div class="card kpi shadow-sm"><div class="card-body"><div class="text-muted small">Productos vendidos</div><div class="valor fs-3">${reporte.productos}</div></div></div></div>
    </div>

    <div class="card shadow-sm border-0 mb-3">
        <div class="card-header bg-white fw-semibold">R01 · Ingresos por ${agrupacion == 'MES' ? 'mes' : 'día'}</div>
        <div class="table-responsive">
            <table class="table align-middle mb-0">
                <thead class="table-light"><tr><th>Periodo</th><th class="text-center">Servicios</th><th class="text-center">Productos</th><th class="text-end">Ingresos</th><th style="width: 35%;"></th></tr></thead>
                <tbody>
                <c:forEach var="fila" items="${reporte.filas}">
                    <tr>
                        <td>${fila.etiqueta}</td><td class="text-center">${fila.servicios}</td><td class="text-center">${fila.productos}</td>
                        <td class="text-end">${f.soles(fila.ingresos)}</td>
                        <td><div class="barra" style="width: ${fila.porcentajeBarra}%"></div></td>
                    </tr>
                </c:forEach>
                <c:if test="${reporte.vacio}"><tr><td colspan="5" class="text-center text-muted py-4">No hay registros para el periodo seleccionado</td></tr></c:if>
                </tbody>
                <c:if test="${not reporte.vacio}">
                    <tfoot><tr class="fw-bold"><td>Total</td><td class="text-center">${reporte.servicios}</td><td class="text-center">${reporte.productos}</td><td class="text-end">${f.soles(reporte.total)}</td><td></td></tr></tfoot>
                </c:if>
            </table>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-lg-6">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white fw-semibold">R02 · Ingresos por barbero</div>
                <table class="table mb-0">
                    <thead class="table-light"><tr><th>Barbero</th><th class="text-center">Atenciones</th><th class="text-end">Ingresos</th><th class="text-end">%</th></tr></thead>
                    <tbody>
                    <c:forEach var="b" items="${porBarbero}">
                        <tr><td><c:out value="${b.barbero}"/></td><td class="text-center">${b.atenciones}</td><td class="text-end">${f.soles(b.ingresos)}</td><td class="text-end">${b.porcentaje}</td></tr>
                    </c:forEach>
                    <c:if test="${empty porBarbero}"><tr><td colspan="4" class="text-center text-muted py-3">No hay registros para el periodo seleccionado</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white fw-semibold">R03 · Servicios más solicitados</div>
                <table class="table mb-0">
                    <thead class="table-light"><tr><th>Servicio</th><th class="text-center">Cantidad</th><th class="text-end">Ingresos</th></tr></thead>
                    <tbody>
                    <c:forEach var="s" items="${ranking}">
                        <tr><td><c:out value="${s.servicio}"/></td><td class="text-center">${s.cantidad}</td><td class="text-end">${f.soles(s.ingresos)}</td></tr>
                    </c:forEach>
                    <c:if test="${empty ranking}"><tr><td colspan="3" class="text-center text-muted py-3">No hay registros para el periodo seleccionado</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
