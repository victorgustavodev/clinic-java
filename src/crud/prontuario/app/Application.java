package crud.prontuario.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class Application {

	
	public static void main(String[] args) {

		IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
		IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

		Scanner sc = new Scanner(System.in);

		int option = 0;

		boolean sair = true;

		while (sair) {

			System.out.println("\n============================\n");
			System.out.println(
					"Olá, seja bem vindo ao nosso sistema de gerenciamento clinico! \nSelecione qual das opções abaixo deseja:"
					+ " \n\n1 - Gerenciar pacientes \n2 - Gerenciar Exames \n");
			try {
		        option = sc.nextInt();
		        sc.nextLine(); // consome o \n restante
		        
		    } catch (InputMismatchException e) {
		        System.out.println("Entrada inválida. Digite um número.");
		        sc.nextLine(); // limpa o buffer da entrada errada
		    }
			

			switch (option) {
			case 1:
				System.out.println("\n============================\n");
				System.out.println("Escolha uma das opções abaixo: \n");
				System.out.println("1 - Adicionar Paciente \n2 - Listar Pacientes \n3 - Editar Paciente \n4 - Excluir Paciente \n0 - Voltar");
				
				try {
					System.out.print("\n-> ");
					option = sc.nextInt();
	            } catch (InputMismatchException e) {
	                System.out.println("\nEntrada inválida! Insira uma opção válida!");
	                continue; 
	            }

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

					try {
						pacienteDao.create(p);
						System.out.println("\nPaciente adicionado com sucesso!");
					} catch (Exception e) {
						throw new DAOException("Ocorreu um erro ao cadastrar o paciente!" + e);
					}
					
					break;

				// OK
				case 2:
					sc.nextLine();
					System.out.println("\n============================\n");

					try {
						for (Paciente paciente : pacienteDao.findAll()) {
							System.out.println(paciente);
						}
							
					} catch (Exception e) {
						throw new DAOException("Ocorreu um erro ao exibir os pacientes!" + e);
					}
					
					
					
					break;

				case 3:
					int editOpcao;
					sc.nextLine();

					System.out.print("\nDigite o CPF do usuário que deseja atualizar: \n");

					
					System.out.print("\n-> ");
					
					String cpfEditarUsuario = sc.nextLine();

					Paciente paciente = null;

					paciente = pacienteDao.findByCPF(cpfEditarUsuario);

					if (paciente != null) {
						System.out.println("\nVocê selecionou o paciente: " + paciente + " para editar.\n");

						System.out.println("O que deseja editar?");
						System.out.println("1. Nome");
						System.out.println("2. CPF");
						System.out.println("3. Nome e CPF");
						
						
						try {
							System.out.print("\n-> ");
							editOpcao = sc.nextInt();
							sc.nextLine();
			            } catch (InputMismatchException e) {
			                System.out.println("\nEntrada inválida! Insira uma opção válida!");
			                sc.nextLine();
			                continue; 
			            }

						boolean atualizadoComSucesso = false;

						while (!atualizadoComSucesso && (editOpcao >= 1 && editOpcao <= 3)) {
							switch (editOpcao) {
							case 1 -> {
								System.out.print("\nNovo nome: ");
								paciente.setNome(sc.nextLine());
							}
							case 2 -> {
								System.out.print("\nNovo CPF: ");
								paciente.setCpf(sc.nextLine());
							}
							case 3 -> {
								System.out.print("\nNovo nome: ");
								paciente.setNome(sc.nextLine());
								System.out.print("\nNovo CPF: ");
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
					
					String cpfDeletar;
					
					sc.nextLine();
					System.out.println("\n============================\n");
					System.out.print("Inserir qual o CPF do paciente que deseja deletar: ");
					System.out.print("-> ");
					cpfDeletar = sc.nextLine();
					
					System.out.println("Tem certeza que deseja excluir o paciente\n" + pacienteDao.findByCPF(cpfDeletar).getNome() + "?");
					System.out.println("S ou N?");
					System.out.print("-> ");
					String opt = sc.nextLine();
										
					if (opt.equalsIgnoreCase("S")) {
						pacienteDao.delete(pacienteDao.findByCPF(cpfDeletar));
						System.out.println("Paciente excluido com sucesso!");
					}

					break;

				case 0:
					System.out.println("Voltando ao menu principal...");
		            break;

				default:
			    	System.out.println("\nOpção inválida. Tente novamente!");
			    	sair = false;
			    	break;
				}

				break;

			// EXAMES
				// EXAMES
			case 2:

				int exameOption;
				
			    System.out.println("\n============================\n");
			    System.out.println("Escolha uma das opções abaixo:");
			    System.out.println("1 - Cadastrar Exames");
			    System.out.println("2 - Listar Exames");
			    System.out.println("3 - Editar Exames");
			    System.out.println("4 - Excluir Exames");
			    System.out.println("0 - Voltar");
			    
			    
			    try {
			    	System.out.print("-> ");
			    	exameOption = sc.nextInt();
				    sc.nextLine();
	            } catch (InputMismatchException e) {
	                System.out.println("\nEntrada inválida! Insira uma opção válida!");
	                sc.nextLine();
	                continue; 
	            }
					    

			    switch (exameOption) {

			        case 1 :
			            System.out.println("\n============================\n");

			            System.out.print("Insira o CPF do paciente: ");
			            String cpfExamePaciente = sc.nextLine();
			            Paciente paciente = pacienteDao.findByCPF(cpfExamePaciente);

			            if (paciente == null) {
			                System.out.println("Paciente não encontrado com o CPF fornecido.");
			                break;
			            }

			            System.out.print("Descrição do exame: ");
			            String descricao = sc.nextLine();
			            LocalDateTime data = LocalDateTime.now();

			            Exame exame = new Exame(descricao, data, paciente);
			            exameDao.create(exame);

			            System.out.println("\nExame adicionado com sucesso!");
			            break;
			        

			        case 2 :
			            ArrayList<Exame> exames = new ArrayList<>(exameDao.findAll());

			            System.out.println("\n============================\n");

			            if (exames.isEmpty()) {
			                System.out.println("Não há nenhum exame disponível.");
			            } else {
			                System.out.println("Lista de exames:");
			                for (Exame ex : exames) {
			                    System.out.println(ex);
			                }
			            }
			            
			            break;
			        

			        case 3 :
			            System.out.print("Insira o ID do exame que deseja alterar: ");
			            Long idExame = sc.nextLong();
			            sc.nextLine();

			            Exame exameUpdate = exameDao.findById(idExame);

			            if (exameUpdate == null) {
			                System.out.println("Exame com ID " + idExame + " não encontrado.");
			                break;
			            }

			            System.out.println("Descrição atual: " + exameUpdate.getDescricao());
			            System.out.print("Nova descrição: ");
			            String novaDescricao = sc.nextLine();

			            exameUpdate.setDescricao(novaDescricao);
			            exameUpdate.setData(LocalDateTime.now());

			            exameDao.update(exameUpdate);
			            System.out.println("Exame atualizado com sucesso.");
			            break;
			        

			        case 4:
			            System.out.print("Insira o ID do exame que deseja deletar: ");
			            Long id = sc.nextLong();
			            sc.nextLine();

			            Exame exame1 = exameDao.findById(id);

			            if (exame1 != null) {
			                exameDao.delete(exame1);
			                System.out.println("Exame excluído com sucesso!");
			            } else {
			                System.out.println("Exame não encontrado com o ID fornecido.");
			            }
			            break;
			        

			        case 0:
			            System.out.println("Voltando ao menu principal...");
			            break;
			        }
			        
			        
	
		    

			default:
		    	
		    	break;
		    
		    	
		}}
		
		

//		dao.update(p);

	}
}
