package crud.prontuario.services;

import java.util.List;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.IConnection;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class Facade {

	private final PacienteDAO pacienteDAO;
	private final ExameDAO exameDAO;

	public Facade(IConnection connection) {
		this.pacienteDAO = new PacienteDAO(connection);
		this.exameDAO = new ExameDAO(connection);
	}

	public void cadastrarPaciente(Paciente paciente) throws DAOException {
		pacienteDAO.create(paciente);
	}

	public void atualizarPaciente(Paciente paciente) throws DAOException {
		pacienteDAO.update(paciente);
	}

	public void deletarPaciente(Paciente paciente) throws DAOException {
		pacienteDAO.delete(paciente);
	}

	public Paciente buscarPacientePorId(Long id) throws DAOException {
		return pacienteDAO.findById(id);
	}

	public Paciente buscarPacientePorCPF(String cpf) throws DAOException {
		return pacienteDAO.findByCPF(cpf);
	}

	public List<Paciente> listarTodosPacientes() throws DAOException {
		return pacienteDAO.findAll();
	}

	public void agendarExame(Exame exame) throws DAOException {
		exameDAO.create(exame);
	}

	public void atualizarExame(Exame exame) throws DAOException {
		exameDAO.update(exame);
	}

	public void deletarExame(Exame exame) throws DAOException {
		exameDAO.delete(exame);
	}

	public List<Exame> listarTodosExames() throws DAOException {
		return exameDAO.findAll();
	}

	public int contarExamesPorPaciente(Long pacienteId) {

		return this.exameDAO.countByPacienteId(pacienteId);
	}

	public Exame buscarExamePorId(Long id) {
		return exameDAO.findById(id);
	}

	public List<Exame> buscarExamesPorNomePaciente(String nomePaciente) throws DAOException {
		return exameDAO.findByPacienteName(nomePaciente);
	}
}