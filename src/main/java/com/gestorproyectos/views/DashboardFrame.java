package com.gestorproyectos.views;

import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.models.Tarea;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.ExcelImporter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JLabel lblProyectosEnProgreso;
    private JLabel lblProyectosAtrasados;
    private JLabel lblTareasActivas;
    private JLabel lblProductividad;
    private MySQLService mysql;

    public DashboardFrame() {
        setTitle("Gestor de Proyectos - Gerencia de Servicios Regionales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        mysql = new MySQLService();

        // Panel contenedor principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Barra lateral para el menú
        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(Color.decode("#221F1F"));
        panelMenu.setPreferredSize(new Dimension(200, getHeight()));
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblTitulo = new JLabel("<html><center>Gestor de<br>Proyectos</center></html>");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelMenu.add(lblTitulo);

        // Botones del menú
        Font bigFont = new Font("SansSerif", Font.BOLD, 16);
        Dimension botonSize = new Dimension(180, 50);

        JButton btnAnalistas = new JButton("Analistas");
        JButton btnProyectos = new JButton("Proyectos");
        JButton btnSprints   = new JButton("Sprints");
        JButton btnTareas    = new JButton("Tareas");
        JButton btnActualizar = new JButton("Actualizar KPIs");
        JButton btnImportarExcel = new JButton("\uD83D\uDCC5 Importar Excel");

        JButton[] botones = {btnAnalistas, btnProyectos, btnSprints, btnTareas, btnActualizar, btnImportarExcel};
        for (JButton btn : botones) {
            btn.setFont(bigFont);
            btn.setMaximumSize(botonSize);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelMenu.add(Box.createVerticalStrut(10));
            panelMenu.add(btn);
        }

        // Panel central con KPIs
        JPanel panelKPIs = new JPanel();
        panelKPIs.setBackground(Color.decode("#FFFFFF"));
        panelKPIs.setLayout(new GridLayout(2, 2, 20, 20));
        panelKPIs.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblProyectosEnProgreso = new JLabel("0");
        lblProyectosAtrasados = new JLabel("0");
        lblTareasActivas = new JLabel("0");
        lblProductividad = new JLabel("0%");

        panelKPIs.add(crearTarjetaKPI("Proyectos En Progreso", lblProyectosEnProgreso, "#FF6F21"));
        panelKPIs.add(crearTarjetaKPI("Proyectos Atrasados", lblProyectosAtrasados, "#8A4FB0"));
        panelKPIs.add(crearTarjetaKPI("Tareas Activas", lblTareasActivas, "#00B570"));
        panelKPIs.add(crearTarjetaKPI("Productividad", lblProductividad, "#FFD400"));

        panelPrincipal.add(panelMenu, BorderLayout.WEST);
        panelPrincipal.add(panelKPIs, BorderLayout.CENTER);

        add(panelPrincipal);
        setLocationRelativeTo(null);
        setVisible(true);

        // Eventos de botones
        btnAnalistas.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        btnProyectos.addActionListener(e -> {
            MainFrame frame = new MainFrame();
            ((JTabbedPane) frame.getContentPane().getComponent(0)).setSelectedIndex(1);
            dispose();
        });

        btnSprints.addActionListener(e -> {
            MainFrame frame = new MainFrame();
            ((JTabbedPane) frame.getContentPane().getComponent(0)).setSelectedIndex(2);
            dispose();
        });

        btnTareas.addActionListener(e -> {
            MainFrame frame = new MainFrame();
            ((JTabbedPane) frame.getContentPane().getComponent(0)).setSelectedIndex(3);
            dispose();
        });

        btnActualizar.addActionListener(e -> cargarKPIs());

        btnImportarExcel.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                new ExcelImporter().importarDesdeArchivo(ruta);
                cargarKPIs();
            }
        });

        cargarKPIs();
    }

    private JPanel crearTarjetaKPI(String titulo, JLabel lblValor, String colorHex) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.decode(colorHex));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tarjeta.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private void cargarKPIs() {
        try {
            List<Proyecto> proyectos = mysql.leerProyectos();
            List<Tarea> tareas = mysql.leerTareas();

            long enProgreso = proyectos.stream()
                    .filter(p -> "En Progreso".equalsIgnoreCase(p.getEstado()))
                    .count();

            long atrasados = proyectos.stream()
                    .filter(p -> !"Finalizado".equalsIgnoreCase(p.getEstado()) &&
                            p.getFechaFinEstimada() != null &&
                            p.getFechaFinEstimada().isBefore(java.time.LocalDate.now()))
                    .count();

            long activas = tareas.stream()
                    .filter(t -> !"Finalizada".equalsIgnoreCase(t.getEstado()))
                    .count();

            long finalizadas = tareas.stream()
                    .filter(t -> "Finalizada".equalsIgnoreCase(t.getEstado()))
                    .count();

            int productividad = tareas.isEmpty() ? 0 : (int) ((finalizadas * 100.0) / tareas.size());

            lblProyectosEnProgreso.setText(String.valueOf(enProgreso));
            lblProyectosAtrasados.setText(String.valueOf(atrasados));
            lblTareasActivas.setText(String.valueOf(activas));
            lblProductividad.setText(productividad + "%");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando indicadores del dashboard");
        }
    }
}
