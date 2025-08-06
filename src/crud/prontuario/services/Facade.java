package crud.prontuario.services;

import java.util.ArrayList;
import java.util.List;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.IEntityDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.exception.DAOException;
import crud.prontuario.exception.BusinessRuleException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class Facade {

	private IEntityDAO<Paciente> pacienteDao = new PacienteDAO(new DatabaseConnectionMySQL());
	private IEntityDAO<Exame> exameDao = new ExameDAO(new DatabaseConnectionMySQL());

	// Pacientes
	public void criarPaciente(Paciente p) throws BusinessRuleException, DAOException {
		
		if (pacienteDao.findByCPF(p.getCpf()) != null) {
			throw new BusinessRuleException("O CPF informado já está cadastrado no sistema.");
		}

		pacienteDao.create(p);
	}
	
	public void deletarPaciente(Paciente p) throws DAOException {
		pacienteDao.delete(p);
	}

	public void editarPaciente(Paciente p) throws DAOException {
		pacienteDao.update(p);
	}

	public List<Paciente> listarPacientes() throws DAOException {	
		List<Paciente> pacientes = new ArrayList<>();
		return pacientes;
	}

	public Paciente buscarPacientePorId(Long id) throws DAOException {
		return pacienteDao.findById(id);
	}

	public Paciente buscarPacientePorCPF(String cpf) throws DAOException {
		return pacienteDao.findByCPF(cpf);
	}

	 // Exames
	
	public void criarExame(Exame e) throws DAOException {
		exameDao.create(e);
	}
	
	public void deletarExame(Exame e) {
		exameDao.delete(e);
	}

	public void editarExame(Exame e) {
		exameDao.update(e);
	}
	
	public Exame buscarExamePorId(Long id) throws DAOException  {
		return exameDao.findById(id);
	}
	
	public List<Exame> buscarExamePorNome(String nome) {
		return exameDao.findByPacienteName(nome);
	}
	
	public List<Exame> listarExames() throws DAOException {
		List<Exame> exames = new ArrayList<>();
		return exames;
	}
	

}