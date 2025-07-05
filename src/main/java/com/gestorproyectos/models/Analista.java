package com.gestorproyectos.models;

import java.util.Objects;

public class Analista {
    private int id;
    private String nombre;
    private String rol;
    private String especialidad;

    // campos adicionales
    private int proyectosAsignados;
    private int tareasAsignadas;
    private int horasAsignadas;
    private int ocupacion;

    public Analista(int id, String nombre, String rol, String especialidad) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.rol = Objects.requireNonNull(rol, "El rol no puede ser nulo");
        this.especialidad = Objects.requireNonNull(especialidad, "La especialidad no puede ser nula");

        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo");
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public int getProyectosAsignados() {
        return proyectosAsignados;
    }

    public void setProyectosAsignados(int proyectosAsignados) {
        this.proyectosAsignados = proyectosAsignados;
    }

    public int getTareasAsignadas() {
        return tareasAsignadas;
    }

    public void setTareasAsignadas(int tareasAsignadas) {
        this.tareasAsignadas = tareasAsignadas;
    }

    public int getHorasAsignadas() {
        return horasAsignadas;
    }

    public void setHorasAsignadas(int horasAsignadas) {
        this.horasAsignadas = horasAsignadas;
    }

    public int getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(int ocupacion) {
        this.ocupacion = ocupacion;
    }

    @Override
    public String toString() {
        return String.format("Analista [ID=%d, Nombre=%s, Rol=%s, Especialidad=%s]", id, nombre, rol, especialidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analista analista = (Analista) o;
        return id == analista.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
