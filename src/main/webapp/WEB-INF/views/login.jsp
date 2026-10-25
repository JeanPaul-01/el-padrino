<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | El Padrino</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/app.css" rel="stylesheet">
</head>
<body style="background: var(--padrino-oscuro);">
<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-lg border-0" style="max-width: 400px; width: 100%;">
        <div class="card-body p-4 p-md-5">
            <div class="text-center mb-4">
                <i class="bi bi-scissors fs-1" style="color: var(--padrino-cafe);"></i>
                <h1 class="h3 fw-bold mt-2">El Padrino</h1>
                <p class="text-muted mb-0">Sistema de gestión de la barbería</p>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2"><i class="bi bi-exclamation-triangle me-1"></i><c:out value="${error}"/></div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
                <div class="mb-3">
                    <label for="usuario" class="form-label">Usuario</label>
                    <input type="text" class="form-control" id="usuario" name="usuario" value="<c:out value='${usuario}'/>" required autofocus>
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label">Contraseña</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-padrino w-100 py-2"><i class="bi bi-box-arrow-in-right me-1"></i> Ingresar</button>
            </form>
            <hr>
            <a href="${pageContext.request.contextPath}/reserva" class="btn btn-outline-secondary w-100">
                <i class="bi bi-calendar-plus me-1"></i> ¿Eres cliente? Reserva tu cita
            </a>
        </div>
    </div>
</div>
</body>
</html>
