package crud.prontuario.view.paciente;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacienteSearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Usa a interface e injeta a dependência da conexão
    private IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

    public PacienteSearchDialog(Frame parent) {
        super(parent, "Localizar Paciente por CPF", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL DE BUSCA (NORTE) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("CPF do Paciente:"));
        searchField = new JTextField(30);
        searchField.setToolTipText("Digite o CPF do paciente para buscar. Deixe em branco para listar todos.");
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Pesquisar");
        searchButton.setToolTipText("Clique para iniciar a busca pelo CPF digitado.");
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.NORTH);

        // --- TABELA DE RESULTADOS (CENTRO) ---
        String[] columnNames = {"ID", "Nome", "CPF", "Data de Nascimento"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PAINEL DE AÇÕES (SUL) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Sair");
        closeButton.setToolTipText("Fecha esta janela de busca.");
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        // --- EVENTOS ---
        searchButton.addActionListener(e -> buscarPaciente());
        closeButton.addActionListener(e -> dispose());
        
        // Carrega todos os pacientes inicialmente
        buscarPaciente();
    }

    private void buscarPaciente() {
        String termoBusca = searchField.getText().trim();
        List<Paciente> pacientesEncontrados = new ArrayList<>();

        try {
            
            if (termoBusca.isEmpty()) {
                pacientesEncontrados = pacienteDao.findAll();
            } else {
                
                if (!termoBusca.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
                    JOptionPane.showMessageDialog(this, "Formato de CPF inválido. Use XXX.XXX.XXX-XX", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                
                Paciente p = ((PacienteDAO) pacienteDao).findByCPF(termoBusca);
                if (p != null) {
                    pacientesEncontrados = Collections.singletonList(p);
                }
            }

            popularTabela(pacientesEncontrados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar pacientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void popularTabela(List<Paciente> pacientes) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Paciente p : pacientes) {
            Object[] rowData = {
                p.getId(),
                p.getNome(),
                p.getCpf(),
                p.getDataDeNascimento() != null ? p.getDataDeNascimento().format(formatter) : "Não informada"
            };
            tableModel.addRow(rowData);
        }
    }
}
