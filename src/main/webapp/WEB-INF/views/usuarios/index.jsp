<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Usuarios"/>
<c:set var="menu" value="usuarios"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jsp" %>

<h1 class="h3 fw-bold mb-3">Usuarios del sistema</h1>

<div class="row g-3">
    <div class="col-lg-4">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">Nuevo usuario</div>
            <div class="card-body">
                <form method="post" action="${ctx}/usuarios">
                    <div class="mb-3">
                        <label class="form-label">Usuario *</label>
                        <input type="text" name="nombreUsuario" class="form-control" maxlength="50" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Contraseña *</label>
                        <input type="password" name="password" class="form-control" minlength="6" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Rol *</label>
                        <select name="rol" class="form-select">
                            <c:forEach var="r" items="${roles}"><option value="${r}">${r.etiqueta}</option></c:forEach>
                        </select>
                    </div>
                    <button class="btn btn-padrino"><i class="bi bi-person-plus me-1"></i>Registrar</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="card shadow-sm border-0">
            <table class="table align-middle mb-0">
                <thead class="table-light"><tr><th>Usuario</th><th>Rol</th><th>Estado</th><th>Cambiar contraseña</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="u" items="${usuarios}">
                    <tr>
                        <td class="fw-semibold"><c:out value="${u.nombreUsuario}"/></td>
                        <td>${u.rol.etiqueta}</td>
                        <td><span class="badge ${u.activo ? 'text-bg-success' : 'text-bg-secondary'}">${u.activo ? 'Activo' : 'Inactivo'}</span></td>
                        <td>
                            <form method="post" action="${ctx}/usuarios/${u.idUsuario}/password" class="d-flex gap-1">
                                <input type="password" name="password" class="form-control form-control-sm" minlength="6" placeholder="Nueva" required>
                                <button class="btn btn-sm btn-outline-dark">Cambiar</button>
                            </form>
                        </td>
                        <td>
                            <c:if test="${u.idUsuario != usuarioActual.idUsuario}">
                                <form method="post" action="${ctx}/usuarios/${u.idUsuario}/estado">
                                    <button class="btn btn-sm btn-outline-${u.activo ? 'danger' : 'success'}">${u.activo ? 'Desactivar' : 'Activar'}</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/pie.jsp" %>
