
package crud.prontuario.view.exame;

import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;
import crud.prontuario.services.Facade;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ExameCreateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	IConnection conexao = new DatabaseConnectionMySQL();

	Facade facade = new Facade(conexao);

	private JComboBox<Object> pacienteComboBox;
	private JFormattedTextField dataExameField;
	private JTextArea descricaoArea;

	public ExameCreateDialog(Frame parent) {
		super(parent, "Cadastrar Novo Exame", true);

		// --- Configuração do Layout ---
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// --- Linha 1: Seleção de Paciente ---
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Paciente:"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1; // Permite que o combobox expanda
		pacienteComboBox = new JComboBox<>();
		add(pacienteComboBox, gbc);

		// --- Linha 2: Data do Exame ---
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Data do Exame (DD/MM/AAAA):"), gbc);

		gbc.gridx = 1;
		try {
			MaskFormatter mascaraData = new MaskFormatter("##/##/####");
			mascaraData.setPlaceholderCharacter('_');
			dataExameField = new JFormattedTextField(mascaraData);
		} catch (ParseException e) {
			dataExameField = new JFormattedTextField(); // Fallback
		}
		add(dataExameField, gbc);

		// --- Linha 3: Descrição do Exame ---
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.NORTH; // Alinha o label no topo
		add(new JLabel("Descrição:"), gbc);

		gbc.gridx = 1;
		gbc.gridheight = 2; // Ocupa 2 linhas de altura
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0; // Permite que a área de texto expanda verticalmente
		descricaoArea = new JTextArea(5, 20);
		descricaoArea.setLineWrap(true);
		descricaoArea.setWrapStyleWord(true);
		add(new JScrollPane(descricaoArea), gbc); // Adiciona a um painel de rolagem

		// --- Linha 4: Painel de Botões ---
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2; // Ocupa 2 colunas
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnSalvar = new JButton("Salvar");
		JButton btnLimpar = new JButton("Limpar");
		JButton btnSair = new JButton("Sair");
		buttonPanel.add(btnSalvar);
		buttonPanel.add(btnLimpar);
		buttonPanel.add(btnSair);
		add(buttonPanel, gbc);

		// --- Ações dos Botões ---
		btnSalvar.addActionListener(e -> salvarExame());
		btnLimpar.addActionListener(e -> limparCampos());
		btnSair.addActionListener(e -> dispose());

		// --- Carregamento de Dados Iniciais ---
		carregarPacientes();

		pack(); // Ajusta o tamanho da janela
		setMinimumSize(new Dimension(500, 350));
		setLocationRelativeTo(parent);
	}

	private void carregarPacientes() {
		pacienteComboBox.addItem("Selecione um paciente..."); // Item inicial
		try {
			List<Paciente> pacientes = facade.listarTodosPacientes();
			for (Paciente paciente : pacientes) {
				pacienteComboBox.addItem(paciente);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void limparCampos() {
		pacienteComboBox.setSelectedIndex(0);
		dataExameField.setValue(null);
		descricaoArea.setText("");
	}

	private void salvarExame() {
		// --- 1. Validação dos Campos ---
		if (pacienteComboBox.getSelectedIndex() <= 0) {
			JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente.", "Erro de Validação",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String dataStr = dataExameField.getText();
		if (dataStr.contains("_")) {
			JOptionPane.showMessageDialog(this, "Por favor, preencha a data completa.", "Erro de Validação",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String descricao = descricaoArea.getText().trim();
		if (descricao.isEmpty()) {
			JOptionPane.showMessageDialog(this, "O campo 'Descrição' não pode estar vazio.", "Erro de Validação",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// --- 2. Conversão e Criação do Objeto ---
		Paciente pacienteSelecionado = (Paciente) pacienteComboBox.getSelectedItem();
		LocalDate dataExame;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			dataExame = LocalDate.parse(dataStr, formatter);
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this, "Data inválida. Use o formato DD/MM/AAAA.", "Erro de Formato",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Crie o objeto Exame (ajuste o construtor conforme sua classe Exame)
		// Assumindo que o construtor agora é Exame(LocalDate data, String descricao,
		// Paciente p)
		Exame novoExame = new Exame(descricao, dataExame, pacienteSelecionado);

		// --- 3. Persistência no Banco de Dados ---
		try {
			facade.agendarExame(novoExame);
			JOptionPane.showMessageDialog(this, "✅ Exame salvo com sucesso!", "Sucesso",
					JOptionPane.INFORMATION_MESSAGE);
			dispose(); // Fecha a janela
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao salvar o exame: " + e.getMessage(), "Erro Crítico",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
