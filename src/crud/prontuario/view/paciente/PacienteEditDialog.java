package crud.prontuario.view.paciente;

import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.IConnection;
import crud.prontuario.database.DatabaseConnectionMySQL; // Supondo que você use esta conexão
import crud.prontuario.model.Paciente;
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

    // --- Componentes da Interface ---
    private JTable tabelaPacientes;
    private DefaultTableModel modeloTabela;
    private JTextField nomeField;
    private JFormattedTextField cpfField; // Alterado para JFormattedTextField para consistência
    private JFormattedTextField dataNascimentoField;
    private JButton btnAtualizar;
    private JButton btnSair;

    // --- Lógica de Negócio ---
    private final PacienteDAO pacienteDao;
    private List<Paciente> listaPacientes; // Armazena a lista de pacientes carregada para evitar múltiplas chamadas ao BD
    private Paciente pacienteSelecionado; // Armazena o objeto do paciente selecionado na tabela

    public PacienteEditDialog(Frame parent) {
        super(parent, "Edição de Paciente", true);

        // A conexão deve ser idealmente injetada, mas vamos instanciá-la aqui para o exemplo
        IConnection dbConnection = new DatabaseConnectionMySQL();
        this.pacienteDao = new PacienteDAO(dbConnection);

        // Configuração geral do layout do Dialog
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // --- PAINEL NORTE: Tabela de Pacientes ---
        criarPainelTabela();
        add(new JScrollPane(tabelaPacientes), BorderLayout.NORTH);

        // --- PAINEL CENTRAL: Formulário de Edição ---
        criarPainelFormulario();
        add(criarPainelFormulario(), BorderLayout.CENTER);

        // --- PAINEL SUL: Botões de Ação ---
        criarPainelBotoes();
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        // Carrega os dados iniciais na tabela
        carregarPacientesNaTabela();

        // Adiciona o listener para seleção de linha
        tabelaPacientes.getSelectionModel().addListSelectionListener(e -> {
            // O 'if' evita que o evento seja disparado duas vezes (ao soltar o mouse)
            if (!e.getValueIsAdjusting()) {
                preencherCamposComSelecao();
            }
        });
    }

    private void criarPainelTabela() {
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Data de Nascimento"}, 0) {
            /**
			 * 
			 */
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
        modeloTabela.setRowCount(0); // Limpa a tabela
        try {
            this.listaPacientes = pacienteDao.findAll(); // Armazena a lista localmente
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
        if (selectedRow != -1) { // -1 significa que nenhuma linha está selecionada
            // Pega o paciente da lista local, usando o índice da linha da tabela
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
            // Se nenhuma linha for selecionada (ou a seleção for limpa), reseta os campos
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

        // 1. Pega os dados dos campos de texto (os dados novos)
        String nomeNovo = nomeField.getText().trim();
        String cpfNovo = cpfField.getText().replaceAll("[._-]", ""); // Remove máscara para validação
        String nascimentoStrNovo = dataNascimentoField.getText();

        // 2. Validações
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

        // 3. Compara os dados novos com os do objeto selecionado
        boolean nomeMudou = !Objects.equals(nomeNovo, pacienteSelecionado.getNome());
        boolean cpfMudou = !Objects.equals(cpfField.getText(), pacienteSelecionado.getCpf()); // Compara com máscara
        boolean dataMudou = !Objects.equals(dataNascimentoNova, pacienteSelecionado.getDataDeNascimento());

        if (!nomeMudou && !cpfMudou && !dataMudou) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração foi detectada.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 4. Se houve mudanças, atualiza o objeto 'Paciente'
        pacienteSelecionado.setNome(nomeNovo);
        pacienteSelecionado.setCpf(cpfField.getText()); // Salva com a máscara
        pacienteSelecionado.setDataDeNascimento(dataNascimentoNova);

        // 5. Tenta persistir a atualização no banco de dados
        try {
            pacienteDao.update(pacienteSelecionado);
            JOptionPane.showMessageDialog(this, "✅ Paciente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            // Atualiza a tabela para refletir a mudança
            carregarPacientesNaTabela();
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o paciente.\n" + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}