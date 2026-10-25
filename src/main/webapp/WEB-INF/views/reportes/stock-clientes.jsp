<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Stock bajo y clientes frecuentes"/>
<c:set var="menu" value="reportes"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Stock bajo y clientes frecuentes</h1>
    <div class="d-flex gap-2 no-print">
        <ul class="nav nav-pills">
            <li class="nav-item"><a class="nav-link text-dark" href="${ctx}/reportes/ingresos">Ingresos</a></li>
            <li class="nav-item"><a class="nav-link active bg-dark" href="${ctx}/reportes/stock-clientes">Stock bajo y clientes frecuentes</a></li>
        </ul>
        <button type="button" class="btn btn-outline-dark" onclick="window.print()"><i class="bi bi-printer me-1"></i>Imprimir / PDF</button>
    </div>
</div>
<p class="text-muted small">Generado el ${f.fechaHora(ahora)} por <c:out value="${usuarioActual.nombreUsuario}"/></p>

<div class="card shadow-sm border-0 mb-3">
    <div class="card-header bg-white fw-semibold">R04 · Productos con stock bajo</div>
    <div class="table-responsive">
        <table class="table align-middle mb-0">
            <thead class="table-light"><tr><th>Producto</th><th class="text-center">Stock</th><th class="text-center">Mínimo</th><th class="text-center">Faltan</th><th>Estado</th></tr></thead>
            <tbody>
            <c:forEach var="i" items="${stockBajo}">
                <tr>
                    <td><c:out value="${i.producto.nombre}"/></td>
                    <td class="text-center">${i.stockActual}</td>
                    <td class="text-center">${i.producto.stockMinimo}</td>
                    <td class="text-center fw-bold">${i.producto.stockMinimo - i.stockActual}</td>
                    <td><span class="badge ${i.stockActual == 0 ? 'text-bg-danger' : 'text-bg-warning'}">${i.estadoStock}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty stockBajo}"><tr><td colspan="5" class="text-center text-muted py-4">Ningún producto está por debajo de su stock mínimo</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<div class="card shadow-sm border-0">
    <div class="card-header bg-white fw-semibold">R05 · Clientes más frecuentes (top 10)</div>
    <div class="table-responsive">
        <table class="table align-middle mb-0">
            <thead class="table-light"><tr><th>#</th><th>Cliente</th><th class="text-center">Visitas</th><th>Última visita</th><th>Fidelización</th></tr></thead>
            <tbody>
            <c:forEach var="cf" items="${clientesFrecuentes}">
                <tr>
                    <td>${cf.posicion}</td>
                    <td><a href="${ctx}/clientes/${cf.idCliente}"><c:out value="${cf.cliente}"/></a></td>
                    <td class="text-center">${cf.visitas}</td>
                    <td>${f.fechaHora(cf.ultimaVisita)}</td>
                    <td><span class="badge ${cf.cortes >= 5 ? 'text-bg-success' : 'text-bg-light border'}">${cf.cortes} / 5</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty clientesFrecuentes}"><tr><td colspan="5" class="text-center text-muted py-4">No hay registros</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
