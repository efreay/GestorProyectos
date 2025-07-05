package com.gestorproyectos.services;

import com.gestorproyectos.models.Analista;
import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.models.Sprint;
import com.gestorproyectos.models.Tarea;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLService {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gestor?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "root";
        return DriverManager.getConnection(url, user, pass);
    }

    public void insertarAnalista(Analista a) throws SQLException {
        String sql = "INSERT INTO analistas (id, nombre, rol, especialidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            ps.setString(2, a.getNombre());
            ps.setString(3, a.getRol());
            ps.setString(4, a.getEspecialidad());
            ps.executeUpdate();
        }
    }

    public void insertarProyecto(Proyecto p) throws SQLException {
        String sql = "INSERT INTO proyectos (id, nombre, analista_asignado, id_analista, fecha_inicio, fecha_fin_estimada, fecha_fin_real, estado, porcentaje_completado, prioridad, epica, area, horas_estimadas, horas_reales, sprint_asignado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getAnalistaAsignado());
            ps.setInt(4, p.getIdAnalista());
            ps.setDate(5, Date.valueOf(p.getFechaInicio()));
            ps.setDate(6, Date.valueOf(p.getFechaFinEstimada()));
            if (p.getFechaFinReal() != null) {
                ps.setDate(7, Date.valueOf(p.getFechaFinReal()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setString(8, p.getEstado());
            ps.setInt(9, p.getPorcentajeCompletado());
            ps.setString(10, p.getPrioridad());
            ps.setString(11, p.getEpica());
            ps.setString(12, p.getArea());
            ps.setInt(13, p.getHorasEstimadas());
            ps.setInt(14, p.getHorasReales());
            ps.setString(15, p.getSprintAsignado());
            ps.executeUpdate();
        }
    }

    public void insertarSprint(Sprint s) throws SQLException {
        String sql = "INSERT INTO sprints (id, nombre, fecha_inicio, fecha_fin, objetivo, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getNombre());
            ps.setDate(3, Date.valueOf(s.getFechaInicio()));
            ps.setDate(4, Date.valueOf(s.getFechaFin()));
            ps.setString(5, s.getObjetivo());
            ps.setString(6, s.getEstado());
            ps.executeUpdate();
        }
    }

    public List<Analista> leerAnalistasConOcupacion() throws SQLException {
        List<Analista> analistas = new ArrayList<>();
        String sql = """
                SELECT a.id, a.nombre, a.rol, a.especialidad,
                (SELECT COUNT(*) FROM proyectos p WHERE p.id_analista = a.id) AS proyectos_asignados,
                (SELECT COUNT(*) FROM tareas t WHERE t.id_analista = a.id) AS tareas_asignadas,
                COALESCE((SELECT SUM(t.duracion_horas) FROM tareas t WHERE t.id_analista = a.id), 0) AS horas_asignadas,
                LEAST(COALESCE((SELECT SUM(t.duracion_horas) FROM tareas t WHERE t.id_analista = a.id),0) * 5, 100) AS ocupacion
                FROM analistas a
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Analista a = new Analista(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getString("especialidad")
                );
                a.setProyectosAsignados(rs.getInt("proyectos_asignados"));
                a.setTareasAsignadas(rs.getInt("tareas_asignadas"));
                a.setHorasAsignadas(rs.getInt("horas_asignadas"));
                a.setOcupacion(rs.getInt("ocupacion"));
                analistas.add(a);
            }
        }
        return analistas;
    }

    public List<Proyecto> leerProyectos() throws SQLException {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proyecto p = new Proyecto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("analista_asignado"),
                        rs.getInt("id_analista"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin_estimada").toLocalDate(),
                        rs.getDate("fecha_fin_real") != null ? rs.getDate("fecha_fin_real").toLocalDate() : null,
                        rs.getString("estado"),
                        rs.getInt("porcentaje_completado"),
                        rs.getString("prioridad"),
                        rs.getString("epica"),
                        rs.getString("area"),
                        rs.getInt("horas_estimadas"),
                        rs.getInt("horas_reales"),
                        rs.getString("sprint_asignado")
                );
                proyectos.add(p);
            }
        }
        return proyectos;
    }

    public List<Sprint> leerSprints() throws SQLException {
        List<Sprint> sprints = new ArrayList<>();
        String sql = "SELECT * FROM sprints";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sprint s = new Sprint(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getString("objetivo"),
                        rs.getString("estado")
                );
                sprints.add(s);
            }
        }
        return sprints;
    }

    public List<Tarea> leerTareas() throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Analista a = new Analista(
                        rs.getInt("id_analista"),
                        rs.getString("analista_nombre"),
                        "", ""
                );
                Tarea t = new Tarea(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getInt("duracion_horas"),
                        a
                );
                tareas.add(t);
            }
        }
        return tareas;
    }
}
