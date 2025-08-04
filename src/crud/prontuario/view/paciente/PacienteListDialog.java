package crud.prontuario.view.paciente;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PacienteListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JTable tabelaPacientes;
    private DefaultTableModel tabelaModel;
    private IEntityDAO<Paciente> pacienteDao;

    public PacienteListDialog(Frame parent) {
        super(parent, "Lista de Pacientes", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

        // Tabela
        tabelaModel = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Nascimento"}, 0);
        tabelaPacientes = new JTable(tabelaModel);
        JScrollPane scrollPane = new JScrollPane(tabelaPacientes);

        add(scrollPane, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel panelBottom = new JPanel();
        panelBottom.add(btnFechar);
        add(panelBottom, BorderLayout.SOUTH);

        carregarPacientes();
    }
	
	private void carregarPacientes() {
	    	
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    	tabelaModel.setRowCount(0);
	    	
	    	
	        try {
	            List<Paciente> pacientes = pacienteDao.findAll();

	            if (pacientes.isEmpty()) {
	            	JOptionPane.showMessageDialog(this, "Nenhum paciente encontrado!");
	            } else {
	                for (Paciente paciente : pacientes) {
	                    tabelaModel.addRow(new Object[]{
	                        paciente.getId(),
	                        paciente.getNome(),
	                        paciente.getCpf(),
	                        paciente.getDataDeNascimento() != null ? paciente.getDataDeNascimento().format(formatter): "Não informada",        	
	                    });
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace(); 
	            JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
	        }
	    }
}
