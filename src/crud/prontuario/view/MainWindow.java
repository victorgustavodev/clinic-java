package crud.prontuario.view;

import crud.prontuario.view.paciente.*;
import crud.prontuario.view.exame.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainWindow() {

        setTitle("Sistema de Gerenciamento de Prontuários");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        // --- Menu: Pacientes ---
        JMenu menuPacientes = new JMenu("Pacientes");
        JMenuItem btnCriarPaciente = new JMenuItem("Criar Paciente");
        JMenuItem btnLocalizarPaciente = new JMenuItem("Localizar");
        JMenuItem btnEditarPaciente = new JMenuItem("Editar");
        JMenuItem btnExcluirPaciente = new JMenuItem("Excluir");

        menuPacientes.add(btnCriarPaciente);
        menuPacientes.add(btnLocalizarPaciente);
        menuPacientes.add(btnEditarPaciente);
        menuPacientes.add(btnExcluirPaciente);
        menuBar.add(menuPacientes);

        // --- Menu: Exames ---
        JMenu menuExames = new JMenu("Exames");
        JMenuItem btnCriarExame = new JMenuItem("Criar Exame");
        JMenuItem btnLocalizarExame = new JMenuItem("Localizar");
        JMenuItem btnEditarExame = new JMenuItem("Editar");
        JMenuItem btnExcluirExame = new JMenuItem("Excluir");

        menuExames.add(btnCriarExame);
        menuExames.add(btnLocalizarExame);
        menuExames.add(btnEditarExame);
        menuExames.add(btnExcluirExame);
        menuBar.add(menuExames);

        // --- Menu: Sair ---
        JMenu menuSair = new JMenu("Sair");
        JMenuItem itemSair = new JMenuItem("Sair da Aplicação");
        menuSair.add(itemSair);
        menuBar.add(menuSair);

        setJMenuBar(menuBar);

        JPanel centralPanel = new JPanel(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Prontuários");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        centralPanel.add(welcomeLabel);

        add(centralPanel, BorderLayout.CENTER);

        //-------PACIENTES--------
        btnCriarPaciente.addActionListener(e -> {
            PacienteCreateDialog createDialog = new PacienteCreateDialog(null);
            createDialog.setVisible(true);
        });

        btnLocalizarPaciente.addActionListener(e -> {
            PacienteSearchDialog searchDialog = new PacienteSearchDialog(null);
            searchDialog.setVisible(true);
        });

        btnEditarPaciente.addActionListener(e -> {
            PacienteEditDialog editDialog = new PacienteEditDialog(this);
            editDialog.setVisible(true);
        });

        btnExcluirPaciente.addActionListener(e -> {
            PacienteDeleteDialog deleteDialog = new PacienteDeleteDialog(null);
            deleteDialog.setVisible(true);
        }
        );

        //-------EXAMES--------
        btnCriarExame.addActionListener(e -> {
            ExameCreateDialog createExameDialog = new ExameCreateDialog(this);
            createExameDialog.setVisible(true);
        });

        btnLocalizarExame.addActionListener(e -> {
            ExameSearchDialog listDialog = new ExameSearchDialog(null);
            listDialog.setVisible(true);
        });

        btnEditarExame.addActionListener(e -> {
            ExameEditDialog editDialog = new ExameEditDialog(null);
            editDialog.setVisible(true);
        });

        btnExcluirExame.addActionListener(e -> {
            ExameDeleteDialog deleteDialog = new ExameDeleteDialog(null);
            deleteDialog.setVisible(true);
        }
        );

        itemSair.addActionListener(e -> fecharAplicacao());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                fecharAplicacao();
            }
        });
    }

    private void fecharAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Você tem certeza que deseja sair da aplicação?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Garante que a interface gráfica seja criada na thread de eventos do Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
}
