package crud.prontuario.view.paciente;

import javax.swing.*;
import java.awt.*;

public class PacienteOptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public PacienteOptionsDialog(Frame parent) {
        super(parent, "Opções de Paciente", true);
        setLayout(new GridLayout(5, 1, 10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JButton btnCriar = new JButton("Criar Paciente");
        JButton btnListar = new JButton("Listar Pacientes");
        JButton btnEditar = new JButton("Editar Paciente");
        JButton btnLocalizar = new JButton("Localizar Paciente");
        JButton btnDeletar = new JButton("Deletar Paciente");
        JButton btnSair = new JButton("Sair");

        add(btnCriar);
        add(btnListar);
        add(btnEditar);
        add(btnLocalizar);
        add(btnDeletar);
        add(btnSair);
        
//
        // Ações dos botões
        btnCriar.addActionListener(e -> {
        	new PacienteCreateDialog(null).setVisible(true);
        });

        btnEditar.addActionListener(e -> {
            new PacienteEditDialog(null).setVisible(true);
        });
        
        btnListar.addActionListener(e -> {
        	new PacienteListDialog(null).setVisible(true);
        });
//
//        btnLocalizar.addActionListener(e -> {
//            new PacienteSearchDialog(null).setVisible(true);
//        });
//
        
        btnDeletar.addActionListener(e -> {
            new PacienteDeleteDialog(null).setVisible(true);
        });

        btnSair.addActionListener(e -> dispose());
    }
}
