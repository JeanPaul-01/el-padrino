<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Perfil del cliente"/>
<c:set var="menu" value="clientes"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0"><c:out value="${cliente.nombres}"/></h1>
    <div class="d-flex gap-2">
        <a href="${ctx}/clientes/${cliente.idCliente}/editar" class="btn btn-outline-secondary"><i class="bi bi-pencil me-1"></i>Editar</a>
        <a href="${ctx}/citas/nueva?clienteId=${cliente.idCliente}" class="btn btn-padrino"><i class="bi bi-calendar-plus me-1"></i>Nueva cita</a>
    </div>
</div>

<div class="row g-3 mb-3">
    <div class="col-lg-5">
        <div class="card shadow-sm border-0 h-100">
            <div class="card-header bg-white fw-semibold">Datos del cliente</div>
            <ul class="list-group list-group-flush">
                <li class="list-group-item"><i class="bi bi-telephone me-2"></i>${cliente.telefono}</li>
                <li class="list-group-item"><i class="bi bi-envelope me-2"></i><c:out value="${empty cliente.correo ? 'Sin correo' : cliente.correo}"/></li>
                <li class="list-group-item"><i class="bi bi-calendar me-2"></i>Cliente desde ${f.fecha(cliente.fechaRegistro)}</li>
                <li class="list-group-item"><i class="bi bi-receipt me-2"></i>${ventas.size()} visita(s) con pago registradas</li>
            </ul>
        </div>
    </div>
    <div class="col-lg-7">
        <div class="card shadow-sm border-0 h-100">
            <div class="card-header bg-white fw-semibold"><i class="bi bi-award me-1"></i> Tarjeta de cliente frecuente</div>
            <div class="card-body">
                <c:set var="cortes" value="${empty tarjeta ? 0 : tarjeta.cortesTarjeta}"/>
                <div class="d-flex gap-2 mb-3">
                    <c:forEach begin="1" end="5" var="n">
                        <span class="sello ${n <= cortes ? 'lleno' : ''}"><i class="bi bi-scissors"></i></span>
                    </c:forEach>
                    <span class="sello ${not empty tarjeta and tarjeta.corteGratisDisponible ? 'lleno bg-success border-success' : ''}"><i class="bi bi-gift"></i></span>
                </div>
                <p class="mb-1"><strong>${cortes} de 5</strong> cortes acumulados</p>
                <c:choose>
                    <c:when test="${not empty tarjeta and tarjeta.corteGratisDisponible}">
                        <span class="badge text-bg-success">Corte gratis disponible: se aplica en su próxima venta</span>
                    </c:when>
                    <c:otherwise>
                        <span class="text-muted">Le faltan ${5 - cortes} corte(s) para el corte gratis.</span>
                    </c:otherwise>
                </c:choose>
                <c:if test="${not empty tarjeta}"><p class="small text-muted mt-2 mb-0">Servicios pagados en total: ${tarjeta.puntos}</p></c:if>
            </div>
        </div>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-6">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">Historial de citas</div>
            <div class="table-responsive">
                <table class="table table-sm align-middle mb-0">
                    <thead class="table-light"><tr><th>Fecha</th><th>Hora</th><th>Servicio</th><th>Barbero</th><th>Estado</th></tr></thead>
                    <tbody>
                    <c:forEach var="c" items="${citas}">
                        <tr>
                            <td>${f.fecha(c.fecha)}</td><td>${f.hora(c.hora)}</td>
                            <td><c:out value="${c.servicio.nombre}"/></td><td><c:out value="${c.barbero.nombres}"/></td>
                            <td><span class="badge text-bg-${c.estado.color}">${c.estado.etiqueta}</span></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty citas}"><tr><td colspan="5" class="text-center text-muted py-3">Sin citas registradas</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">Historial de servicios y compras</div>
            <div class="table-responsive">
                <table class="table table-sm align-middle mb-0">
                    <thead class="table-light"><tr><th>Fecha</th><th>Detalle</th><th class="text-end">Total</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach var="v" items="${ventas}">
                        <tr>
                            <td>${f.fechaHora(v.fecha)}</td>
                            <td>
                                <c:forEach var="d" items="${v.detalles}" varStatus="st">
                                    <c:out value="${d.descripcion}"/><c:if test="${d.cantidad > 1}"> x${d.cantidad}</c:if><c:if test="${not st.last}">, </c:if>
                                </c:forEach>
                                <c:if test="${v.conCorteGratis}"><span class="badge text-bg-success ms-1">Corte gratis</span></c:if>
                            </td>
                            <td class="text-end">${f.soles(v.total)}</td>
                            <td><a href="${ctx}/ventas/${v.idVenta}" class="btn btn-sm btn-link">Ver</a></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty ventas}"><tr><td colspan="4" class="text-center text-muted py-3">Sin ventas registradas</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
