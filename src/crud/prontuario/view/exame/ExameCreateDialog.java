package crud.prontuario.view.exame;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ExameCreateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JComboBox<Object> pacienteComboBox;
    private JTextField tipoExameField;
    private JFormattedTextField dataExameField;
    private JTextArea descricaoArea;

    // --- Camada de Acesso a Dados (DAO) ---
    IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
    IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

    public ExameCreateDialog(Frame parent) {
        super(parent, "Cadastrar Novo Exame", true);

        // --- Configuração do Layout ---
        // Usar GridBagLayout para mais controle e flexibilidade
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Linha 1: Seleção de Paciente ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Paciente:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Permite que o combobox expanda horizontalmente
        pacienteComboBox = new JComboBox<>();
        add(pacienteComboBox, gbc);

        // --- Linha 2: Tipo de Exame ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // Reseta a expansão
        add(new JLabel("Tipo de Exame:"), gbc);

        gbc.gridx = 1;
        tipoExameField = new JTextField(20);
        add(tipoExameField, gbc);

        // --- Linha 3: Data do Exame ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Data do Exame (DD/MM/AAAA):"), gbc);

        gbc.gridx = 1;
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            dataExameField = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            dataExameField = new JFormattedTextField(); // Fallback sem máscara
        }
        add(dataExameField, gbc);

        // --- Linha 4: Descrição do Exame ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH; // Alinha o label no topo
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 2; // Ocupa duas linhas de altura
        gbc.fill = GridBagConstraints.BOTH; // Preenche altura e largura
        gbc.weighty = 1.0; // Permite que a área de texto expanda verticalmente
        descricaoArea = new JTextArea(5, 20);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        add(new JScrollPane(descricaoArea), gbc); // Adiciona a área de texto a um painel de rolagem

        // --- Linha 5: Painel de Botões ---
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Ocupa a largura de duas colunas
        gbc.gridheight = 1; // Reseta a altura
        gbc.weighty = 0; // Reseta a expansão vertical
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o painel de botões

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnSair = new JButton("Sair");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnSair);
        add(buttonPanel, gbc);

        // --- Ações dos Botões ---
        btnSalvar.addActionListener(e -> salvarExame());
        btnLimpar.addActionListener(e -> limparCampos());
        btnSair.addActionListener(e -> dispose());

        // --- Carregamento de Dados Iniciais ---
        carregarPacientes();

        pack(); // Ajusta o tamanho da janela aos componentes
        setMinimumSize(new Dimension(500, 400)); // Define um tamanho mínimo
        setLocationRelativeTo(parent);
    }

    private void carregarPacientes() {
        // Adiciona um item inicial não selecionável
        pacienteComboBox.addItem("Selecione um paciente...");

        try {
            List<Paciente> pacientes = pacienteDao.findAll();
            for (Paciente paciente : pacientes) {
                pacienteComboBox.addItem(paciente);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar a lista de pacientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        pacienteComboBox.setSelectedIndex(0);
        tipoExameField.setText("");
        dataExameField.setValue(null); // Limpa o JFormattedTextField
        descricaoArea.setText("");
    }

    private void salvarExame() {
        // --- 1. Validação dos Campos ---
        if (pacienteComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um paciente.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoExame = tipoExameField.getText().trim();
        if (tipoExame.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Tipo de Exame' não pode estar vazio.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dataStr = dataExameField.getText();
        if (dataStr.contains("_")) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a data completa.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String descricao = descricaoArea.getText().trim();
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Descrição' não pode estar vazio.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Conversão e Criação do Objeto ---
        Paciente pacienteSelecionado = (Paciente) pacienteComboBox.getSelectedItem();
        LocalDate dataExame;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataExame = LocalDate.parse(dataStr, formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato DD/MM/AAAA.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crie o objeto Exame (ajuste o construtor conforme sua classe Exame)
        Exame novoExame = new Exame();

        // --- 3. Persistência no Banco de Dados ---
        try {
            exameDao.create(novoExame);
            JOptionPane.showMessageDialog(this, "Exame salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a janela após salvar
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o exame no banco de dados: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
