<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error | El Padrino</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5" style="max-width: 640px;">
    <div class="card border-0 shadow-sm">
        <div class="card-body p-4 text-center">
            <h1 class="display-6 fw-bold">
                <c:choose>
                    <c:when test="${status == 403}">Acceso restringido</c:when>
                    <c:when test="${status == 404 or not empty mensaje}">No encontrado</c:when>
                    <c:otherwise>Ocurrió un error</c:otherwise>
                </c:choose>
            </h1>
            <p class="text-muted mt-3">
                <c:choose>
                    <c:when test="${not empty mensaje}"><c:out value="${mensaje}"/></c:when>
                    <c:when test="${not empty message}"><c:out value="${message}"/></c:when>
                    <c:otherwise>No se pudo completar la operación. Intente nuevamente.</c:otherwise>
                </c:choose>
            </p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-dark mt-2">Volver al inicio</a>
        </div>
    </div>
</div>
</body>
</html>
