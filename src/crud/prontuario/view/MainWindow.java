package crud.prontuario.view;

import crud.prontuario.view.paciente.PacienteOptionsDialog;
import crud.prontuario.view.exame.ExameOptionsDialog; // supondo que exista

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Prontuário Médico");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1, 10, 10));

        JLabel titulo = new JLabel("Sistema de Gerenciamento Clínico", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnPacientes = new JButton("Gerenciar Pacientes");
        JButton btnExames = new JButton("Gerenciar Exames");

        // Ações dos botões
        btnPacientes.addActionListener(e -> {
            dispose(); // fecha a MainWindow
            new PacienteOptionsDialog(null).setVisible(true); // ou passar this como parent se preferir
        });

        btnExames.addActionListener(e -> {
            dispose(); // fecha a MainWindow
            new ExameOptionsDialog(null).setVisible(true); // substitua pelo painel real que você tiver
        });

        // Adiciona os componentes à janela
        add(titulo);
        add(btnPacientes);
        add(btnExames);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
