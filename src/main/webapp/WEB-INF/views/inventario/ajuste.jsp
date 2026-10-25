<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Ajuste de stock"/>
<c:set var="menu" value="inventario"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Ajuste de stock</h1>

<div class="card shadow-sm border-0" style="max-width: 560px;">
    <div class="card-body p-4">
        <h2 class="h5"><c:out value="${item.producto.nombre}"/></h2>
        <p class="text-muted">Stock actual: <strong>${item.stockActual}</strong> · Stock mínimo: ${item.producto.stockMinimo}</p>
        <form method="post" action="${ctx}/inventario/ajuste">
            <input type="hidden" name="productoId" value="${item.producto.idProducto}">
            <div class="mb-3">
                <label class="form-label">Movimiento *</label>
                <div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="tipo" id="entrada" value="ENTRADA" checked>
                        <label class="form-check-label" for="entrada">Entrada (compra / reposición)</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="tipo" id="salida" value="SALIDA">
                        <label class="form-check-label" for="salida">Salida (merma / uso interno)</label>
                    </div>
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Cantidad *</label>
                <input type="number" name="cantidad" class="form-control" min="1" step="1" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Motivo</label>
                <input type="text" name="motivo" class="form-control" maxlength="150" placeholder="Ej.: compra al proveedor">
            </div>
            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-padrino"><i class="bi bi-check2 me-1"></i>Aplicar ajuste</button>
                <a href="${ctx}/inventario" class="btn btn-outline-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
