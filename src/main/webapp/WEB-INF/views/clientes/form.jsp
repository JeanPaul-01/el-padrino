<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="${empty cliente.idCliente ? 'Nuevo cliente' : 'Editar cliente'}"/>
<c:set var="menu" value="clientes"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">${titulo}</h1>

<div class="card shadow-sm border-0" style="max-width: 640px;">
    <div class="card-body p-4">
        <form method="post" action="${ctx}/clientes">
            <c:if test="${not empty cliente.idCliente}"><input type="hidden" name="idCliente" value="${cliente.idCliente}"></c:if>
            <div class="mb-3">
                <label class="form-label">Nombres y apellidos *</label>
                <input type="text" name="nombres" class="form-control" maxlength="100" value="<c:out value='${cliente.nombres}'/>" required>
            </div>
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Celular *</label>
                    <input type="tel" name="telefono" class="form-control" pattern="9[0-9]{8}" maxlength="9" title="9 dígitos que empiecen con 9"
                           value="<c:out value='${cliente.telefono}'/>" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Correo</label>
                    <input type="email" name="correo" class="form-control" maxlength="100" value="<c:out value='${cliente.correo}'/>">
                </div>
            </div>
            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-padrino"><i class="bi bi-save me-1"></i>Guardar</button>
                <a href="${ctx}/clientes" class="btn btn-outline-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
