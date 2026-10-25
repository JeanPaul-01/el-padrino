<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Programa de fidelización"/>
<c:set var="menu" value="fidelizacion"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-1">Programa de fidelización</h1>
<p class="text-muted">Cada 5 cortes pagados, el 6.º es gratis. Los cortes se suman automáticamente al registrar una venta con servicios.</p>

<div class="row g-3 mb-4">
    <div class="col-md-4"><div class="card kpi shadow-sm"><div class="card-body">
        <div class="text-muted">Clientes en el programa</div><div class="valor">${resumen.clientesEnPrograma}</div></div></div></div>
    <div class="col-md-4"><div class="card kpi shadow-sm"><div class="card-body">
        <div class="text-muted">Cortes gratis disponibles</div><div class="valor text-success">${resumen.cortesGratisDisponibles}</div></div></div></div>
    <div class="col-md-4"><div class="card kpi shadow-sm"><div class="card-body">
        <div class="text-muted">Canjes del mes</div><div class="valor">${resumen.canjesDelMes}</div></div></div></div>
</div>

<div class="card shadow-sm border-0">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light"><tr><th>Cliente</th><th>Cortes acumulados</th><th style="width: 30%;">Avance</th><th>Servicios pagados</th><th>Estado</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="t" items="${resumen.detalle}">
                <tr>
                    <td class="fw-semibold"><c:out value="${t.cliente.nombres}"/></td>
                    <td>${t.cortesTarjeta} / ${t.cortesRequeridos}</td>
                    <td>
                        <div class="progress" role="progressbar" aria-valuenow="${t.avancePorcentaje}" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar ${t.corteGratisDisponible ? 'bg-success' : 'bg-dark'}" style="width: ${t.avancePorcentaje}%">${t.avancePorcentaje}%</div>
                        </div>
                    </td>
                    <td>${t.puntos}</td>
                    <td>
                        <c:choose>
                            <c:when test="${t.corteGratisDisponible}"><span class="badge text-bg-success">Corte gratis disponible</span></c:when>
                            <c:otherwise><span class="badge text-bg-light border">En progreso · faltan ${t.cortesFaltantes}</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><a href="${ctx}/clientes/${t.cliente.idCliente}" class="btn btn-sm btn-link">Perfil</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty resumen.detalle}"><tr><td colspan="6" class="text-center text-muted py-4">Aún no hay clientes en el programa</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
