package org.example.cliente.gui;

import org.example.cliente.network.ClientNetwork;
import org.example.servidor.database.dtos.PacienteDTO;
import org.example.shared.Request;
import org.example.shared.Response;
import org.example.servidor.database.entities.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PacientePanel extends JPanel {
    private JTextField curpField;
    private JTextField nombreField;
    private JTextField telefonoField;
    private JTextField emailField;
    private JButton agregarBtn;
    private JButton buscarBtn;
    private JButton actualizarBtn;
    private JButton eliminarBtn;
    private JButton limpiarBtn;
    private JTable pacientesTable;
    private DefaultTableModel tableModel;
    private List<PanelObserver> observers = new ArrayList<>();

    public PacientePanel() {
        initComponents();
        loadPacientes();
    }

    public void addObserver(PanelObserver observer) {
        observers.add(observer);
    }

    private void notifyPacientesUpdated() {
        for (PanelObserver observer : observers) {
            observer.updatePacientes();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Campos del formulario
        formPanel.add(new JLabel("CURP:"));
        curpField = new JTextField();
        formPanel.add(curpField);

        formPanel.add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        formPanel.add(nombreField);

        formPanel.add(new JLabel("Teléfono:"));
        telefonoField = new JTextField();
        formPanel.add(telefonoField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        agregarBtn = new JButton("Agregar");
        agregarBtn.addActionListener(this::agregarPaciente);
        buttonPanel.add(agregarBtn);

        buscarBtn = new JButton("Buscar");
        buscarBtn.addActionListener(this::buscarPaciente);
        buttonPanel.add(buscarBtn);

        actualizarBtn = new JButton("Actualizar");
        actualizarBtn.addActionListener(this::actualizarPaciente);
        buttonPanel.add(actualizarBtn);

        eliminarBtn = new JButton("Eliminar");
        eliminarBtn.addActionListener(this::eliminarPaciente);
        buttonPanel.add(eliminarBtn);

        limpiarBtn = new JButton("Limpiar");
        limpiarBtn.addActionListener(this::limpiarCampos);
        buttonPanel.add(limpiarBtn);

        // Panel norte (formulario + botones)
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tabla de pacientes
        String[] columnNames = {"CURP", "Nombre", "Teléfono", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pacientesTable = new JTable(tableModel);
        pacientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pacientesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = pacientesTable.getSelectedRow();
                    if (row >= 0) {
                        String curp = (String) tableModel.getValueAt(row, 0);
                        cargarPaciente(curp);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pacientesTable);

        // Agregar componentes al panel principal
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void agregarPaciente(ActionEvent e) {
        if (!validarCampos()) return;

        Paciente paciente = new Paciente(
                curpField.getText().trim(),
                nombreField.getText().trim(),
                telefonoField.getText().trim(),
                emailField.getText().trim()
        );

        Response response = ClientNetwork.sendRequest(new Request("CREATE_PACIENTE", paciente));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Paciente agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadPacientes();
            notifyPacientesUpdated();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPaciente(ActionEvent e) {
        String curp = JOptionPane.showInputDialog(this, "Ingrese la CURP del paciente:", "Buscar Paciente", JOptionPane.QUESTION_MESSAGE);
        if (curp != null && !curp.trim().isEmpty()) {
            cargarPaciente(curp.trim());
        }
    }

    private void actualizarPaciente(ActionEvent e) {
        if (!validarCampos()) return;

        Paciente paciente = new Paciente(
                curpField.getText().trim(),
                nombreField.getText().trim(),
                telefonoField.getText().trim(),
                emailField.getText().trim()
        );

        Response response = ClientNetwork.sendRequest(new Request("UPDATE_PACIENTE", paciente));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Paciente actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadPacientes();
            notifyPacientesUpdated();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPaciente(ActionEvent e) {
        if (curpField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero busque un paciente para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al paciente con CURP " + curpField.getText().trim() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Response response = ClientNetwork.sendRequest(new Request("DELETE_PACIENTE", curpField.getText().trim()));

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Paciente eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                loadPacientes();
                notifyPacientesUpdated();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos(ActionEvent e) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        curpField.setText("");
        nombreField.setText("");
        telefonoField.setText("");
        emailField.setText("");
    }

    private boolean validarCampos() {
        StringBuilder errors = new StringBuilder();

        if (curpField.getText().trim().isEmpty()) {
            errors.append("- La CURP es obligatoria\n");
        } else if (curpField.getText().trim().length() != 18) {
            errors.append("- La CURP debe tener 18 caracteres\n");
        }

        if (nombreField.getText().trim().isEmpty()) {
            errors.append("- El nombre es obligatorio\n");
        }

        if (telefonoField.getText().trim().isEmpty()) {
            errors.append("- El teléfono es obligatorio\n");
        }

        if (emailField.getText().trim().isEmpty()) {
            errors.append("- El email es obligatorio\n");
        } else if (!emailField.getText().trim().contains("@")) {
            errors.append("- Ingrese un email válido\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Corrija los siguientes errores:\n" + errors.toString(),
                    "Error en el formulario",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void cargarPaciente(String curp) {
        Response response = ClientNetwork.sendRequest(new Request("GET_PACIENTE", curp));

        if (response.isSuccess()) {
            PacienteDTO paciente = (PacienteDTO) response.getData();
            if (paciente != null) {
                curpField.setText(paciente.getCurp());
                nombreField.setText(paciente.getNombre());
                telefonoField.setText(paciente.getTelefono());
                emailField.setText(paciente.getEmail());
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un paciente con esa CURP", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPacientes() {
        Response response = ClientNetwork.sendRequest(new Request("GET_ALL_PACIENTES", null));

        if (response.isSuccess()) {
            List<PacienteDTO> pacientes = (List<PacienteDTO>) response.getData();
            tableModel.setRowCount(0);

            for (PacienteDTO paciente : pacientes) {
                Object[] row = {
                        paciente.getCurp(),
                        paciente.getNombre(),
                        paciente.getTelefono(),
                        paciente.getEmail()
                };
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar pacientes: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}