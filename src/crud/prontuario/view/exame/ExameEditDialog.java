package crud.prontuario.view.exame;

import crud.prontuario.model.*;
import crud.prontuario.database.*;
import crud.prontuario.services.Facade;
import crud.prontuario.exception.DAOException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ExameEditDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	IConnection conexao = new DatabaseConnectionMySQL();

	Facade facade = new Facade(conexao);

	private JTable tabelaExames;
	private DefaultTableModel modeloTabela;
	private JComboBox<Paciente> pacienteComboBox;
	private JFormattedTextField dataExameField;
	private JTextArea descricaoArea;
	private JButton btnAtualizar;

	private List<Exame> listaExames;
	private Exame exameSelecionado;

	public ExameEditDialog(Frame parent) {
		super(parent, "Edição de Exame", true);

		setLayout(new BorderLayout(10, 10));
		setSize(850, 700);
		setLocationRelativeTo(parent);

		add(criarPainelTabela(), BorderLayout.NORTH);
		add(criarPainelFormulario(), BorderLayout.CENTER);
		add(criarPainelBotoes(), BorderLayout.SOUTH);
		carregarPacientesNoComboBox();
		carregarExamesNaTabela();

		tabelaExames.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				preencherCamposComSelecao();
			}
		});
	}
	
	//Controller

	private JScrollPane criarPainelTabela() {
		modeloTabela = new DefaultTableModel(new Object[] { "ID Exame", "Descrição", "Data do Exame", "Paciente" }, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tabelaExames = new JTable(modeloTabela);
		tabelaExames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(tabelaExames);
		scrollPane.setPreferredSize(new Dimension(800, 250));
		return scrollPane;
	}

	private JPanel criarPainelFormulario() {
		JPanel painelFormulario = new JPanel(new GridBagLayout());
		painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados para Edição"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		painelFormulario.add(new JLabel("Descrição:"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		descricaoArea = new JTextArea(5, 20);
		descricaoArea.setLineWrap(true);
		descricaoArea.setWrapStyleWord(true);
		painelFormulario.add(new JScrollPane(descricaoArea), gbc);

		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.EAST;
		painelFormulario.add(new JLabel("Data do Exame (DD/MM/AAAA):"), gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		try {
			MaskFormatter mascaraData = new MaskFormatter("##/##/####");
			mascaraData.setPlaceholderCharacter('_');
			dataExameField = new JFormattedTextField(mascaraData);
		} catch (ParseException e) {
			dataExameField = new JFormattedTextField();
			e.printStackTrace();
		}
		painelFormulario.add(dataExameField, gbc);

		gbc.gridy = 2;
		gbc.gridx = 0;
		painelFormulario.add(new JLabel("Paciente:"), gbc);

		gbc.gridx = 1;
		pacienteComboBox = new JComboBox<>();
		painelFormulario.add(pacienteComboBox, gbc);

		return painelFormulario;
	}

	private JPanel criarPainelBotoes() {
		JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnAtualizar = new JButton("Atualizar");
		JButton btnSair = new JButton("Sair");

		btnAtualizar.addActionListener(e -> atualizarExame());
		btnSair.addActionListener(e -> dispose());

		painelBotoes.add(btnAtualizar);
		painelBotoes.add(btnSair);

		return painelBotoes;
	}

	private void carregarPacientesNoComboBox() {
		try {
			List<Paciente> pacientes = facade.listarTodosPacientes();
			DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();
			for (Paciente p : pacientes) {
				model.addElement(p);
			}
			pacienteComboBox.setModel(model);
		} catch (DAOException e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void carregarExamesNaTabela() {
		modeloTabela.setRowCount(0);
		try {
			this.listaExames = facade.listarTodosExames();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for (Exame ex : listaExames) {
				String nomePaciente = (ex.getPaciente() != null) ? ex.getPaciente().getNome() : "N/A";
				String dataFormatada = (ex.getDataExame() != null) ? ex.getDataExame().format(formatter) : "";
				modeloTabela.addRow(new Object[] { ex.getId(), ex.getDescricao(), dataFormatada, nomePaciente });
			}
		} catch (DAOException e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar exames: " + e.getMessage(), "Erro de Banco de Dados",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void preencherCamposComSelecao() {
		int selectedRow = tabelaExames.getSelectedRow();
		if (selectedRow != -1) {
			exameSelecionado = listaExames.get(selectedRow);

			descricaoArea.setText(exameSelecionado.getDescricao());

			if (exameSelecionado.getDataExame() != null) {
				dataExameField
						.setText(exameSelecionado.getDataExame().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			} else {
				dataExameField.setText("");
			}

			pacienteComboBox.setSelectedItem(exameSelecionado.getPaciente());
		} else {
			exameSelecionado = null;
			descricaoArea.setText("");
			dataExameField.setValue(null);
			pacienteComboBox.setSelectedIndex(-1);
		}
	}

	private void atualizarExame() {
		if (exameSelecionado == null) {
			JOptionPane.showMessageDialog(this, "Por favor, selecione um exame na tabela para editar.",
					"Nenhum Exame Selecionado", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String descricaoNova = descricaoArea.getText().trim();
		String dataStrNova = dataExameField.getText();
		Paciente pacienteNovo = (Paciente) pacienteComboBox.getSelectedItem();

		if (descricaoNova.isEmpty() || dataStrNova.contains("_") || pacienteNovo == null) {
			JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos corretamente.",
					"Erro de Validação", JOptionPane.ERROR_MESSAGE);
			return;
		}

		LocalDate dataExameNova;
		try {
			dataExameNova = LocalDate.parse(dataStrNova, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this, "Data do exame inválida. Use o formato DD/MM/AAAA.",
					"Erro de Validação", JOptionPane.ERROR_MESSAGE);
			return;
		}

		exameSelecionado.setDescricao(descricaoNova);
		exameSelecionado.setDataExame(dataExameNova);
		exameSelecionado.setPaciente(pacienteNovo);

		try {
			facade.atualizarExame(exameSelecionado);
			JOptionPane.showMessageDialog(this, "✅ Exame atualizado com sucesso!", "Sucesso",
					JOptionPane.INFORMATION_MESSAGE);

			int selectedRow = tabelaExames.getSelectedRow();

			carregarExamesNaTabela();

			if (selectedRow != -1 && selectedRow < tabelaExames.getRowCount()) {
				tabelaExames.setRowSelectionInterval(selectedRow, selectedRow);
			}

		} catch (DAOException e) {
			JOptionPane.showMessageDialog(this, "Não foi possível atualizar o exame.\n" + e.getMessage(),
					"Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
		}
	}
}