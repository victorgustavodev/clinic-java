package crud.prontuario.view.paciente;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacienteSearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JFormattedTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

 // O "throws ParseException" foi removido daqui
    public PacienteSearchDialog(Frame parent) {
        super(parent, "Localizar Paciente por CPF", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL DE BUSCA (NORTE) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        searchPanel.add(new JLabel("CPF do Paciente:"));

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            searchField = new JFormattedTextField(mascaraCpf);
        } catch (ParseException e) {        
            e.printStackTrace();           
            searchField = new JFormattedTextField();
            JOptionPane.showMessageDialog(this, "Erro ao inicializar o campo de CPF.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
        }
        
        searchField.setPreferredSize(new Dimension(140, 25));
        searchField.setToolTipText("Digite o CPF para buscar. Deixe em branco para listar todos.");
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Pesquisar");
        searchButton.setToolTipText("Clique para iniciar a busca");
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Nome", "CPF", "Data de Nascimento"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 2L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Sair");
        closeButton.setToolTipText("Fecha esta janela de busca.");
        actionPanel.add(closeButton);
        add(actionPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> buscarPaciente());
        closeButton.addActionListener(e -> dispose());
        
        buscarPaciente();
    }

    private void buscarPaciente() {
        List<Paciente> pacientesEncontrados = new ArrayList<>();

        try {
           
            if (searchField.getValue() == null) {
                
                pacientesEncontrados = pacienteDao.findAll();
            } else {
                
                String cpfParaBuscar = (String) searchField.getValue();
                
               
                Paciente p = ((PacienteDAO) pacienteDao).findByCPF(cpfParaBuscar);
                
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

        if (pacientes.isEmpty()) {

        } else {
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
}