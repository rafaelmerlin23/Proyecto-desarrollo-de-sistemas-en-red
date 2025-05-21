package org.example.cliente.gui;


import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        setTitle("Sistema Médico - Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Crear los paneles
        MedicoPanel medicoPanel = new MedicoPanel();
        PacientePanel pacientePanel = new PacientePanel();
        CitaPanel citaPanel = new CitaPanel();

        medicoPanel.addObserver(citaPanel);
        pacientePanel.addObserver(citaPanel);

        // Agregar los paneles al tabbedPane
        tabbedPane.addTab("Médicos", medicoPanel);
        tabbedPane.addTab("Pacientes", pacientePanel);
        tabbedPane.addTab("Citas", citaPanel);

        // Agregar el tabbedPane al frame
        add(tabbedPane, BorderLayout.CENTER);
    }
}