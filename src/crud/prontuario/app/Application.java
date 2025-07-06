package crud.prontuario.app;

import java.sql.SQLException;
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

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
		IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

		Scanner sc = new Scanner(System.in);

		int option = 0;

		boolean sair = true;

		while (sair) {
			
			System.out.println("\n============================\n");
			System.out.println("Olá, seja bem vindo ao nosso sistema de gerenciamento clinico! \nSelecione qual das opções abaixo deseja: \n\n1 - Gerenciar pacientes \n2 - Gerenciar Exames \n0 - SAIR");
			System.out.print("\n---> ");
			option = sc.nextInt();
			
			switch(option) {
			case 1:
				System.out.println("\n============================\n");
				System.out.println("Escolha uma das opções abaixo: \n1 - CADASTRAR PACIENTE \n2 - LISTAR PACIENTES \n3 - EDITAR PACIENTE \n4 - EXCLUIR PACIENTE \n5 - ASSOCIAR EXAME AO PACIENTE \n0 - SAIR");
				System.out.print("\n--->: ");
				option = sc.nextInt();
				
				switch (option) {
				//OK
				case 1:
					sc.nextLine();
					System.out.println("\n============================\n");
					System.out.print("Insira o nome do paciente: ");

					String nome = sc.nextLine();
					
					System.out.print("Insira o CPF do paciente: ");

					String cpf = sc.nextLine();
					
					Paciente p = new Paciente(nome, cpf);
					
					pacienteDao.create(p);
					break;
				
				//OK
				case 2:
					sc.nextLine();
					System.out.println("\n============================\n");

					for (Paciente paciente : pacienteDao.findAll()) {
						System.out.println(paciente);
					}
					
					
					break;
					
					case 3:
						sc.nextLine();
											
						System.out.println("Buscar paciente para editar por:");
		                System.out.println("1. ID");
		                System.out.println("2. CPF");
		                System.out.print("Escolha uma opção: ");
		                int buscaOpcao = sc.nextInt();
		                sc.nextLine();
	
		                Paciente paciente = null;
	
		                if (buscaOpcao == 1) {
		                    System.out.print("Digite o ID do paciente: ");
		                    Long id = sc.nextLong();
		                    sc.nextLine();
		                    paciente = pacienteDao.findById(id);
		                } else if (buscaOpcao == 2) {
		                    System.out.print("Digite o CPF do paciente: ");
		                    String cpfPaciente = sc.nextLine();
		                    paciente = pacienteDao.findByCPF(cpfPaciente);
		                } else {
		                    System.out.println("Opção inválida.");
		                }
	
		                if (paciente != null) {
		                    System.out.println("Você selecionou o paciente: " + paciente.getNome() + " para editar.");
	
		                    System.out.println("O que deseja editar?");
		                    System.out.println("1. Nome");
		                    System.out.println("2. CPF");
		                    System.out.println("3. Ambos");
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
	
		                        try {
		                            pacienteDao.update(paciente);
		                            System.out.println("Paciente atualizado com sucesso!");
		                            atualizadoComSucesso = true;
		                        } catch (SQLException e) {
		                            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("cpf")) {
		                                System.out.println("CPF já existe. Por favor, informe um CPF diferente.");
		                                System.out.print("Novo CPF: ");
		                                paciente.setCpf(sc.nextLine());
		                            } else {
		                                System.out.println("Erro ao atualizar paciente: " + e.getMessage());
		                                break;
		                            }
		                        }
		                    }
		                } else {
		                    System.out.println("Paciente não encontrado.");
		                }
		            } 
			}
					break;
			case 4:
				sc.nextLine();
				System.out.println("\n============================\n");
				System.out.print("Inserir qual o CPF do paciente que deseja deletar: ");
				String cpfDeletar = sc.nextLine();
				
				Paciente p1 = pacienteDao.findByCPF(cpfDeletar);
				
				pacienteDao.delete(p1);
				
				System.out.println(p1 + " -> Excluido com sucesso!");
				
				break;
				
			case 5: 
				sc.nextLine();
				System.out.println("\n============================\n");
				System.out.println("Insira o cpf do paciente que deseja associar o exame");
				String cpfExameAssociado = sc.nextLine();
				
				pacienteDao.findByCPF(cpfExameAssociado);
				
				break;
				
			case 0:
				
				System.out.println("Saindo...");
				break;
			
			default:
				sair = false;
				break;
			}
			
			
			break;
				
				 //OK 
				
			
			case 2:
				
				System.out.println("\n============================\n");
				System.out.println("Escolha uma das opções abaixo: \n1 - CADASTRAR EXAME \n2 - LISTAR EXAMES \n3 - EDITAR EXAME \n4 - EXCLUIR EXAME ");
				System.out.print("\n---> ");
				option = sc.nextInt();
				
				switch (option){
				
				case 1:
				    sc.nextLine();
					System.out.println("\n============================\n");
					
				    System.out.print("Descrição do exame: ");
				    String descricao = sc.nextLine();

				    LocalDateTime data = LocalDateTime.now();
				    
				    System.out.print("Inserir qual o CPF do paciente: ");
					String cpfExameAssociado = sc.nextLine();
					
					Paciente p1 = pacienteDao.findByCPF(cpfExameAssociado);
					
					Exame e = new Exame(descricao, data, p1);

					try {
						exameDao.create(e);
						System.out.println("\nExame criado com sucesso!");
						System.out.println("\n" + e);
					} catch (Exception e2) {
						System.out.println(e2);// TODO: handle exception
					}
					
				    break;
				
				case 2:
					sc.nextLine();
					
					ArrayList<Exame> exames = new ArrayList<Exame>(exameDao.findAll());
					
//					if (exames.size() == 0 ) {
//						System.out.println("\nNão há nenhum exame disponivel!");
//						
//					} else {
						for (Exame exame : exames) {
							System.out.println(exame);
						} 
//					}
				
					break;
					
				case 3: 
					sc.nextLine();
					
					System.out.println("Inserir cpf do paciente que deseja editar: ");
					 break;
					 
				case 4:
					sc.nextLine();
					
					System.out.println("Inserir qual o cpf do paciente que deseja deletar: ");
					
				default:
					sair = false;
					break;
				}
				
				break;
			
		}
//		dao.update(p);

	}
}
