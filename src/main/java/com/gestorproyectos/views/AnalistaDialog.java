package com.gestorproyectos.views;

import com.gestorproyectos.models.Analista;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;

public class AnalistaDialog extends JDialog {

    public AnalistaDialog(JFrame owner) {
        super(owner, "Añadir Participante", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();

        String[] roles = {"Líder Procesos", "Analista Senior", "Analista Junior"};
        JComboBox<String> cmbRol = new JComboBox<>(roles);

        String[] especialidades = {"Automatización", "Análisis de datos", "Documentación", "Optimización",
                "UI/UX", "Integraciones", "Testing", "BPM"};
        JComboBox<String> cmbEspecialidad = new JComboBox<>(especialidades);

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
        add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        add(cmbRol, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1;
        add(cmbEspecialidad, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String nombre = txtNombre.getText().trim();
                String rol = (String) cmbRol.getSelectedItem();
                String especialidad = (String) cmbEspecialidad.getSelectedItem();

                if (nombre.isEmpty()) {
                    Mensajes.mostrarError("El nombre no puede estar vacío.");
                    return;
                }

                Analista nuevo = new Analista(id, nombre, rol, especialidad);
                MySQLService service = new MySQLService();
                service.insertarAnalista(nuevo);
                Mensajes.mostrarInfo("Participante guardado en la base de datos.");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error guardando el participante.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
