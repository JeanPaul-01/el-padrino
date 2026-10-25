<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Servicios"/>
<c:set var="menu" value="servicios"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Servicios y precios</h1>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">${empty servicio.idServicio ? 'Nuevo servicio' : 'Editar servicio'}</div>
            <div class="card-body">
                <form method="post" action="${ctx}/servicios">
                    <c:if test="${not empty servicio.idServicio}"><input type="hidden" name="idServicio" value="${servicio.idServicio}"></c:if>
                    <div class="mb-3">
                        <label class="form-label">Nombre *</label>
                        <input type="text" name="nombre" class="form-control" maxlength="100" value="<c:out value='${servicio.nombre}'/>" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Descripción</label>
                        <input type="text" name="descripcion" class="form-control" maxlength="255" value="<c:out value='${servicio.descripcion}'/>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Precio (S/) *</label>
                        <input type="number" name="precio" class="form-control" min="0" step="0.01" value="${servicio.precio}" required>
                    </div>
                    <button class="btn btn-padrino"><i class="bi bi-save me-1"></i>Guardar</button>
                    <c:if test="${not empty servicio.idServicio}"><a href="${ctx}/servicios" class="btn btn-outline-secondary">Cancelar</a></c:if>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="card shadow-sm border-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light"><tr><th>Servicio</th><th>Descripción</th><th class="text-end">Precio</th><th>Estado</th><th class="text-end">Acciones</th></tr></thead>
                <tbody>
                <c:forEach var="s" items="${servicios}">
                    <tr class="${s.activo ? '' : 'text-muted'}">
                        <td class="fw-semibold"><c:out value="${s.nombre}"/></td>
                        <td><c:out value="${s.descripcion}"/></td>
                        <td class="text-end">${f.soles(s.precio)}</td>
                        <td><span class="badge ${s.activo ? 'text-bg-success' : 'text-bg-secondary'}">${s.activo ? 'Activo' : 'Inactivo'}</span></td>
                        <td class="text-end text-nowrap">
                            <a href="${ctx}/servicios?editar=${s.idServicio}" class="btn btn-sm btn-outline-secondary"><i class="bi bi-pencil"></i></a>
                            <form method="post" action="${ctx}/servicios/${s.idServicio}/estado" class="d-inline">
                                <button class="btn btn-sm btn-outline-${s.activo ? 'danger' : 'success'}">${s.activo ? 'Desactivar' : 'Activar'}</button>
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
