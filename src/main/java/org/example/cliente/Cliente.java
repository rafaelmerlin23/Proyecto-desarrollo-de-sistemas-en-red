package org.example.cliente;


import org.example.cliente.gui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Cliente {
    public static void main(String[] args) {
        // Configurar look and feel
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}