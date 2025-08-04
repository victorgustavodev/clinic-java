package crud.prontuario.view.paciente;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PacienteEditDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField nomeField;
    private JTextField cpfField;
    private JFormattedTextField dataDeNascimentoField;
    private final Paciente paciente;
    private final IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

    public PacienteEditDialog(Frame parent) {
        super(parent, "Editar Paciente", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // Campo Nome
        add(new JLabel("Nome:"));
		this.paciente = new Paciente();
        nomeField = new JTextField(paciente.getNome());
        add(nomeField);

        // Campo CPF
        add(new JLabel("CPF (XXX.XXX.XXX-XX):"));
        cpfField = new JTextField(paciente.getCpf());
        add(cpfField);

        // Campo Data de Nascimento
        add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dataDeNascimentoField = new JFormattedTextField(dateMask);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = paciente.getDataDeNascimento().format(formatter);
            dataDeNascimentoField.setText(dataFormatada);
        } catch (ParseException e) {
            dataDeNascimentoField = new JFormattedTextField();
        }
        add(dataDeNascimentoField);

        // Botões
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        add(btnSalvar);
        add(btnCancelar);

        btnSalvar.addActionListener(e -> salvarAtualizacao());
        btnCancelar.addActionListener(e -> dispose());

        setVisible(true);
        add(new JLabel("Tela de Edição ainda não implementada"), BorderLayout.CENTER);
    }

    private void salvarAtualizacao() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String nascimentoStr = dataDeNascimentoField.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || nascimentoStr.contains("_")) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.");
            return;
        }

        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }

        LocalDate dataNascimento;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimento = LocalDate.parse(nascimentoStr, formatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato DD/MM/AAAA.");
            return;
        }

        // Atualiza os dados no objeto
        paciente.setNome(nome);
        paciente.setCpf(cpf);
        paciente.setDataDeNascimento(dataNascimento);

        try {
            pacienteDao.update(paciente);
            JOptionPane.showMessageDialog(this, "✅ Paciente atualizado com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage());
        }
    }
}
