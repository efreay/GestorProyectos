package com.gestorproyectos.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Tarea implements Serializable {
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int duracionHoras;
    private Analista analista;
    private String estado;  // ✅ NUEVO CAMPO

    public Tarea(String nombre, String descripcion, LocalDate fechaInicio,
                 LocalDate fechaFin, int duracionHoras, Analista analista, String estado) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.descripcion = (descripcion != null) ? descripcion : "";
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracionHoras = Math.max(duracionHoras, 0);
        this.analista = analista;
        this.estado = (estado != null) ? estado : "Pendiente";  // ✅ Valor por defecto
    }

    // Constructor anterior opcional para compatibilidad
    public Tarea(String nombre, String descripcion, LocalDate fechaInicio,
                 LocalDate fechaFin, int duracionHoras, Analista analista) {
        this(nombre, descripcion, fechaInicio, fechaFin, duracionHoras, analista, "Pendiente");
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getDuracionHoras() { return duracionHoras; }
    public Analista getAnalista() { return analista; }
    public String getEstado() { return estado; }  // ✅ NUEVO GETTER

    // Setters
    public void setEstado(String estado) {
        this.estado = (estado != null) ? estado : "Pendiente";
    }

    @Override
    public String toString() {
        return String.format("Tarea [Nombre=%s, Estado=%s, FechaInicio=%s, FechaFin=%s, Duración=%d horas, Analista=%s]",
                nombre,
                estado,
                (fechaInicio != null ? fechaInicio : "Sin fecha"),
                (fechaFin != null ? fechaFin : "Sin fecha"),
                duracionHoras,
                (analista != null ? analista.getNombre() : "Sin asignar"));
    }
}
