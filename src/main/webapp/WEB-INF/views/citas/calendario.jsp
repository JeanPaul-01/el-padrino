<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Calendario de citas"/>
<c:set var="menu" value="citas"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>
<c:set var="volver" value="/citas?semana=${lunes}${empty barberoSeleccionado ? '' : '&barbero='}${barberoSeleccionado}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Calendario de citas</h1>
    <a href="${ctx}/citas/nueva${empty barberoSeleccionado ? '' : '?barberoId='}${barberoSeleccionado}" class="btn btn-padrino"><i class="bi bi-calendar-plus me-1"></i>Nueva cita</a>
</div>

<div class="card shadow-sm border-0 mb-3 no-print">
    <div class="card-body">
        <form method="get" action="${ctx}/citas" class="row g-2 align-items-end">
            <div class="col-sm-4 col-lg-3">
                <label class="form-label small text-muted mb-1">Barbero</label>
                <select name="barbero" class="form-select" onchange="this.form.submit()">
                    <option value="">Todos los barberos</option>
                    <c:forEach var="b" items="${barberos}">
                        <option value="${b.idBarbero}" ${b.idBarbero == barberoSeleccionado ? 'selected' : ''}><c:out value="${b.nombres}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-4 col-lg-3">
                <label class="form-label small text-muted mb-1">Semana del</label>
                <input type="date" name="semana" value="${f.iso(lunes)}" class="form-control" onchange="this.form.submit()">
            </div>
            <div class="col-sm-4 col-lg-6 d-flex gap-2 justify-content-lg-end">
                <a class="btn btn-outline-secondary" href="${ctx}/citas?semana=${semanaAnterior}&barbero=${barberoSeleccionado}"><i class="bi bi-chevron-left"></i> Anterior</a>
                <a class="btn btn-outline-secondary" href="${ctx}/citas?barbero=${barberoSeleccionado}">Hoy</a>
                <a class="btn btn-outline-secondary" href="${ctx}/citas?semana=${semanaSiguiente}&barbero=${barberoSeleccionado}">Siguiente <i class="bi bi-chevron-right"></i></a>
            </div>
        </form>
        <div class="mt-3 small">
            <span class="me-2 text-muted">Estados:</span>
            <c:forEach var="e" items="${estados}"><span class="badge text-bg-${e.color} me-1">${e.etiqueta}</span></c:forEach>
        </div>
    </div>
</div>

<div class="row g-2">
    <c:forEach var="dia" items="${dias}">
        <div class="col-md-6 col-xl-2">
            <div class="card border-0 shadow-sm dia-col ${dia.hoy ? 'border-top border-4 border-dark' : ''}">
                <div class="card-header bg-white text-center">
                    <div class="fw-bold">${f.dia(dia.fecha)}</div>
                    <small class="text-muted">${dia.citas.size()} cita(s)</small>
                </div>
                <div class="card-body p-2">
                    <c:forEach var="c" items="${dia.citas}">
                        <div class="card cita-card border-${c.estado.color} mb-2">
                            <div class="card-body p-2 small">
                                <div class="d-flex justify-content-between">
                                    <strong>${f.hora(c.hora)}</strong>
                                    <span class="badge text-bg-${c.estado.color}">${c.estado.etiqueta}</span>
                                </div>
                                <div><a href="${ctx}/clientes/${c.cliente.idCliente}" class="text-decoration-none"><c:out value="${c.cliente.nombres}"/></a></div>
                                <div class="text-muted"><c:out value="${c.servicio.nombre}"/> · <c:out value="${c.barbero.nombres}"/></div>
                                <c:if test="${not empty c.observaciones}"><div class="text-muted fst-italic"><c:out value="${c.observaciones}"/></div></c:if>
                                <c:if test="${c.estado.modificable}">
                                    <div class="d-flex gap-1 mt-2 no-print">
                                        <c:if test="${c.estado == 'PENDIENTE'}">
                                            <form method="post" action="${ctx}/citas/${c.idCita}/estado">
                                                <input type="hidden" name="estado" value="CONFIRMADA"><input type="hidden" name="volver" value="${volver}">
                                                <button class="btn btn-sm btn-outline-success py-0" title="Confirmar"><i class="bi bi-check2"></i></button>
                                            </form>
                                        </c:if>
                                        <a href="${ctx}/citas/${c.idCita}/atender" class="btn btn-sm btn-padrino py-0" title="Atender y cobrar"><i class="bi bi-cash"></i></a>
                                        <form method="post" action="${ctx}/citas/${c.idCita}/estado" onsubmit="return confirm('¿Cancelar la cita?');">
                                            <input type="hidden" name="estado" value="CANCELADA"><input type="hidden" name="volver" value="${volver}">
                                            <button class="btn btn-sm btn-outline-danger py-0" title="Cancelar"><i class="bi bi-x-lg"></i></button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty dia.citas}"><p class="text-center text-muted small mt-3">Sin citas</p></c:if>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
