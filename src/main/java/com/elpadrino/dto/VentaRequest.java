package com.elpadrino.dto;

import com.elpadrino.model.MetodoPago;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos enviados desde la pantalla "Registro de venta y pago".
 * Cada posición de las listas representa una línea de la venta.
 */
public class VentaRequest {

    private Integer clienteId;
    private Integer barberoId;
    private Integer citaId;
    private MetodoPago metodoPago;
    /** "SERVICIO" o "PRODUCTO" por cada línea. */
    private List<String> tipos = new ArrayList<>();
    private List<Integer> itemIds = new ArrayList<>();
    private List<Integer> cantidades = new ArrayList<>();

    public int getNumeroLineas() {
        return tipos == null ? 0 : tipos.size();
    }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public Integer getBarberoId() { return barberoId; }
    public void setBarberoId(Integer barberoId) { this.barberoId = barberoId; }
    public Integer getCitaId() { return citaId; }
    public void setCitaId(Integer citaId) { this.citaId = citaId; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public List<String> getTipos() { return tipos; }
    public void setTipos(List<String> tipos) { this.tipos = tipos; }
    public List<Integer> getItemIds() { return itemIds; }
    public void setItemIds(List<Integer> itemIds) { this.itemIds = itemIds; }
    public List<Integer> getCantidades() { return cantidades; }
    public void setCantidades(List<Integer> cantidades) { this.cantidades = cantidades; }
}
