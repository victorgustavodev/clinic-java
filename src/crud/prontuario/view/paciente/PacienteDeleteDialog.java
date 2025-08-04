package crud.prontuario.view.paciente;

import java.awt.*;
import javax.swing.*;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

public class PacienteDeleteDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JTextField cpfField;
    private IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

    public PacienteDeleteDialog(Frame parent) {
        super(parent, "Excluir Paciente", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(400, 200);
        setLocationRelativeTo(parent);

        // CPF input
        add(new JLabel("CPF (XXX.XXX.XXX-XX):"));
        cpfField = new JTextField();
        add(cpfField);

        // Botões
        JButton btnDeletar = new JButton("Deletar");
        JButton btnCancelar = new JButton("Cancelar");
        add(btnDeletar);
        add(btnCancelar);

        btnDeletar.addActionListener(e -> deletarPaciente());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void deletarPaciente() {
        String cpf = cpfField.getText().trim();

        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Paciente paciente = pacienteDao.findByCPF(cpf);

        if (paciente == null) {
            JOptionPane.showMessageDialog(this, "Paciente não encontrado com o CPF informado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o paciente?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            pacienteDao.delete(paciente);
            JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso.");
            dispose();
        }
    }
}
