<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Clientes"/>
<c:set var="menu" value="clientes"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Clientes</h1>
    <a href="${ctx}/clientes/nuevo" class="btn btn-padrino"><i class="bi bi-person-plus me-1"></i>Nuevo cliente</a>
</div>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <form method="get" action="${ctx}/clientes" class="row g-2 mb-3">
            <div class="col-md-6">
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-search"></i></span>
                    <input type="text" name="q" class="form-control" placeholder="Buscar por nombre o teléfono" value="<c:out value='${q}'/>">
                </div>
            </div>
            <div class="col-auto">
                <button class="btn btn-dark">Buscar</button>
                <c:if test="${not empty q}"><a href="${ctx}/clientes" class="btn btn-link">Limpiar</a></c:if>
            </div>
        </form>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light"><tr><th>Nombres</th><th>Teléfono</th><th>Correo</th><th>Registrado</th><th class="text-end">Acciones</th></tr></thead>
                <tbody>
                <c:forEach var="cl" items="${clientes}">
                    <tr>
                        <td class="fw-semibold"><c:out value="${cl.nombres}"/></td>
                        <td>${cl.telefono}</td>
                        <td><c:out value="${empty cl.correo ? '—' : cl.correo}"/></td>
                        <td>${f.fecha(cl.fechaRegistro)}</td>
                        <td class="text-end">
                            <a href="${ctx}/clientes/${cl.idCliente}" class="btn btn-sm btn-outline-dark"><i class="bi bi-clock-history"></i> Historial</a>
                            <a href="${ctx}/clientes/${cl.idCliente}/editar" class="btn btn-sm btn-outline-secondary"><i class="bi bi-pencil"></i></a>
                            <a href="${ctx}/citas/nueva?clienteId=${cl.idCliente}" class="btn btn-sm btn-padrino"><i class="bi bi-calendar-plus"></i></a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty clientes}">
                    <tr><td colspan="5" class="text-center text-muted py-4">No se encontraron clientes</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
        <small class="text-muted">${clientes.size()} cliente(s)</small>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
