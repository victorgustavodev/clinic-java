package crud.prontuario.view.exame;

import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;
import crud.prontuario.services.Facade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExameSearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	IConnection conexao = new DatabaseConnectionMySQL(); 
	Facade facade = new Facade(conexao);
	
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ExameSearchDialog(Frame parent) {
        super(parent, "Localizar Exame", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Buscar por Nome do Paciente:"));
        
        searchField = new JTextField(30);
        searchField.setToolTipText("Digite o nome ou parte do nome do paciente para filtrar os resultados.");
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Pesquisar");
        searchButton.setToolTipText("Clique para iniciar a busca com o termo digitado.");
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID Exame", "Nome do Paciente", "Data", "Descrição"};
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
        JButton closeButton = new JButton("Sair");
        closeButton.setToolTipText("Fecha esta janela de busca.");
        
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> buscarExames());
        closeButton.addActionListener(e -> dispose());
        
        buscarExames();
    }

    private void buscarExames() {
        String nomePaciente = searchField.getText().trim();
        
        tableModel.setRowCount(0);

        try {
            
            List<Exame> exames = facade.buscarExamesPorNomePaciente(nomePaciente);
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
            JOptionPane.showMessageDialog(this, "Erro ao buscar exames: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
