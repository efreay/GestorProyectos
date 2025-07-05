package com.gestorproyectos.views;

import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ProyectoDialog extends JDialog {

    public ProyectoDialog(JFrame owner) {
        super(owner, "Añadir Proyecto", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();

        JComboBox<String> cmbAnalista = new JComboBox<>(obtenerAnalistas());
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Planeado", "En progreso", "Finalizado"});
        JComboBox<String> cmbPrioridad = new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
        JComboBox<String> cmbArea = new JComboBox<>(new String[]{"TI", "Negocio", "BackOffice", "Legal"});

        JTextField txtEpica = new JTextField();
        JTextField txtPorcentaje = new JTextField("0");

        JTextField txtFechaInicio = new JTextField("2025-01-01");
        JTextField txtFechaFin = new JTextField("2025-01-15");

        JButton btnGuardar = new JButton("Guardar");

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("ID:"), gbc); gbc.gridx = 1; add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nombre:"), gbc); gbc.gridx = 1; add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Analista:"), gbc); gbc.gridx = 1; add(cmbAnalista, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Estado:"), gbc); gbc.gridx = 1; add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Prioridad:"), gbc); gbc.gridx = 1; add(cmbPrioridad, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Área:"), gbc); gbc.gridx = 1; add(cmbArea, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Épica:"), gbc); gbc.gridx = 1; add(txtEpica, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Porcentaje completado:"), gbc); gbc.gridx = 1; add(txtPorcentaje, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        add(new JLabel("Fecha Inicio:"), gbc); gbc.gridx = 1; add(txtFechaInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        add(new JLabel("Fecha Fin:"), gbc); gbc.gridx = 1; add(txtFechaFin, gbc);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String nombre = txtNombre.getText().trim();
                String analistaAsignado = (String) cmbAnalista.getSelectedItem();
                String estado = (String) cmbEstado.getSelectedItem();
                String prioridad = (String) cmbPrioridad.getSelectedItem();
                String epica = txtEpica.getText().trim();
                String area = (String) cmbArea.getSelectedItem();
                int porcentaje = Integer.parseInt(txtPorcentaje.getText());
                LocalDate fechaInicio = LocalDate.parse(txtFechaInicio.getText());
                LocalDate fechaFin = LocalDate.parse(txtFechaFin.getText());

                Proyecto nuevo = new Proyecto(
                        id, nombre, analistaAsignado, 0,
                        fechaInicio, fechaFin, null,
                        estado, porcentaje, prioridad,
                        epica, area, 0, 0, null
                );

                MySQLService service = new MySQLService();
                service.insertarProyecto(nuevo);

                Mensajes.mostrarInfo("Proyecto guardado en la base de datos.");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error guardando el proyecto.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private String[] obtenerAnalistas() {
        // Esto se puede optimizar conectando a la BD
        return new String[]{"Ana López", "Jorge Martínez", "María González", "David Jiménez"};
    }
}
