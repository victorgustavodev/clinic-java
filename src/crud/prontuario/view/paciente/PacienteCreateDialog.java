package crud.prontuario.view.paciente;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.database.IConnection;
import crud.prontuario.model.Paciente;
import crud.prontuario.services.Facade;
import crud.prontuario.utils.*;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PacienteCreateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	IConnection conexao = new DatabaseConnectionMySQL();

	Facade facade = new Facade(conexao);

	private JTextField nomeField;
	private JFormattedTextField cpfField;
	private JFormattedTextField dataDeNascimentoField;

	public PacienteCreateDialog(Frame parent) {
		super(parent, "Cadastrar Paciente", true);
		setLayout(new GridLayout(5, 2, 10, 10));
		setSize(500, 250);
		setLocationRelativeTo(parent);

		add(new JLabel("Nome:"));
		nomeField = new JTextField();
		add(nomeField);

		add(new JLabel("CPF (XXX.XXX.XXX-XX):"));

		try {
			MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
			mascaraCpf.setPlaceholderCharacter('_');
			cpfField = new JFormattedTextField(mascaraCpf);
		} catch (ParseException e) {
			cpfField = new JFormattedTextField();
			e.printStackTrace();
		}

		add(cpfField);

		add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
		try {
			MaskFormatter mascaraData = new MaskFormatter("##/##/####");
			mascaraData.setPlaceholderCharacter('_');
			dataDeNascimentoField = new JFormattedTextField(mascaraData);
		} catch (ParseException e) {
			dataDeNascimentoField = new JFormattedTextField();
			e.printStackTrace();
		}
		add(dataDeNascimentoField);

		JButton btnSalvar = new JButton("Salvar");
		add(btnSalvar);

		JButton btnLimpar = new JButton("Limpar");
		add(btnLimpar);

		JButton btnSair = new JButton("Sair");
		add(btnSair);

		add(new JLabel(""));

		btnSalvar.addActionListener(e -> salvarPaciente());
		btnLimpar.addActionListener(e -> limparCampos());
		btnSair.addActionListener(e -> dispose());
	}

	private void salvarPaciente() {
		String nome = nomeField.getText().trim();
		String cpf = cpfField.getText();
		String nascimentoStr = dataDeNascimentoField.getText();

		if (nome.isEmpty() || cpf.contains("_") || nascimentoStr.contains("_")) {
			JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos completamente.",
					"Erro de Validação", JOptionPane.ERROR_MESSAGE);
			return;
		}

		LocalDate dataNascimento;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			dataNascimento = LocalDate.parse(nascimentoStr, formatter);
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato DD/MM/AAAA.",
					"Erro de Validação", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			Paciente paciente = new Paciente(nome, cpf, dataNascimento);

			if (ValidationsAndFormatting.validarCPF(cpf)) {
				facade.cadastrarPaciente(paciente);
				JOptionPane.showMessageDialog(this, "✅ Paciente salvo com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Não foi possivel salvar o paciente, verifique o CPF!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
			}
			dispose();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Não foi possivel criar o paciente, CPF já cadastrado!",
					"Erro de Duplicidade", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void limparCampos() {
		nomeField.setText("");
		cpfField.setValue(null);
		dataDeNascimentoField.setValue(null);
	}

}