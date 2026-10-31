<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Inventario"/>
<c:set var="menu" value="inventario"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h1 class="h3 fw-bold mb-0">Inventario de productos</h1>
    <div class="d-flex gap-2">
        <a href="${ctx}/inventario/exportar" class="btn btn-outline-success"><i class="bi bi-file-earmark-excel me-1"></i>Exportar Excel</a>
        <a href="${ctx}/reportes/stock-clientes" class="btn btn-outline-dark"><i class="bi bi-file-earmark-text me-1"></i>Reporte de stock bajo</a>
        <a href="${ctx}/productos/nuevo" class="btn btn-padrino"><i class="bi bi-plus-lg me-1"></i>Nuevo producto</a>
    </div>
</div>

<c:if test="${criticos > 0}">
    <div class="alert alert-warning"><i class="bi bi-exclamation-triangle me-1"></i>
        Hay <strong>${criticos}</strong> producto(s) con stock igual o menor al mínimo. Reponga a tiempo para no perder ventas.</div>
</c:if>

<div class="card shadow-sm border-0">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr><th>Producto</th><th>Categoría</th><th class="text-end">Precio</th><th class="text-center">Stock actual</th>
                <th class="text-center">Stock mínimo</th><th>Estado</th><th>Actualizado</th><th class="text-end">Acciones</th></tr>
            </thead>
            <tbody>
            <c:forEach var="i" items="${inventario}">
                <tr class="${not i.producto.activo ? 'text-muted' : (i.stockCritico ? 'table-danger' : '')}">
                    <td class="fw-semibold"><c:out value="${i.producto.nombre}"/>
                        <c:if test="${not i.producto.activo}"><span class="badge text-bg-secondary ms-1">Inactivo</span></c:if></td>
                    <td><c:out value="${i.producto.categoria}"/></td>
                    <td class="text-end">${f.soles(i.producto.precio)}</td>
                    <td class="text-center fw-bold">${i.stockActual}</td>
                    <td class="text-center">${i.producto.stockMinimo}</td>
                    <td>
                        <c:choose>
                            <c:when test="${i.stockActual == 0}"><span class="badge text-bg-danger">Agotado</span></c:when>
                            <c:when test="${i.stockCritico}"><span class="badge text-bg-warning">Stock bajo</span></c:when>
                            <c:otherwise><span class="badge text-bg-success">Normal</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="small">${f.fechaHora(i.fechaActualizacion)}</td>
                    <td class="text-end text-nowrap">
                        <a href="${ctx}/inventario/${i.producto.idProducto}/ajuste" class="btn btn-sm btn-padrino" title="Ajustar stock"><i class="bi bi-arrow-left-right"></i> Stock</a>
                        <a href="${ctx}/productos/${i.producto.idProducto}/editar" class="btn btn-sm btn-outline-secondary" title="Editar"><i class="bi bi-pencil"></i></a>
                        <form method="post" action="${ctx}/productos/${i.producto.idProducto}/estado" class="d-inline">
                            <button class="btn btn-sm btn-outline-${i.producto.activo ? 'danger' : 'success'}" title="${i.producto.activo ? 'Desactivar' : 'Activar'}">
                                <i class="bi bi-${i.producto.activo ? 'eye-slash' : 'eye'}"></i></button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty inventario}"><tr><td colspan="8" class="text-center text-muted py-4">No hay productos registrados</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
