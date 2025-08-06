package crud.prontuario.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Classe utilitária final que agrupa métodos estáticos para validação e
 * formatação de dados comuns, como CPF, datas e nomes. Esta classe não pode ser
 * instanciada.
 *
 * @author Gemini
 * @version 1.0
 *
 *
 */
public final class ValidationsAndFormatting {

    // Regex para validar nomes (permite letras, acentos e espaços)
    private static final Pattern NOME_PATTERN = Pattern.compile("^[\\p{L} ]{3,100}$");

    /**
     * Construtor privado para impedir a instanciação da classe utilitária.
     * Lança IllegalStateException se for tentado instanciar via reflection.
     */
    private ValidationsAndFormatting() {
        throw new IllegalStateException("Classe utilitária não deve ser instanciada.");
    }

    /**
     * Valida um número de CPF brasileiro. O método verifica o formato (com ou
     * sem máscara), a quantidade de dígitos, se todos os dígitos são iguais e
     * calcula os dois dígitos verificadores.
     *
     * @param cpf O CPF a ser validado, pode conter máscara (XXX.XXX.XXX-XX).
     * @return {@code true} se o CPF for válido, {@code false} caso contrário.
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        // Remove caracteres não numéricos
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        // 1. Verifica se tem 11 dígitos
        if (cpfLimpo.length() != 11) {
            return false;
        }

        // 2. Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // 3. Cálculo do 1º dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (10 - i);
            }
            int resto = soma % 11;
            int digitoVerificador1 = (resto < 2) ? 0 : 11 - resto;

            // Compara o dígito calculado com o dígito do CPF
            if (digitoVerificador1 != Integer.parseInt(String.valueOf(cpfLimpo.charAt(9)))) {
                return false;
            }

            // 4. Cálculo do 2º dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (11 - i);
            }
            resto = soma % 11;
            int digitoVerificador2 = (resto < 2) ? 0 : 11 - resto;

            // Compara o dígito calculado com o dígito do CPF
            return digitoVerificador2 == Integer.parseInt(String.valueOf(cpfLimpo.charAt(10)));

        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Formata uma string de CPF para o padrão XXX.XXX.XXX-XX.
     *
     * @param cpf O CPF com 11 dígitos a ser formatado.
     * @return A string do CPF formatada.
     * @throws IllegalArgumentException se o CPF fornecido não contiver
     * exatamente 11 dígitos após a limpeza de caracteres não numéricos.
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF não pode ser nulo.");
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos para formatação.");
        }

        return cpfLimpo.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Valida uma data no formato "DD/MM/AAAA". A validação considera anos
     * bissextos e a quantidade de dias em cada mês.
     *
     * @param data A string da data no formato "DD/MM/AAAA".
     * @return {@code true} se a data for válida, {@code false} caso contrário.
     */
    public static boolean validarData(String data) {
        if (data == null || !data.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        // Usar ResolverStyle.STRICT para garantir que datas inválidas (ex: 31/04/2023) não sejam aceitas.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate.parse(data, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Formata uma string de data para o formato "DD/MM/AAAA". O método primeiro
     * valida a data para garantir sua integridade.
     *
     * @param data A string da data a ser formatada (esperada no formato
     * DD/MM/AAAA).
     * @return A string da data formatada "DD/MM/AAAA".
     * @throws IllegalArgumentException se a data for inválida.
     */
    public static String formatarData(String data) {
        if (!validarData(data)) {
            throw new IllegalArgumentException("A data fornecida é inválida ou não está no formato DD/MM/AAAA.");
        }
        // Se a data já é válida no formato, apenas a retorna.
        return data;
    }

    /**
     * Valida um nome de pessoa. O nome deve conter entre 3 e 100 caracteres,
     * permitindo apenas letras (incluindo acentuadas) e espaços.
     *
     * @param nome O nome a ser validado.
     * @return {@code true} se o nome for válido, {@code false} caso contrário.
     */
    public static boolean validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        return NOME_PATTERN.matcher(nome).matches();
    }
}
