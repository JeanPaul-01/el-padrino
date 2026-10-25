<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="${empty producto.idProducto ? 'Nuevo producto' : 'Editar producto'}"/>
<c:set var="menu" value="inventario"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">${titulo}</h1>

<div class="card shadow-sm border-0" style="max-width: 720px;">
    <div class="card-body p-4">
        <form method="post" action="${ctx}/productos">
            <c:if test="${not empty producto.idProducto}"><input type="hidden" name="idProducto" value="${producto.idProducto}"></c:if>
            <div class="row g-3">
                <div class="col-md-8">
                    <label class="form-label">Nombre *</label>
                    <input type="text" name="nombre" class="form-control" maxlength="100" value="<c:out value='${producto.nombre}'/>" required pattern=".*\S.*" title="El nombre no puede estar vacío ni contener solo espacios">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Categoría</label>
                    <input type="text" name="categoria" class="form-control" maxlength="50" list="categorias" value="<c:out value='${producto.categoria}'/>">
                    <datalist id="categorias"><option value="Cabello"><option value="Barba"><option value="Afeitado"><option value="Accesorios"></datalist>
                </div>
                <div class="col-12">
                    <label class="form-label">Descripción</label>
                    <input type="text" name="descripcion" class="form-control" maxlength="255" value="<c:out value='${producto.descripcion}'/>">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Precio (S/) *</label>
                    <input type="number" name="precio" class="form-control" min="0.01" step="0.01" value="${producto.precio}" required title="El precio debe ser mayor que 0">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Stock mínimo *</label>
                    <input type="number" name="stockMinimo" class="form-control" min="0" step="1" value="${producto.stockMinimo}" required>
                    <div class="form-text">Se alerta cuando el stock llega a este nivel.</div>
                </div>
                <c:if test="${empty producto.idProducto}">
                    <div class="col-md-4">
                        <label class="form-label">Stock inicial *</label>
                        <input type="number" name="stockInicial" class="form-control" min="0" step="1" value="${empty stockInicial ? 0 : stockInicial}" required>
                    </div>
                </c:if>
            </div>
            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-padrino"><i class="bi bi-save me-1"></i>Guardar</button>
                <a href="${ctx}/inventario" class="btn btn-outline-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
