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

        // PANEL ANALISTAS
        JPanel panelAnalistas = new JPanel(new BorderLayout());
        tablaAnalistas = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Rol", "Especialidad", "Proyectos", "Tareas", "Horas", "Ocupación"}, 0
        ));
        panelAnalistas.add(new JScrollPane(tablaAnalistas), BorderLayout.CENTER);

        JPanel barraAnalistas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevoAnalista = new JButton("Añadir Participante");
        JButton btnRefrescarAnalistas = new JButton("Refrescar");
        barraAnalistas.add(btnNuevoAnalista);
        barraAnalistas.add(btnRefrescarAnalistas);
        panelAnalistas.add(barraAnalistas, BorderLayout.NORTH);

        btnNuevoAnalista.addActionListener(e -> new AnalistaDialog(this));
        btnRefrescarAnalistas.addActionListener(e -> cargarAnalistas());

        tabs.addTab("Analistas", panelAnalistas);

        // PANEL PROYECTOS
        JPanel panelProyectos = new JPanel(new BorderLayout());
        tablaProyectos = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Analista", "Estado", "Fecha Inicio", "Fecha Fin Estimada", "Porcentaje", "Prioridad", "Epica", "Área"}, 0
        ));
        panelProyectos.add(new JScrollPane(tablaProyectos), BorderLayout.CENTER);

        JPanel barraProyectos = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevoProyecto = new JButton("Añadir Proyecto");
        JButton btnRefrescarProyectos = new JButton("Refrescar");
        barraProyectos.add(btnNuevoProyecto);
        barraProyectos.add(btnRefrescarProyectos);
        panelProyectos.add(barraProyectos, BorderLayout.NORTH);

        btnNuevoProyecto.addActionListener(e -> new ProyectoDialog(this));
        btnRefrescarProyectos.addActionListener(e -> cargarProyectos());

        tabs.addTab("Proyectos", panelProyectos);

        // PANEL SPRINTS
        JPanel panelSprints = new JPanel(new BorderLayout());
        tablaSprints = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Objetivo", "Estado", "Fecha Inicio", "Fecha Fin"}, 0
        ));
        panelSprints.add(new JScrollPane(tablaSprints), BorderLayout.CENTER);

        JPanel barraSprints = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevoSprint = new JButton("Añadir Sprint");
        JButton btnRefrescarSprints = new JButton("Refrescar");
        barraSprints.add(btnNuevoSprint);
        barraSprints.add(btnRefrescarSprints);
        panelSprints.add(barraSprints, BorderLayout.NORTH);

        btnNuevoSprint.addActionListener(e -> new SprintDialog(this));
        btnRefrescarSprints.addActionListener(e -> cargarSprints());

        tabs.addTab("Sprints", panelSprints);

        // PANEL TAREAS
        JPanel panelTareas = new JPanel(new BorderLayout());
        tablaTareas = new JTable(new DefaultTableModel(
                new Object[]{"Nombre", "Descripción", "Fecha Inicio", "Fecha Fin", "Duración (horas)", "Analista"}, 0
        ));
        panelTareas.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);

        JPanel barraTareas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefrescarTareas = new JButton("Refrescar");
        barraTareas.add(btnRefrescarTareas);
        panelTareas.add(barraTareas, BorderLayout.NORTH);

        btnRefrescarTareas.addActionListener(e -> cargarTareas());

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
                        t.getAnalista() != null ? t.getAnalista().getNombre() : "Sin asignar"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("No se pudieron cargar las tareas.");
        }
    }
}
