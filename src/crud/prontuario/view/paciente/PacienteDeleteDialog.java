package crud.prontuario.view.paciente;

import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.database.IConnection;
import crud.prontuario.model.Paciente;
import crud.prontuario.services.Facade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PacienteDeleteDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	IConnection conexao = new DatabaseConnectionMySQL();

	Facade facade = new Facade(conexao);

	private JTable table;
	private DefaultTableModel tableModel;

	public PacienteDeleteDialog(Frame parent) {
		super(parent, "Deletar Paciente", true);
		setSize(800, 600);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout(10, 10));

		JLabel instructionLabel = new JLabel("Selecione um paciente na tabela para deletar.", SwingConstants.CENTER);
		instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
		add(instructionLabel, BorderLayout.NORTH);

		String[] columnNames = { "ID", "Nome", "CPF", "Data de Nascimento" };
		tableModel = new DefaultTableModel(columnNames, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton deleteButton = new JButton("Deletar Selecionado");
		deleteButton.setToolTipText("Exclui o paciente selecionado e todos os seus exames associados.");

		JButton closeButton = new JButton("Sair");
		closeButton.setToolTipText("Fecha esta janela sem fazer alterações.");

		actionPanel.add(deleteButton);
		actionPanel.add(closeButton);
		add(actionPanel, BorderLayout.SOUTH);

		deleteButton.addActionListener(e -> deletarPacienteSelecionado());
		closeButton.addActionListener(e -> dispose());

		carregarTodosPacientes();
	}
	
	//Controller

	private void carregarTodosPacientes() {
		tableModel.setRowCount(0);

		try {
			List<Paciente> pacientes = facade.listarTodosPacientes();

			if (pacientes.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Nenhum paciente encontrado para exibir.", "Aviso",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			for (Paciente p : pacientes) {
				Object[] rowData = { p.getId(), p.getNome(), p.getCpf(),
						p.getDataDeNascimento() != null ? p.getDataDeNascimento().format(formatter) : "Não informada" };
				tableModel.addRow(rowData);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void deletarPacienteSelecionado() {
		int selectedRow = table.getSelectedRow();

		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente na tabela para deletar.",
					"Nenhum Paciente Selecionado", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Long pacienteId = (Long) tableModel.getValueAt(selectedRow, 0);
		String pacienteNome = (String) tableModel.getValueAt(selectedRow, 1);

		int resposta = JOptionPane.showConfirmDialog(this,
				"Você tem certeza que deseja excluir o paciente '" + pacienteNome
						+ "'?\nTODOS os seus exames também serão excluídos.",
				"Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (resposta == JOptionPane.YES_OPTION) {
			try {
				
				Paciente pacienteParaDeletar = new Paciente();
				pacienteParaDeletar.setId(pacienteId);

				facade.deletarPaciente(pacienteParaDeletar);

				
				tableModel.removeRow(selectedRow);

				JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!", "Sucesso",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Erro ao excluir o paciente: " + e.getMessage(), "Erro Crítico",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
}
