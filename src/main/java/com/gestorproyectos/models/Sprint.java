package com.gestorproyectos.models;

import java.time.LocalDate;
import java.util.Objects;

public class Sprint {
    private String id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String objetivo;
    private String estado;

    public Sprint(String id, String nombre, LocalDate fechaInicio, LocalDate fechaFin,
                  String objetivo, String estado) {
        this.id = (id != null && !id.isBlank()) ? id : "SIN-ID";
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.fechaInicio = fechaInicio;  // puede ser nula
        this.fechaFin = fechaFin;        // puede ser nula
        this.objetivo = (objetivo != null) ? objetivo : "";
        this.estado = validarEstado(estado);

        validarFechas();
    }

    private String validarEstado(String estado) {
        String[] estadosValidos = {"Planificado", "En progreso", "Completado", "Cancelado"};
        if (estado == null) return "Desconocido";
        for (String valido : estadosValidos) {
            if (valido.equalsIgnoreCase(estado)) {
                return valido;
            }
        }
        return "Desconocido";
    }

    private void validarFechas() {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public String getObjetivo() { return objetivo; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) {
        this.estado = validarEstado(estado);
    }

    public void setFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        validarFechas();
    }

    public boolean estaActivo() {
        LocalDate hoy = LocalDate.now();
        return estado.equals("En progreso") ||
                (fechaInicio != null && fechaFin != null && hoy.isAfter(fechaInicio) && hoy.isBefore(fechaFin));
    }

    public boolean estaCompletado() {
        return estado.equals("Completado");
    }

    @Override
    public String toString() {
        return String.format("Sprint [ID=%s, Nombre=%s, Fechas=%s a %s, Estado=%s]",
                id, nombre, fechaInicio, fechaFin, estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return id.equals(sprint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
