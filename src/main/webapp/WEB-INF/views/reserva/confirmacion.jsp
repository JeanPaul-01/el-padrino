<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reserva confirmada | El Padrino</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/app.css" rel="stylesheet">
</head>
<body>
<div class="container py-5" style="max-width: 560px;">
    <div class="card shadow-sm border-0 text-center">
        <div class="card-body p-5">
            <i class="bi bi-check-circle-fill text-success" style="font-size: 3.5rem;"></i>
            <h1 class="h3 fw-bold mt-3">¡Reserva registrada!</h1>
            <p class="text-muted">Te esperamos, <c:out value="${cita.cliente.nombres}"/>.</p>
            <ul class="list-group list-group-flush text-start my-4">
                <li class="list-group-item"><i class="bi bi-calendar me-2"></i><strong>Fecha:</strong> ${f.fecha(cita.fecha)}</li>
                <li class="list-group-item"><i class="bi bi-clock me-2"></i><strong>Hora:</strong> ${f.hora(cita.hora)}</li>
                <li class="list-group-item"><i class="bi bi-scissors me-2"></i><strong>Servicio:</strong> <c:out value="${cita.servicio.nombre}"/> (${f.soles(cita.servicio.precio)})</li>
                <li class="list-group-item"><i class="bi bi-person me-2"></i><strong>Barbero:</strong> <c:out value="${cita.barbero.nombres}"/></li>
                <li class="list-group-item"><i class="bi bi-hash me-2"></i><strong>Código de reserva:</strong> ${cita.idCita}</li>
            </ul>
            <p class="small text-muted">La recepción confirmará tu cita. Si necesitas cancelarla, comunícate por WhatsApp indicando tu código.</p>
            <a href="${ctx}/reserva" class="btn btn-padrino">Hacer otra reserva</a>
        </div>
    </div>
</div>
</body>
</html>
