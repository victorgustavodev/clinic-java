package crud.prontuario.view.paciente;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;
import crud.prontuario.exception.DAOException;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class PacienteEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Componentes da Interface ---
    private JTextField nomeField;
    private JTextField cpfField;
    private JFormattedTextField dataNascimentoField;

    // --- Lógica de Negócio ---
    private final IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
    private final Paciente pacienteParaEditar;

    // NOVO: Variáveis para armazenar os dados originais do paciente
    private String nomeOriginal;
    private String cpfOriginal;
    private LocalDate dataNascimentoOriginal;

    public PacienteEditDialog(Frame parent) {
        super(parent, "Editar Paciente", true);
        
        
        Paciente paciente = new Paciente();
        
		this.pacienteParaEditar = paciente;

        // NOVO: Armazena os dados originais assim que a janela é criada
        if (paciente != null) {
            this.nomeOriginal = paciente.getNome();
            this.cpfOriginal = paciente.getCpf();
            this.dataNascimentoOriginal = paciente.getDataDeNascimento();
        }

        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // --- Componentes ---
        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("CPF (XXX.XXX.XXX-XX):"));
        cpfField = new JTextField();
        add(cpfField);

        add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            dataNascimentoField = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            dataNascimentoField = new JFormattedTextField();
        }
        add(dataNascimentoField);

        JButton btnSalvar = new JButton("Salvar Alterações");
        JButton btnCancelar = new JButton("Cancelar");

        add(btnSalvar);
        add(btnCancelar);
        
        popularCampos();

        // --- Eventos ---
        btnSalvar.addActionListener(e -> atualizarPaciente());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void popularCampos() {
        if (pacienteParaEditar != null) {
            nomeField.setText(pacienteParaEditar.getNome());
            cpfField.setText(pacienteParaEditar.getCpf());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (pacienteParaEditar.getDataDeNascimento() != null) {
                dataNascimentoField.setText(pacienteParaEditar.getDataDeNascimento().format(formatter));
            }
        }
    }

    /**
     * NOVO: Método de atualização com a lógica de comparação.
     * Este método reflete o comportamento do seu código de console.
     */
    private void atualizarPaciente() {
        // 1. Pega os dados dos campos de texto (os dados novos)
        String nomeNovo = nomeField.getText().trim();
        String cpfNovo = cpfField.getText().trim();
        String nascimentoStrNovo = dataNascimentoField.getText().trim();

        // 2. Validações básicas de preenchimento
        if (nomeNovo.isEmpty() || cpfNovo.isEmpty() || nascimentoStrNovo.contains("_")) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos corretamente.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cpfNovo.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Use o formato XXX.XXX.XXX-XX.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
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

        // 3. Compara os dados novos com os originais para ver se algo mudou
        boolean nomeMudou = !Objects.equals(nomeNovo, this.nomeOriginal);
        boolean cpfMudou = !Objects.equals(cpfNovo, this.cpfOriginal);
        boolean dataMudou = !Objects.equals(dataNascimentoNova, this.dataNascimentoOriginal);

        boolean algumaCoisaMudou = nomeMudou || cpfMudou || dataMudou;

        // 4. Se nada mudou, informa o usuário e não faz nada no banco
        if (!algumaCoisaMudou) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração foi detectada.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        // 5. Se houve mudanças, atualiza o objeto 'Paciente'
        pacienteParaEditar.setNome(nomeNovo);
        pacienteParaEditar.setCpf(cpfNovo);
        pacienteParaEditar.setDataDeNascimento(dataNascimentoNova);

        // 6. Tenta persistir a atualização no banco de dados
        try {
            pacienteDao.update(pacienteParaEditar);
            JOptionPane.showMessageDialog(this, "✅ Paciente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a janela após salvar
        } catch (DAOException e) {
            // Exibe a mensagem de erro vinda da sua exceção customizada
            JOptionPane.showMessageDialog(this, "Não foi possível atualizar o paciente.\n" + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}