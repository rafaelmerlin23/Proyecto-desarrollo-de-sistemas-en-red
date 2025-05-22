package org.example.cliente.gui;

import com.toedter.calendar.JDateChooser;
import org.example.cliente.network.ClientNetwork;
import org.example.servidor.database.dtos.*;
import org.example.servidor.database.entities.*;
import org.example.shared.Request;
import org.example.shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CitaPanel extends JPanel implements PanelObserver {
    private JDateChooser dateChooser;
    private JComboBox<String> horaComboBox;
    private JTextField motivoField;
    private JComboBox<Medico> medicoComboBox;
    private JComboBox<Paciente> pacienteComboBox;
    private JButton agregarBtn, buscarBtn, actualizarBtn, eliminarBtn, limpiarBtn, limpiarFiltroBtn;
    private JTable citasTable;
    private DefaultTableModel tableModel;
    private JLabel idLabel;

    public CitaPanel() {
        initComponents();
        loadMedicos();
        loadPacientes();
        loadCitas();
    }

    @Override
    public void updateMedicos() {
        loadMedicos();
    }

    @Override
    public void updatePacientes() {
        loadPacientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("ID:"));
        idLabel = new JLabel("Nueva cita");
        formPanel.add(idLabel);

        formPanel.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        formPanel.add(dateChooser);

        formPanel.add(new JLabel("Hora:"));
        horaComboBox = new JComboBox<>(new String[]{"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"});
        formPanel.add(horaComboBox);

        formPanel.add(new JLabel("Motivo:"));
        motivoField = new JTextField();
        formPanel.add(motivoField);

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
        limpiarBtn.addActionListener(e -> limpiarCampos());
        buttonPanel.add(limpiarBtn);

        limpiarFiltroBtn = new JButton("Limpiar filtro");
        limpiarFiltroBtn.addActionListener(e -> limpiarFiltro());
        buttonPanel.add(limpiarFiltroBtn);

        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Fecha", "Hora", "Motivo", "Médico", "Paciente"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        citasTable = new JTable(tableModel);
        citasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        citasTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && citasTable.getSelectedRow() >= 0) {
                    Object idValue = tableModel.getValueAt(citasTable.getSelectedRow(), 0);
                    if (idValue instanceof Long) {
                        cargarCita((Long) idValue);
                    } else if (idValue instanceof Integer) {
                        cargarCita(((Integer) idValue).longValue());
                    } else {
                        try {
                            cargarCita(Long.parseLong(idValue.toString()));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(CitaPanel.this, "ID inválido: " + idValue, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(citasTable), BorderLayout.CENTER);
    }

    private void buscarCitasPorFecha(ActionEvent e) {
        Date fecha = dateChooser.getDate();
        if (fecha == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        fecha = cal.getTime();

        try {
            Response response = ClientNetwork.sendRequest(new Request("GET_CITAS_BY_FECHA", fecha));
            if (response.isSuccess()) {
                List<CitaDTO> citas = (List<CitaDTO>) response.getData(); // Cambiar a CitaDTO
                tableModel.setRowCount(0);

                if (citas.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No hay citas para esa fecha", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                for (CitaDTO c : citas) {
                    tableModel.addRow(new Object[]{
                            c.getId(),
                            df.format(c.getFecha()),
                            c.getHora(),
                            c.getMotivo(),
                            c.getMedicoNombre(),
                            c.getPacienteNombre()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al buscar: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Excepción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCita(Long id) {
        Response response = ClientNetwork.sendRequest(new Request("GET_CITA", id));
        if (response.isSuccess()) {
            CitaDTO citaDTO = (CitaDTO) response.getData(); // Cambiar a CitaDTO
            if (citaDTO != null) {
                idLabel.setText(citaDTO.getId().toString());
                dateChooser.setDate(citaDTO.getFecha());
                horaComboBox.setSelectedItem(citaDTO.getHora());
                motivoField.setText(citaDTO.getMotivo());

                seleccionarItemComboBox(medicoComboBox, citaDTO.getMedicoCedula());
                seleccionarItemComboBox(pacienteComboBox, citaDTO.getPacienteCurp());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar cita: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private <T> void seleccionarItemComboBox(JComboBox<T> comboBox, String id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item instanceof Medico && ((Medico) item).getCedula().equals(id)) {
                comboBox.setSelectedIndex(i);
                break;
            } else if (item instanceof Paciente && ((Paciente) item).getCurp().equals(id)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    public void limpiarFiltro(){
        tableModel.setRowCount(0);
        loadCitas();
    }

    private void agregarCita(ActionEvent e) {
        if (!validarCampos()) return;

        CitaCrearDTO dto = new CitaCrearDTO(
                dateChooser.getDate(),
                (String) horaComboBox.getSelectedItem(),
                motivoField.getText().trim(),
                ((Medico) medicoComboBox.getSelectedItem()).getCedula(),
                ((Paciente) pacienteComboBox.getSelectedItem()).getCurp()
        );

        Response r = ClientNetwork.sendRequest(new Request("CREATE_CITA", dto));
        if (r.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Cita creada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadCitas();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + r.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCita(ActionEvent e) {
        if (idLabel.getText().equals("Nueva cita")) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para actualizar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;

        UpdateCitaDTO dto = new UpdateCitaDTO(
                Long.parseLong(idLabel.getText()),
                dateChooser.getDate(),
                (String) horaComboBox.getSelectedItem(),
                motivoField.getText().trim(),
                ((Medico) medicoComboBox.getSelectedItem()).getCedula(),
                ((Paciente) pacienteComboBox.getSelectedItem()).getCurp()
        );

        Response r = ClientNetwork.sendRequest(new Request("UPDATE_CITA", dto));

        if (r.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Cita actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            loadCitas();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + r.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCita(ActionEvent e) {
        if (idLabel.getText().equals("Nueva cita")) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar cita con ID " + idLabel.getText() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = Long.parseLong(idLabel.getText());
            Response r = ClientNetwork.sendRequest(new Request("DELETE_CITA", id));
            if (r.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cita eliminada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                loadCitas();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + r.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        Date fechaSeleccionada = dateChooser.getDate();
        String horaSeleccionada = (String) horaComboBox.getSelectedItem();
        Date fechaHoraActual = new Date();

        if (fechaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (horaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una hora", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Combinar fecha y hora seleccionadas
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaSeleccionada);

        String[] partesHora = horaSeleccionada.split(":");
        int hora = Integer.parseInt(partesHora[0]);
        int minuto = Integer.parseInt(partesHora[1]);

        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date fechaHoraSeleccionada = cal.getTime();

        if (fechaHoraSeleccionada.before(fechaHoraActual)) {
            JOptionPane.showMessageDialog(this, "No se puede seleccionar una fecha y hora anteriores a la actual", "Fecha y hora inválidas", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (motivoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un motivo", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (medicoComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (pacienteComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private void loadMedicos() {
        Response r = ClientNetwork.sendRequest(new Request("GET_ALL_MEDICOS", null));
        if (r.isSuccess()) {
            List<MedicoDTO> lista = (List<MedicoDTO>) r.getData();
            DefaultComboBoxModel<Medico> model = new DefaultComboBoxModel<>();
            lista.forEach(dto -> model.addElement(new Medico(dto.getCedula(), dto.getNombre(), dto.getEspecialidad(), dto.getEmail())));
            medicoComboBox.setModel(model);
        }
    }

    private void loadPacientes() {
        Response r = ClientNetwork.sendRequest(new Request("GET_ALL_PACIENTES", null));
        if (r.isSuccess()) {
            List<PacienteDTO> lista = (List<PacienteDTO>) r.getData();
            DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();
            lista.forEach(dto -> model.addElement(new Paciente(dto.getCurp(), dto.getNombre(), dto.getTelefono(), dto.getEmail())));
            pacienteComboBox.setModel(model);
        }
    }

    private void loadCitas() {
        Response r = ClientNetwork.sendRequest(new Request("GET_ALL_CITAS", null));
        if (r.isSuccess()) {
            List<CitaDTO> lista = (List<CitaDTO>) r.getData();
            tableModel.setRowCount(0);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            for (CitaDTO c : lista) {
                tableModel.addRow(new Object[]{c.getId(), df.format(c.getFecha()), c.getHora(), c.getMotivo(), c.getMedicoNombre(), c.getPacienteNombre()});
            }
        }
    }
}
