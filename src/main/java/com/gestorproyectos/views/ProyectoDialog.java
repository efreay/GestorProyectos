package com.gestorproyectos.views;

import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * ProyectoDialog: ahora soporta CREAR y EDITAR (como tu AnalistaDialog).
 * - new ProyectoDialog(owner) -> Añadir Proyecto (insertarProyecto)
 * - new ProyectoDialog(owner, proyectoExistente) -> Editar Proyecto (actualizarProyecto)
 *
 * Campos incluidos: ID, Nombre, Analista, Estado, Prioridad, Área, Épica, Porcentaje, Fecha Inicio, Fecha Fin Estimada.
 * (Fecha Fin Real se mantiene null aquí; si la manejas en otra vista puedes ajustarlo.)
 */
public class ProyectoDialog extends JDialog {

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();

    private final JComboBox<String> cmbAnalista = new JComboBox<>(obtenerAnalistas());
    private final JComboBox<String> cmbEstado   = new JComboBox<>(new String[]{"Planeado", "En progreso", "Finalizado"});
    private final JComboBox<String> cmbPrioridad= new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
    private final JComboBox<String> cmbArea     = new JComboBox<>(new String[]{"TI", "Negocio", "BackOffice", "Legal"});

    private final JTextField txtEpica = new JTextField();
    private final JTextField txtPorcentaje = new JTextField("0");

    private final JTextField txtFechaInicio = new JTextField("2025-01-01");
    private final JTextField txtFechaFinEst = new JTextField("2025-01-15");

    private final JButton btnGuardar;

    private final MySQLService mysql = new MySQLService();

    public ProyectoDialog(JFrame owner) {
        this(owner, null);
    }

    public ProyectoDialog(JFrame owner, Proyecto proyecto) {
        super(owner, proyecto == null ? "Añadir Proyecto" : "Editar Proyecto", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int r = 0;

        // ID
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtId, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtNombre, gbc);

        // Analista
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Analista:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(cmbAnalista, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(cmbEstado, gbc);

        // Prioridad
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Prioridad:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(cmbPrioridad, gbc);

        // Área
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Área:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(cmbArea, gbc);

        // Épica
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Épica:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtEpica, gbc);

        // Porcentaje
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Porcentaje completado:"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtPorcentaje, gbc);

        // Fecha inicio
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Fecha Inicio (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtFechaInicio, gbc);

        // Fecha fin estimada
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        add(new JLabel("Fecha Fin Estimada (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = r++; gbc.weightx = 1;
        add(txtFechaFinEst, gbc);

        // Botón
        btnGuardar = new JButton(proyecto == null ? "Guardar" : "Guardar Cambios");
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        // Carga si viene en modo edición
        if (proyecto != null) {
            txtId.setText(proyecto.getId());
            txtId.setEditable(false); // No permitir cambiar ID al editar

            txtNombre.setText(proyecto.getNombre());
            cmbAnalista.setSelectedItem(proyecto.getAnalistaAsignado());
            cmbEstado.setSelectedItem(proyecto.getEstado());
            cmbPrioridad.setSelectedItem(proyecto.getPrioridad());
            txtEpica.setText(proyecto.getEpica());
            cmbArea.setSelectedItem(proyecto.getArea());
            txtPorcentaje.setText(String.valueOf(proyecto.getPorcentajeCompletado()));
            txtFechaInicio.setText(proyecto.getFechaInicio() == null ? "" : proyecto.getFechaInicio().toString());
            txtFechaFinEst.setText(proyecto.getFechaFinEstimada() == null ? "" : proyecto.getFechaFinEstimada().toString());
        }

        btnGuardar.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String nombre = txtNombre.getText().trim();
                String analistaAsignado = (String) cmbAnalista.getSelectedItem();
                String estado = (String) cmbEstado.getSelectedItem();
                String prioridad = (String) cmbPrioridad.getSelectedItem();
                String epica = txtEpica.getText().trim();
                String area = (String) cmbArea.getSelectedItem();

                if (id.isEmpty()) {
                    Mensajes.mostrarError("El ID no puede estar vacío.");
                    return;
                }
                if (nombre.isEmpty()) {
                    Mensajes.mostrarError("El nombre no puede estar vacío.");
                    return;
                }
                if (analistaAsignado == null || analistaAsignado.isBlank()) {
                    Mensajes.mostrarError("El analista asignado es obligatorio.");
                    return;
                }

                int porcentaje;
                try {
                    porcentaje = Integer.parseInt(txtPorcentaje.getText().trim());
                } catch (NumberFormatException ex2) {
                    Mensajes.mostrarError("El porcentaje debe ser un número entero.");
                    return;
                }
                if (porcentaje < 0 || porcentaje > 100) {
                    Mensajes.mostrarError("El porcentaje debe estar entre 0 y 100.");
                    return;
                }

                LocalDate fechaInicio = parseFecha(txtFechaInicio.getText().trim(), "Fecha Inicio");
                LocalDate fechaFinEst = parseFecha(txtFechaFinEst.getText().trim(), "Fecha Fin Estimada");

                // idAnalista y horas: de momento 0 (ajusta si luego integras selección real)
                int idAnalista = 0;
                int horasEstimadas = 0;
                int horasReales = 0;
                String sprintAsignado = null;

                Proyecto nuevo = new Proyecto(
                        id, nombre, analistaAsignado, idAnalista,
                        fechaInicio, fechaFinEst, null,              // fechaFinReal = null aquí
                        estado, porcentaje, prioridad,
                        epica, area, horasEstimadas, horasReales, sprintAsignado
                );

                if (proyecto == null) {
                    mysql.insertarProyecto(nuevo);
                    Mensajes.mostrarInfo("Proyecto guardado en la base de datos.");
                } else {
                    mysql.actualizarProyecto(nuevo);
                    Mensajes.mostrarInfo("Proyecto actualizado correctamente.");
                }
                dispose();

            } catch (IllegalArgumentException ex) {
                Mensajes.mostrarError(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error guardando el proyecto.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private static String[] obtenerAnalistas() {
        // Puedes reemplazar por consulta a BD
        return new String[]{"Ana López", "Jorge Martínez", "María González", "David Jiménez"};
    }

    private LocalDate parseFecha(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatoria (yyyy-MM-dd).");
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(campo + " inválida. Usa formato yyyy-MM-dd.");
        }
    }
}
