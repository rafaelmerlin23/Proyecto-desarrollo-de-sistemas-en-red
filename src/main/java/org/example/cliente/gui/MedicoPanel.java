package org.example.cliente.gui;


import org.example.cliente.network.ClientNetwork;
import org.example.servidor.database.dtos.MedicoDTO;
import org.example.shared.*;
import org.example.servidor.database.entities.Medico;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoPanel extends JPanel {
    private JTextField cedulaField, nombreField, especialidadField, emailField;
    private JButton agregarBtn, buscarBtn, actualizarBtn, eliminarBtn, limpiarBtn;
    private JTable medicosTable;
    private DefaultTableModel tableModel;
    private List<PanelObserver> observers = new ArrayList<>();

    public MedicoPanel() {
        initComponents();
        loadMedicos();
    }

    public void addObserver(PanelObserver observer) {
        observers.add(observer);
    }

    private void notifyMedicosUpdated() {
        for (PanelObserver observer : observers) {
            observer.updateMedicos();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Cédula:"));
        cedulaField = new JTextField();
        formPanel.add(cedulaField);

        formPanel.add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        formPanel.add(nombreField);

        formPanel.add(new JLabel("Especialidad:"));
        especialidadField = new JTextField();
        formPanel.add(especialidadField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        agregarBtn = new JButton("Agregar");
        agregarBtn.addActionListener(this::agregarMedico);
        buttonPanel.add(agregarBtn);

        buscarBtn = new JButton("Buscar");
        buscarBtn.addActionListener(this::buscarMedico);
        buttonPanel.add(buscarBtn);

        actualizarBtn = new JButton("Actualizar");
        actualizarBtn.addActionListener(this::actualizarMedico);
        buttonPanel.add(actualizarBtn);

        eliminarBtn = new JButton("Eliminar");
        eliminarBtn.addActionListener(this::eliminarMedico);
        buttonPanel.add(eliminarBtn);

        limpiarBtn = new JButton("Limpiar");
        limpiarBtn.addActionListener(this::limpiarCampos);
        buttonPanel.add(limpiarBtn);

        // Panel norte (formulario + botones)
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tabla de médicos
        String[] columnNames = {"Cédula", "Nombre", "Especialidad", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicosTable = new JTable(tableModel);
        medicosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = medicosTable.getSelectedRow();
                    if (row >= 0) {
                        String cedula = (String) tableModel.getValueAt(row, 0);
                        cargarMedico(cedula);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicosTable);

        // Agregar componentes al panel principal
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void agregarMedico(ActionEvent e) {
        if (!validarCampos()) return;

        Medico medico = new Medico(
                cedulaField.getText().trim(),
                nombreField.getText().trim(),
                especialidadField.getText().trim(),
                emailField.getText().trim()
        );

        Response response = ClientNetwork.sendRequest(new Request("CREATE_MEDICO", medico));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Médico agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadMedicos();
            notifyMedicosUpdated();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarMedico(ActionEvent e) {
        String cedula = JOptionPane.showInputDialog(this, "Ingrese la cédula del médico:", "Buscar Médico", JOptionPane.QUESTION_MESSAGE);
        if (cedula != null && !cedula.trim().isEmpty()) {
            cargarMedico(cedula.trim());
        }
    }

    private void actualizarMedico(ActionEvent e) {
        if (!validarCampos()) return;

        Medico medico = new Medico(
                cedulaField.getText().trim(),
                nombreField.getText().trim(),
                especialidadField.getText().trim(),
                emailField.getText().trim()
        );

        Response response = ClientNetwork.sendRequest(new Request("UPDATE_MEDICO", medico));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Médico actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadMedicos();
            notifyMedicosUpdated();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMedico(ActionEvent e) {
        if (cedulaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero busque un médico para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al médico con cédula " + cedulaField.getText().trim() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Response response = ClientNetwork.sendRequest(new Request("DELETE_MEDICO", cedulaField.getText().trim()));

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Médico eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                loadMedicos();
                notifyMedicosUpdated();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos(ActionEvent e) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        cedulaField.setText("");
        nombreField.setText("");
        especialidadField.setText("");
        emailField.setText("");
    }

    private boolean validarCampos() {
        if (cedulaField.getText().trim().isEmpty() ||
                nombreField.getText().trim().isEmpty() ||
                especialidadField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validación básica de email
        if (!emailField.getText().trim().contains("@")) {
            JOptionPane.showMessageDialog(this, "Ingrese un email válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void cargarMedico(String cedula) {
        Response response = ClientNetwork.sendRequest(new Request("GET_MEDICO", cedula));

        if (response.isSuccess()) {
            MedicoDTO medico = (MedicoDTO) response.getData();
            if (medico != null) {
                cedulaField.setText(medico.getCedula());
                nombreField.setText(medico.getNombre());
                especialidadField.setText(medico.getEspecialidad());
                emailField.setText(medico.getEmail());
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un médico con esa cédula", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedicos() {
        Response response = ClientNetwork.sendRequest(new Request("GET_ALL_MEDICOS", null));

        if (response.isSuccess()) {
            List<MedicoDTO> medicos = (List<MedicoDTO>) response.getData();
            tableModel.setRowCount(0);

            for (MedicoDTO medico : medicos) {
                Object[] row = {
                        medico.getCedula(),
                        medico.getNombre(),
                        medico.getEspecialidad(),
                        medico.getEmail()
                };
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar médicos: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}