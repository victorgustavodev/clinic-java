package crud.prontuario.view.exame;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExameDeleteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JTable table;
    private DefaultTableModel tableModel;
    IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
	IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

    public ExameDeleteDialog(Frame parent) {
        super(parent, "Deletar Exame", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Rótulo de Instrução (NORTE) ---
        JLabel instructionLabel = new JLabel("Selecione um exame na tabela para deletar.", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(instructionLabel, BorderLayout.NORTH);

        // --- TABELA DE EXAMES (CENTRO) ---
        String[] columnNames = {"ID Exame", "Nome do Paciente", "Data", "Descrição"};
        tableModel = new DefaultTableModel(columnNames, 0) {

			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Células não editáveis
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PAINEL DE AÇÕES (SUL) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Botão Deletar
        JButton deleteButton = new JButton("Deletar Selecionado");
        deleteButton.setToolTipText("Exclui o exame selecionado na tabela permanentemente.");
        
        // Botão Sair
        JButton closeButton = new JButton("Sair");
        closeButton.setToolTipText("Fecha esta janela sem fazer alterações.");
        
        actionPanel.add(deleteButton);
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        // --- EVENTOS ---
        deleteButton.addActionListener(e -> deletarExameSelecionado());
        closeButton.addActionListener(e -> dispose());
        
        // Carrega todos os exames ao abrir a janela
        carregarTodosExames();
    }

    private void carregarTodosExames() {
        // Limpa a tabela
        tableModel.setRowCount(0);

        try {
           
            List<Exame> exames = exameDao.findByPacienteName(""); 
            
            if (exames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum exame encontrado para exibir.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Exame exame : exames) {
                Object[] rowData = {
                    exame.getId(),
                    exame.getPaciente().getNome(),
                    exame.getDataExame().format(formatter),
                    exame.getDescricao()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar exames: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deletarExameSelecionado() {
        int selectedRow = table.getSelectedRow();

        // Verifica se uma linha foi selecionada
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um exame na tabela para deletar.", "Nenhum Exame Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega o ID do exame da primeira coluna da linha selecionada
        Long exameId = (Long) tableModel.getValueAt(selectedRow, 0);
        String pacienteNome = (String) tableModel.getValueAt(selectedRow, 1);

        // --- Caixa de Diálogo de Confirmação ---
        int resposta = JOptionPane.showConfirmDialog(
            this,
            "Você tem certeza que deseja excluir o exame do paciente '" + pacienteNome + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        // Se o usuário confirmar a exclusão
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                Exame e = exameDao.findById(exameId);
                exameDao.delete(e);

                tableModel.removeRow(selectedRow);
                
                JOptionPane.showMessageDialog(this, "Exame excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir o exame: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
