package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class PacienteDAO implements IEntityDAO<Paciente> {

	private IConnection conn;

	public PacienteDAO(IConnection connection) {
		this.conn = connection;
	}

	@Override
	public void create(Paciente t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("INSERT INTO PACIENTES (nome, cpf) VALUES (?, ?);");
			pstm.setString(1, t.getNome());
			pstm.setString(2, t.getCpf());
			pstm.executeUpdate();
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel criar o paciente. \nErro: " + e);
		}
	}

	@Override
	public Paciente findById(Long id) {
		Paciente p = null;
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES WHERE ID = ?;");
			pstm.setLong(1, id);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				p = new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf"));
				carregarExames(p);
			}
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel buscar o paciente. \nErro: " + e);
		}
		return p;
	}

	@Override
	public void delete(Paciente paciente) {
		try {
			PreparedStatement pstmExames = conn.getConnection()
					.prepareStatement("DELETE FROM EXAMES WHERE PACIENTE_ID = ?;");
			pstmExames.setLong(1, paciente.getId());
			pstmExames.execute();
			pstmExames.close();

			PreparedStatement pstmPaciente = conn.getConnection()
					.prepareStatement("DELETE FROM PACIENTES WHERE ID = ?;");
			pstmPaciente.setLong(1, paciente.getId());
			pstmPaciente.execute();
			pstmPaciente.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel deletar o paciente. \nErro: " + e);
		}
	}
	
	public Paciente findByCPF(String cpf) {
	    Paciente paciente = null;
	    try {
	        PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES WHERE cpf = ?;");
	        pstm.setString(1, cpf);
	        ResultSet rs = pstm.executeQuery();
	        if (rs.next()) {
	            paciente = new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf"));

	            // Carregar e setar exames
	            List<Exame> exames = carregarExames(paciente);
	            paciente.setExames(exames);
	        }
	        rs.close();
	        pstm.close();
	    } catch (SQLException e) {
	    	throw new DAOException("Não foi possivel buscar o paciente. \nErro: " + e);
	    }
	    return paciente;
	}


	@Override
	public List<Paciente> findAll() {
	    List<Paciente> pacientes = new ArrayList<>();
	    try {
	        PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES;");
	        ResultSet rs = pstm.executeQuery();

	        while (rs.next()) {
	            Paciente paciente = new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf"));
	            
	            pacientes.add(paciente);
	        }
	        pstm.close();
	    } catch (SQLException e) {
	        throw new DAOException("Não foi possível buscar os pacientes! \nErro: ", e);
	    }
	    return pacientes;
	}
	
	private List<Exame> carregarExames(Paciente paciente) {
	    List<Exame> exames = new ArrayList<>();
	    try {
	        PreparedStatement pstm = conn.getConnection()
	                .prepareStatement("SELECT * FROM EXAMES WHERE paciente_id = ?;");
	        pstm.setLong(1, paciente.getId());
	        ResultSet rs = pstm.executeQuery();

	        while (rs.next()) {
	            Exame exame = new Exame();
	            exame.setId(rs.getLong("id"));
	            exame.setDescricao(rs.getString("descricao"));
	            exame.setData(rs.getTimestamp("data_exame").toLocalDateTime());
	            exame.setpaciente(paciente);
	            exames.add(exame);
	        }

	        rs.close();
	        pstm.close();
	    } catch (SQLException e) {
	        throw new DAOException("Erro ao carregar exames do paciente ID: " + paciente.getId(), e);
	    }

	    return exames;
	}


	@Override
	public void update(Paciente t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("UPDATE PACIENTES SET NOME = ?, CPF = ? WHERE ID = ?;");
			pstm.setString(1, t.getNome());
			pstm.setString(2, t.getCpf());
			pstm.setLong(3, t.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel atualizar o paciente. \nErro: " + e);
		}
	}

}
