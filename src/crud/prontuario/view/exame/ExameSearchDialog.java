package crud.prontuario.view.exame;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExameSearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

    public ExameSearchDialog(Frame parent) {
        super(parent, "Localizar Exame", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL DE BUSCA (NORTE) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Rótulo para o campo de busca
        searchPanel.add(new JLabel("Buscar por Nome do Paciente:"));
        
        // Campo de texto para a busca
        searchField = new JTextField(30);
        searchField.setToolTipText("Digite o nome ou parte do nome do paciente para filtrar os resultados.");
        searchPanel.add(searchField);
        
        // Botão de pesquisa
        JButton searchButton = new JButton("Pesquisar");
        searchButton.setToolTipText("Clique para iniciar a busca com o termo digitado.");
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.NORTH);

        // --- TABELA DE RESULTADOS (CENTRO) ---
        String[] columnNames = {"ID Exame", "Nome do Paciente", "Data", "Descrição"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PAINEL DE AÇÕES (SUL) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Sair");
        closeButton.setToolTipText("Fecha esta janela de busca.");
        
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        // --- EVENTOS ---
        searchButton.addActionListener(e -> buscarExames());
        closeButton.addActionListener(e -> dispose());
        
        // Carrega todos os exames inicialmente
        buscarExames();
    }

    private void buscarExames() {
        String nomePaciente = searchField.getText().trim();
        
        // Limpa a tabela antes de popular
        tableModel.setRowCount(0);

        try {
            // Usa o novo método do DAO para buscar pelo nome do paciente
            List<Exame> exames = exameDao.findByPacienteName(nomePaciente); 
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Exame exame : exames) {
                Object[] rowData = {
                    exame.getId(),
                    exame.getPaciente().getNome(), // Pega o nome do paciente associado
                    exame.getDataExame().format(formatter),
                    exame.getDescricao()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar exames: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
