package crud.prontuario.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class Application {

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
		IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

		Scanner sc = new Scanner(System.in);

		int option = 0;

		boolean sair = true;

		while (sair) {

			System.out.println("\n============================\n");
			System.out.println(
					"Olá, seja bem vindo ao nosso sistema de gerenciamento clinico! \nSelecione qual das opções abaixo deseja: \n\n1 - Gerenciar pacientes \n2 - Gerenciar Exames");
			System.out.print("\n---> ");
			option = sc.nextInt();

			switch (option) {
			case 1:
				System.out.println("\n============================\n");
				System.out.println(
						"Escolha uma das opções abaixo: \n1 - Adicionar Paciente \n2 - Listar Pacientes \n3 - Editar Paciente \n4 - Excluir Paciente \n0 - Sair");
				System.out.print("\n--->: ");
				option = sc.nextInt();

				switch (option) {
				// OK
				case 1:
					sc.nextLine();
					System.out.println("\n============================\n");
					System.out.print("Insira o nome do paciente: ");

					String nome = sc.nextLine();

					System.out.print("Insira o CPF do paciente: ");

					String cpf = sc.nextLine();

					Paciente p = new Paciente(nome, cpf);

					pacienteDao.create(p);

					System.out.println("Paciente adicionado com sucesso!");
					break;

				// OK
				case 2:
					sc.nextLine();
					System.out.println("\n============================\n");

					for (Paciente paciente : pacienteDao.findAll()) {
						System.out.println(paciente);
					}

					break;

				case 3:
					sc.nextLine();

					System.out.print("Digite o CPF do usuário que deseja atualizar: ");

					String cpfEditarUsuario = sc.nextLine();

					Paciente paciente = null;

					paciente = pacienteDao.findByCPF(cpfEditarUsuario);

					if (paciente != null) {
						System.out.println("Você selecionou o paciente: " + paciente + " para editar.");

						System.out.println("O que deseja editar?");
						System.out.println("1. Nome");
						System.out.println("2. CPF");
						System.out.println("3. Nome e CPF");
						System.out.print("Escolha uma opção: ");
						int editOpcao = sc.nextInt();
						sc.nextLine();

						boolean atualizadoComSucesso = false;

						while (!atualizadoComSucesso && (editOpcao >= 1 && editOpcao <= 3)) {
							switch (editOpcao) {
							case 1 -> {
								System.out.print("Novo nome: ");
								paciente.setNome(sc.nextLine());
							}
							case 2 -> {
								System.out.print("Novo CPF: ");
								paciente.setCpf(sc.nextLine());
							}
							case 3 -> {
								System.out.print("Novo nome: ");
								paciente.setNome(sc.nextLine());
								System.out.print("Novo CPF: ");
								paciente.setCpf(sc.nextLine());
							}
							default -> {
								System.out.println("Opção inválida. Nenhuma alteração feita.");
								break;
							}
							}

							pacienteDao.update(paciente);
							System.out.println("Paciente atualizado com sucesso!");
							atualizadoComSucesso = true;
						}
					} else {
						System.out.println("Paciente não encontrado.");
					}

				case 4:
					sc.nextLine();
					System.out.println("\n============================\n");
					System.out.print("Inserir qual o CPF do paciente que deseja deletar: ");
					String cpfDeletar = sc.nextLine();

					Paciente p1 = pacienteDao.findByCPF(cpfDeletar);

					System.out.println("Tem certeza que deseja excluir o paciente\n" + p1 + "?");
					System.out.println("S ou N?");
					System.err.print("\n--->");
					if (sc.nextLine() == "S") {
						pacienteDao.delete(p1);
					}

					System.out.println("Paciente excluido com sucesso!");

					break;

				case 0:

					System.out.println("\nSaindo...");
					break;

				default:
					sair = false;
					break;
				}

				break;

			// EXAMES
			case 2:

				System.out.println("\n============================\n");
				System.out.println(
						"Escolha uma das opções abaixo: \n1 - Cadastrar Exames \n2 - Listar Exames \n3 - Editar Exames \n4 - Excluir exames \n0 - Sair ");
				System.out.print("\n---> ");
				option = sc.nextInt();

				switch (option) {

				case 1:
					sc.nextLine();
					System.out.println("\n============================\n");

					System.out.println("Insira o cpf do paciente: ");
					String cpfExamePaciente = sc.nextLine();
					Paciente paciente = pacienteDao.findByCPF(cpfExamePaciente);

					System.out.print("Descrição do exame: ");
					String descricao = sc.nextLine();

					LocalDateTime data = LocalDateTime.now();

					Exame e = new Exame(descricao, data);

					paciente.addExame(e);

					System.out.println("\nExame adicionado com sucesso!");

					break;

				case 2:
					sc.nextLine();

					ArrayList<Exame> exames = new ArrayList<>(exameDao.findAll());

					System.out.println("\n============================\n");

					if (exames.isEmpty()) {
						System.out.println("Não há nenhum exame disponível.");
					} else {
						System.out.println("Lista de exames:");
						for (Exame exame : exames) {
							System.out.println(exame);
						}
					}
					break;

				case 3:
					sc.nextLine();

					System.out.println("Inserir cpf do paciente que deseja editar: ");
					break;

				case 4:
					sc.nextLine();

					System.out.println("Insira o ID do exame que deseja deletar: ");
					System.out.print("---> ");

					Long id = sc.nextLong();
					sc.nextLine();

					Exame exame = exameDao.findById(id);

					if (exame != null) {
						exameDao.delete(exame);
						System.out.println("Exame excluído com sucesso!");
					} else {
						System.out.println("Exame não encontrado com o ID fornecido.");
					}

					break;

				default:
					sair = false;
					break;
				}

				break;

			}
		}

//		dao.update(p);

	}
}
