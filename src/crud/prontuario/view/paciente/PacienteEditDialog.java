package crud.prontuario.view.paciente;

import crud.prontuario.database.*;
import crud.prontuario.model.Paciente;
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
import java.util.Objects;

public class PacienteEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
	IConnection conexao = new DatabaseConnectionMySQL();

	Facade facade = new Facade(conexao);

    // --- Componentes da Interface ---
    private JTable tabelaPacientes;
    private DefaultTableModel modeloTabela;
    private JTextField nomeField;
    private JFormattedTextField cpfField;
    private JFormattedTextField dataNascimentoField;
    private JButton btnAtualizar;
    private JButton btnSair;

    // --- Lógica de Negócio ---
    private List<Paciente> listaPacientes;
    private Paciente pacienteSelecionado;

    public PacienteEditDialog(Frame parent) {
        super(parent, "Edição de Paciente", true);
        

        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        criarPainelTabela();
        add(new JScrollPane(tabelaPacientes), BorderLayout.NORTH);

        criarPainelFormulario();
        add(criarPainelFormulario(), BorderLayout.CENTER);

        criarPainelBotoes();
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        carregarPacientesNaTabela();

        tabelaPacientes.getSelectionModel().addListSelectionListener(e -> {
            
            if (!e.getValueIsAdjusting()) {
                preencherCamposComSelecao();
            }
        });
    }

    private void criarPainelTabela() {
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Data de Nascimento"}, 0) {

			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPacientes = new JTable(modeloTabela);
        tabelaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel criarPainelFormulario() {
        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados para Edição"));

        nomeField = new JTextField();
        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(nomeField);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(mascaraCpf);
        } catch (ParseException e) {
            cpfField = new JFormattedTextField();
            e.printStackTrace();
        }
        painelFormulario.add(new JLabel("CPF:"));
        painelFormulario.add(cpfField);

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            dataNascimentoField = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            dataNascimentoField = new JFormattedTextField();
            e.printStackTrace();
        }
        painelFormulario.add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
        painelFormulario.add(dataNascimentoField);

        return painelFormulario;
    }

    private JPanel criarPainelBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAtualizar = new JButton("Atualizar");
        btnSair = new JButton("Sair");

        btnAtualizar.addActionListener(e -> atualizarPaciente());
        btnSair.addActionListener(e -> dispose());

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnSair);
        
        return painelBotoes;
    }

    private void carregarPacientesNaTabela() {
        modeloTabela.setRowCount(0);
        try {
            this.listaPacientes = facade.listarTodosPacientes();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Paciente p : listaPacientes) {
                String dataFormatada = (p.getDataDeNascimento() != null) ? p.getDataDeNascimento().format(formatter) : "";
                modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), p.getCpf(), dataFormatada});
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tabelaPacientes.getSelectedRow();
        if (selectedRow != -1) {
            pacienteSelecionado = listaPacientes.get(selectedRow);

            nomeField.setText(pacienteSelecionado.getNome());
            cpfField.setText(pacienteSelecionado.getCpf());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (pacienteSelecionado.getDataDeNascimento() != null) {
                dataNascimentoField.setText(pacienteSelecionado.getDataDeNascimento().format(formatter));
            } else {
                dataNascimentoField.setText("");
            }
        } else {

            pacienteSelecionado = null;
            nomeField.setText("");
            cpfField.setText("");
            dataNascimentoField.setText("");
        }
    }

    private void atualizarPaciente() {
        if (pacienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente na tabela para editar.", "Nenhum Paciente Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeNovo = nomeField.getText().trim();
        String cpfNovo = cpfField.getText().replaceAll("[._-]", "");
        String nascimentoStrNovo = dataNascimentoField.getText();

        if (nomeNovo.isEmpty() || cpfNovo.length() != 11 || nascimentoStrNovo.contains("_")) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos corretamente.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate dataNascimentoNova;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimentoNova = LocalDate.parse(nascimentoStrNovo, formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato DD/MM/AAAA.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean nomeMudou = !Objects.equals(nomeNovo, pacienteSelecionado.getNome());
        boolean cpfMudou = !Objects.equals(cpfField.getText(), pacienteSelecionado.getCpf());
        boolean dataMudou = !Objects.equals(dataNascimentoNova, pacienteSelecionado.getDataDeNascimento());

        if (!nomeMudou && !cpfMudou && !dataMudou) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração foi detectada.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        pacienteSelecionado.setNome(nomeNovo);
        pacienteSelecionado.setCpf(cpfField.getText()); 
        pacienteSelecionado.setDataDeNascimento(dataNascimentoNova);

        try {
        	
        	facade.atualizarPaciente(pacienteSelecionado);
            JOptionPane.showMessageDialog(this, "✅ Paciente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            
            carregarPacientesNaTabela();
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o paciente.\n" + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}