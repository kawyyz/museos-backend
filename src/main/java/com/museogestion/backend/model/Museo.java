package com.museogestion.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "museos")
public class Museo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    @Size(min = 2, max = 3)
    private String sigla;

    @NotBlank
    private String zona;

    @Column(length = 500)
    private String descripcion;

    @Min(0)
    private int precioEntrada;

    @Min(1)
    private int capacidadDiaria;

    private boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getPrecioEntrada() { return precioEntrada; }
    public void setPrecioEntrada(int precioEntrada) { this.precioEntrada = precioEntrada; }
    public int getCapacidadDiaria() { return capacidadDiaria; }
    public void setCapacidadDiaria(int capacidadDiaria) { this.capacidadDiaria = capacidadDiaria; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}