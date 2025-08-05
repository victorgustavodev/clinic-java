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

    // ALTERADO: Nomes de variáveis seguindo a convenção Java (camelCase)
	private JTextField nomeField;
    private JFormattedTextField cpfField; // ALTERADO: de JTextField para JFormattedTextField
    private JFormattedTextField dataDeNascimentoField;

    IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
    
    public PacienteCreateDialog(Frame parent) {
        super(parent, "Cadastrar Paciente", true);
        setLayout(new GridLayout(5, 2, 10, 10)); // Layout ajustado para menos linhas
        setSize(500, 250); // Tamanho ajustado para o conteúdo
        setLocationRelativeTo(parent);

        // Componentes
        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("CPF (XXX.XXX.XXX-XX):"));
        // NOVO: Bloco try-catch para criar o JFormattedTextField do CPF com a máscara
        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(mascaraCpf);
        } catch (ParseException e) {
            cpfField = new JFormattedTextField(); // Fallback
            e.printStackTrace();
        }
        add(cpfField);

        add(new JLabel("Data de Nascimento (DD/MM/AAAA):"));
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            dataDeNascimentoField = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            dataDeNascimentoField = new JFormattedTextField(); // Fallback
            e.printStackTrace();
        }
        add(dataDeNascimentoField);

        JButton btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        JButton btnLimpar = new JButton("Limpar");
        add(btnLimpar);
        
        // Botão Sair adicionado para fechar a janela
        JButton btnSair = new JButton("Sair");
        add(btnSair);
        
        // Painel vazio para preencher o layout se necessário
        add(new JLabel("")); 


        // Eventos
        btnSalvar.addActionListener(e -> salvarPaciente());
        btnLimpar.addActionListener(e -> limparCampos());
        btnSair.addActionListener(e -> dispose());
    }

    private void salvarPaciente() {
        String nome = nomeField.getText().trim();
        // getText() já retorna a string com a máscara
        String cpf = cpfField.getText();
        String nascimentoStr = dataDeNascimentoField.getText();

        // ALTERADO: Validação unificada para checar se campos estão preenchidos (sem o '_')
        if (nome.isEmpty() || cpf.contains("_") || nascimentoStr.contains("_")) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos completamente.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // REMOVIDO: A validação com regex se torna redundante, pois a máscara já força o formato.
        // A checagem por "_" já garante que o campo foi todo preenchido.

        LocalDate dataNascimento;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimento = LocalDate.parse(nascimentoStr, formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato DD/MM/AAAA.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Criando o paciente com os dados validados
        try {
            Paciente paciente = new Paciente(nome, cpf, dataNascimento);
            if(validar(cpf)) {
            	pacienteDao.create(paciente);
            	JOptionPane.showMessageDialog(this, "✅ Paciente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
            	JOptionPane.showMessageDialog(this, "Não foi possivel salvar o paciente, verifique o CPF!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }            
            dispose(); // Fecha a janela após salvar
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao salvar o paciente:\n" + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        cpfField.setValue(null); // .setValue(null) é a forma correta de limpar um JFormattedTextField
        dataDeNascimentoField.setValue(null);
    }

        public static boolean validar(String cpf) {
            // Remove caracteres não numéricos (pontos, traços)
            String cpfLimpo = cpf.replaceAll("[^\\d]", "");

            // 1. Verifica se tem 11 dígitos
            if (cpfLimpo.length() != 11) {
                return false;
            }

            // 2. Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
            if (cpfLimpo.matches("(\\d)\\1{10}")) {
                return false;
            }

            try {
                // 3. Cálculo do primeiro dígito verificador
                int soma = 0;
                for (int i = 0; i < 9; i++) {
                    soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (10 - i);
                }
                int resto = soma % 11;
                int digitoVerificador1 = (resto < 2) ? 0 : 11 - resto;

                // 4. Verifica o primeiro dígito
                if (digitoVerificador1 != Integer.parseInt(String.valueOf(cpfLimpo.charAt(9)))) {
                    return false;
                }

                // 5. Cálculo do segundo dígito verificador
                soma = 0;
                for (int i = 0; i < 10; i++) {
                    soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (11 - i);
                }
                resto = soma % 11;
                int digitoVerificador2 = (resto < 2) ? 0 : 11 - resto;
                
                // 6. Verifica o segundo dígito e retorna o resultado final
                return digitoVerificador2 == Integer.parseInt(String.valueOf(cpfLimpo.charAt(10)));

            } catch (NumberFormatException e) {
                // Se ocorrer um erro na conversão, o CPF é inválido
                return false;
            }
        
    }

}