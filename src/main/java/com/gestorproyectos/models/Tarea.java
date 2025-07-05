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

    public Tarea(String nombre, String descripcion, LocalDate fechaInicio,
                 LocalDate fechaFin, int duracionHoras, Analista analista) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.descripcion = (descripcion != null) ? descripcion : "";
        this.fechaInicio = fechaInicio;  // puede ser nula
        this.fechaFin = fechaFin;        // puede ser nula
        this.duracionHoras = (duracionHoras >= 0) ? duracionHoras : 0;
        this.analista = analista;        // puede ser nulo
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getDuracionHoras() { return duracionHoras; }
    public Analista getAnalista() { return analista; }

    @Override
    public String toString() {
        return String.format("Tarea [Nombre=%s, FechaInicio=%s, FechaFin=%s, Duración=%d horas, Analista=%s]",
                nombre,
                (fechaInicio != null ? fechaInicio : "Sin fecha"),
                (fechaFin != null ? fechaFin : "Sin fecha"),
                duracionHoras,
                (analista != null ? analista.getNombre() : "Sin asignar"));
    }
}
