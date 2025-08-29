package com.gestorproyectos.views;

import com.gestorproyectos.models.Analista;
import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.models.Sprint;
import com.gestorproyectos.models.Tarea;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {

    private JTable tablaAnalistas;
    private JTable tablaProyectos;
    private JTable tablaSprints;
    private JTable tablaTareas;

    private MySQLService mysql;

    public MainFrame() {
        setTitle("Gestor de Proyectos");
        setSize(1300, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        mysql = new MySQLService();

        JTabbedPane tabs = new JTabbedPane();

        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(150, 40);

        // PANEL ANALISTAS
        JPanel panelAnalistas = new JPanel(new BorderLayout());
        tablaAnalistas = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Rol", "Especialidad", "Proyectos", "Tareas", "Horas", "Ocupación"}, 0
        ));
        panelAnalistas.add(new JScrollPane(tablaAnalistas), BorderLayout.CENTER);

        JPanel barraAnalistas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevoAnalista = new JButton("\u2795 Añadir");
        JButton btnEditarAnalista = new JButton("\u270E Editar");
        JButton btnRefrescarAnalistas = new JButton("\uD83D\uDD04 Refrescar");
        JButton btnVolverAnalistas = new JButton("\uD83D\uDD19 Volver");

        btnNuevoAnalista.setFont(buttonFont);
        btnEditarAnalista.setFont(buttonFont);
        btnRefrescarAnalistas.setFont(buttonFont);
        btnVolverAnalistas.setFont(buttonFont);

        btnNuevoAnalista.setPreferredSize(buttonSize);
        btnEditarAnalista.setPreferredSize(buttonSize);
        btnRefrescarAnalistas.setPreferredSize(buttonSize);
        btnVolverAnalistas.setPreferredSize(buttonSize);

        barraAnalistas.add(btnNuevoAnalista);
        barraAnalistas.add(btnEditarAnalista);
        barraAnalistas.add(btnRefrescarAnalistas);
        barraAnalistas.add(btnVolverAnalistas);
        panelAnalistas.add(barraAnalistas, BorderLayout.NORTH);

        btnNuevoAnalista.addActionListener(e -> new AnalistaDialog(this));
        btnEditarAnalista.addActionListener(e -> {
            int fila = tablaAnalistas.getSelectedRow();
            if (fila == -1) {
                Mensajes.mostrarAdvertencia("Selecciona un analista para editar.");
                return;
            }
            int id = (int) tablaAnalistas.getValueAt(fila, 0);
            String nombre = safeString(tablaAnalistas.getValueAt(fila, 1));
            String rol = safeString(tablaAnalistas.getValueAt(fila, 2));
            String especialidad = safeString(tablaAnalistas.getValueAt(fila, 3));
            com.gestorproyectos.models.Analista a = new com.gestorproyectos.models.Analista(id, nombre, rol, especialidad);
            new AnalistaDialog(this, a);
        });
        btnRefrescarAnalistas.addActionListener(e -> cargarAnalistas());
        btnVolverAnalistas.addActionListener(e -> {
            new DashboardFrame();
            dispose();
        });

        tabs.addTab("Analistas", panelAnalistas);

        // PANEL PROYECTOS
        JPanel panelProyectos = new JPanel(new BorderLayout());
        tablaProyectos = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Analista", "Estado", "Fecha Inicio", "Fecha Fin Estimada", "Porcentaje", "Prioridad", "Epica", "Área"}, 0
        ));
        panelProyectos.add(new JScrollPane(tablaProyectos), BorderLayout.CENTER);

        JPanel barraProyectos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevoProyecto = new JButton("\u2795 Añadir");
        JButton btnEditarProyecto = new JButton("\u270E Editar");
        JButton btnRefrescarProyectos = new JButton("\uD83D\uDD04 Refrescar");
        JButton btnVolverProyectos = new JButton("\uD83D\uDD19 Volver");

        btnNuevoProyecto.setFont(buttonFont);
        btnEditarProyecto.setFont(buttonFont);
        btnRefrescarProyectos.setFont(buttonFont);
        btnVolverProyectos.setFont(buttonFont);

        btnNuevoProyecto.setPreferredSize(buttonSize);
        btnEditarProyecto.setPreferredSize(buttonSize);
        btnRefrescarProyectos.setPreferredSize(buttonSize);
        btnVolverProyectos.setPreferredSize(buttonSize);

        barraProyectos.add(btnNuevoProyecto);
        barraProyectos.add(btnEditarProyecto);
        barraProyectos.add(btnRefrescarProyectos);
        barraProyectos.add(btnVolverProyectos);
        panelProyectos.add(barraProyectos, BorderLayout.NORTH);

        btnNuevoProyecto.addActionListener(e -> new ProyectoDialog(this));
        btnEditarProyecto.addActionListener(e -> {
            int fila = tablaProyectos.getSelectedRow();
            if (fila == -1) {
                Mensajes.mostrarAdvertencia("Selecciona un proyecto para editar.");
                return;
            }

            String id = safeString(tablaProyectos.getValueAt(fila, 0));
            String nombre = safeString(tablaProyectos.getValueAt(fila, 1));
            String analistaAsignado = safeString(tablaProyectos.getValueAt(fila, 2));
            String estado = safeString(tablaProyectos.getValueAt(fila, 3));

            LocalDate fechaInicio = safeLocalDate(tablaProyectos.getValueAt(fila, 4));
            LocalDate fechaFinEst = safeLocalDate(tablaProyectos.getValueAt(fila, 5));

            Integer porcentajeObj = safeInteger(tablaProyectos.getValueAt(fila, 6));
            int porcentaje = porcentajeObj != null ? porcentajeObj : 0;

            String prioridad = safeString(tablaProyectos.getValueAt(fila, 7));
            String epica = safeString(tablaProyectos.getValueAt(fila, 8));
            String area = safeString(tablaProyectos.getValueAt(fila, 9));

            Proyecto p = new Proyecto(
                    id, nombre, analistaAsignado, 0,
                    fechaInicio, fechaFinEst, null,
                    estado, porcentaje, prioridad,
                    epica, area, 0, 0, null
            );
            new ProyectoDialog(this, p);
        });

        btnRefrescarProyectos.addActionListener(e -> cargarProyectos());
        btnVolverProyectos.addActionListener(e -> {
            new DashboardFrame();
            dispose();
        });

        tabs.addTab("Proyectos", panelProyectos);

        // PANEL SPRINTS
        JPanel panelSprints = new JPanel(new BorderLayout());
        tablaSprints = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Objetivo", "Estado", "Fecha Inicio", "Fecha Fin"}, 0
        ));
        panelSprints.add(new JScrollPane(tablaSprints), BorderLayout.CENTER);

        JPanel barraSprints = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevoSprint = new JButton("\u2795 Añadir");
        JButton btnEditarSprint = new JButton("\u270E Editar"); // NUEVO
        JButton btnRefrescarSprints = new JButton("\uD83D\uDD04 Refrescar");
        JButton btnVolverSprints = new JButton("\uD83D\uDD19 Volver");

        btnNuevoSprint.setFont(buttonFont);
        btnEditarSprint.setFont(buttonFont);
        btnRefrescarSprints.setFont(buttonFont);
        btnVolverSprints.setFont(buttonFont);

        btnNuevoSprint.setPreferredSize(buttonSize);
        btnEditarSprint.setPreferredSize(buttonSize);
        btnRefrescarSprints.setPreferredSize(buttonSize);
        btnVolverSprints.setPreferredSize(buttonSize);

        barraSprints.add(btnNuevoSprint);
        barraSprints.add(btnEditarSprint); // NUEVO
        barraSprints.add(btnRefrescarSprints);
        barraSprints.add(btnVolverSprints);
        panelSprints.add(barraSprints, BorderLayout.NORTH);

        btnNuevoSprint.addActionListener(e -> new SprintDialog(this));
        btnEditarSprint.addActionListener(e -> { // NUEVO
            int fila = tablaSprints.getSelectedRow();
            if (fila == -1) {
                Mensajes.mostrarAdvertencia("Selecciona un sprint para editar.");
                return;
            }
            String id = safeString(tablaSprints.getValueAt(fila, 0));
            String nombre = safeString(tablaSprints.getValueAt(fila, 1));
            String objetivo = safeString(tablaSprints.getValueAt(fila, 2));
            String estado = safeString(tablaSprints.getValueAt(fila, 3));
            LocalDate fechaInicio = safeLocalDate(tablaSprints.getValueAt(fila, 4));
            LocalDate fechaFin = safeLocalDate(tablaSprints.getValueAt(fila, 5));

            Sprint s = new Sprint(id, nombre, fechaInicio, fechaFin, objetivo, estado);
            new SprintDialog(this, s);
        });
        btnRefrescarSprints.addActionListener(e -> cargarSprints());
        btnVolverSprints.addActionListener(e -> {
            new DashboardFrame();
            dispose();
        });

        tabs.addTab("Sprints", panelSprints);

        // PANEL TAREAS
        JPanel panelTareas = new JPanel(new BorderLayout());
        tablaTareas = new JTable(new DefaultTableModel(
                new Object[]{"Nombre", "Descripción", "Fecha Inicio", "Fecha Fin", "Duración (horas)", "Analista", "Estado"}, 0
        ));
        panelTareas.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);

        JPanel barraTareas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevaTarea = new JButton("\u2795 Añadir");
        JButton btnEditarTarea = new JButton("\u270E Editar"); // NUEVO
        JButton btnRefrescarTareas = new JButton("\uD83D\uDD04 Refrescar");
        JButton btnVolverTareas = new JButton("\uD83D\uDD19 Volver");

        btnNuevaTarea.setFont(buttonFont);
        btnEditarTarea.setFont(buttonFont);
        btnRefrescarTareas.setFont(buttonFont);
        btnVolverTareas.setFont(buttonFont);

        btnNuevaTarea.setPreferredSize(buttonSize);
        btnEditarTarea.setPreferredSize(buttonSize);
        btnRefrescarTareas.setPreferredSize(buttonSize);
        btnVolverTareas.setPreferredSize(buttonSize);

        barraTareas.add(btnNuevaTarea);
        barraTareas.add(btnEditarTarea); // NUEVO
        barraTareas.add(btnRefrescarTareas);
        barraTareas.add(btnVolverTareas);
        panelTareas.add(barraTareas, BorderLayout.NORTH);

        btnNuevaTarea.addActionListener(e -> new TareaDialog(this));

        // ====== ARREGLO RECOMENDADO AQUÍ ======
        btnEditarTarea.addActionListener(e -> { // NUEVO (con fix)
            int fila = tablaTareas.getSelectedRow();
            if (fila == -1) {
                Mensajes.mostrarAdvertencia("Selecciona una tarea para editar.");
                return;
            }
            String nombre = safeString(tablaTareas.getValueAt(fila, 0));
            String descripcion = safeString(tablaTareas.getValueAt(fila, 1));
            LocalDate fechaInicio = safeLocalDate(tablaTareas.getValueAt(fila, 2));
            LocalDate fechaFin = safeLocalDate(tablaTareas.getValueAt(fila, 3));
            Integer durObj = safeInteger(tablaTareas.getValueAt(fila, 4));
            int duracion = durObj != null ? durObj : 0;
            String analistaNombre = safeString(tablaTareas.getValueAt(fila, 5));
            String estado = safeString(tablaTareas.getValueAt(fila, 6));

            // IMPORTANTE: evitar crear Analista con id=0 (tu modelo lo prohíbe)
            Analista a = null;
            if (analistaNombre != null && !analistaNombre.isBlank()
                    && !"Sin asignar".equalsIgnoreCase(analistaNombre)) {
                // Usamos un ID dummy positivo para pasar la validación del constructor.
                a = new Analista(1, analistaNombre, "", "");
            }

            Tarea t;
            try {
                // Si existe constructor con 'estado'
                t = new Tarea(nombre, descripcion, fechaInicio, fechaFin, duracion, a, estado);
            } catch (NoSuchMethodError | Exception ignore) {
                // Fallback si sólo tienes el constructor sin 'estado'
                t = new Tarea(nombre, descripcion, fechaInicio, fechaFin, duracion, a);
                // Si tu clase Tarea tiene setEstado, podrías usar: t.setEstado(estado);
            }

            new TareaDialog(this, t);
        });
        // ====== FIN ARREGLO ======

        btnRefrescarTareas.addActionListener(e -> cargarTareas());
        btnVolverTareas.addActionListener(e -> {
            new DashboardFrame();
            dispose();
        });

        tabs.addTab("Tareas", panelTareas);

        add(tabs);

        cargarDatosIniciales();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarDatosIniciales() {
        cargarAnalistas();
        cargarProyectos();
        cargarSprints();
        cargarTareas();
    }

    private void cargarAnalistas() {
        try {
            List<Analista> analistas = mysql.leerAnalistasConOcupacion();
            DefaultTableModel model = (DefaultTableModel) tablaAnalistas.getModel();
            model.setRowCount(0);
            for (Analista a : analistas) {
                model.addRow(new Object[]{
                        a.getId(),
                        a.getNombre(),
                        a.getRol(),
                        a.getEspecialidad(),
                        a.getProyectosAsignados(),
                        a.getTareasAsignadas(),
                        a.getHorasAsignadas(),
                        a.getOcupacion() + "%"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("No se pudieron cargar los analistas.");
        }
    }

    private void cargarProyectos() {
        try {
            List<Proyecto> proyectos = mysql.leerProyectos();
            DefaultTableModel model = (DefaultTableModel) tablaProyectos.getModel();
            model.setRowCount(0);
            for (Proyecto p : proyectos) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getNombre(),
                        p.getAnalistaAsignado(),
                        p.getEstado(),
                        p.getFechaInicio(),
                        p.getFechaFinEstimada(),
                        p.getPorcentajeCompletado(),
                        p.getPrioridad(),
                        p.getEpica(),
                        p.getArea()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("No se pudieron cargar los proyectos.");
        }
    }

    private void cargarSprints() {
        try {
            List<Sprint> sprints = mysql.leerSprints();
            DefaultTableModel model = (DefaultTableModel) tablaSprints.getModel();
            model.setRowCount(0);
            for (Sprint s : sprints) {
                model.addRow(new Object[]{
                        s.getId(),
                        s.getNombre(),
                        s.getObjetivo(),
                        s.getEstado(),
                        s.getFechaInicio(),
                        s.getFechaFin()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("No se pudieron cargar los sprints.");
        }
    }

    private void cargarTareas() {
        try {
            List<Tarea> tareas = mysql.leerTareas();
            DefaultTableModel model = (DefaultTableModel) tablaTareas.getModel();
            model.setRowCount(0);
            for (Tarea t : tareas) {
                model.addRow(new Object[]{
                        t.getNombre(),
                        t.getDescripcion(),
                        t.getFechaInicio(),
                        t.getFechaFin(),
                        t.getDuracionHoras(),
                        t.getAnalista() != null ? t.getAnalista().getNombre() : "Sin asignar",
                        t.getEstado() != null ? t.getEstado() : "Pendiente"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("No se pudieron cargar las tareas.");
        }
    }

    // ---- Utilidades seguras para castear valores de la tabla ----
    private static String safeString(Object v) {
        return v == null ? "" : v.toString();
    }
    private static Integer safeInteger(Object v) {
        if (v == null) return null;
        if (v instanceof Integer) return (Integer) v;
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
    }
    private static LocalDate safeLocalDate(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDate) return (LocalDate) v;
        try { return LocalDate.parse(v.toString()); } catch (Exception e) { return null; }
    }
}
