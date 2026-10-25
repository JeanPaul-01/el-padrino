<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Barberos"/>
<c:set var="menu" value="barberos"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Barberos</h1>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">${empty barbero.idBarbero ? 'Nuevo barbero' : 'Editar barbero'}</div>
            <div class="card-body">
                <form method="post" action="${ctx}/barberos">
                    <c:if test="${not empty barbero.idBarbero}"><input type="hidden" name="idBarbero" value="${barbero.idBarbero}"></c:if>
                    <div class="mb-3">
                        <label class="form-label">Nombres *</label>
                        <input type="text" name="nombres" class="form-control" maxlength="100" value="<c:out value='${barbero.nombres}'/>" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Celular</label>
                        <input type="tel" name="telefono" class="form-control" pattern="9[0-9]{8}" maxlength="9" value="<c:out value='${barbero.telefono}'/>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Especialidad</label>
                        <input type="text" name="especialidad" class="form-control" maxlength="100" value="<c:out value='${barbero.especialidad}'/>">
                    </div>
                    <button class="btn btn-padrino"><i class="bi bi-save me-1"></i>Guardar</button>
                    <c:if test="${not empty barbero.idBarbero}"><a href="${ctx}/barberos" class="btn btn-outline-secondary">Cancelar</a></c:if>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="card shadow-sm border-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light"><tr><th>Nombres</th><th>Celular</th><th>Especialidad</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
                <tbody>
                <c:forEach var="b" items="${barberos}">
                    <tr class="${b.activo ? '' : 'text-muted'}">
                        <td class="fw-semibold"><c:out value="${b.nombres}"/></td>
                        <td>${b.telefono}</td>
                        <td><c:out value="${b.especialidad}"/></td>
                        <td><span class="badge ${b.activo ? 'text-bg-success' : 'text-bg-secondary'}">${b.activo ? 'Activo' : 'Inactivo'}</span></td>
                        <td class="text-end text-nowrap">
                            <a href="${ctx}/citas?barbero=${b.idBarbero}" class="btn btn-sm btn-outline-dark" title="Ver agenda"><i class="bi bi-calendar-week"></i></a>
                            <a href="${ctx}/barberos?editar=${b.idBarbero}" class="btn btn-sm btn-outline-secondary"><i class="bi bi-pencil"></i></a>
                            <form method="post" action="${ctx}/barberos/${b.idBarbero}/estado" class="d-inline">
                                <button class="btn btn-sm btn-outline-${b.activo ? 'danger' : 'success'}">${b.activo ? 'Desactivar' : 'Activar'}</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
