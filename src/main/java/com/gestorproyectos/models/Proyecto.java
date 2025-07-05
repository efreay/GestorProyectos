package com.gestorproyectos.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Proyecto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String analistaAsignado;
    private int idAnalista;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
    private LocalDate fechaFinReal;
    private String estado;
    private int porcentajeCompletado;
    private String prioridad;
    private String epica;
    private String area;
    private int horasEstimadas;
    private int horasReales;
    private String sprintAsignado;

    public Proyecto(String id, String nombre, String analistaAsignado, int idAnalista,
                    LocalDate fechaInicio, LocalDate fechaFinEstimada, LocalDate fechaFinReal,
                    String estado, int porcentajeCompletado, String prioridad,
                    String epica, String area, int horasEstimadas, int horasReales,
                    String sprintAsignado) {

        this.id = (id != null && !id.isBlank()) ? id : "SIN-ID";
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.analistaAsignado = Objects.requireNonNull(analistaAsignado, "El analista asignado no puede ser nulo");
        this.idAnalista = (idAnalista >= 0) ? idAnalista : 0;
        this.fechaInicio = fechaInicio;
        this.fechaFinEstimada = fechaFinEstimada;
        this.fechaFinReal = fechaFinReal;
        this.estado = (estado != null && !estado.isBlank()) ? estado : "Desconocido";
        this.porcentajeCompletado = (porcentajeCompletado >= 0 && porcentajeCompletado <= 100) ? porcentajeCompletado : 0;
        this.prioridad = (prioridad != null && !prioridad.isBlank()) ? prioridad : "Media";
        this.epica = (epica != null) ? epica : "";
        this.area = (area != null) ? area : "";
        this.horasEstimadas = (horasEstimadas >= 0) ? horasEstimadas : 0;
        this.horasReales = (horasReales >= 0) ? horasReales : 0;
        this.sprintAsignado = (sprintAsignado != null) ? sprintAsignado : "";
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAnalistaAsignado() { return analistaAsignado; }
    public int getIdAnalista() { return idAnalista; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFinEstimada() { return fechaFinEstimada; }
    public LocalDate getFechaFinReal() { return fechaFinReal; }
    public String getEstado() { return estado; }
    public int getPorcentajeCompletado() { return porcentajeCompletado; }
    public String getPrioridad() { return prioridad; }
    public String getEpica() { return epica; }
    public String getArea() { return area; }
    public int getHorasEstimadas() { return horasEstimadas; }
    public int getHorasReales() { return horasReales; }
    public String getSprintAsignado() { return sprintAsignado; }

    // Setters
    public void setEstado(String estado) {
        this.estado = (estado != null && !estado.isBlank()) ? estado : "Desconocido";
    }

    public void setPorcentajeCompletado(int porcentajeCompletado) {
        this.porcentajeCompletado = (porcentajeCompletado >= 0 && porcentajeCompletado <= 100) ? porcentajeCompletado : 0;
    }

    public void setHorasReales(int horasReales) {
        this.horasReales = (horasReales >= 0) ? horasReales : 0;
    }

    public void setFechaFinReal(LocalDate fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    // Métodos útiles
    public boolean estaAtrasado() {
        if (fechaFinReal != null) return false;
        return LocalDate.now().isAfter(fechaFinEstimada) && !estado.equalsIgnoreCase("Completado");
    }

    public double getDesviacionHoras() {
        return horasReales - horasEstimadas;
    }

    @Override
    public String toString() {
        return String.format("Proyecto [ID=%s, Nombre=%s, Estado=%s, Avance=%d%%, Analista=%s]",
                id, nombre, estado, porcentajeCompletado, analistaAsignado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return id.equals(proyecto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
