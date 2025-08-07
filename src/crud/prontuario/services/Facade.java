package crud.prontuario.services;

import java.util.List;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.IConnection;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

/**
 * Facade para simplificar a interação com o subsistema de acesso a dados (DAOs).
 * Fornece um ponto de entrada único para operações relacionadas a Pacientes e Exames,
 * escondendo a complexidade de lidar com múltiplos DAOs.
 * @author Gemini
 */
public class Facade {

    private final PacienteDAO pacienteDAO;
    private final ExameDAO exameDAO;

    /**
     * Construtor que inicializa a Facade e os DAOs necessários.
     * @param connection A instância da conexão com o banco de dados.
     */
    public Facade(IConnection connection) {
        this.pacienteDAO = new PacienteDAO(connection);
        this.exameDAO = new ExameDAO(connection);
    }

    // ========== MÉTODOS PARA PACIENTE ==========

    /**
     * Cadastra um novo paciente no banco de dados.
     * @param paciente O objeto Paciente a ser criado.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void cadastrarPaciente(Paciente paciente) throws DAOException {
        pacienteDAO.create(paciente);
    }

    /**
     * Atualiza os dados de um paciente existente.
     * @param paciente O objeto Paciente com os dados atualizados.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void atualizarPaciente(Paciente paciente) throws DAOException {
        pacienteDAO.update(paciente);
    }

    /**
     * Deleta um paciente e todos os seus exames associados.
     * @param paciente O objeto Paciente a ser deletado.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void deletarPaciente(Paciente paciente) throws DAOException {
        pacienteDAO.delete(paciente);
    }

    /**
     * Busca um paciente pelo seu ID. Os exames do paciente são carregados junto.
     * @param id O ID do paciente.
     * @return O objeto Paciente encontrado, ou null se não existir.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public Paciente buscarPacientePorId(Long id) throws DAOException {
        return pacienteDAO.findById(id);
    }

    /**
     * Busca um paciente pelo seu CPF. Os exames do paciente são carregados junto.
     * @param cpf O CPF do paciente.
     * @return O objeto Paciente encontrado, ou null se não existir.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public Paciente buscarPacientePorCPF(String cpf) throws DAOException {
        return pacienteDAO.findByCPF(cpf);
    }

    /**
     * Lista todos os pacientes cadastrados.
     * @return Uma lista de todos os pacientes.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public List<Paciente> listarTodosPacientes() throws DAOException {
        return pacienteDAO.findAll();
    }

    // ========== MÉTODOS PARA EXAME ==========

    /**
     * Agenda (cria) um novo exame para um paciente.
     * @param exame O objeto Exame a ser criado. O paciente associado já deve existir.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void agendarExame(Exame exame) throws DAOException {
        exameDAO.create(exame);
    }

    /**
     * Atualiza os dados de um exame existente.
     * @param exame O objeto Exame com os dados atualizados.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void atualizarExame(Exame exame) throws DAOException {
        exameDAO.update(exame);
    }

    /**
     * Deleta um exame específico do banco de dados.
     * @param exame O objeto Exame a ser deletado.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public void deletarExame(Exame exame) throws DAOException {
        exameDAO.delete(exame);
    }
    
    /**
     * Lista todos os exames de todos os pacientes.
     * @return Uma lista de todos os exames.
     * @throws DAOException Se ocorrer um erro durante a operação no banco de dados.
     */
    public List<Exame> listarTodosExames() throws DAOException {
        return exameDAO.findAll();
    }
    
    
//    public Exame findById(Long id)
    public Exame buscarExamePorId(Long id) {
    	return exameDAO.findById(id);
    }
    
    public List<Exame> buscarExamesPorNomePaciente(String nomePaciente) throws DAOException {
        return exameDAO.findByPacienteName(nomePaciente);
    }
}