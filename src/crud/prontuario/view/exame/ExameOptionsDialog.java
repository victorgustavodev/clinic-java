package crud.prontuario.view.exame;

import javax.swing.*;

import java.awt.*;

public class ExameOptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ExameOptionsDialog(Frame parent) {
        super(parent, "Opções de Exame", true);
        setLayout(new GridLayout(10, 1, 10, 20));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JButton btnCriar = new JButton("Criar Exame");
        JButton btnEditar = new JButton("Editar Exame");
        JButton btnLocalizar = new JButton("Localizar Exame");
        JButton btnDeletar = new JButton("Excluir Exame");
        JButton btnSair = new JButton("Sair");

        add(btnCriar);
        add(btnEditar);
        add(btnLocalizar);
        add(btnDeletar);
        add(btnSair);
        

        btnCriar.addActionListener(e -> {
        	dispose();
        	new ExameCreateDialog(null).setVisible(true);
        });


        btnSair.addActionListener(e -> dispose());
    }
}
