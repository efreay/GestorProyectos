package com.gestorproyectos.views;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Gestor de Proyectos - Gerencia de Servicios Regionales");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);

        // Panel contenedor principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Barra lateral para el menú
        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(Color.decode("#221F1F"));
        panelMenu.setPreferredSize(new Dimension(200, getHeight()));
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("<html><center>Gestor de<br>Proyectos</center></html>");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        panelMenu.add(lblTitulo);

        JButton btnAnalistas = new JButton("Analistas");
        JButton btnProyectos = new JButton("Proyectos");
        JButton btnSprints = new JButton("Sprints");
        JButton btnTareas = new JButton("Tareas");

        btnAnalistas.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnProyectos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSprints.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTareas.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelMenu.add(btnAnalistas);
        panelMenu.add(btnProyectos);
        panelMenu.add(btnSprints);
        panelMenu.add(btnTareas);

        // Panel central con KPIs
        JPanel panelKPIs = new JPanel();
        panelKPIs.setBackground(Color.decode("#FFFFFF"));
        panelKPIs.setLayout(new GridLayout(2, 2, 20, 20));
        panelKPIs.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tarjetas de indicadores base
        panelKPIs.add(crearTarjetaKPI("Proyectos En Progreso", "0", "#FF6F21"));
        panelKPIs.add(crearTarjetaKPI("Proyectos Atrasados", "0", "#8A4FB0"));
        panelKPIs.add(crearTarjetaKPI("Tareas Activas", "0", "#00B570"));
        panelKPIs.add(crearTarjetaKPI("Productividad", "0%", "#FFD400"));

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
            JOptionPane.showMessageDialog(this, "Módulo Proyectos en construcción");
        });

        btnSprints.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Módulo Sprints en construcción");
        });

        btnTareas.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Módulo Tareas en construcción");
        });
    }

    private JPanel crearTarjetaKPI(String titulo, String valor, String colorHex) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.decode(colorHex));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tarjeta.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }
}
