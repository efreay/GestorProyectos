package com.gestorproyectos.views;

import com.gestorproyectos.models.Sprint;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SprintDialog extends JDialog {

    // Campos de UI como atributos (para reutilizar en el listener)
    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtObjetivo = new JTextField();
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Planeado", "En progreso", "Finalizado"});
    private final JTextField txtFechaInicio = new JTextField("2025-01-01");
    private final JTextField txtFechaFin = new JTextField("2025-01-15");

    private final JButton btnGuardar;

    private final MySQLService service = new MySQLService();

    // Constructor CREAR (conserva tu comportamiento actual)
    public SprintDialog(JFrame owner) {
        this(owner, null);
    }

    // Constructor EDITAR (nuevo)
    public SprintDialog(JFrame owner, Sprint sprint) {
        super(owner, sprint == null ? "Añadir Sprint" : "Editar Sprint", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int r = 0;

        // ID
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(txtId, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(txtNombre, gbc);

        // Objetivo
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Objetivo:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(txtObjetivo, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(cmbEstado, gbc);

        // Fecha inicio
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(txtFechaInicio, gbc);

        // Fecha fin
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Fecha Fin (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1.0;
        add(txtFechaFin, gbc);

        // Botón guardar
        btnGuardar = new JButton(sprint == null ? "Guardar" : "Guardar Cambios");
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        // Si viene en modo edición, precarga y bloquea ID
        if (sprint != null) {
            txtId.setText(sprint.getId());
            txtId.setEditable(false); // No permitir cambiar ID al editar
            txtNombre.setText(sprint.getNombre());
            txtObjetivo.setText(sprint.getObjetivo());
            cmbEstado.setSelectedItem(sprint.getEstado());

            txtFechaInicio.setText(sprint.getFechaInicio() != null ? sprint.getFechaInicio().toString() : "");
            txtFechaFin.setText(sprint.getFechaFin() != null ? sprint.getFechaFin().toString() : "");
        }

        // Acción guardar (inserta o actualiza según corresponda)
        btnGuardar.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String nombre = txtNombre.getText().trim();
                String objetivo = txtObjetivo.getText().trim();
                String estado = (String) cmbEstado.getSelectedItem();

                if (id.isEmpty() || nombre.isEmpty() || objetivo.isEmpty()) {
                    Mensajes.mostrarError("Todos los campos son obligatorios.");
                    return;
                }

                LocalDate fechaInicio = parseFechaObligatoria(txtFechaInicio.getText().trim(), "Fecha Inicio");
                LocalDate fechaFin = parseFechaObligatoria(txtFechaFin.getText().trim(), "Fecha Fin");

                Sprint nuevo = new Sprint(id, nombre, fechaInicio, fechaFin, objetivo, estado);

                if (sprint == null) {
                    // CREAR
                    service.insertarSprint(nuevo);
                    Mensajes.mostrarInfo("Sprint guardado en la base de datos.");
                } else {
                    // EDITAR
                    service.actualizarSprint(nuevo); // requiere el método en MySQLService
                    Mensajes.mostrarInfo("Sprint actualizado correctamente.");
                }
                dispose();

            } catch (IllegalArgumentException ex) {
                Mensajes.mostrarError(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error guardando el sprint. Verifica los datos.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    // --- Utilidades de validación ---
    private LocalDate parseFechaObligatoria(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatoria (YYYY-MM-DD).");
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(campo + " inválida. Usa formato YYYY-MM-DD.");
        }
    }
}
