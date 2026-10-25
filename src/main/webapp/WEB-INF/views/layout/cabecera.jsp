<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${titulo}"/> | El Padrino</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/app.css" rel="stylesheet">
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="d-lg-flex">
    <nav class="sidebar p-3 flex-shrink-0">
        <a href="${ctx}/dashboard" class="d-block marca fs-5 mb-3 text-decoration-none">
            <i class="bi bi-scissors"></i> El Padrino
        </a>
        <ul class="nav flex-column">
            <li><a class="nav-link ${menu == 'dashboard' ? 'active' : ''}" href="${ctx}/dashboard"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
            <li class="seccion">Atención</li>
            <li><a class="nav-link ${menu == 'citas' ? 'active' : ''}" href="${ctx}/citas"><i class="bi bi-calendar-week me-2"></i>Citas</a></li>
            <li><a class="nav-link ${menu == 'clientes' ? 'active' : ''}" href="${ctx}/clientes"><i class="bi bi-people me-2"></i>Clientes</a></li>
            <li><a class="nav-link ${menu == 'ventas' ? 'active' : ''}" href="${ctx}/ventas"><i class="bi bi-cash-coin me-2"></i>Ventas</a></li>
            <li><a class="nav-link ${menu == 'fidelizacion' ? 'active' : ''}" href="${ctx}/fidelizacion"><i class="bi bi-award me-2"></i>Fidelización</a></li>
            <li class="seccion">Gestión</li>
            <li><a class="nav-link ${menu == 'inventario' ? 'active' : ''}" href="${ctx}/inventario"><i class="bi bi-box-seam me-2"></i>Inventario</a></li>
            <li><a class="nav-link ${menu == 'reportes' ? 'active' : ''}" href="${ctx}/reportes/ingresos"><i class="bi bi-bar-chart-line me-2"></i>Reportes</a></li>
            <li><a class="nav-link ${menu == 'servicios' ? 'active' : ''}" href="${ctx}/servicios"><i class="bi bi-list-check me-2"></i>Servicios</a></li>
            <li><a class="nav-link ${menu == 'barberos' ? 'active' : ''}" href="${ctx}/barberos"><i class="bi bi-person-badge me-2"></i>Barberos</a></li>
            <c:if test="${usuarioActual.administrador}">
                <li><a class="nav-link ${menu == 'usuarios' ? 'active' : ''}" href="${ctx}/usuarios"><i class="bi bi-shield-lock me-2"></i>Usuarios</a></li>
            </c:if>
            <li class="seccion">Clientes</li>
            <li><a class="nav-link" href="${ctx}/reserva" target="_blank"><i class="bi bi-globe me-2"></i>Reserva en línea</a></li>
        </ul>
        <hr class="border-secondary">
        <div class="text-white-50 small px-2">
            <i class="bi bi-person-circle"></i> <c:out value="${usuarioActual.nombreUsuario}"/>
            <span class="badge bg-secondary ms-1">${usuarioActual.rol.etiqueta}</span>
        </div>
        <a href="${ctx}/logout" class="nav-link mt-2"><i class="bi bi-box-arrow-right me-2"></i>Cerrar sesión</a>
    </nav>

    <main class="flex-grow-1 p-3 p-lg-4">
        <c:if test="${not empty ok}">
            <div class="alert alert-success alert-dismissible fade show no-print">
                <i class="bi bi-check-circle me-1"></i> <c:out value="${ok}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show no-print">
                <i class="bi bi-exclamation-triangle me-1"></i> <c:out value="${error}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
