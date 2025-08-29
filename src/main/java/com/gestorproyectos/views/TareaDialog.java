package com.gestorproyectos.views;

import com.gestorproyectos.models.Tarea;
import com.gestorproyectos.models.Analista;
import com.gestorproyectos.services.MySQLService;
import com.gestorproyectos.utils.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TareaDialog extends JDialog {

    // Campos UI como atributos para reutilizarlos en edición
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtFechaInicio = new JTextField("2025-01-01");
    private final JTextField txtFechaFin = new JTextField("2025-01-05");
    private final JTextField txtDuracion = new JTextField("8");

    private final JComboBox<Analista> cmbAnalistas = new JComboBox<>();
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Pendiente", "En progreso", "Completada"});

    private final JButton btnGuardar;

    private final MySQLService service = new MySQLService();

    // Clave temporal para actualizar (si viene en edición)
    private String nombreOriginal = null;
    private LocalDate fechaInicioOriginal = null;

    // Constructor CREAR (conserva tu comportamiento actual)
    public TareaDialog(JFrame owner) {
        this(owner, null);
    }

    // Constructor EDITAR (nuevo)
    public TareaDialog(JFrame owner, Tarea tarea) {
        super(owner, tarea == null ? "Añadir Tarea" : "Editar Tarea", true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Cargar analistas en el combo
        cargarAnalistas(cmbAnalistas);

        int r = 0;

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(txtNombre, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Descripción:"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(txtDescripcion, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(txtFechaInicio, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Fecha Fin (YYYY-MM-DD):"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(txtFechaFin, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Duración (horas):"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(txtDuracion, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Analista:"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(cmbAnalistas, gbc);

        gbc.gridx=0; gbc.gridy=r; gbc.weightx=0; add(new JLabel("Estado:"), gbc);
        gbc.gridx=1; gbc.gridy=r++; gbc.weightx=1; add(cmbEstado, gbc);

        btnGuardar = new JButton(tarea == null ? "Guardar" : "Guardar Cambios");
        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=2; add(btnGuardar, gbc);

        // ---- MODO EDICIÓN: precargar campos y recordar clave original ----
        if (tarea != null) {
            txtNombre.setText(tarea.getNombre());
            txtDescripcion.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            txtFechaInicio.setText(tarea.getFechaInicio() != null ? tarea.getFechaInicio().toString() : "");
            txtFechaFin.setText(tarea.getFechaFin() != null ? tarea.getFechaFin().toString() : "");
            txtDuracion.setText(String.valueOf(tarea.getDuracionHoras()));

            // Seleccionar analista por ID si coincide; si no, por nombre
            if (tarea.getAnalista() != null) {
                seleccionarAnalistaEnCombo(tarea.getAnalista());
            }

            // Estado (si tu modelo/tabla lo usa)
            if (tarea.getEstado() != null && !tarea.getEstado().isBlank()) {
                cmbEstado.setSelectedItem(tarea.getEstado());
            } else {
                cmbEstado.setSelectedItem("Pendiente");
            }

            // Guardar clave original para UPDATE
            nombreOriginal = tarea.getNombre();
            fechaInicioOriginal = tarea.getFechaInicio();
        } else {
            // Valores por defecto al crear
            cmbEstado.setSelectedItem("Pendiente");
        }

        // ---- Acción Guardar (insertar/actualizar) ----
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                LocalDate fechaInicio = parseFechaObligatoria(txtFechaInicio.getText().trim(), "Fecha Inicio");
                LocalDate fechaFin = parseFecha(txtFechaFin.getText().trim());
                int duracion = parseEnteroNoNegativo(txtDuracion.getText().trim(), "Duración (horas)");
                Analista analista = (Analista) cmbAnalistas.getSelectedItem();
                String estado = (String) cmbEstado.getSelectedItem();

                if (nombre.isEmpty() || descripcion.isEmpty()) {
                    Mensajes.mostrarError("Todos los campos son obligatorios.");
                    return;
                }

                // Construcción del objeto Tarea
                Tarea nueva;
                try {
                    // Preferimos el constructor con estado si existe en tu clase
                    nueva = new Tarea(nombre, descripcion, fechaInicio, fechaFin, duracion, analista, estado);
                } catch (NoSuchMethodError | Exception ignore) {
                    // Fallback al constructor sin estado
                    nueva = new Tarea(nombre, descripcion, fechaInicio, fechaFin, duracion, analista);
                    // Si tu clase Tarea tiene setEstado, podrías usarlo aquí:
                    // nueva.setEstado(estado);
                }

                if (nombreOriginal == null && fechaInicioOriginal == null) {
                    // CREAR
                    service.insertarTarea(nueva);
                    Mensajes.mostrarInfo("Tarea guardada correctamente.");
                } else {
                    // EDITAR (requiere que hayas añadido MySQLService.actualizarTarea)
                    service.actualizarTarea(nueva, nombreOriginal, fechaInicioOriginal);
                    Mensajes.mostrarInfo("Tarea actualizada correctamente.");
                }
                dispose();

            } catch (IllegalArgumentException ex) {
                Mensajes.mostrarError(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                Mensajes.mostrarError("Error al guardar la tarea. Verifica los datos.");
            }
        });

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    // ---- Utilidades ----

    private void cargarAnalistas(JComboBox<Analista> combo) {
        try {
            List<Analista> analistas = service.obtenerAnalistas();
            combo.removeAllItems();
            for (Analista a : analistas) {
                combo.addItem(a);
            }
            // Agrega opción "Sin asignar" al final si te interesa
            // combo.addItem(new Analista(0, "Sin asignar", "", ""));
        } catch (Exception e) {
            Mensajes.mostrarError("Error cargando analistas.");
        }
    }

    private void seleccionarAnalistaEnCombo(Analista objetivo) {
        // 1) Intento por ID
        for (int i = 0; i < cmbAnalistas.getItemCount(); i++) {
            Analista item = cmbAnalistas.getItemAt(i);
            if (item != null && item.getId() == objetivo.getId()) {
                cmbAnalistas.setSelectedIndex(i);
                return;
            }
        }
        // 2) Intento por nombre (si el id era 0 o no coincide)
        if (objetivo.getNombre() != null) {
            for (int i = 0; i < cmbAnalistas.getItemCount(); i++) {
                Analista item = cmbAnalistas.getItemAt(i);
                if (item != null && objetivo.getNombre().equalsIgnoreCase(item.getNombre())) {
                    cmbAnalistas.setSelectedIndex(i);
                    return;
                }
            }
        }
        // Si no lo encuentra, lo deja como está
    }

    private LocalDate parseFecha(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha Fin inválida. Usa formato YYYY-MM-DD.");
        }
    }

    private LocalDate parseFechaObligatoria(String s, String campo) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatoria (YYYY-MM-DD).");
        }
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(campo + " inválida. Usa formato YYYY-MM-DD.");
        }
    }

    private int parseEnteroNoNegativo(String s, String campo) {
        try {
            int v = Integer.parseInt(s);
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + " debe ser un entero >= 0.");
        }
    }
}
