package crud.prontuario.view.paciente;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PacienteCreateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField Nome;
    private JTextField Cpf;
    private JFormattedTextField DataDeNascimento;


    IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());

    
    public PacienteCreateDialog(Frame parent) {
        super(parent, "Cadastrar Paciente", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // Componentes
        add(new JLabel("Nome:"));
        Nome = new JTextField();
        Nome.setMaximumSize(new Dimension(300, 30));
        add(Nome);

        add(new JLabel("CPF (XXX.XXX.XXX-XX):"));
        Cpf = new JTextField();
        add(Cpf);

        add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            DataDeNascimento = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            DataDeNascimento = new JFormattedTextField(); // fallback
        }
        add(DataDeNascimento);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnSair = new JButton("Sair");

        add(btnSalvar);
        add(btnLimpar);
        add(btnSair);

        // Eventos
        btnSalvar.addActionListener(e -> salvarPaciente());
        btnLimpar.addActionListener(e -> limparCampos());
        btnSair.addActionListener(e -> dispose());
    }

    private void salvarPaciente() {
        String nome = Nome.getText().trim();
        String cpf = Cpf.getText().trim();
        String nascimentoStr = DataDeNascimento.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || nascimentoStr.contains("_")) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos corretamente.");
            return;
        }

        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Use o formato XXX.XXX.XXX-XX.");
            return;
        }

        // Converter a string para LocalDate
        LocalDate dataNascimento;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimento = LocalDate.parse(nascimentoStr, formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato DD/MM/AAAA.");
            return;
        }

        Paciente paciente = new Paciente(nome, cpf, dataNascimento);
        pacienteDao.create(paciente);

        JOptionPane.showMessageDialog(this, "Paciente salvo com sucesso.");
        dispose();
    }

    private void limparCampos() {
        Nome.setText("");
        Cpf.setText("");
        DataDeNascimento.setText("");
    }
}
