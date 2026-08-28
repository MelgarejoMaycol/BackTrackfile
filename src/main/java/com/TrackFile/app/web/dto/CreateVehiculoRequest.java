package com.TrackFile.app.web.dto;

public class CreateVehiculoRequest {
    private Integer idPropietario; // FK propietarios.id_propietario
    private String placa;
    private String vin;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private Integer kilometrajeActual;

    public Integer getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getKilometrajeActual() { return kilometrajeActual; }
    public void setKilometrajeActual(Integer kilometrajeActual) { this.kilometrajeActual = kilometrajeActual; }
}