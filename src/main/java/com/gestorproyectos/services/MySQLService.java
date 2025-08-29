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

    /* ======================= ANALISTAS ======================= */

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

    public void actualizarAnalista(Analista a) throws SQLException {
        String sql = "UPDATE analistas SET nombre = ?, rol = ?, especialidad = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getRol());
            ps.setString(3, a.getEspecialidad());
            ps.setInt(4, a.getId());
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

    public List<Analista> obtenerAnalistas() throws SQLException {
        List<Analista> analistas = new ArrayList<>();
        String sql = "SELECT * FROM analistas";
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
                analistas.add(a);
            }
        }
        return analistas;
    }

    /* ======================= PROYECTOS ======================= */

    public void insertarProyecto(Proyecto p) throws SQLException {
        String sql = """
            INSERT INTO proyectos 
            (id, nombre, analista_asignado, id_analista, fecha_inicio, fecha_fin_estimada, fecha_fin_real, 
             estado, porcentaje_completado, prioridad, epica, area, horas_estimadas, horas_reales, sprint_asignado) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getAnalistaAsignado());
            ps.setInt(4, p.getIdAnalista());

            // fechas (null-safe)
            if (p.getFechaInicio() != null) ps.setDate(5, Date.valueOf(p.getFechaInicio())); else ps.setNull(5, Types.DATE);
            if (p.getFechaFinEstimada() != null) ps.setDate(6, Date.valueOf(p.getFechaFinEstimada())); else ps.setNull(6, Types.DATE);
            if (p.getFechaFinReal() != null) ps.setDate(7, Date.valueOf(p.getFechaFinReal())); else ps.setNull(7, Types.DATE);

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

    /** Soporte de edición de proyecto */
    public void actualizarProyecto(Proyecto p) throws SQLException {
        String sql = """
            UPDATE proyectos SET
                nombre = ?,
                analista_asignado = ?,
                id_analista = ?,
                fecha_inicio = ?,
                fecha_fin_estimada = ?,
                fecha_fin_real = ?,
                estado = ?,
                porcentaje_completado = ?,
                prioridad = ?,
                epica = ?,
                area = ?,
                horas_estimadas = ?,
                horas_reales = ?,
                sprint_asignado = ?
            WHERE id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,  p.getNombre());
            ps.setString(2,  p.getAnalistaAsignado());
            ps.setInt(3,     p.getIdAnalista());

            if (p.getFechaInicio() != null) ps.setDate(4,  Date.valueOf(p.getFechaInicio())); else ps.setNull(4, Types.DATE);
            if (p.getFechaFinEstimada() != null) ps.setDate(5,  Date.valueOf(p.getFechaFinEstimada())); else ps.setNull(5, Types.DATE);
            if (p.getFechaFinReal() != null) ps.setDate(6,  Date.valueOf(p.getFechaFinReal())); else ps.setNull(6, Types.DATE);

            ps.setString(7,  p.getEstado());
            ps.setInt(8,     p.getPorcentajeCompletado());
            ps.setString(9,  p.getPrioridad());
            ps.setString(10, p.getEpica());
            ps.setString(11, p.getArea());
            ps.setInt(12,    p.getHorasEstimadas());
            ps.setInt(13,    p.getHorasReales());
            ps.setString(14, p.getSprintAsignado());

            ps.setString(15, p.getId());

            ps.executeUpdate();
        }
    }

    public List<Proyecto> leerProyectos() throws SQLException {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date sqlInicio = rs.getDate("fecha_inicio");
                Date sqlFinEst = rs.getDate("fecha_fin_estimada");
                Date sqlFinReal = rs.getDate("fecha_fin_real");

                LocalDate fechaInicio = (sqlInicio != null) ? sqlInicio.toLocalDate() : null;
                LocalDate fechaFinEst = (sqlFinEst != null) ? sqlFinEst.toLocalDate() : null;
                LocalDate fechaFinReal = (sqlFinReal != null) ? sqlFinReal.toLocalDate() : null;

                Proyecto p = new Proyecto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("analista_asignado"),
                        rs.getInt("id_analista"),
                        fechaInicio,
                        fechaFinEst,
                        fechaFinReal,
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

    /** (Opcional) Obtener un proyecto por ID para precargar al editar */
    public Proyecto obtenerProyectoPorId(String id) throws SQLException {
        String sql = "SELECT * FROM proyectos WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date sqlInicio = rs.getDate("fecha_inicio");
                    Date sqlFinEst = rs.getDate("fecha_fin_estimada");
                    Date sqlFinReal = rs.getDate("fecha_fin_real");

                    LocalDate fechaInicio = (sqlInicio != null) ? sqlInicio.toLocalDate() : null;
                    LocalDate fechaFinEst = (sqlFinEst != null) ? sqlFinEst.toLocalDate() : null;
                    LocalDate fechaFinReal = (sqlFinReal != null) ? sqlFinReal.toLocalDate() : null;

                    return new Proyecto(
                            rs.getString("id"),
                            rs.getString("nombre"),
                            rs.getString("analista_asignado"),
                            rs.getInt("id_analista"),
                            fechaInicio,
                            fechaFinEst,
                            fechaFinReal,
                            rs.getString("estado"),
                            rs.getInt("porcentaje_completado"),
                            rs.getString("prioridad"),
                            rs.getString("epica"),
                            rs.getString("area"),
                            rs.getInt("horas_estimadas"),
                            rs.getInt("horas_reales"),
                            rs.getString("sprint_asignado")
                    );
                }
            }
        }
        return null;
    }

    /* ======================= SPRINTS ======================= */

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

    /** NUEVO: actualizar sprint (soporte edición) */
    public void actualizarSprint(Sprint s) throws SQLException {
        String sql = "UPDATE sprints SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, objetivo = ?, estado = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setDate(2, s.getFechaInicio() != null ? Date.valueOf(s.getFechaInicio()) : null);
            ps.setDate(3, s.getFechaFin() != null ? Date.valueOf(s.getFechaFin()) : null);
            ps.setString(4, s.getObjetivo());
            ps.setString(5, s.getEstado());
            ps.setString(6, s.getId());
            ps.executeUpdate();
        }
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

    /* ======================= TAREAS ======================= */

    public void insertarTarea(Tarea t) throws SQLException {
        String sql = """
            INSERT INTO tareas 
            (nombre, descripcion, fecha_inicio, fecha_fin, duracion_horas, id_analista, analista_nombre, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());

            if (t.getFechaInicio() != null) ps.setDate(3, Date.valueOf(t.getFechaInicio())); else ps.setNull(3, Types.DATE);
            if (t.getFechaFin() != null) ps.setDate(4, Date.valueOf(t.getFechaFin())); else ps.setNull(4, Types.DATE);

            ps.setInt(5, t.getDuracionHoras());
            ps.setInt(6, t.getAnalista() != null ? t.getAnalista().getId() : 0);
            ps.setString(7, t.getAnalista() != null ? t.getAnalista().getNombre() : "Sin asignar");
            ps.setString(8, t.getEstado() != null ? t.getEstado() : "Pendiente");

            ps.executeUpdate();
        }
    }

    /** NUEVO: actualizar tarea (clave temporal: nombre_original + fecha_inicio_original) */
    public void actualizarTarea(Tarea t, String nombreOriginal, LocalDate fechaInicioOriginal) throws SQLException {
        String sql = """
            UPDATE tareas SET 
                nombre = ?, 
                descripcion = ?, 
                fecha_inicio = ?, 
                fecha_fin = ?, 
                duracion_horas = ?, 
                id_analista = ?, 
                analista_nombre = ?, 
                estado = ?
            WHERE nombre = ? AND fecha_inicio = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            if (t.getDescripcion() != null) ps.setString(2, t.getDescripcion()); else ps.setNull(2, Types.VARCHAR);
            if (t.getFechaInicio() != null) ps.setDate(3, Date.valueOf(t.getFechaInicio())); else ps.setNull(3, Types.DATE);
            if (t.getFechaFin() != null) ps.setDate(4, Date.valueOf(t.getFechaFin())); else ps.setNull(4, Types.DATE);
            ps.setInt(5, t.getDuracionHoras());
            ps.setInt(6, t.getAnalista() != null ? t.getAnalista().getId() : 0);
            ps.setString(7, t.getAnalista() != null ? t.getAnalista().getNombre() : "Sin asignar");
            ps.setString(8, t.getEstado() != null ? t.getEstado() : "Pendiente");

            ps.setString(9, nombreOriginal);
            if (fechaInicioOriginal != null) ps.setDate(10, Date.valueOf(fechaInicioOriginal)); else ps.setNull(10, Types.DATE);

            ps.executeUpdate();
        }
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

                Date sqlFechaInicio = rs.getDate("fecha_inicio");
                Date sqlFechaFin = rs.getDate("fecha_fin");

                LocalDate fechaInicio = (sqlFechaInicio != null) ? sqlFechaInicio.toLocalDate() : null;
                LocalDate fechaFin = (sqlFechaFin != null) ? sqlFechaFin.toLocalDate() : null;

                String estado = rs.getString("estado");
                if (estado == null || estado.isBlank()) {
                    estado = "Pendiente";
                }

                Tarea t = new Tarea(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        fechaInicio,
                        fechaFin,
                        rs.getInt("duracion_horas"),
                        a,
                        estado
                );

                tareas.add(t);
            }
        }
        return tareas;
    }
}
