package com.gestorproyectos.views;

import com.gestorproyectos.models.Analista;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;

public class AnalistaDialog extends JDialog {

    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEspecialidad;
    private JButton btnGuardar;
    private MySQLService mysql;

    public AnalistaDialog(JFrame owner) {
        this(owner, null);
    }

    public AnalistaDialog(JFrame owner, Analista analista) {
        super(owner, analista == null ? "Añadir Participante" : "Editar Participante", true);
        mysql = new MySQLService();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtNombre = new JTextField();

        String[] roles = {"Líder Procesos", "Analista Senior", "Analista Junior"};
        cmbRol = new JComboBox<>(roles);

        String[] especialidades = {"Automatización", "Análisis de datos", "Documentación", "Optimización",
                "UI/UX", "Integraciones", "Testing", "BPM"};
        cmbEspecialidad = new JComboBox<>(especialidades);

        btnGuardar = new JButton(analista == null ? "Guardar" : "Guardar Cambios");

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

        if (analista != null) {
            txtId.setText(String.valueOf(analista.getId()));
            txtId.setEditable(false);
            txtNombre.setText(analista.getNombre());
            cmbRol.setSelectedItem(analista.getRol());
            cmbEspecialidad.setSelectedItem(analista.getEspecialidad());
        }

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

                if (analista == null) {
                    mysql.insertarAnalista(nuevo);
                    Mensajes.mostrarInfo("Participante guardado en la base de datos.");
                } else {
                    mysql.actualizarAnalista(nuevo);
                    Mensajes.mostrarInfo("Participante actualizado correctamente.");
                }
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