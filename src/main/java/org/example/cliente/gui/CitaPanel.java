package org.example.cliente.gui;


import com.toedter.calendar.JDateChooser;
import org.example.cliente.gui.components.DateLabelFormatter;
import org.example.cliente.network.ClientNetwork;
import org.example.servidor.database.dtos.CitaDTO;
import org.example.servidor.database.dtos.MedicoDTO;
import org.example.servidor.database.dtos.PacienteDTO;
import org.example.servidor.database.entities.Cita;
import org.example.servidor.database.entities.Medico;
import org.example.servidor.database.entities.Paciente;
import org.example.shared.Request;
import org.example.shared.Response;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class CitaPanel extends JPanel implements PanelObserver {
    private JDateChooser dateChooser;
    private JComboBox<String> horaComboBox;
    private JTextField motivoField;
    private JComboBox<Medico> medicoComboBox;
    private JComboBox<Paciente> pacienteComboBox;
    private JButton agregarBtn, buscarBtn, actualizarBtn, eliminarBtn, limpiarBtn;
    private JTable citasTable;
    private DefaultTableModel tableModel;
    private JLabel idLabel;

    @Override
    public void updateMedicos() {
        loadMedicos();
    }

    @Override
    public void updatePacientes() {
        loadPacientes();
    }

    public CitaPanel() {
        initComponents();
        loadMedicos();
        loadPacientes();
        loadCitas();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        // ID (solo lectura)
        formPanel.add(new JLabel("ID:"));
        idLabel = new JLabel("Nueva cita");
        formPanel.add(idLabel);

        // Fecha con JDatePicker
        formPanel.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy"); // Formato de fecha
        formPanel.add(dateChooser);
        Properties p = new Properties();
        p.put("text.today", "Hoy");
        p.put("text.month", "Mes");
        p.put("text.year", "Año");


        // Hora
        formPanel.add(new JLabel("Hora:"));
        String[] horas = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        horaComboBox = new JComboBox<>(horas);
        formPanel.add(horaComboBox);

        // Motivo
        formPanel.add(new JLabel("Motivo:"));
        motivoField = new JTextField();
        formPanel.add(motivoField);

        // Médico
        formPanel.add(new JLabel("Médico:"));
        medicoComboBox = new JComboBox<>();
        medicoComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Medico) {
                    Medico m = (Medico) value;
                    setText(m.getNombre() + " (" + m.getEspecialidad() + ")");
                }
                return this;
            }
        });
        formPanel.add(medicoComboBox);

        // Paciente
        formPanel.add(new JLabel("Paciente:"));
        pacienteComboBox = new JComboBox<>();
        pacienteComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Paciente) {
                    Paciente p = (Paciente) value;
                    setText(p.getNombre());
                }
                return this;
            }
        });
        formPanel.add(pacienteComboBox);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        agregarBtn = new JButton("Agregar");
        agregarBtn.addActionListener(this::agregarCita);
        buttonPanel.add(agregarBtn);

        buscarBtn = new JButton("Buscar por fecha");
        buscarBtn.addActionListener(this::buscarCitasPorFecha);
        buttonPanel.add(buscarBtn);

        actualizarBtn = new JButton("Actualizar");
        actualizarBtn.addActionListener(this::actualizarCita);
        buttonPanel.add(actualizarBtn);

        eliminarBtn = new JButton("Eliminar");
        eliminarBtn.addActionListener(this::eliminarCita);
        buttonPanel.add(eliminarBtn);

        limpiarBtn = new JButton("Limpiar");
        limpiarBtn.addActionListener(this::limpiarCampos);
        buttonPanel.add(limpiarBtn);

        // Panel norte (formulario + botones)
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Tabla de citas
        String[] columnNames = {"ID", "Fecha", "Hora", "Motivo", "Médico", "Paciente"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        citasTable = new JTable(tableModel);
        citasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        citasTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = citasTable.getSelectedRow();
                    if (row >= 0) {
                        Long id = (Long) tableModel.getValueAt(row, 0);
                        cargarCita(id);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(citasTable);

        // Agregar componentes al panel principal
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void agregarCita(ActionEvent e) {
        if (!validarCampos()) return;

        Cita cita = new Cita(
                dateChooser.getDate(),
                (String) horaComboBox.getSelectedItem(),
                motivoField.getText().trim(),
                (Medico) medicoComboBox.getSelectedItem(),
                (Paciente) pacienteComboBox.getSelectedItem()
        );

        Response response = ClientNetwork.sendRequest(new Request("CREATE_CITA", cita));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Cita agregada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadCitas();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarCitasPorFecha(ActionEvent e) {
        Date fecha = dateChooser.getDate();
        if (fecha == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha para buscar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Response response = ClientNetwork.sendRequest(new Request("GET_CITAS_BY_FECHA", fecha));

        if (response.isSuccess()) {
            List<Cita> citas = (List<Cita>) response.getData();
            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Cita cita : citas) {
                Object[] row = {
                        cita.getId(),
                        dateFormat.format(cita.getFecha()),
                        cita.getHora(),
                        cita.getMotivo(),
                        cita.getMedico().getNombre(),
                        cita.getPaciente().getNombre()
                };
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCita(ActionEvent e) {
        if (idLabel.getText().equals("Nueva cita")) {
            JOptionPane.showMessageDialog(this, "Primero busque una cita para actualizar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) return;

        Cita cita = new Cita(
                dateChooser.getDate(),
                (String) horaComboBox.getSelectedItem(),
                motivoField.getText().trim(),
                (Medico) medicoComboBox.getSelectedItem(),
                (Paciente) pacienteComboBox.getSelectedItem()
        );

        cita.setId(Long.parseLong(idLabel.getText()));

        Response response = ClientNetwork.sendRequest(new Request("UPDATE_CITA", cita));

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Cita actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadCitas();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCita(ActionEvent e) {
        if (idLabel.getText().equals("Nueva cita")) {
            JOptionPane.showMessageDialog(this, "Primero busque una cita para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar la cita con ID " + idLabel.getText() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Response response = ClientNetwork.sendRequest(new Request("DELETE_CITA", Long.parseLong(idLabel.getText())));

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cita eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                loadCitas();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos(ActionEvent e) {
        limpiarCampos();
    }

    private void limpiarCampos() {
        idLabel.setText("Nueva cita");
        dateChooser.setDate(null);
        horaComboBox.setSelectedIndex(0);
        motivoField.setText("");
        medicoComboBox.setSelectedIndex(0);
        pacienteComboBox.setSelectedIndex(0);
    }

    private boolean validarCampos() {
        if (dateChooser.getDate() == null ||
                motivoField.getText().trim().isEmpty() ||
                medicoComboBox.getSelectedItem() == null ||
                pacienteComboBox.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void cargarCita(Long id) {
        Response response = ClientNetwork.sendRequest(new Request("GET_CITA", id));

        if (response.isSuccess()) {
            Cita cita = (Cita) response.getData();
            if (cita != null) {
                idLabel.setText(cita.getId().toString());
                dateChooser.setDate(cita.getFecha());
                horaComboBox.setSelectedItem(cita.getHora());
                motivoField.setText(cita.getMotivo());

                // Seleccionar médico y paciente en los combobox
                for (int i = 0; i < medicoComboBox.getItemCount(); i++) {
                    if (medicoComboBox.getItemAt(i).getCedula().equals(cita.getMedico().getCedula())) {
                        medicoComboBox.setSelectedIndex(i);
                        break;
                    }
                }

                for (int i = 0; i < pacienteComboBox.getItemCount(); i++) {
                    if (pacienteComboBox.getItemAt(i).getCurp().equals(cita.getPaciente().getCurp())) {
                        pacienteComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la cita", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedicos() {
        Response response = ClientNetwork.sendRequest(new Request("GET_ALL_MEDICOS", null));

        if (response.isSuccess()) {
            List<MedicoDTO> dtoList = (List<MedicoDTO>) response.getData();
            List<Medico> medicos = dtoList.stream()
                    .map(dto -> new Medico(dto.getCedula(), dto.getNombre(), dto.getEspecialidad(), dto.getEmail()))
                    .collect(Collectors.toList());
            DefaultComboBoxModel<Medico> model = new DefaultComboBoxModel<>();

            for (Medico medico : medicos) {
                model.addElement(medico);
            }

            medicoComboBox.setModel(model);
        }
    }

    private void loadPacientes() {
        Response response = ClientNetwork.sendRequest(new Request("GET_ALL_PACIENTES", null));

        if (response.isSuccess()) {
            List<PacienteDTO> dtoList = (List<PacienteDTO>) response.getData();
            List<Paciente> pacientes = dtoList.stream()
                    .map(dto -> new Paciente(dto.getCurp(),dto.getNombre(),dto.getTelefono(),dto.getEmail()))
                    .collect(Collectors.toList());
            DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();

            for (Paciente paciente : pacientes) {
                model.addElement(paciente);
            }

            pacienteComboBox.setModel(model);
        }
    }

    private void loadCitas() {
        Response response = ClientNetwork.sendRequest(new Request("GET_ALL_CITAS", null));

        if (response.isSuccess()) {
            List<CitaDTO> citas = (List<CitaDTO>) response.getData();
            tableModel.setRowCount(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (CitaDTO cita : citas) {
                Object[] row = {
                        cita.getId(),
                        dateFormat.format(cita.getFecha()),
                        cita.getHora(),
                        cita.getMotivo(),
                        cita.getMedicoNombre(),
                        cita.getPacienteNombre()
                };
                tableModel.addRow(row);
            }
        }
    }
}