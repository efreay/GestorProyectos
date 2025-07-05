package com.gestorproyectos.views;

import com.gestorproyectos.models.Sprint;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SprintDialog extends JDialog {

    public SprintDialog(JFrame owner) {
        super(owner, "Añadir Sprint", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtObjetivo = new JTextField();

        String[] estados = {"Planeado", "En progreso", "Finalizado"};
        JComboBox<String> cmbEstado = new JComboBox<>(estados);

        JTextField txtFechaInicio = new JTextField("2025-01-01");
        JTextField txtFechaFin = new JTextField("2025-01-15");

        JButton btnGuardar = new JButton("Guardar");

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Objetivo:"), gbc);
        gbc.gridx = 1;
        add(txtObjetivo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        add(txtFechaInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Fecha Fin (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        add(txtFechaFin, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String nombre = txtNombre.getText().trim();
                String objetivo = txtObjetivo.getText().trim();
                String estado = (String) cmbEstado.getSelectedItem();
                LocalDate fechaInicio = LocalDate.parse(txtFechaInicio.getText());
                LocalDate fechaFin = LocalDate.parse(txtFechaFin.getText());

                if (id.isEmpty() || nombre.isEmpty() || objetivo.isEmpty()) {
                    Mensajes.mostrarError("Todos los campos son obligatorios.");
                    return;
                }

                Sprint nuevo = new Sprint(id, nombre, fechaInicio, fechaFin, objetivo, estado);
                MySQLService service = new MySQLService();
                service.insertarSprint(nuevo);

                Mensajes.mostrarInfo("Sprint guardado en la base de datos.");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error guardando el sprint. Verifica los datos.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
