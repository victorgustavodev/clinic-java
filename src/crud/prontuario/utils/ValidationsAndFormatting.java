package crud.prontuario.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public final class ValidationsAndFormatting {

	private static final Pattern NOME_PATTERN = Pattern.compile("^[\\p{L} ]{3,100}$");

	private ValidationsAndFormatting() {
		throw new IllegalStateException("Classe utilitária não deve ser instanciada.");
	}

	public static boolean validarCPF(String cpf) {
		if (cpf == null || cpf.isEmpty()) {
			return false;
		}

		String cpfLimpo = cpf.replaceAll("[^0-9]", "");

		if (cpfLimpo.length() != 11) {
			return false;
		}

		if (cpfLimpo.matches("(\\d)\\1{10}")) {
			return false;
		}

		try {
			int soma = 0;
			for (int i = 0; i < 9; i++) {
				soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (10 - i);
			}
			int resto = soma % 11;
			int digitoVerificador1 = (resto < 2) ? 0 : 11 - resto;

			if (digitoVerificador1 != Integer.parseInt(String.valueOf(cpfLimpo.charAt(9)))) {
				return false;
			}

			soma = 0;
			for (int i = 0; i < 10; i++) {
				soma += Integer.parseInt(String.valueOf(cpfLimpo.charAt(i))) * (11 - i);
			}
			resto = soma % 11;
			int digitoVerificador2 = (resto < 2) ? 0 : 11 - resto;

			return digitoVerificador2 == Integer.parseInt(String.valueOf(cpfLimpo.charAt(10)));

		} catch (NumberFormatException e) {
			return false;
		}
	}

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

	public static boolean validarData(String data) {
		if (data == null || !data.matches("\\d{2}/\\d{2}/\\d{4}")) {
			return false;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

		try {
			LocalDate.parse(data, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public static String formatarData(String data) {
		if (!validarData(data)) {
			throw new IllegalArgumentException("A data fornecida é inválida ou não está no formato DD/MM/AAAA.");
		}
		return data;
	}

	public static boolean validarNome(String nome) {
		if (nome == null || nome.trim().isEmpty()) {
			return false;
		}
		return NOME_PATTERN.matcher(nome).matches();
	}
}
