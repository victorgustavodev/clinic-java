package crud.prontuario.view.exame;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.IConnection;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Janela de diálogo para editar informações de exames existentes.
 * Exibe uma tabela com todos os exames e um formulário para edição.
 */
public class ExameEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JTable tabelaExames;
    private DefaultTableModel modeloTabela;
    private JComboBox<Paciente> pacienteComboBox;
    private JFormattedTextField dataExameField;
    private JTextArea descricaoArea;
    private JButton btnAtualizar;

    // --- Lógica de Negócio ---
    private final ExameDAO exameDao;
    private final PacienteDAO pacienteDao;
    private List<Exame> listaExames; // Cache local da lista de exames para performance
    private Exame exameSelecionado; // Objeto do exame atualmente selecionado na tabela

    public ExameEditDialog(Frame parent) {
        super(parent, "Edição de Exame", true);

        // Inicialização dos DAOs para acesso ao banco de dados
        IConnection dbConnection = new DatabaseConnectionMySQL();
        this.exameDao = new ExameDAO(dbConnection);
        this.pacienteDao = new PacienteDAO(dbConnection);

        // Configuração do layout principal da janela
        setLayout(new BorderLayout(10, 10));
        setSize(850, 700);
        setLocationRelativeTo(parent);

        // --- Montagem da UI ---
        // Adiciona a tabela de exames na parte superior (norte)
        add(criarPainelTabela(), BorderLayout.NORTH);
        // Adiciona o formulário de edição no centro
        add(criarPainelFormulario(), BorderLayout.CENTER);
        // Adiciona os botões de ação na parte inferior (sul)
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        // --- Carregamento de Dados Iniciais ---
        carregarPacientesNoComboBox();
        carregarExamesNaTabela();

        // --- Adição de Eventos ---
        // Listener para detectar a seleção de uma linha na tabela
        tabelaExames.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCamposComSelecao();
            }
        });
    }

    /**
     * Cria e configura o painel que contém a JTable de exames.
     */
    private JScrollPane criarPainelTabela() {
        modeloTabela = new DefaultTableModel(new Object[]{"ID Exame", "Descrição", "Data do Exame", "Paciente"}, 0) {

			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição direta na tabela
            }
        };
        tabelaExames = new JTable(modeloTabela);
        tabelaExames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        
        JScrollPane scrollPane = new JScrollPane(tabelaExames);
        scrollPane.setPreferredSize(new Dimension(800, 250)); // Define uma altura preferencial para a tabela
        return scrollPane;
    }

    /**
     * Cria e configura o painel do formulário com os campos para edição.
     * Usa GridBagLayout para um alinhamento flexível e preciso.
     */
    private JPanel criarPainelFormulario() {
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados para Edição"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Linha 1: Descrição ---
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

        // --- Linha 2: Data do Exame ---
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

        // --- Linha 3: Paciente ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        painelFormulario.add(new JLabel("Paciente:"), gbc);

        gbc.gridx = 1;
        pacienteComboBox = new JComboBox<>();
        painelFormulario.add(pacienteComboBox, gbc);

        return painelFormulario;
    }
    
    /**
     * Cria e configura o painel com os botões "Atualizar" e "Sair".
     */
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

    /**
     * Busca todos os pacientes no banco de dados e popula o JComboBox.
     */
    private void carregarPacientesNoComboBox() {
        try {
            List<Paciente> pacientes = pacienteDao.findAll();
            DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();
            for (Paciente p : pacientes) {
                model.addElement(p);
            }
            pacienteComboBox.setModel(model);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca todos os exames no banco de dados e popula a JTable.
     */
    private void carregarExamesNaTabela() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de preencher
        try {
            this.listaExames = exameDao.findAll(); // Armazena a lista para uso posterior
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Exame ex : listaExames) {
                String nomePaciente = (ex.getPaciente() != null) ? ex.getPaciente().getNome() : "N/A";
                String dataFormatada = (ex.getDataExame() != null) ? ex.getDataExame().format(formatter) : "";
                modeloTabela.addRow(new Object[]{ex.getId(), ex.getDescricao(), dataFormatada, nomePaciente});
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar exames: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Chamado quando uma linha da tabela é selecionada.
     * Preenche o formulário com os dados do exame selecionado.
     */
    private void preencherCamposComSelecao() {
        int selectedRow = tabelaExames.getSelectedRow();
        if (selectedRow != -1) { // Verifica se uma linha foi realmente selecionada
            exameSelecionado = listaExames.get(selectedRow);

            descricaoArea.setText(exameSelecionado.getDescricao());
            
            if (exameSelecionado.getDataExame() != null) {
                dataExameField.setText(exameSelecionado.getDataExame().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                dataExameField.setText("");
            }

            // Seleciona o paciente correto no ComboBox.
            // Isso funciona graças aos métodos equals() e hashCode() na classe Paciente.
            pacienteComboBox.setSelectedItem(exameSelecionado.getPaciente());
        } else {
            // Limpa os campos se a seleção for removida
            exameSelecionado = null;
            descricaoArea.setText("");
            dataExameField.setValue(null);
            pacienteComboBox.setSelectedIndex(-1);
        }
    }

    /**
     * Chamado pelo botão "Atualizar".
     * Valida os dados do formulário e, se válidos, persiste as alterações no banco de dados.
     */
    private void atualizarExame() {
        if (exameSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um exame na tabela para editar.", "Nenhum Exame Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Coleta e valida os dados do formulário
        String descricaoNova = descricaoArea.getText().trim();
        String dataStrNova = dataExameField.getText();
        Paciente pacienteNovo = (Paciente) pacienteComboBox.getSelectedItem();

        if (descricaoNova.isEmpty() || dataStrNova.contains("_") || pacienteNovo == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos corretamente.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate dataExameNova;
        try {
            dataExameNova = LocalDate.parse(dataStrNova, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data do exame inválida. Use o formato DD/MM/AAAA.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Atualiza o objeto 'Exame' em memória
        exameSelecionado.setDescricao(descricaoNova);
        exameSelecionado.setDataExame(dataExameNova);
        exameSelecionado.setPaciente(pacienteNovo);

        // 3. Persiste a atualização no banco de dados
        try {
            exameDao.update(exameSelecionado);
            JOptionPane.showMessageDialog(this, "✅ Exame atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            int selectedRow = tabelaExames.getSelectedRow(); // Salva a linha selecionada
            
            carregarExamesNaTabela(); // Atualiza a tabela para refletir a mudança

            // Restaura a seleção da linha, se possível
            if(selectedRow != -1 && selectedRow < tabelaExames.getRowCount()){
                tabelaExames.setRowSelectionInterval(selectedRow, selectedRow);
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o exame.\n" + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}